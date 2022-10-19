package io.jenkins.plugins.lgtm.domain.bitbucket

import io.jenkins.plugins.lgtm.domain.Authorization
import java.util.Base64

class BitbucketServerAuthentication(
    private val username: String,
    private val password: String,
) : Authorization {
    override fun asHeaderValue(): String {
        val encodedSecret = Base64.getEncoder().encodeToString("$username:$password".toByteArray())
        return "Basic $encodedSecret"
    }
}
