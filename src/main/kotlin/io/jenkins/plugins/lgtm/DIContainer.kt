package io.jenkins.plugins.lgtm

import com.google.common.annotations.VisibleForTesting
import io.jenkins.plugins.lgtm.domain.bitbucket.AuthenticatedBitbucketServerUser
import io.jenkins.plugins.lgtm.domain.picture.LgtmPictureRepository
import io.jenkins.plugins.lgtm.infrastructure.BitbucketServerPullRequestApiImpl
import io.jenkins.plugins.lgtm.infrastructure.HttpClient
import io.jenkins.plugins.lgtm.infrastructure.LgtmoonPictureRepository
import kotlin.reflect.KClass

class DIContainer {
    companion object {
        private val objects = mutableMapOf<KClass<*>, Any?>().apply {
            val httpClient = HttpClient()
            put(HttpClient::class, httpClient)

            val lgtmPictureRepository = LgtmoonPictureRepository(httpClient)
            put(LgtmPictureRepository::class, lgtmPictureRepository)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @VisibleForTesting
    fun <T> get(clazz: KClass<*>): T = objects[clazz] as? T ?: throw NoSuchElementException()

    fun bitbucketServerPullRequestApiFactory(
        baseUrl: String,
        organizationName: String,
        repositoryName: String,
    ): BitbucketServerPullRequestApiImpl =
        BitbucketServerPullRequestApiImpl(baseUrl, organizationName, repositoryName, get(HttpClient::class))

    fun bitbucketServerUserFactory(
        username: String,
        password: String,
        bitbucketServerPullRequestApi: BitbucketServerPullRequestApiImpl,
    ): AuthenticatedBitbucketServerUser = AuthenticatedBitbucketServerUser(
        username, password,
        get(LgtmPictureRepository::class),
        bitbucketServerPullRequestApi,
    )
}
