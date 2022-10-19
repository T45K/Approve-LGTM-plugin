package io.jenkins.plugins.lgtm

import io.jenkins.plugins.lgtm.domain.git.PullRequest
import io.jenkins.plugins.lgtm.presentation.JenkinsLogger
import io.jenkins.plugins.lgtm.usecase.PostLgtmPictureUsecase
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
            JenkinsLogger.info("$inputUserName is not target of this job.")
            return
        }

        val container = DIContainer()
        val api = container.bitbucketServerPullRequestApiFactory(hostName, organizationName, repositoryName)
        val user = container.bitbucketServerUserFactory(username, password, api)
        val pullRequest = PullRequest(pullRequestId)
        PostLgtmPictureUsecase().execute(user, pullRequest)
    }
}
