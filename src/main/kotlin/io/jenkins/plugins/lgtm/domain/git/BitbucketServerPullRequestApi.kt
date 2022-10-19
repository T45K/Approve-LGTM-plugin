package io.jenkins.plugins.lgtm.domain.git

import arrow.core.Either
import io.jenkins.plugins.lgtm.domain.HttpClient

class BitbucketServerPullRequestApi(
    private val hostName: String,
    private val organizationName: String,
    private val repositoryName: String,
    private val httpClient: HttpClient,
) {

    // https://developer.atlassian.com/server/bitbucket/rest/v805/api-group-pull-requests/#api-api-latest-projects-projectkey-repos-repositoryslug-pull-requests-pullrequestid-comments-post
    // rest/api/latest/projects/{projectKey}/repos/{repositorySlug}/pull-requests/{pullRequestId}/comments
    fun sendComment(
        comment: String,
        pullRequest: PullRequest,
        authentication: BitbucketServerAuthentication
    ): Either<String, String> {
        return httpClient.post(
            hostName,
            "rest/api/latest/projects/$organizationName/repos/$repositoryName/pull-requests/${pullRequest.id}/comments",
            PullRequestCommentRequest(comment),
            Any::class.java,
            authentication,
        )
            ?.let { Either.Right("Succeeded to post a comment") }
            ?: Either.Left("Failed to post a comment")
    }
}

private data class PullRequestCommentRequest(val text: String)
