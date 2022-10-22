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

    // https://developer.atlassian.com/server/bitbucket/rest/v805/api-group-pull-requests/#api-api-latest-projects-projectkey-repos-repositoryslug-pull-requests-pullrequestid-activities-get
    override fun fetchAllCommentsIn(
        pullRequest: PullRequest,
        authentication: BitbucketServerAuthentication
    ): Either<List<String>, List<PullRequestComment>> =
        httpClient.get(
            hostName,
            "rest/api/latest/projects/$organizationName/repos/$repositoryName/pull-requests/${pullRequest.id}/activities",
            ActivitiesResponse::class.java,
            authentication
        ).map { response ->
            response.values
                .filter { it.isCommentAction() }
                .map { PullRequestComment(it.comment!!.id, it.comment.text, BitbucketServerUser(it.user.name)) }
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

data class ActivitiesResponse(val values: List<ActivityResponse>) {
    data class ActivityResponse(val action: String, val comment: CommentResponse?, val user: UserResponse) {
        data class UserResponse(val name: String)

        data class CommentResponse(val id: Int, val text: String)

        fun isCommentAction(): Boolean = this.action == "COMMENTED" && this.comment != null
    }
}
