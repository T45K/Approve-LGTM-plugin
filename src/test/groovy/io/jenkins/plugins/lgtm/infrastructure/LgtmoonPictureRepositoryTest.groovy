package io.jenkins.plugins.lgtm.infrastructure

import arrow.core.Either
import arrow.core.EitherKt
import arrow.core.NonEmptyList
import arrow.core.NonEmptyListKt
import spock.lang.Specification

class LgtmoonPictureRepositoryTest extends Specification {

    def 'findRandom succeeds when calling real server'() {
        given:
        def sut = new LgtmoonPictureRepository(new HttpClient())

        expect:
        sut.findRandom().isRight()
    }

    def 'findRandom appends error message when calling API fails'() {
        given:
        final def httpClient = Stub(HttpClient) {
            get(*_) >> EitherKt.left(NonEmptyListKt.nonEmptyListOf('Communication error.'))
        }
        final def sut = new LgtmoonPictureRepository(httpClient)

        when:
        def result = sut.findRandom()

        then:
        result.isLeft()
        (result as Either.Left<NonEmptyList<String>>).value ==
            NonEmptyListKt.nonEmptyListOf('Communication error.', 'Failed to fetch LGTM picture.')
    }
}
