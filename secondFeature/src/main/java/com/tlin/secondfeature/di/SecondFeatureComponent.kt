package com.tlin.secondfeature.di

import androidx.savedstate.SavedStateRegistryOwner
import com.example.database.DatabaseSource
import com.example.network.ApiSource
import com.tlin.secondfeature.viewModel.SecondFeatureViewModelFactory
import dagger.BindsInstance
import dagger.Component
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@Component(
    dependencies = [ApiSource::class]
)
interface SecondFeatureComponent {
    @Component.Factory
    interface Factory {
        fun create(apiSource: ApiSource,
                   @BindsInstance
                   coroutineScope: CoroutineScope,
                   @BindsInstance
                   registryOwner: SavedStateRegistryOwner
        ) : SecondFeatureComponent
    }

    val factory: SecondFeatureViewModelFactory
}