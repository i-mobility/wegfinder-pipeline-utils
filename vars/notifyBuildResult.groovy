import groovy.json.JsonOutput
import hudson.tasks.test.AbstractTestResultAction

def call(args) {


    buildStatus = args.buildStatus ?: 'SUCCESS'
    channel = args.channel
    pretext = args.pretext

    // Override default values based on build status
    if (buildStatus == 'SUCCESS') {
        colorCode = 'good'
    } else if (buildStatus == 'WARNING') {
        colorCode = 'warning'
    } else {
        colorCode = 'danger'
    }

    def buildType = "Unkown"

    def isSCM = currentBuild.rawBuild.getCause(hudson.triggers.SCMTrigger$SCMTriggerCause) != null
    def isUser = currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause) != null

    def branch = env.CHANGE_BRANCH ?: env.GIT_BRANCH

    def title = "${buildStatus}: ${env.JOB_NAME} #${env.BUILD_NUMBER}"


    def testStatus = "-"
    def failedTests = "-"

    AbstractTestResultAction testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
    if (testResultAction != null) {
        def total = testResultAction.totalCount
        def failed = testResultAction.failCount
        def skipped = testResultAction.skipCount
        def passed = total - failed - skipped

        failedTests = "${testResultAction.getFailedTests()}"

        testStatus = "Passed: ${passed}, Failed: ${failed} ${testResultAction.failureDiffString}, Skipped: ${skipped}"
    }
    testResultAction = null

    def attachments = JsonOutput.toJson([
        [
            fallback: title,
            color: colorCode,
            title: title,
            pretext: pretext,
            title_link: env.RUN_DISPLAY_URL,
            fields: [
                [
                    title: "branch",
                    value: branch,
                    short: false
                ],
                [
                    title: "details",
                    value: env.RUN_DISPLAY_URL,
                    short: false
                ],
                [
                    title: "trigger",
                    value: currentBuild.rawBuild.getCauses().first().getShortDescription(),
                    short: false
                ],
                [
                    title: "test-status",
                    value: testStatus,
                    short: false
                ],
                [
                    title: "failed-tests",
                    value: failedTests,
                    short: false
                ]
            ]
        ]
    ])

    slackSend(
        channel: channel,
        color: colorCode,
        attachments: attachments
    )
}