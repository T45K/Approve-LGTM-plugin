package io.jenkins.plugins.lgtm

import com.google.common.annotations.VisibleForTesting
import io.jenkins.plugins.lgtm.domain.HttpClient
import io.jenkins.plugins.lgtm.domain.git.BitbucketServerPullRequestApi
import io.jenkins.plugins.lgtm.domain.git.BitbucketServerUser
import io.jenkins.plugins.lgtm.domain.picture.LgtmPictureRepository
import io.jenkins.plugins.lgtm.infrastructure.HttpClientImpl
import io.jenkins.plugins.lgtm.infrastructure.LgtmoonPictureRepository
import kotlin.reflect.KClass

class DIContainer {
    companion object {
        private val objects = mutableMapOf<KClass<*>, Any?>().apply {
            val httpClient = HttpClientImpl()
            put(HttpClient::class, httpClient)

            val lgtmPictureRepository = LgtmoonPictureRepository(httpClient)
            put(LgtmPictureRepository::class, lgtmPictureRepository)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @VisibleForTesting
    fun <T> get(clazz: KClass<*>): T = objects[clazz] as? T ?: throw NoSuchElementException()

    fun bitbucketServerPullRequestApiFactory(
        hostName: String,
        organizationName: String,
        repositoryName: String,
    ): BitbucketServerPullRequestApi =
        BitbucketServerPullRequestApi(hostName, organizationName, repositoryName, get(HttpClient::class))

    fun bitbucketServerUserFactory(
        username: String,
        password: String,
        bitbucketServerPullRequestApi: BitbucketServerPullRequestApi,
    ): BitbucketServerUser = BitbucketServerUser(
        username, password,
        get(LgtmPictureRepository::class),
        bitbucketServerPullRequestApi,
    )
}
