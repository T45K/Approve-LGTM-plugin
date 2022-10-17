package io.jenkins.plugins.lgtm

import io.jenkins.plugins.lgtm.domain.LgtmPictureRepository
import io.jenkins.plugins.lgtm.infrastructure.BitbucketServerAuthentication
import io.jenkins.plugins.lgtm.infrastructure.BitbucketServerPullRequestApiImpl
import io.jenkins.plugins.lgtm.infrastructure.HttpClient
import io.jenkins.plugins.lgtm.usecase.PostLgtmPictureUsecase
import kotlin.reflect.KClass
import kotlin.reflect.full.defaultType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.starProjectedType

class DIContainer {
    private val classInstanceMap = arrayOf<Pair<KClass<*>, Any?>>(
        HttpClient::class to null,
        BitbucketServerAuthentication::class to null,
        LgtmPictureRepository::class to null,
        BitbucketServerPullRequestApiImpl::class to null,
        PostLgtmPictureUsecase::class to null,
    )

    fun init(diElements: List<String>) {
        classInstanceMap.forEachIndexed { index, (clazz, _) ->
            val constructor = clazz.constructors.iterator().next()
            constructor.parameters
                .map { constructorParam ->
                }
        }
    }

}

class Objects {

}
