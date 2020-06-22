package com.example.feature.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.feature.R
import com.example.feature.di.DaggerFeatureComponent
import com.example.feature.di.FeatureComponent
import com.example.feature.viewModel.FeatureViewModel
import com.example.feature.viewModel.FeatureViewModelFactory
import com.example.network.dependencySource
import kotlinx.android.synthetic.main.feature_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi


@InternalCoroutinesApi
class FeatureFragment: Fragment() {

    lateinit var featureComponent: FeatureComponent
    val viewModel by viewModels<FeatureViewModel> { FeatureViewModelFactory(featureComponent, this) }
    val args by navArgs<FeatureFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.feature_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        featureComponent = DaggerFeatureComponent.factory().create(
            requireActivity().dependencySource(),
            requireActivity().dependencySource(),
            CoroutineScope(Dispatchers.IO)
        )

        feature_list.adapter = FeatureListAdapter(View.OnFocusChangeListener { _, focused ->
            if (!focused) viewModel.calculate()
        })
    }

    override fun onStart() {
        super.onStart()

        observeData()
        populateTables()
    }

    private fun populateTables() {
        viewModel.populateTables(args.name)
    }

    private fun observeData() {
        viewModel.run {
            allEntries.observe(this@FeatureFragment, Observer {
                feature_list.updateList(it)
                viewModel.calculate()
            })
            calculations.observe(this@FeatureFragment, Observer {
                feature_list.updateCalculations(it)
                net_worth.text = it.third.toString()
            })
        }
    }

}