package com.example.feature.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.example.database.dto.MySealedClass.*
import com.example.database.dto.EntryType
import com.example.database.dto.MySealedClass
import com.example.feature.repo.FeatureRepo
import com.example.network.APIWorker
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@InternalCoroutinesApi
class FeatureViewModel @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val featureRepo: FeatureRepo
) : ViewModel() {

    var currentUser: String? = null
        private set

    val allEntries: LiveData<List<MySealedClass>>
        get() = mutableAllEntries
    private val mutableAllEntries: MutableLiveData<List<MySealedClass>> = MutableLiveData()

    val calculations: LiveData<Triple<Float, Float, Float>>
        get() = mutableCalculations
    private val mutableCalculations: MutableLiveData<Triple<Float, Float, Float>> = MutableLiveData()


    fun populateTables(user: String?) {
        user?.run {
            currentUser = user
            coroutineScope.launch {
                featureRepo.loadData(user).collect {entries ->
                    if (entries.isNotEmpty()) {
                        mutableAllEntries.postValue(listOf(Header("Assets"), Header("Cash and Investments"))
                            .plus(entries.filter { it.type == EntryType.CASH_INVESTMENT })
                            .plus(Header("Long Term Assets"))
                            .plus(entries.filter { it.type == EntryType.LONG_TERM_ASSET })
                            .plus(Footer("Total Assets", 0f))
                            .plus(listOf(Header("Liabilities"), Header("Short Term Liabilities")))
                            .plus(entries.filter { it.type == EntryType.SHORT_TERM_LIABILITY })
                            .plus(Header("Long Term Debt"))
                            .plus(entries.filter { it.type == EntryType.LONG_TERM_DEBT })
                            .plus(Footer("Total Liabilities", 0f)))
                    } else {
                        mutableAllEntries.postValue(createDefaultEntries(user))
                    }
                }
            }
        }
    }

    private fun saveData() {
        coroutineScope.launch {
            currentUser?.run { featureRepo.deleteData(this) }
            featureRepo.saveData(allEntries.value?.filterIsInstance<EntryDto>())
        }
    }

    fun addEntry(entry: EntryDto) {
        //modifyValue()
    }

    fun removeEntry(entry: EntryDto) {
        //modifyValue()
    }

    public override fun onCleared() {
        saveData()
        super.onCleared()
    }

    fun calculate() {
        coroutineScope.launch {
            featureRepo.calculate(allEntries.value?.filterIsInstance<EntryDto>() ?: emptyList())
                .collect {
                    mutableCalculations.postValue(it)
                }
        }
    }

    private fun createDefaultEntries(user: String): List<MySealedClass> {
        return listOf(
            Header("Assets"),
            Header("Cash and Investments"),
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
            Header("Long Term Assets"),
            EntryDto("Primary Home", 0f, EntryType.LONG_TERM_ASSET, user),
            EntryDto("Secondary Home", 0f, EntryType.LONG_TERM_ASSET, user),
            Footer("Total Assets", 0f),
            Header("Liabilities"),
            Header("Short Term Liabilities"),
            EntryDto("Credit Card 1", 0f, EntryType.SHORT_TERM_LIABILITY, user),
            EntryDto("Credit Card 2", 0f, EntryType.SHORT_TERM_LIABILITY, user),
            Header("Long Term Debt"),
            EntryDto("Mortgage 1", 0f, EntryType.LONG_TERM_DEBT, user),
            EntryDto("Mortgage 2", 0f, EntryType.LONG_TERM_DEBT, user),
            EntryDto("Line of Credit", 0f, EntryType.LONG_TERM_DEBT, user),
            EntryDto("Investment Loan", 0f, EntryType.LONG_TERM_DEBT, user),
            EntryDto("Student Loan", 0f, EntryType.LONG_TERM_DEBT, user),
            EntryDto("Car Loan", 0f, EntryType.LONG_TERM_DEBT, user),
            Footer("Total Liabilities", 0f)
        )
    }
}