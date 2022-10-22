package io.jenkins.plugins.lgtm.domain.bitbucket

import arrow.core.Either
import arrow.core.NonEmptyList

interface BitbucketServerPullRequestApi {

    fun sendComment(
        commentText: String,
        pullRequest: PullRequest,
        authentication: BitbucketServerAuthentication,
    ): Either<NonEmptyList<String>, *>

    fun fetchAllCommentsIn(
        pullRequest: PullRequest,
        authentication: BitbucketServerAuthentication,
    ): Either<NonEmptyList<String>, List<PullRequestComment>>

    fun deleteComment(
        pullRequest: PullRequest,
        comment: PullRequestComment,
        authentication: BitbucketServerAuthentication,
    ): Either<NonEmptyList<String>, *>
}
