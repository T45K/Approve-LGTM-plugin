package io.jenkins.plugins.lgtm.domain

data class LgtmPicture(private val url: String) {
    fun asMarkDown(): String = "![LGTM]($url)"
}
