package io.jenkins.plugins.lgtm.usecase

import arrow.core.Either
import io.jenkins.plugins.lgtm.domain.bitbucket.AuthenticatedBitbucketServerUser
import io.jenkins.plugins.lgtm.domain.bitbucket.PullRequest
import io.jenkins.plugins.lgtm.presentation.JenkinsLogger

class ApprovedUsecase : Usecase {
    override fun execute(user: AuthenticatedBitbucketServerUser, pullRequest: PullRequest) {
        when (val commentPostResult = user.sendPictureTo(pullRequest)) {
            is Either.Left -> {
                for (errorMessage in commentPostResult.value) {
                    JenkinsLogger.info(errorMessage)
                }
                JenkinsLogger.info("Failed to execute approval job.")
                throw JobFailureException()
            }

            is Either.Right -> JenkinsLogger.info("Succeeded to execute approval job.")
        }
    }
}
