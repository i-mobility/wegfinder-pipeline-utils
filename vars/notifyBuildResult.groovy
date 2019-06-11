import groovy.json.JsonOutput

def call(
    String buildStatus,
    String channel,
    String pretext = null
) {
    buildStatus = buildStatus ?: 'SUCCESS'

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

    if (env.CHANGE_BRANCH) {
        buildType = "Pull Request"
    } else if (params.NIGHTLY == 'YES') {
        buildType = "Nightly"
    } else if (isSCM) {
        buildType = "GIT Push"
    } else if (isUser) {
        buildType = "Manually Triggered"
    } else {
        buildType = "Unkown trigger"
    }

    def title = "${buildStatus}: ${buildType} ${env.JOB_NAME} #${env.BUILD_NUMBER}"

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