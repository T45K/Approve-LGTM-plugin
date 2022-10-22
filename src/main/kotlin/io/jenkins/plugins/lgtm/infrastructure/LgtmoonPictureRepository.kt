package io.jenkins.plugins.lgtm.infrastructure

import arrow.core.Either
import arrow.core.NonEmptyList
import io.jenkins.plugins.lgtm.domain.picture.LgtmPicture
import io.jenkins.plugins.lgtm.domain.picture.LgtmPictureRepository

class LgtmoonPictureRepository(
    private val httpClient: HttpClient,
) : LgtmPictureRepository {
    companion object {
        const val base = "https://lgtmoon.dev"
        const val path = "api/images/random"
    }

    override fun findRandom(): Either<NonEmptyList<String>, LgtmPicture> =
        httpClient.get(base, path, LgtmoonApiResponse::class.java)
            .map { LgtmPicture(it.images[0].url) }
            .mapLeft { it + "Failed to fetch LGTM picture." }
}

data class LgtmoonApiResponse(val images: List<LgtmoonImage>)

data class LgtmoonImage(val url: String, private val isConverted: Boolean)
