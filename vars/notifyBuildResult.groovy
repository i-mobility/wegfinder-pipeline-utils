import groovy.json.JsonOutput
import hudson.tasks.test.AbstractTestResultAction

def call(args) {


    buildStatus = args.buildStatus ?: 'SUCCESS'
    channel = args.channel
    pretext = args.pretext
    artifacts = "-"
    if (args.artifacts != null) {
        artifacts = "<${env.BUILD_URL}/artifact/${args.artifacts}|Artifacts>"
    }

    // Override default values based on build status
    if (buildStatus == 'SUCCESS') {
        colorCode = 'good'
    } else if (buildStatus == 'WARNING') {
        colorCode = 'warning'
    } else {
        colorCode = 'danger'
    }

    def branchName = env.CHANGE_BRANCH ?: env.GIT_BRANCH
    def branchURL = env.CHANGE_URL

    def title = "${buildStatus}: ${branchName} #${env.BUILD_NUMBER}"


    def testStatus = "-"
    def failedTests = "-"

    AbstractTestResultAction testResultAction = currentBuild.rawBuild.getAction(AbstractTestResultAction.class)

    if (testResultAction != null) {
        def total = testResultAction.totalCount
        def failed = testResultAction.failCount
        def skipped = testResultAction.skipCount
        def passed = total - failed - skipped

        def failedTestsArray = []

        if (testResultAction.getFailedTests() != null) {
            for(element in testResultAction.getFailedTests()) {
                failedTestsArray << element.getName()
            }
        }

        if (!failedTestsArray.isEmpty()) {
            failedTests = failedTestsArray.join("\n")
        }

        testStatus = "Passed: ${passed}, Failed: ${failed} ${testResultAction.failureDiffString}, Skipped: ${skipped}"
    }
    testResultAction = null


    def branchField = branchName

    if (branchURL != null) {
        branchField = "<${branchURL}|${branchName}>"
    }

    def buildTrigger = currentBuild.rawBuild.getCauses().first().getShortDescription()

    if (params.NIGHTLY == 'YES') {
        buildTrigger = "Nightly"
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
                    title: "artifacts",
                    value: "${artifacts}",
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
