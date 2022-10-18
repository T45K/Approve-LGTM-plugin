package io.jenkins.plugins.lgtm.domain.picture

interface LgtmPictureRepository {
    fun findRandom(): LgtmPicture?
}
