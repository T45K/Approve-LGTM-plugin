package io.jenkins.plugins.lgtm.infrastructure

import arrow.core.Either
import arrow.core.NonEmptyList
import com.fasterxml.jackson.databind.JsonNode
import groovy.json.JsonSlurper
import io.jenkins.plugins.lgtm.dataclass.JsonStructure
import io.jenkins.plugins.lgtm.domain.bitbucket.BitbucketServerAuthentication
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import mockwebserver3.RecordedRequest
import spock.lang.Specification

class HttpClientTest extends Specification {

    final def sut = new HttpClient()

    final def mockWebServer = new MockWebServer()

    def 'get does GET request and binds response body to given type'() {
        given:
        final def responseBody = '''
            {
                "intValue"     : 1,
                "stringValue"  : "str",
                "booleanValue" : true,
                "nullValue"    : null,
                "objectValue"  : {
                    "key": "value"
                },
                "listValue"    : ["foo", "bar", "baz"]
            }
        '''

        mockWebServer.enqueue(new MockResponse().setBody(responseBody))

        expect:
        sut.get(JsonStructure, mockWebServer.url(path).toString(), path, [:], null).orNull() ==
            new JsonStructure(1, 'str', true, null,
                new JsonStructure.Inner('value'),
                ['foo', 'bar', 'baz'])

        where:
        path << ['', 'foo', 'foo/bar/baz']
    }

    @SuppressWarnings(['ChangeToOperator', 'GroovyPointlessBoolean', 'GroovyAssignabilityCheck'])
    def 'get can map response body to JsonNode class'() {
        given:
        final def responseBody = '''
            {
                "intValue"     : 1,
                "stringValue"  : "str",
                "booleanValue" : true,
                "nullValue"    : null,
                "objectValue"  : {
                    "key": "value"
                },
                "listValue"    : ["foo", "bar", "baz"]
            }
        '''

        mockWebServer.enqueue(new MockResponse().setBody(responseBody))

        when:
        final def jsonNode = sut.get(JsonNode, mockWebServer.url('').toString(), '', [:], null).orNull()

        then:
        jsonNode['intValue'].asInt() == 1
        jsonNode['stringValue'].asText() == 'str'
        jsonNode['booleanValue'].asBoolean() == true
        jsonNode['nullValue'].isNull()
        jsonNode['objectValue']['key'].asText() == 'value'
        jsonNode['listValue'].asList()[0].asText() == 'foo'
        jsonNode['listValue'].asList()[1].asText() == 'bar'
        jsonNode['listValue'].asList()[2].asText() == 'baz'
    }

    def 'get ignores JSON properties defined the given class'() {
        given:
        final def responseBody = '''
            {
                "intValue"     : 1,
                "stringValue"  : "str",
                "booleanValue" : true,
                "nullValue"    : null,
                "objectValue"  : {
                    "key": "value"
                },
                "listValue"    : ["foo", "bar", "baz"],
                "unknown"      : false
            }
        '''

        mockWebServer.enqueue(new MockResponse().setBody(responseBody))

        expect:
        sut.get(JsonStructure, mockWebServer.url('').toString(), '', [:], null).orNull() ==
            new JsonStructure(1, 'str', true, null,
                new JsonStructure.Inner('value'),
                ['foo', 'bar', 'baz'])
    }

    def 'get returns failure when auth is needed'() {
        given:
        mockWebServer.dispatcher = { final RecordedRequest recordedRequest ->
            if (recordedRequest.headers['Authorization'] == null) {
                return new MockResponse().setResponseCode(401).setBody('{}')
            } else {
                return new MockResponse().setBody('{}')
            }
        }

        expect:
        sut.get(JsonNode, mockWebServer.url('').toString(), '', [:], auth).isRight() == isSuccessful

        where:
        auth                                            || isSuccessful
        null                                            || false
        new BitbucketServerAuthentication('foo', 'bar') || true
    }

    def 'get returns failure reason as Either#Left'() {
        given:
        final def responseBody = '{}'

        mockWebServer.enqueue(new MockResponse().setBody(responseBody))

        when:
        final def either = sut.get(JsonStructure, mockWebServer.url('').toString(), '', [:], null)

        then:
        either.isLeft()
        (either as Either.Left<NonEmptyList<String>>).value[0].split('\n')[0] ==
            'com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException: Instantiation of [simple type, class io.jenkins.plugins.lgtm.dataclass.JsonStructure] value failed for JSON property stringValue due to missing (therefore NULL) value for creator parameter stringValue which is a non-nullable type'
    }

    def 'post does POST request'() {
        mockWebServer.enqueue(new MockResponse().setBody('{}'))
        def requestBody = new JsonStructure(1, 'str', true, null,
            new JsonStructure.Inner('value'),
            ['foo', 'bar', 'baz'])

        when:
        final def either = sut.post(requestBody, JsonNode, mockWebServer.url('').toString(), '', [:], null)

        then:
        either.isRight()
        mockWebServer.requestCount == 1
        new JsonSlurper().parseText(mockWebServer.takeRequest().body.readUtf8()) == [
            intValue    : 1,
            stringValue : 'str',
            booleanValue: true,
            nullValue   : null,
            objectValue : [
                key: 'value'
            ],
            listValue   : ['foo', 'bar', 'baz']
        ]
    }

    def 'delete does DELETE request'() {
        mockWebServer.enqueue(new MockResponse().setBody('{}'))

        when:
        final def either = sut.delete(mockWebServer.url('').toString(), '', [:], null)

        then:
        either.isRight()
        mockWebServer.requestCount == 1
        mockWebServer.takeRequest().method == 'DELETE'
    }
}
