package io.jenkins.plugins.lgtm.infrastructure

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jenkins.plugins.lgtm.presentation.JenkinsLogger
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request

class HttpClient {
    private val client = OkHttpClient()
    private val objectMapper = jacksonObjectMapper()

    fun <T> get(
        host: String, path: String, clazz: Class<T>,
        auth: Authorization = NoAuthorization,
    ): T? {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host(host)
            .addPathSegments(path)
            .build()

        val request = Request.Builder()
            .url(url)
            .authorizationHeader(auth)
            .get()
            .build()

        return try {
            client.newCall(request)
                .execute()
                .use { objectMapper.readValue(it.body.bytes(), clazz) }
        } catch (e: Exception) {
            JenkinsLogger.info(e.message ?: e.stackTraceToString())
            null
        }
    }

    private fun Request.Builder.authorizationHeader(auth: Authorization): Request.Builder =
        if (auth == NoAuthorization) this@authorizationHeader
        else this.header("Authorization", auth.asHeaderValue())
}
