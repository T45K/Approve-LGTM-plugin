package io.jenkins.plugins.lgtm.infrastructure

import arrow.core.Either
import io.jenkins.plugins.lgtm.domain.picture.LgtmPicture
import io.jenkins.plugins.lgtm.domain.picture.LgtmPictureRepository

class LgtmoonPictureRepository(
    private val httpClient: HttpClient,
) : LgtmPictureRepository {
    companion object {
        const val host = "lgtmoon.dev"
        const val path = "api/images/random"
    }

    override fun findRandom(): Either<List<String>, LgtmPicture> =
        httpClient.get(host, path, LgtmoonApiResponse::class.java)
            .map { LgtmPicture(it.images[0].url) }
            .mapLeft { it + "Failed to fetch LGTM picture." }
}

data class LgtmoonApiResponse(val images: List<LgtmoonImage>)

data class LgtmoonImage(val url: String, private val isConverted: Boolean)
