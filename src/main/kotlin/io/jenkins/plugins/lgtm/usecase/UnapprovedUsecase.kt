package io.jenkins.plugins.lgtm.usecase

import arrow.core.Either
import io.jenkins.plugins.lgtm.domain.bitbucket.AuthenticatedBitbucketServerUser
import io.jenkins.plugins.lgtm.domain.bitbucket.PullRequest
import io.jenkins.plugins.lgtm.presentation.JenkinsLogger

class UnapprovedUsecase : Usecase {
    override fun execute(user: AuthenticatedBitbucketServerUser, pullRequest: PullRequest) {
        when (val commentDeleteResponse = user.deletePastLgtmComment(pullRequest)) {
            is Either.Left -> {
                for (errorMessage in commentDeleteResponse.value) {
                    JenkinsLogger.info(errorMessage)
                }
                JenkinsLogger.info("Failed to execute unapproval job.")
                throw JobFailureException()
            }

            is Either.Right -> JenkinsLogger.info("Succeeded to execute unapproval job.")
        }
    }
}
