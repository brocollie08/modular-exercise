package com.example.feature.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.*
import com.example.database.dto.MySealedClass
import com.example.feature.R
import com.example.feature.databinding.*

class FeatureListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {

    init {
        this.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        addItemDecoration(DividerItemDecoration(context, VERTICAL))
    }

    fun updateList(entryList: List<MySealedClass>?) {
        (adapter as? FeatureListAdapter)?.submitList(entryList?.toMutableList())
    }

    fun updateCalculations(assetLiabilityNet: Triple<Float, Float, Float>) {
        (adapter as? FeatureListAdapter)?.updateCalculations(assetLiabilityNet)
    }
}

class FeatureListAdapter(
    private val listener: View.OnFocusChangeListener,
    private val addClickListener: AddClickListener,
    private val longClickListener: View.OnLongClickListener
) : ListAdapter<MySealedClass, FeatureListAdapter.EntryViewHolder>(EntryDiffCallback()) {

    private val HEADER = 0
    private val ENTRY = 1
    private val FOOTER = 2
    private val ADDER = 3

    fun updateCalculations(results: Triple<Float, Float, Float>) {
        Log.d("+++", "${results.first}  ${results.second}  ${results.third}")
        currentList.apply {
            (find { it.classTitle == "Total Assets" } as? MySealedClass.Footer)?.value = results.first
            notifyItemChanged(currentList.indexOfFirst { it.classTitle == "Total Assets" })
            (find { it.classTitle == "Total Liabilities" } as? MySealedClass.Footer)?.value = results.second
            notifyItemChanged(currentList.indexOfFirst { it.classTitle == "Total Liabilities" })
            (find { it.classTitle == "total assets" } as? MySealedClass.Footer)?.value = results.third
        }
    }

    override fun getItemCount() = currentList.size

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is MySealedClass.Header -> HEADER
            is MySealedClass.EntryDto -> ENTRY
            is MySealedClass.Footer -> FOOTER
            is MySealedClass.Adder -> ADDER
        }
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        when (val currentItem = currentList[position]) {
            is MySealedClass.Header -> {holder.bind(currentItem)}
            is MySealedClass.EntryDto -> {holder.bind(currentItem, listener, longClickListener)}
            is MySealedClass.Footer -> {holder.bind(currentItem)}
            is MySealedClass.Adder -> {holder.bind(currentItem, addClickListener)}
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        return when (viewType) {
            HEADER -> {
                EntryViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.entry_header_layout,
                    parent, false)) }
            ENTRY -> {
                EntryViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.entry_entry_layout,
                    parent, false))}
            FOOTER -> {
                EntryViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.entry_footer_layout,
                    parent, false))}
            ADDER -> {
                EntryViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                    R.layout.entry_adder_layout,
                    parent, false))}

            else -> {
                throw IllegalArgumentException("what's the viewType???")
            }
        }
    }

    class EntryViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MySealedClass.Header) {
            (binding as EntryHeaderLayoutBindingImpl).headerData = item
        }

        fun bind(item: MySealedClass.EntryDto, listener: View.OnFocusChangeListener, longClick: View.OnLongClickListener) {
            (binding as EntryEntryLayoutBindingImpl).run {
                entryData = item
                focusListener = listener
                root.setOnLongClickListener(longClick)
            }
        }

        fun bind(item: MySealedClass.Footer) {
            (binding as EntryFooterLayoutBindingImpl).footerData = item
        }

        fun bind(item: MySealedClass.Adder, listener: AddClickListener) {
            (binding as EntryAdderLayoutBindingImpl).run {
                adderData = item
                clickListener = listener
            }
        }
    }

    class EntryDiffCallback : DiffUtil.ItemCallback<MySealedClass>() {
        override fun areItemsTheSame(oldItem: MySealedClass, newItem: MySealedClass): Boolean = oldItem == newItem
        override fun areContentsTheSame(oldItem: MySealedClass, newItem: MySealedClass): Boolean {
            return if (oldItem is MySealedClass.Footer && newItem is MySealedClass.Footer) {
                oldItem.value == newItem.value
            } else if (oldItem is MySealedClass.EntryDto && newItem is MySealedClass.EntryDto) {
                oldItem.value == newItem.value
            } else {
                oldItem.classTitle == newItem.classTitle
            }
        }
    }

}
