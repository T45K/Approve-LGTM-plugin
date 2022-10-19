package io.jenkins.plugins.lgtm.domain.picture

import arrow.core.Either

interface LgtmPictureRepository {
    fun findRandom(): Either<List<String>, LgtmPicture>
}
