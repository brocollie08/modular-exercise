package com.example.feature.views

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.database.dto.MySealedClass
import com.example.feature.R
import com.example.feature.databinding.EntryEntryLayoutBinding
import com.example.feature.di.DaggerFeatureComponent
import com.example.feature.di.FeatureComponent
import com.example.feature.viewModel.FeatureViewModel
import com.example.network.dependencySource
import kotlinx.android.synthetic.main.feature_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi


@InternalCoroutinesApi
class FeatureFragment: Fragment() {

    lateinit var featureComponent: FeatureComponent
    val viewModel by viewModels<FeatureViewModel> { featureComponent.factory }
    private val args by navArgs<FeatureFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.feature_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = args.name

        featureComponent = DaggerFeatureComponent.factory().create(
            requireActivity().dependencySource(),
            requireActivity().dependencySource(),
            CoroutineScope(Dispatchers.IO),
            this
        )

        feature_list.adapter = FeatureListAdapter(
            { v, focused ->
                if (!focused) {
                    viewModel.calculate()
                    DataBindingUtil.findBinding<EntryEntryLayoutBinding>(v)?.entryData?.run {
                        viewModel.saveSingleEntry(this)
                    }
                }
            },
            object : AddClickListener {
                override fun onClick(item: MySealedClass.Adder) {
                    showAddDialog(item)
                }
            },
            {
                DataBindingUtil.findBinding<EntryEntryLayoutBinding>(it)?.entryData?.run {
                    showRemoveDialog(this)
                }
                true
            }
        )
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

    private fun showAddDialog(item: MySealedClass.Adder) {
        val view = layoutInflater.inflate(R.layout.alert_dialog, null)
        val addDialog =  AlertDialog.Builder(requireContext()).also {
            it.setTitle("Add a new entry")
            it.setView(view)
            it.setPositiveButton("OK") { _, _ ->
                val title = view.findViewById<EditText>(R.id.title_field).text.toString()
                val value = view.findViewById<EditText>(R.id.value_field).text.toString()
                if (title.isNotEmpty() && value.isNotEmpty()) {
                    val entry = MySealedClass.EntryDto(
                        title,
                        value.toFloat(),
                        item.type,
                        viewModel.currentUser?: ""
                    )
                    viewModel.run {
                        addNewEntry(entry)
                        saveSingleEntry(entry)
                    }
                }
            }
        }.create()
        addDialog.show()
    }

    private fun showRemoveDialog(item: MySealedClass.EntryDto) {
        AlertDialog.Builder(requireContext()).also {
            it.setTitle("Remove ${item.entryName}?")
            it.setPositiveButton("Remove") { _, _ ->
                viewModel.removeSingleEntry(item)
            }
            it.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

}

interface AddClickListener {
    fun onClick(item: MySealedClass.Adder)
}