package io.jenkins.plugins.lgtm.usecase

import io.jenkins.plugins.lgtm.domain.bitbucket.AuthenticatedBitbucketServerUser
import io.jenkins.plugins.lgtm.domain.bitbucket.PullRequest

interface Usecase {
    fun execute(user: AuthenticatedBitbucketServerUser, pullRequest: PullRequest)
}
