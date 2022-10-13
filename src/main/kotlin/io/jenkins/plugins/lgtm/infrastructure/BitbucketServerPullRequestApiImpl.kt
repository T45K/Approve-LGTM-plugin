package io.jenkins.plugins.lgtm.infrastructure

import arrow.core.Either
import io.jenkins.plugins.lgtm.domain.BitbucketServerPullRequestApi
import io.jenkins.plugins.lgtm.domain.LgtmPicture
import io.jenkins.plugins.lgtm.domain.PullRequest

class BitbucketServerPullRequestApiImpl(
    private val httpClient: HttpClient,
    private val authentication: BitbucketServerAuthentication,
) : BitbucketServerPullRequestApi {
    companion object {
        const val host = "" // TODO
        const val path = ""
    }

    override fun sendLgtmPicture(lgtmPicture: LgtmPicture, pullRequest: PullRequest): Either<String, String> {
        TODO()
    }
}
