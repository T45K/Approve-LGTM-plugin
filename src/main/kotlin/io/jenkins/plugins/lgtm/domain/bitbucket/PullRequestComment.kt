package io.jenkins.plugins.lgtm.domain.bitbucket

data class PullRequestComment(val id: Int, val text: String, val version: Int, val user: BitbucketServerUser) {
    fun isLgtmPictureComment(): Boolean = this.text.startsWith("![LGTM]")
}
