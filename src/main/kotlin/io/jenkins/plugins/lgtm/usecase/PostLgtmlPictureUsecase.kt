package io.jenkins.plugins.lgtm.usecase

import arrow.core.Either
import io.jenkins.plugins.lgtm.domain.git.BitbucketServerUser
import io.jenkins.plugins.lgtm.domain.git.PullRequest
import io.jenkins.plugins.lgtm.presentation.JenkinsLogger

class PostLgtmPictureUsecase {
    fun execute(user: BitbucketServerUser, pullRequest: PullRequest) {
        when (val commentPostResult = user.sendPictureTo(pullRequest)) {
            is Either.Left -> {
                JenkinsLogger.info("Failed to execute job due to the following reason.")
                JenkinsLogger.info(commentPostResult.value)
            }

            is Either.Right -> JenkinsLogger.info("Succeeded to execute job.")
        }
    }
}
