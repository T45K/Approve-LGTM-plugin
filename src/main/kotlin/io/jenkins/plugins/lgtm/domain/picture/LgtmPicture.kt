package io.jenkins.plugins.lgtm.domain.picture

data class LgtmPicture(private val url: String) {
    fun asMarkDown(): String = "![LGTM]($url)"
}
