package com.example.feature.viewModel

import androidx.lifecycle.*
import com.example.database.dto.MySealedClass.*
import com.example.database.dto.EntryType
import com.example.database.dto.MySealedClass
import com.example.feature.repo.FeatureRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@InternalCoroutinesApi
class FeatureViewModel @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val featureRepo: FeatureRepository,
    private val stateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val USER = "USER"
    }

    var currentUser: String? = null
        private set
        get() = stateHandle.get(USER)

    val allEntries: LiveData<MutableList<MySealedClass>>
        get() = mutableAllEntries
    private val mutableAllEntries: MutableLiveData<MutableList<MySealedClass>> = MutableLiveData()

    val calculations: LiveData<Triple<Float, Float, Float>>
        get() = mutableCalculations
    private val mutableCalculations: MutableLiveData<Triple<Float, Float, Float>> = MutableLiveData()


    fun populateTables(user: String?) {
        (user ?: currentUser)?.run {
            stateHandle.set(USER, this)
            viewModelScope.launch(coroutineScope.coroutineContext) {
                featureRepo.loadData(this@run).collect { entries ->
                    if (entries.isNotEmpty()) {
                        mutableAllEntries.postValue(createEntriesFromList(entries))
                    } else {
                        mutableAllEntries.postValue(createDefaultEntries(this@run))
                    }
                }
            }
        }
    }

    fun addNewEntry(entryDto: EntryDto) {
        val entryList = mutableAllEntries.value?.filterIsInstance<EntryDto>() ?: emptyList()
        mutableAllEntries.value = createEntriesFromList(entryList.toMutableList().also { oldList->
            oldList.find { item -> item.entryName == entryDto.entryName }?.let { oldList.remove(it) }
            oldList.add(entryDto)
        }.toList())
    }

    fun saveSingleEntry(entryDto: EntryDto) {
        viewModelScope.launch(coroutineScope.coroutineContext) {
            featureRepo.addEntry(entryDto)
        }
    }

    fun removeSingleEntry(entry: EntryDto) {
        val newlist = mutableAllEntries.value?.also { it.remove(entry) }
        mutableAllEntries.value = newlist
        viewModelScope.launch(coroutineScope.coroutineContext) {
            featureRepo.removeEntry(entry)
        }
    }

    fun calculate() {
        viewModelScope.launch(coroutineScope.coroutineContext) {
            featureRepo.calculate(allEntries.value?.filterIsInstance<EntryDto>() ?: emptyList())
                .collect {
                    mutableCalculations.postValue(it)
                }
        }
    }

    public override fun onCleared() {
        saveData()
        super.onCleared()
    }

    private fun saveData() {
        viewModelScope.launch(coroutineScope.coroutineContext) {
            featureRepo.saveData(allEntries.value?.filterIsInstance<EntryDto>())
        }
    }

    private fun createEntriesFromList(list: List<EntryDto>): MutableList<MySealedClass> {
        return mutableListOf<MySealedClass>(Header("Assets"), Header("Cash and Investments"))
            .apply {
                addAll(list.filter { it.type == EntryType.CASH_INVESTMENT })
                add(Adder("Add Cash or Investments", EntryType.CASH_INVESTMENT))
                add(Header("Long Term Assets"))
                addAll(list.filter { it.type == EntryType.LONG_TERM_ASSET })
                add(Adder("Add Long Term Asset", EntryType.LONG_TERM_ASSET))
                add(Footer("Total Assets", 0f))
                addAll(listOf(Header("Liabilities"), Header("Short Term Liabilities")))
                addAll(list.filter { it.type == EntryType.SHORT_TERM_LIABILITY })
                add(Adder("Add Short Term Liabilities", EntryType.SHORT_TERM_LIABILITY))
                add(Header("Long Term Debt"))
                addAll(list.filter { it.type == EntryType.LONG_TERM_DEBT })
                add(Adder("Add Long Term Debt", EntryType.LONG_TERM_DEBT))
                add(Footer("Total Liabilities", 0f))
            }
    }

    private fun createDefaultEntries(user: String): MutableList<MySealedClass> {
        return createEntriesFromList(listOf(
            EntryDto("Chequing", 0f, EntryType.CASH_INVESTMENT, user),
            EntryDto("Savings for Taxes", 0f, EntryType.CASH_INVESTMENT, user),
            EntryDto("Rainy Day Fund", 0f, EntryType.CASH_INVESTMENT, user),
            EntryDto("Savings for Fun", 0f, EntryType.CASH_INVESTMENT, user),
            EntryDto("Savings for Travel", 0f, EntryType.CASH_INVESTMENT, user),
            EntryDto("Savings for Personal Development", 0f, EntryType.CASH_INVESTMENT, user),
            EntryDto("Investment 1", 0f, EntryType.CASH_INVESTMENT, user),
            EntryDto("Investment 2", 0f, EntryType.CASH_INVESTMENT, user),
            EntryDto("Investment 3", 0f, EntryType.CASH_INVESTMENT, user),
            EntryDto("Investment 4", 0f, EntryType.CASH_INVESTMENT, user),
            EntryDto("Primary Home", 0f, EntryType.LONG_TERM_ASSET, user),
            EntryDto("Secondary Home", 0f, EntryType.LONG_TERM_ASSET, user),
            EntryDto("Credit Card 1", 0f, EntryType.SHORT_TERM_LIABILITY, user),
            EntryDto("Credit Card 2", 0f, EntryType.SHORT_TERM_LIABILITY, user),
            EntryDto("Mortgage 1", 0f, EntryType.LONG_TERM_DEBT, user),
            EntryDto("Mortgage 2", 0f, EntryType.LONG_TERM_DEBT, user),
            EntryDto("Line of Credit", 0f, EntryType.LONG_TERM_DEBT, user),
            EntryDto("Investment Loan", 0f, EntryType.LONG_TERM_DEBT, user),
            EntryDto("Student Loan", 0f, EntryType.LONG_TERM_DEBT, user),
            EntryDto("Car Loan", 0f, EntryType.LONG_TERM_DEBT, user)
        ))
    }
}