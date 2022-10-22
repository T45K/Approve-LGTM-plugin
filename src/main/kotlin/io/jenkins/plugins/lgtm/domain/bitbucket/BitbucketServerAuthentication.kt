package io.jenkins.plugins.lgtm.domain.bitbucket

import io.jenkins.plugins.lgtm.domain.Authorization
import okhttp3.Credentials

class BitbucketServerAuthentication(
    private val username: String,
    private val password: String,
) : Authorization {
    override fun asHeaderValue(): String = Credentials.basic(username, password)
}
