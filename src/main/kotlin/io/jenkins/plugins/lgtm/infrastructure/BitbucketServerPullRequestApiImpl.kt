package io.jenkins.plugins.lgtm.infrastructure

import arrow.core.Either
import io.jenkins.plugins.lgtm.domain.BitbucketServerPullRequestApi
import io.jenkins.plugins.lgtm.domain.LgtmPicture
import io.jenkins.plugins.lgtm.domain.PullRequest

class BitbucketServerPullRequestApiImpl(
    private val httpClient: HttpClient,
    private val authentication: BitbucketServerAuthentication,
) : BitbucketServerPullRequestApi {
    override fun sendLgtmPicture(lgtmPicture: LgtmPicture, pullRequest: PullRequest): Either<String, String> {
        TODO("Not yet implemented")
    }
}
