package io.jenkins.plugins.lgtm

import io.jenkins.plugins.lgtm.domain.bitbucket.PullRequest
import io.jenkins.plugins.lgtm.presentation.JenkinsLogger
import io.jenkins.plugins.lgtm.usecase.ApprovalUsecase
import io.jenkins.plugins.lgtm.usecase.UnapprovalUsecase
import java.io.PrintStream

class EntryPoint(
    private val jenkinsPrintStream: PrintStream,
    private val definedUserName: String,
    private val inputUserName: String,
    private val hostName: String,
    private val organizationName: String,
    private val repositoryName: String,
    private val username: String,
    private val password: String,
    private val pullRequestId: String,
) {

    fun start() {
        JenkinsLogger.delegate = jenkinsPrintStream

        if (definedUserName != inputUserName) {
            JenkinsLogger.info("$inputUserName user is not target of this job.")
            return
        }

        val usecase = when ("webhook name") {
            "approval" -> ApprovalUsecase()
            "unapproval" -> UnapprovalUsecase()
            else -> {
                JenkinsLogger.info("webhook name trigger is not target of this job.")
                return
            }
        }

        val container = DIContainer()
        val api = container.bitbucketServerPullRequestApiFactory(hostName, organizationName, repositoryName)
        val user = container.bitbucketServerUserFactory(username, password, api)
        val pullRequest = PullRequest(pullRequestId)

        usecase.execute(user, pullRequest)
    }
}
