package io.jenkins.plugins.lgtm.infrastructure

import spock.lang.Specification

class LgtmPictureRepositoryImplTest extends Specification {

    def 'call real server'() {
        given:
        def sut = new LgtmPictureRepositoryImpl(new HttpClient())

        when:
        def lgtmPicture = sut.findRandom()

        then:
        noExceptionThrown()
        lgtmPicture != null
    }
}
