package io.jenkins.plugins.lgtm.infrastructure

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jenkins.plugins.lgtm.domain.Authorization
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import io.jenkins.plugins.lgtm.util.`|`

class HttpClient {
    private val client = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val response = chain.request() `|` chain::proceed
            if (!response.isSuccessful) {
                throw CommunicationFailureException(
                    """
                        status code: ${response.code}
                        response body: ${response.body.string()}
                    """.trimIndent()
                )
            }
            response
        })
        .build()
    private val objectMapper = jacksonObjectMapper()

    fun <T> get(
        host: String, path: String, clazz: Class<T>,
        auth: Authorization? = null,
    ): Either<List<String>, T> {
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
                .right()
        } catch (e: Exception) {
            listOf(e.messageWithStackTrace()).left()
        }
    }

    fun <Req, Res> post(
        host: String, path: String,
        requestBodyObject: Req,
        responseBodyClass: Class<Res>,
        auth: Authorization? = null,
    ): Either<List<String>, Res> {
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
                .right()
        } catch (e: Exception) {
            listOf(e.messageWithStackTrace()).left()
        }
    }

    fun <T> delete(
        host: String, path: String,
        responseBodyClass: Class<T>,
        auth: Authorization? = null,
    ): Either<List<String>, T> {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host(host)
            .addPathSegments(path)
            .build()

        val request = Request.Builder()
            .url(url)
            .authorizationHeader(auth)
            .delete()
            .build()

        return try {
            client.newCall(request)
                .execute()
                .use { objectMapper.readValue(it.body.bytes(), responseBodyClass) }
                .right()
        } catch (e: Exception) {
            listOf(e.messageWithStackTrace()).left()
        }
    }

    private fun Request.Builder.authorizationHeader(auth: Authorization?): Request.Builder =
        if (auth == null) this@authorizationHeader
        else this.header("Authorization", auth.asHeaderValue())
}

class CommunicationFailureException(override val message: String?) : RuntimeException()

private fun Exception.messageWithStackTrace(): String =
    if (message != null) {
        """$message
            |${stackTraceToString()}
        """.trimMargin()
    } else {
        stackTraceToString()
    }
