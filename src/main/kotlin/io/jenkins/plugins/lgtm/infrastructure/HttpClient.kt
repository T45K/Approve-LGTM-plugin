package io.jenkins.plugins.lgtm.infrastructure

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.nonEmptyListOf
import arrow.core.right
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonMapperBuilder
import io.jenkins.plugins.lgtm.domain.Authorization
import io.jenkins.plugins.lgtm.util.`|`
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

open class HttpClient {
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
    private val jsonMapper = jacksonMapperBuilder()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .build()

    open fun <T> get(
        clazz: Class<T>,
        base: String, path: String,
        params: Map<String, List<String>> = emptyMap(),
        auth: Authorization? = null,
    ): Either<NonEmptyList<String>, T> {
        val url = base.toHttpUrl().newBuilder()
            .addPathSegments(path)
            .addQueryParameters(params)
            .build()

        val request = Request.Builder()
            .url(url)
            .authorizationHeader(auth)
            .get()
            .build()

        return try {
            client.newCall(request)
                .execute()
                .use { jsonMapper.readValue(it.body.bytes(), clazz) }
                .right()
        } catch (e: Exception) {
            nonEmptyListOf(e.stackTraceToString()).left()
        }
    }

    fun <Req, Res> post(
        requestBodyObject: Req, responseBodyClass: Class<Res>,
        base: String,
        path: String,
        params: Map<String, List<String>> = emptyMap(),
        auth: Authorization? = null,
    ): Either<NonEmptyList<String>, Res> {
        val url = base.toHttpUrl().newBuilder()
            .addPathSegments(path)
            .addQueryParameters(params)
            .build()

        val requestBody = jsonMapper.writeValueAsString(requestBodyObject)
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(url)
            .authorizationHeader(auth)
            .post(requestBody)
            .build()

        return try {
            client.newCall(request)
                .execute()
                .use { jsonMapper.readValue(it.body.bytes(), responseBodyClass) }
                .right()
        } catch (e: Exception) {
            nonEmptyListOf(e.stackTraceToString()).left()
        }
    }

    fun delete(
        base: String, path: String,
        params: Map<String, List<String>> = emptyMap(),
        auth: Authorization? = null,
    ): Either<NonEmptyList<String>, Unit> {
        val url = base.toHttpUrl().newBuilder()
            .addPathSegments(path)
            .addQueryParameters(params)
            .build()

        val request = Request.Builder()
            .url(url)
            .authorizationHeader(auth)
            .delete()
            .build()

        return try {
            client.newCall(request)
                .execute()
                .close()
            Either.Right(Unit)
        } catch (e: Exception) {
            nonEmptyListOf(e.stackTraceToString()).left()
        }
    }

    private fun Request.Builder.authorizationHeader(auth: Authorization?): Request.Builder =
        if (auth == null) this@authorizationHeader
        else this.header("Authorization", auth.asHeaderValue())

    private fun HttpUrl.Builder.addQueryParameters(params: Map<String, List<String>>): HttpUrl.Builder =
        this.apply {
            for ((name, values) in params) {
                for (value in values) {
                    this.addQueryParameter(name, value)
                }
            }
        }
}

class CommunicationFailureException(override val message: String?) : RuntimeException()
