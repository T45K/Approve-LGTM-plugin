package io.jenkins.plugins.lgtm.infrastructure

import arrow.core.Either
import io.jenkins.plugins.lgtm.domain.BitbucketServerPullRequestApi
import io.jenkins.plugins.lgtm.domain.LgtmPicture
import io.jenkins.plugins.lgtm.domain.PullRequest

class BitbucketServerPullRequestApiImpl(
    private val hostName: String,
    private val organizationName: String,
    private val repositoryName: String,
    private val httpClient: HttpClient,
    private val authentication: BitbucketServerAuthentication,
) : BitbucketServerPullRequestApi {

    // https://developer.atlassian.com/server/bitbucket/rest/v805/api-group-pull-requests/#api-api-latest-projects-projectkey-repos-repositoryslug-pull-requests-pullrequestid-comments-post
    // rest/api/latest/projects/{projectKey}/repos/{repositorySlug}/pull-requests/{pullRequestId}/comments
    override fun sendLgtmPicture(lgtmPicture: LgtmPicture, pullRequest: PullRequest): Either<String, String> {
        return httpClient.post(
            hostName,
            "rest/api/latest/projects/$organizationName/repos/$repositoryName/pull-requests/${pullRequest.id}/comments",
            PullRequestCommentRequest(lgtmPicture.asMarkDown()),
            Unit.javaClass,
            authentication,
        )
            ?.let { Either.Right("Succeeded to post a comment") }
            ?: Either.Left("Failed to post a comment")
    }
}

data class PullRequestCommentRequest(val text: String)
