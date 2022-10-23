package io.jenkins.plugins.lgtm.infrastructure

import arrow.core.Either
import arrow.core.NonEmptyList
import io.jenkins.plugins.lgtm.domain.bitbucket.BitbucketServerAuthentication
import io.jenkins.plugins.lgtm.domain.bitbucket.BitbucketServerPullRequestApi
import io.jenkins.plugins.lgtm.domain.bitbucket.BitbucketServerUser
import io.jenkins.plugins.lgtm.domain.bitbucket.PullRequest
import io.jenkins.plugins.lgtm.domain.bitbucket.PullRequestComment

class BitbucketServerPullRequestApiImpl(
    private val baseUrl: String,
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
    ): Either<NonEmptyList<String>, *> =
        httpClient.post(
            PullRequestCommentRequest(commentText),
            Any::class.java,
            baseUrl,
            "rest/api/latest/projects/$organizationName/repos/$repositoryName/pull-requests/${pullRequest.id}/comments",
            auth = authentication,
        ).mapLeft { it + "Failed to post comment." }

    // https://developer.atlassian.com/server/bitbucket/rest/v805/api-group-pull-requests/#api-api-latest-projects-projectkey-repos-repositoryslug-pull-requests-pullrequestid-activities-get
    override fun fetchAllCommentsIn(
        pullRequest: PullRequest,
        authentication: BitbucketServerAuthentication
    ): Either<NonEmptyList<String>, List<PullRequestComment>> =
        httpClient.get(
            ActivitiesResponse::class.java,
            baseUrl,
            "rest/api/latest/projects/$organizationName/repos/$repositoryName/pull-requests/${pullRequest.id}/activities",
            auth = authentication
        ).map { response ->
            response.values
                .filter { it.isCommentAction() }
                .map {
                    PullRequestComment(
                        it.comment!!.id,
                        it.comment.text,
                        it.comment.version,
                        BitbucketServerUser(it.user.name)
                    )
                }
        }.mapLeft { it + "Failed to fetch comments." }

    override fun deleteComment(
        pullRequest: PullRequest,
        comment: PullRequestComment,
        authentication: BitbucketServerAuthentication
    ): Either<NonEmptyList<String>, *> =
        httpClient.delete(
            Any::class.java,
            baseUrl,
            "rest/api/latest/projects/$organizationName/repos/$repositoryName/pull-requests/${pullRequest.id}/comments/${comment.id}",
            mapOf("version" to listOf(comment.version.toString())),
            authentication
        ).mapLeft { it + "Failed to delete comment." }
}

data class PullRequestCommentRequest(val text: String)

data class ActivitiesResponse(val values: List<ActivityResponse>) {
    data class ActivityResponse(val action: String, val comment: CommentResponse?, val user: UserResponse) {
        data class UserResponse(val name: String)

        data class CommentResponse(val id: Int, val version: Int, val text: String)

        fun isCommentAction(): Boolean = this.action == "COMMENTED" && this.comment != null
    }
}
