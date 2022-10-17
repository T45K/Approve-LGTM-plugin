package io.jenkins.plugins.lgtm.usecase

import io.jenkins.plugins.lgtm.domain.BitbucketServerPullRequestApi
import io.jenkins.plugins.lgtm.domain.LgtmPictureRepository
import io.jenkins.plugins.lgtm.domain.PullRequest
import io.jenkins.plugins.lgtm.presentation.JenkinsLogger

class PostLgtmPictureUsecase(
    private val lgtmPictureRepository: LgtmPictureRepository,
    private val bitbucketServerPullRequestApi: BitbucketServerPullRequestApi,
) {
    fun execute(pullRequest: PullRequest) {
        val lgtmPicture = lgtmPictureRepository.findRandom() ?: run {
            JenkinsLogger.info("Cannot retrieve LGTM picture from API.")
            return
        }

        bitbucketServerPullRequestApi.sendLgtmPicture(lgtmPicture, pullRequest)
    }
}
