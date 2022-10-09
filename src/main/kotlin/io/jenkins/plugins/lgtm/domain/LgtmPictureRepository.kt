package io.jenkins.plugins.lgtm.domain

interface LgtmPictureRepository {
    fun findRandom(): LgtmPicture?
}
