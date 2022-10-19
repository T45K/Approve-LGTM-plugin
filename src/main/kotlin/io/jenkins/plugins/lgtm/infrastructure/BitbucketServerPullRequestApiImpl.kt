package io.jenkins.plugins.lgtm.infrastructure

import arrow.core.Either
import io.jenkins.plugins.lgtm.domain.bitbucket.BitbucketServerAuthentication
import io.jenkins.plugins.lgtm.domain.bitbucket.BitbucketServerPullRequestApi
import io.jenkins.plugins.lgtm.domain.bitbucket.BitbucketServerUser
import io.jenkins.plugins.lgtm.domain.bitbucket.PullRequest
import io.jenkins.plugins.lgtm.domain.bitbucket.PullRequestComment

class BitbucketServerPullRequestApiImpl(
    private val hostName: String,
    private val organizationName: String,
    private val repositoryName: String,
    private val httpClient: HttpClient,
) : BitbucketServerPullRequestApi {

    // https://developer.atlassian.com/server/bitbucket/rest/v805/api-group-pull-requests/#api-api-latest-projects-projectkey-repos-repositoryslug-pull-requests-pullrequestid-comments-post
    // rest/api/latest/projects/{projectKey}/repos/{repositorySlug}/pull-requests/{pullRequestId}/comments
    override fun sendComment(
        commentText: String,
        pullRequest: PullRequest,
        authentication: BitbucketServerAuthentication
    ): Either<List<String>, *> =
        httpClient.post(
            hostName,
            "rest/api/latest/projects/$organizationName/repos/$repositoryName/pull-requests/${pullRequest.id}/comments",
            PullRequestCommentRequest(commentText),
            Any::class.java,
            authentication,
        ).mapLeft { it + "Failed to post comment." }

    // https://developer.atlassian.com/server/bitbucket/rest/v805/api-group-pull-requests/#api-api-latest-projects-projectkey-repos-repositoryslug-pull-requests-pullrequestid-comments-get
    override fun fetchAllCommentsIn(
        pullRequest: PullRequest,
        authentication: BitbucketServerAuthentication
    ): Either<List<String>, List<PullRequestComment>> =
        httpClient.get(
            hostName,
            "rest/api/latest/projects/$organizationName/repos/$repositoryName/pull-requests/${pullRequest.id}/comments",
            CommentsResponse::class.java,
            authentication
        ).map { response ->
            response.values.map { PullRequestComment(it.id, it.text, BitbucketServerUser(it.author.name)) }
        }.mapLeft { it + "Failed to fetch comments." }

    override fun deleteComment(
        pullRequest: PullRequest,
        comment: PullRequestComment,
        authentication: BitbucketServerAuthentication
    ): Either<List<String>, *> =
        httpClient.delete(
            hostName,
            "rest/api/latest/projects/$organizationName/repos/$repositoryName/pull-requests/${pullRequest.id}/comments/${comment.id}",
            Any::class.java,
            authentication
        ).mapLeft { it + "Failed to delete comment." }
}

data class PullRequestCommentRequest(val text: String)

data class CommentsResponse(val values: List<CommentResponse>)

data class CommentResponse(val id: Int, val text: String, val author: AuthorResponse)

data class AuthorResponse(val name: String) // TODO: nameかdisplayNameのどっちか
