package io.jenkins.plugins.lgtm.domain

interface HttpClient {
    fun <T> get(
        host: String, path: String, clazz: Class<T>,
        auth: Authorization? = null,
    ): T?

    fun <Req, Res> post(
        host: String, path: String,
        requestBodyObject: Req,
        responseBodyClass: Class<Res>,
        auth: Authorization? = null,
    ): Res?
}
