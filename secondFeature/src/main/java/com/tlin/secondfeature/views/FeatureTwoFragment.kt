package com.tlin.secondfeature.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.tlin.secondfeature.databinding.FeatureTwoFragmentBinding
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class FeatureTwoFragment: Fragment() {

    private lateinit var _binding: FeatureTwoFragmentBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FeatureTwoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = NavHostFragment.findNavController(this)

        binding.calculateButton.run {
            setOnClickListener {
                /*if (binding.input.text.toString().isEmpty()) return@setOnClickListener
                val action = FeatureTwoFragmentDirections.actionFeatureTwoFragmentSelf().apply {
                    with(binding.input.text.toString().toInt()) {
                        when {
                            binding.input.hint.contains("per") -> this@apply.perColor = this
                            binding.input.hint.contains("picked") -> this@apply.numPick = this
                            else -> this@apply.colorNum = this
                        }
                    }
                }
                binding.input.text.toString().toInt().let {
                    viewModel.goNext(it)
                    navController.navigate(action)
                }*/
                val numColors = binding.colorsInput.text.toString().toInt()
                val perColor = binding.perColorInput.text.toString().toInt()
                val numPicked = binding.numPickedInput.text.toString().toInt()
                val numRun = binding.numRunInput.text.toString().toInt()
                if (numColors > 0 && perColor > 0 && numPicked > 0 && numRun > 0) {
                    val action = FeatureTwoFragmentDirections.actionFeatureTwoFragmentToResultFragment().apply {
                        this.colorNum = numColors
                        this.perColor = perColor
                        this.numPick = numPicked
                        this.numRun = numRun
                    }
                    navController.navigate(action)
                }
            }
        }
    }
}