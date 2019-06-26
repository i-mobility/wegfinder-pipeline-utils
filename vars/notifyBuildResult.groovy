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

    def branchName = env.CHANGE_BRANCH ?: env.GIT_BRANCH
    def branchURL = env.CHANGE_URL

    def title = "${buildStatus}: ${env.JOB_NAME} #${env.BUILD_NUMBER}"


    def testStatus = "-"
    def failedTests = "-"

    AbstractTestResultAction testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)
    if (testResultAction != null) {
        def total = testResultAction.totalCount
        def failed = testResultAction.failCount
        def skipped = testResultAction.skipCount
        def passed = total - failed - skipped


        if (!testResultAction.getFailedTests().isEmpty()) {
           failedTests = testResultAction.getFailedTests().map { it.getName() }.join("\n")
        }

        testStatus = "Passed: ${passed}, Failed: ${failed} ${testResultAction.failureDiffString}, Skipped: ${skipped}"
    }
    testResultAction = null

    def fields = []

    def branchField = branchName

    if (branchURL != null) {
        branchField = "<${branchURL}|${branchName}>"
    } 

    def attachments = JsonOutput.toJson([
        [
            fallback: title,
            color: colorCode,
            title: title,
            pretext: pretext,
            title_link: env.RUN_DISPLAY_URL,
            fields: [
                [
                    title: "github",
                    value: branchField,
                    short: true
                ],
                [
                    title: "jenkins",
                    value: "<${env.RUN_DISPLAY_URL}|${env.JOB_NAME}>",
                    short: false
                ],
                [
                    title: "trigger",
                    value: currentBuild.rawBuild.getCauses().first().getShortDescription(),
                    short: true
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