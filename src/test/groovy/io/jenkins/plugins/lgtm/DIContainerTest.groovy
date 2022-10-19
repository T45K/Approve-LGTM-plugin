package io.jenkins.plugins.lgtm

import io.jenkins.plugins.lgtm.domain.HttpClient
import io.jenkins.plugins.lgtm.domain.bitbucket.AuthenticatedBitbucketServerUser
import io.jenkins.plugins.lgtm.domain.picture.LgtmPictureRepository
import io.jenkins.plugins.lgtm.infrastructure.HttpClientImpl
import io.jenkins.plugins.lgtm.infrastructure.LgtmoonPictureRepository
import kotlin.jvm.JvmClassMappingKt
import spock.lang.Specification

class DIContainerTest extends Specification {

  def sut = new DIContainer()

  def 'get can returns object based on given type'() {
    expect:
    sut.get(clazz).class == instanceType

    where:
    clazz                                                   || instanceType
    JvmClassMappingKt.getKotlinClass(HttpClient)            || HttpClientImpl
    JvmClassMappingKt.getKotlinClass(LgtmPictureRepository) || LgtmoonPictureRepository
  }

  def 'get throws NoSuchElement exception when given type is not target of DI'() {
    when:
    sut.get(JvmClassMappingKt.getKotlinClass(AuthenticatedBitbucketServerUser))

    then:
    thrown(NoSuchElementException)
  }
}
