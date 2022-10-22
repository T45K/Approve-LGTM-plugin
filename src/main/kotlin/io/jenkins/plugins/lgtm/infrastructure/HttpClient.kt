package io.jenkins.plugins.lgtm.infrastructure

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.right
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.jenkins.plugins.lgtm.domain.Authorization
import io.jenkins.plugins.lgtm.util.`|`
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

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
        base: String, path: String, clazz: Class<T>,
        auth: Authorization? = null,
    ): Either<NonEmptyList<String>, T> {
        val url = base.toHttpUrl().newBuilder().addPathSegments(path).build()

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
            nonEmptyListOf(e.stackTraceToString()).left()
        }
    }

    fun <Req, Res> post(
        base: String, path: String,
        requestBodyObject: Req,
        responseBodyClass: Class<Res>,
        auth: Authorization? = null,
    ): Either<NonEmptyList<String>, Res> {
        val url = base.toHttpUrl().newBuilder().addPathSegments(path).build()

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
            nonEmptyListOf(e.stackTraceToString()).left()
        }
    }

    fun <T> delete(
        base: String, path: String,
        responseBodyClass: Class<T>,
        auth: Authorization? = null,
    ): Either<NonEmptyList<String>, T> {
        val url = base.toHttpUrl().newBuilder().addPathSegments(path).build()

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
            nonEmptyListOf(e.stackTraceToString()).left()
        }
    }

    private fun Request.Builder.authorizationHeader(auth: Authorization?): Request.Builder =
        if (auth == null) this@authorizationHeader
        else this.header("Authorization", auth.asHeaderValue())
}

class CommunicationFailureException(override val message: String?) : RuntimeException()
