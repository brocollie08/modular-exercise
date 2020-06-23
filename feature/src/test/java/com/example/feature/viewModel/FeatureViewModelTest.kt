package com.example.feature.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.example.database.dto.EntryType
import com.example.database.dto.MySealedClass
import com.example.feature.repo.FeatureRepo
import com.example.database.dto.MySealedClass.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class FeatureViewModelTest {

    val userName = "user"

    val defaultList = listOf(
        Header("Assets"),
        Header("Cash and Investments"),
        EntryDto("Chequing", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Savings for Taxes", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Rainy Day Fund", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Savings for Fun", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Savings for Travel", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Savings for Personal Development", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Investment 1", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Investment 2", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Investment 3", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Investment 4", 0f, EntryType.CASH_INVESTMENT, userName),
        Adder("Add Cash or Investments", EntryType.CASH_INVESTMENT),
        Header("Long Term Assets"),
        EntryDto("Primary Home", 0f, EntryType.LONG_TERM_ASSET, userName),
        EntryDto("Secondary Home", 0f, EntryType.LONG_TERM_ASSET, userName),
        Adder("Add Long Term Asset", EntryType.LONG_TERM_ASSET),
        Footer("Total Assets", 0f),
        Header("Liabilities"),
        Header("Short Term Liabilities"),
        EntryDto("Credit Card 1", 0f, EntryType.SHORT_TERM_LIABILITY, userName),
        EntryDto("Credit Card 2", 0f, EntryType.SHORT_TERM_LIABILITY, userName),
        Adder("Add Short Term Liabilities", EntryType.SHORT_TERM_LIABILITY),
        Header("Long Term Debt"),
        EntryDto("Mortgage 1", 0f, EntryType.LONG_TERM_DEBT, userName),
        EntryDto("Mortgage 2", 0f, EntryType.LONG_TERM_DEBT, userName),
        EntryDto("Line of Credit", 0f, EntryType.LONG_TERM_DEBT, userName),
        EntryDto("Investment Loan", 0f, EntryType.LONG_TERM_DEBT, userName),
        EntryDto("Student Loan", 0f, EntryType.LONG_TERM_DEBT, userName),
        EntryDto("Car Loan", 0f, EntryType.LONG_TERM_DEBT, userName),
        Adder("Add Long Term Debt", EntryType.LONG_TERM_DEBT),
        Footer("Total Liabilities", 0f)
    )

    val defaultListNoHeaders = listOf(
        EntryDto("Chequing", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Savings for Taxes", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Rainy Day Fund", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Savings for Fun", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Savings for Travel", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Savings for Personal Development", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Investment 1", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Investment 2", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Investment 3", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Investment 4", 0f, EntryType.CASH_INVESTMENT, userName),
        EntryDto("Primary Home", 0f, EntryType.LONG_TERM_ASSET, userName),
        EntryDto("Secondary Home", 0f, EntryType.LONG_TERM_ASSET, userName),
        EntryDto("Credit Card 1", 0f, EntryType.SHORT_TERM_LIABILITY, userName),
        EntryDto("Credit Card 2", 0f, EntryType.SHORT_TERM_LIABILITY, userName),
        EntryDto("Mortgage 1", 0f, EntryType.LONG_TERM_DEBT, userName),
        EntryDto("Mortgage 2", 0f, EntryType.LONG_TERM_DEBT, userName),
        EntryDto("Line of Credit", 0f, EntryType.LONG_TERM_DEBT, userName),
        EntryDto("Investment Loan", 0f, EntryType.LONG_TERM_DEBT, userName),
        EntryDto("Student Loan", 0f, EntryType.LONG_TERM_DEBT, userName),
        EntryDto("Car Loan", 0f, EntryType.LONG_TERM_DEBT, userName)
    )

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: FeatureViewModel

    @Mock
    lateinit var featureRepo: FeatureRepo
    @Mock
    private lateinit var mockEntriesObserver: Observer<List<MySealedClass>>
    @Mock
    private lateinit var mockTripleObserver: Observer<Triple<Float, Float, Float>>
    @Mock
    private lateinit var stateHandle: SavedStateHandle

    @Before
    fun setUp() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        viewModel = FeatureViewModel(CoroutineScope(Dispatchers.Main), featureRepo, stateHandle)

        var savedString = ""

        doNothing().`when` (stateHandle).set(ArgumentMatchers.any(), eq(userName)).apply {
            savedString = userName
        }

        `when` (stateHandle.get(ArgumentMatchers.anyString()) as? String).thenReturn(savedString)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `populateTable sets current user`() {
        runBlockingTest {
            `when` (featureRepo.loadData(userName)).thenReturn(
                flow { emit(emptyList<EntryDto>()) }
            )
            viewModel.populateTables(userName)
            assertEquals("user", viewModel.currentUser)
        }
    }

    @Test
    fun `populateTables creates default table if user doesn't have existing data`() {
        val flow = flow { emit(emptyList<EntryDto>()) }
        runBlockingTest {
            `when` (featureRepo.loadData(userName)).thenReturn(
                flow
            )
            viewModel.allEntries.observeForever(mockEntriesObserver)

            viewModel.populateTables(userName)

            verify(mockEntriesObserver).onChanged(defaultList)
            viewModel.allEntries.removeObserver(mockEntriesObserver)
        }
    }

    @Test
    fun `populateTable returns correct table if user has data saved`() {
        val flow = flow { emit(defaultListNoHeaders) }
        runBlockingTest {
            `when` (featureRepo.loadData(userName)).thenReturn(
                flow
            )
            viewModel.allEntries.observeForever(mockEntriesObserver)

            viewModel.populateTables(userName)

            verify(mockEntriesObserver).onChanged(defaultList)
            viewModel.allEntries.removeObserver(mockEntriesObserver)
        }
    }



    @Test
    fun `populateTable does nothing if user is not defined and state not saved`() {
        `when` (stateHandle.get(ArgumentMatchers.anyString()) as? String).thenReturn(null)

        viewModel.allEntries.observeForever(mockEntriesObserver)
        viewModel.populateTables(null)

        verify(mockEntriesObserver, never()).onChanged(ArgumentMatchers.anyList())
        viewModel.allEntries.removeObserver(mockEntriesObserver)
    }

    @Test
    fun `populateTable with state saved works even with null parameter`() {
        runBlockingTest {
            //first time load with name in order to save state
            `when` (featureRepo.loadData(userName)).thenReturn(
                flow { emit(emptyList<EntryDto>()) }
            )
            viewModel.allEntries.observeForever(mockEntriesObserver)

            viewModel.populateTables(userName)

            //second time load with null user
            viewModel.populateTables(null)

            //both featureRepo.loadData and observer should have been triggered twice
            verify(featureRepo, times(2)).loadData(userName)
            verify(mockEntriesObserver, times(2)).onChanged(defaultList)
            viewModel.allEntries.removeObserver(mockEntriesObserver)
        }
    }

    @Test
    fun `viewModel clear saves data to db`() {
        runBlockingTest {
            `when` (featureRepo.loadData(userName)).thenReturn(
                flow { emit(emptyList<EntryDto>()) }
            )
            viewModel.populateTables(userName)

            viewModel.onCleared()
            verify(featureRepo, times(1)).saveData(defaultListNoHeaders)
        }
    }

    @Test
    fun `viewModel calculate only passes entry values to repo`() {
        runBlockingTest {
            `when` (featureRepo.loadData(userName)).thenReturn(
                flow { emit(emptyList<EntryDto>()) }
            )
            `when` (featureRepo.calculate(ArgumentMatchers.anyList())).thenReturn(
                flow { emit(Triple(1f, 1f, 1f)) }
            )
            viewModel.populateTables(userName)

            viewModel.calculate()
            verify(featureRepo, times(1)).calculate(defaultListNoHeaders)
        }
    }

    @Test
    fun `viewModel calculate updates livedata`() {
        runBlockingTest {
            `when` (featureRepo.loadData(userName)).thenReturn(
                flow { emit(emptyList<EntryDto>()) }
            )
            `when` (featureRepo.calculate(ArgumentMatchers.anyList())).thenReturn(
                flow { emit(Triple(1f, 1f, 1f)) }
            )
            viewModel.populateTables(userName)

            viewModel.calculations.observeForever(mockTripleObserver)
            viewModel.calculate()
            verify(featureRepo).calculate(defaultListNoHeaders)
            verify(mockTripleObserver).onChanged(Triple(1f, 1f, 1f))
        }
    }

    @Test
    fun `save single entry triggers repo method for one entry`() {
        runBlockingTest {
            val entry = EntryDto("name", 1f, EntryType.SHORT_TERM_LIABILITY, "me")
            viewModel.saveSingleEntry(entry)
            verify(featureRepo, times(1)).addEntry(entry)
        }
    }

    @Test
    fun `remove single entry triggers repo method for one entry`() {
        runBlockingTest {
            val entry = EntryDto("name", 1f, EntryType.SHORT_TERM_LIABILITY, "me")
            viewModel.removeSingleEntry(entry)
            verify(featureRepo, times(1)).removeEntry(entry)
        }
    }
}