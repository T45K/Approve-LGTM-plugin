package io.jenkins.plugins.lgtm.infrastructure

import spock.lang.Specification

class LgtmoonPictureRepositoryTest extends Specification {

    def 'call real server'() {
        given:
        def sut = new LgtmoonPictureRepository(new HttpClient())

        when:
        def lgtmPicture = sut.findRandom()

        then:
        noExceptionThrown()
        lgtmPicture != null
    }
}
