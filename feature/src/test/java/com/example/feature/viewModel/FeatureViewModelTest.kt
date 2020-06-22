package com.example.feature.viewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
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

    val defaultList = listOf(
        Header("Assets"),
        Header("Cash and Investments"),
        EntryDto("Chequing", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Savings for Taxes", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Rainy Day Fund", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Savings for Fun", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Savings for Travel", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Savings for Personal Development", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Investment 1", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Investment 2", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Investment 3", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Investment 4", 0f, EntryType.CASH_INVESTMENT, "user"),
        Header("Long Term Assets"),
        EntryDto("Primary Home", 0f, EntryType.LONG_TERM_ASSET, "user"),
        EntryDto("Secondary Home", 0f, EntryType.LONG_TERM_ASSET, "user"),
        Footer("Total Assets", 0f),
        Header("Liabilities"),
        Header("Short Term Liabilities"),
        EntryDto("Credit Card 1", 0f, EntryType.SHORT_TERM_LIABILITY, "user"),
        EntryDto("Credit Card 2", 0f, EntryType.SHORT_TERM_LIABILITY, "user"),
        Header("Long Term Debt"),
        EntryDto("Mortgage 1", 0f, EntryType.LONG_TERM_DEBT, "user"),
        EntryDto("Mortgage 2", 0f, EntryType.LONG_TERM_DEBT, "user"),
        EntryDto("Line of Credit", 0f, EntryType.LONG_TERM_DEBT, "user"),
        EntryDto("Investment Loan", 0f, EntryType.LONG_TERM_DEBT, "user"),
        EntryDto("Student Loan", 0f, EntryType.LONG_TERM_DEBT, "user"),
        EntryDto("Car Loan", 0f, EntryType.LONG_TERM_DEBT, "user"),
        Footer("Total Liabilities", 0f)
    )

    val defaultListNoHeaders = listOf(
        EntryDto("Chequing", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Savings for Taxes", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Rainy Day Fund", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Savings for Fun", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Savings for Travel", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Savings for Personal Development", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Investment 1", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Investment 2", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Investment 3", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Investment 4", 0f, EntryType.CASH_INVESTMENT, "user"),
        EntryDto("Primary Home", 0f, EntryType.LONG_TERM_ASSET, "user"),
        EntryDto("Secondary Home", 0f, EntryType.LONG_TERM_ASSET, "user"),
        EntryDto("Credit Card 1", 0f, EntryType.SHORT_TERM_LIABILITY, "user"),
        EntryDto("Credit Card 2", 0f, EntryType.SHORT_TERM_LIABILITY, "user"),
        EntryDto("Mortgage 1", 0f, EntryType.LONG_TERM_DEBT, "user"),
        EntryDto("Mortgage 2", 0f, EntryType.LONG_TERM_DEBT, "user"),
        EntryDto("Line of Credit", 0f, EntryType.LONG_TERM_DEBT, "user"),
        EntryDto("Investment Loan", 0f, EntryType.LONG_TERM_DEBT, "user"),
        EntryDto("Student Loan", 0f, EntryType.LONG_TERM_DEBT, "user"),
        EntryDto("Car Loan", 0f, EntryType.LONG_TERM_DEBT, "user")
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

    @Before
    fun setUp() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        viewModel = FeatureViewModel(CoroutineScope(Dispatchers.Main), featureRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setUpDefaultList() {
        viewModel.populateTables("user")
    }

    @Test
    fun `populate table sets current user`() {
        runBlockingTest {
            `when` (featureRepo.loadData("user")).thenReturn(
                flow { emit(emptyList<EntryDto>()) }
            )
            viewModel.populateTables("user")
            assertEquals("user", viewModel.currentUser)
        }
    }

    @Test
    fun `populate tables creates default table if user doesn't have existing data`() {
        val flow = flow { emit(emptyList<EntryDto>()) }
        runBlockingTest {
            `when` (featureRepo.loadData("user")).thenReturn(
                flow
            )
            viewModel.allEntries.observeForever(mockEntriesObserver)

            viewModel.populateTables("user")

            verify(mockEntriesObserver).onChanged(defaultList)
            viewModel.allEntries.removeObserver(mockEntriesObserver)
        }
    }

    @Test
    fun `populate table returns correct table if user has data saved`() {
        val flow = flow { emit(defaultListNoHeaders) }
        runBlockingTest {
            `when` (featureRepo.loadData("user")).thenReturn(
                flow
            )
            viewModel.allEntries.observeForever(mockEntriesObserver)

            viewModel.populateTables("user")

            verify(mockEntriesObserver).onChanged(defaultList)
            viewModel.allEntries.removeObserver(mockEntriesObserver)
        }
    }



    @Test
    fun `populate table does nothing if user is not defined`() {
        viewModel.allEntries.observeForever(mockEntriesObserver)
        viewModel.populateTables(null)

        verify(mockEntriesObserver, never()).onChanged(ArgumentMatchers.anyList())
        viewModel.allEntries.removeObserver(mockEntriesObserver)
    }

    @Test
    fun `viewModel clear first deletes user data`() {
        runBlockingTest {
            `when` (featureRepo.loadData("user")).thenReturn(
                flow { emit(emptyList<EntryDto>()) }
            )
            viewModel.populateTables("user")

            viewModel.onCleared()
            verify(featureRepo, times(1)).deleteData("user")
        }
    }

    @Test
    fun `viewModel clear saves data to db`() {
        runBlockingTest {
            `when` (featureRepo.loadData("user")).thenReturn(
                flow { emit(emptyList<EntryDto>()) }
            )
            viewModel.populateTables("user")

            viewModel.onCleared()
            verify(featureRepo, times(1)).saveData(defaultListNoHeaders)
        }
    }

    @Test
    fun `viewModel calculate only passes entry values to repo`() {
        runBlockingTest {
            `when` (featureRepo.loadData("user")).thenReturn(
                flow { emit(emptyList<EntryDto>()) }
            )
            `when` (featureRepo.calculate(ArgumentMatchers.anyList())).thenReturn(
                flow { emit(Triple(1f, 1f, 1f)) }
            )
            viewModel.populateTables("user")

            viewModel.calculate()
            verify(featureRepo, times(1)).calculate(defaultListNoHeaders)
        }
    }

    @Test
    fun `viewModel calculate updates livedata`() {
        runBlockingTest {
            `when` (featureRepo.loadData("user")).thenReturn(
                flow { emit(emptyList<EntryDto>()) }
            )
            `when` (featureRepo.calculate(ArgumentMatchers.anyList())).thenReturn(
                flow { emit(Triple(1f, 1f, 1f)) }
            )
            viewModel.populateTables("user")

            viewModel.calculations.observeForever(mockTripleObserver)
            viewModel.calculate()
            verify(mockTripleObserver).onChanged(Triple(1f, 1f, 1f))
        }
    }
}