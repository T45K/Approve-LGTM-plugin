package io.jenkins.plugins.lgtm.domain.bitbucket

open class BitbucketServerUser(private val username: String) {

    fun hasSameName(other: BitbucketServerUser): Boolean = this.username == other.username
}
