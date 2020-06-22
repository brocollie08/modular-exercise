package com.example.feature.repo

import com.example.database.MyDao
import com.example.database.dto.EntryType
import com.example.database.dto.MySealedClass.EntryDto
import com.example.network.APIWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule


@ExperimentalCoroutinesApi
@FlowPreview
@RunWith(MockitoJUnitRunner::class)
class FeatureRepoTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    lateinit var mApiWorker: APIWorker
    @Mock
    lateinit var mDao: MyDao

    private lateinit var repo: FeatureRepo

    private val list = listOf(
        EntryDto("first", 1f, EntryType.LONG_TERM_ASSET, "me"),
        EntryDto("first", 2f, EntryType.CASH_INVESTMENT, "me"),
        EntryDto("first", 3f, EntryType.SHORT_TERM_LIABILITY, "me"),
        EntryDto("first", 4f, EntryType.LONG_TERM_DEBT, "me")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        repo = FeatureRepo(mApiWorker, mDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saving valid data should trigger dao save method`() {
        runBlockingTest {
            repo.saveData(list)
            verify(mDao, times(1)).saveUserData(list)
        }
    }

    @Test
    fun `calculate calls api with correct values`() {
        runBlockingTest {
            repo.calculate(list)
            verify(mApiWorker).calculate(listOf(1f, 2f), listOf(3f, 4f))
        }

    }

    @Test
    fun `calculate returns correct answer`() {
        val expectedResult = Triple(1f, 2f, 3f)
        var realResult: Triple<Float, Float, Float>? = null
        runBlockingTest {
            `when`(mApiWorker.calculate(
                anyList(),
                anyList())
            ).thenReturn(expectedResult)

            repo.calculate(list).collect {
                realResult = it
            }
        }

        assertEquals(expectedResult, realResult)

    }

    @Test
    fun `loadData gets user data`() {
        runBlockingTest {
            repo.loadData("me")
            verify(mDao, times(1)).getUserData(anyString())
        }
    }

    @Test
    fun `loadData emits user data`() {
        val expectedResult = list
        var realResult: List<EntryDto> = emptyList()
        runBlockingTest {
            `when`(mDao.getUserData(anyString()))
                .thenReturn(list)

            repo.loadData("me").collect {
                realResult = it
            }
        }

        assertEquals(expectedResult, realResult)
    }

    @Test
    fun `deleteData calls delete on correct user`() {
        runBlockingTest {
            repo.deleteData("user")
            verify(mDao, times(1)).deleteUserData("user")
            verify(mDao, never()).deleteUserData("")
        }
    }

    @Test
    fun `saveData does not do anything if data is null`() {
        runBlockingTest {
            repo.saveData(null)
            verify(mDao, never()).saveUserData(ArgumentMatchers.anyList())
        }
    }

    @Test
    fun `saveData does saves correct list of data`() {
        runBlockingTest {
            repo.saveData(list)
            verify(mDao, times(1)).saveUserData(list)
        }
    }
}