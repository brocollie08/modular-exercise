package com.tlin.secondfeature.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.network.dependencySource
import com.tlin.secondfeature.viewModel.FeatureTwoViewModel
import com.tlin.secondfeature.R
import com.tlin.secondfeature.databinding.ResultFragmentBinding
import com.tlin.secondfeature.di.DaggerSecondFeatureComponent
import com.tlin.secondfeature.di.SecondFeatureComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class ResultFragment: Fragment() {
    private lateinit var _binding: ResultFragmentBinding
    private val binding get() = _binding
    private lateinit var featureComponent: SecondFeatureComponent
    val viewModel: FeatureTwoViewModel by navGraphViewModels(R.id.feature_two_nav_graph) { featureComponent.factory }
    private val args by navArgs<ResultFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ResultFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        featureComponent = DaggerSecondFeatureComponent.factory().create(
            requireActivity().dependencySource(),
            CoroutineScope(Dispatchers.IO),
            this
        )
    }

    override fun onStart() {
        super.onStart()
        observeData()
        viewModel.calculateRuns(args.colorNum, args.perColor, args.numPick)
    }

    private fun observeData() {
        viewModel.run {
            result.observe(this@ResultFragment) {
                println(Thread.currentThread())
                println(it)
            }
        }
    }
}