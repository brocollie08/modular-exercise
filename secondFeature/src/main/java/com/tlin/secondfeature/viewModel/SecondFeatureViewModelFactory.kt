package com.tlin.secondfeature.viewModel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.tlin.secondfeature.repo.SecondFeatureRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class SecondFeatureViewModelFactory @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val secondFeatureRepo: SecondFeatureRepo,
    registryOwner: SavedStateRegistryOwner
): AbstractSavedStateViewModelFactory(registryOwner, null) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return when (modelClass) {
            FeatureTwoViewModel::class.java -> FeatureTwoViewModel(coroutineScope, secondFeatureRepo, handle) as T
            else -> null as T
        }
    }

}