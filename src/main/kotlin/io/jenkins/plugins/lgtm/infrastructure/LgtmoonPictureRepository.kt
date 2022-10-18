package io.jenkins.plugins.lgtm.infrastructure

import io.jenkins.plugins.lgtm.domain.picture.LgtmPicture
import io.jenkins.plugins.lgtm.domain.picture.LgtmPictureRepository

class LgtmoonPictureRepository(
    private val httpClient: HttpClientImpl,
) : LgtmPictureRepository {
    companion object {
        const val host = "lgtmoon.dev"
        const val path = "api/images/random"
    }

    override fun findRandom(): LgtmPicture? =
        httpClient.get(host, path, LgtmoonApiResponse::class.java)?.let {
            LgtmPicture(it.images[0].url)
        }
}

data class LgtmoonApiResponse(val images: List<LgtmoonImage>)

data class LgtmoonImage(val url: String, private val isConverted: Boolean)
