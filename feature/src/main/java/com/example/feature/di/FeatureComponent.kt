package com.example.feature.di

import com.example.database.DatabaseSource
import com.example.feature.viewModel.FeatureViewModel
import com.example.network.ApiSource
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@Component(
    dependencies = [ApiSource::class, DatabaseSource::class]
)
interface FeatureComponent {
    @Component.Factory
    interface Factory {
        fun create(apiSource: ApiSource, databaseSource: DatabaseSource,
                   @BindsInstance
                   coroutineScope: CoroutineScope) : FeatureComponent
    }

    val coroutineScope: CoroutineScope
    val featureViewModel: FeatureViewModel
}