package io.jenkins.plugins.lgtm.infrastructure

interface Authorization {
    fun asHeaderValue(): String
}

object NoAuthorization : Authorization {
    override fun asHeaderValue(): String {
        throw RuntimeException("Don't call asHeaderValue method of NoAuthorization")
    }
}
