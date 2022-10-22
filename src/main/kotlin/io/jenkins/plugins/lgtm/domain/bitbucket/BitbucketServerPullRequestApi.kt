package io.jenkins.plugins.lgtm.domain.bitbucket

import arrow.core.Either

interface BitbucketServerPullRequestApi {

    fun sendComment(
        commentText: String,
        pullRequest: PullRequest,
        authentication: BitbucketServerAuthentication,
    ): Either<List<String>, *>

    fun fetchAllCommentsIn(
        pullRequest: PullRequest,
        authentication: BitbucketServerAuthentication,
    ): Either<List<String>, List<PullRequestComment>>

    fun deleteComment(
        pullRequest: PullRequest,
        comment: PullRequestComment,
        authentication: BitbucketServerAuthentication,
    ): Either<List<String>, *>
}
