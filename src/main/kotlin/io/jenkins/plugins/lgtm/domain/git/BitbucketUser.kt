package io.jenkins.plugins.lgtm.domain.git

import arrow.core.Either
import arrow.core.flatMap
import io.jenkins.plugins.lgtm.domain.picture.LgtmPictureRepository

class BitbucketServerUser(
    username: String,
    password: String,
    private val lgtmPictureRepository: LgtmPictureRepository,
    private val bitbucketServerPullRequestApi: BitbucketServerPullRequestApi,
) {
    private val authentication = BitbucketServerAuthentication(username, password)

    fun sendPictureTo(pr: PullRequest): Either<String, String> =
        (lgtmPictureRepository.findRandom()
            ?.let { Either.Right(it) }
            ?: Either.Left("Failed to fetch LGTM image."))
            .flatMap { bitbucketServerPullRequestApi.sendComment(it.asMarkDown(), pr, authentication) }
}
