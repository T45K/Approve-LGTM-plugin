package io.jenkins.plugins.lgtm.infrastructure

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jenkins.plugins.lgtm.presentation.JenkinsLogger
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class HttpClient {
    private val client = OkHttpClient()
    private val objectMapper = jacksonObjectMapper()

    fun <T> get(
        host: String, path: String, clazz: Class<T>,
        auth: Authorization? = null,
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

    fun <Req, Res> post(
        host: String, path: String,
        requestBodyObject: Req,
        responseBodyClass: Class<Res>,
        auth: Authorization? = null,
    ): Res? {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host(host)
            .addPathSegments(path)
            .build()

        val requestBody = objectMapper.writeValueAsString(requestBodyObject)
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .authorizationHeader(auth)
            .post(requestBody)
            .build()

        return try {
            client.newCall(request)
                .execute()
                .use { objectMapper.readValue(it.body.bytes(), responseBodyClass) }
        } catch (e: Exception) {
            JenkinsLogger.info(e.message ?: e.stackTraceToString())
            null
        }
    }

    private fun Request.Builder.authorizationHeader(auth: Authorization?): Request.Builder =
        if (auth == null) this@authorizationHeader
        else this.header("Authorization", auth.asHeaderValue())
}
