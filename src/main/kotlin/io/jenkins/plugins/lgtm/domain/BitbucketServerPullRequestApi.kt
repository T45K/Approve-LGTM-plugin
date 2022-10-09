package io.jenkins.plugins.lgtm.domain

import arrow.core.Either

interface BitbucketServerPullRequestApi {
    fun sendLgtmPicture(lgtmPicture: LgtmPicture, pullRequest: PullRequest): Either<String, String>
}
