package com.example.feature

import com.example.network.APIWorker
import com.example.network.ApiSource
import dagger.Component

@Component(
    dependencies = [ApiSource::class]
)
interface FeatureComponent {
    @Component.Factory
    interface Factory {
        fun create(apiSource: ApiSource) : FeatureComponent
    }

    val apiWorker: APIWorker
}