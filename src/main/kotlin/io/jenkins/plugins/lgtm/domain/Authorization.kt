package io.jenkins.plugins.lgtm.domain

interface Authorization {
    fun asHeaderValue(): String
}
