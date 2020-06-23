package com.example.feature.viewModel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.feature.di.FeatureComponent
import com.example.feature.repo.FeatureRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Inject

@InternalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class FeatureViewModelFactory @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val featureRepo: FeatureRepo,
    registryOwner: SavedStateRegistryOwner
): AbstractSavedStateViewModelFactory(registryOwner, null) {
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return when (modelClass) {
            FeatureViewModel::class.java -> FeatureViewModel(coroutineScope, featureRepo, handle) as T
            else -> null as T
        }
    }

}