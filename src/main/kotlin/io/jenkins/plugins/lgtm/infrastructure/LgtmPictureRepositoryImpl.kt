package io.jenkins.plugins.lgtm.infrastructure

import io.jenkins.plugins.lgtm.domain.LgtmPicture
import io.jenkins.plugins.lgtm.domain.LgtmPictureRepository

class LgtmPictureRepositoryImpl(
    private val httpClient: HttpClient,
) : LgtmPictureRepository {
    override fun findRandom(): LgtmPicture? {
        TODO("Not yet implemented")
    }
}
