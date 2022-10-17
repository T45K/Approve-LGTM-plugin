package io.jenkins.plugins.lgtm.infrastructure

sealed interface Authorization {
    fun asHeaderValue(): String
}
