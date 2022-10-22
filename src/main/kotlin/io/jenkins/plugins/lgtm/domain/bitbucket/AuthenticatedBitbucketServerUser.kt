package io.jenkins.plugins.lgtm.domain.bitbucket

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.flatMap
import io.jenkins.plugins.lgtm.domain.picture.LgtmPictureRepository

class AuthenticatedBitbucketServerUser(
    username: String,
    password: String,
    private val lgtmPictureRepository: LgtmPictureRepository,
    private val bitbucketServerPullRequestApi: BitbucketServerPullRequestApi,
) : BitbucketServerUser(username) {
    private val authentication = BitbucketServerAuthentication(username, password)

    fun sendPictureTo(pr: PullRequest): Either<NonEmptyList<String>, *> =
        lgtmPictureRepository.findRandom()
            .flatMap { bitbucketServerPullRequestApi.sendComment(it.asMarkDown(), pr, authentication) }

    fun deletePastLgtmComment(pr: PullRequest): Either<NonEmptyList<String>, *> =
        bitbucketServerPullRequestApi.fetchAllCommentsIn(pr, authentication)
            .map { comments ->
                comments.filter { it.user.hasSameName(this) }.find { it.isLgtmPictureComment() }
            }.flatMap {
                if (it == null) {
                    Either.Right(Unit)
                } else {
                    bitbucketServerPullRequestApi.deleteComment(pr, it, authentication)
                }
            }
}
