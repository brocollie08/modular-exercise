package com.example.feature.viewModel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.example.feature.di.FeatureComponent
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class FeatureViewModelFactory constructor(
    private val featureComponent: FeatureComponent,
    private val registryOwner: SavedStateRegistryOwner,
    private var defaultArgs: Bundle? = null
): AbstractSavedStateViewModelFactory(registryOwner, defaultArgs) {
    /*override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            FeatureViewModel::class.java -> featureComponent.featureViewModel as T
            else -> null as T
        }
    }*/

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return when (modelClass) {
            FeatureViewModel::class.java -> featureComponent.featureViewModel as T
            else -> null as T
        }
    }

}