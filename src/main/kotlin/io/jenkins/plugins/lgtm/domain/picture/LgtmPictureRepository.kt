package io.jenkins.plugins.lgtm.domain.picture

import arrow.core.Either
import arrow.core.NonEmptyList

interface LgtmPictureRepository {
    fun findRandom(): Either<NonEmptyList<String>, LgtmPicture>
}
