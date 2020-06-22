package com.example.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import com.example.database.dto.MySealedClass.*
import com.example.database.dto.EntryType
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.*

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var myDao: MyDao
    private lateinit var db: Database

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

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, Database::class.java)
            .build()
        myDao = db.userDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `db should initially be empty`() {
        runBlocking {
            val list = myDao.getUserData("user")
            assertEquals(0, list.size)
        }
    }

    @Test
    fun `get user data only gets data with correct owner`() {
        runBlocking {
            myDao.insert(EntryDto("Credit Card 1", 0f, EntryType.SHORT_TERM_LIABILITY, "user"))
            myDao.insert(EntryDto("Credit Card 2", 0f, EntryType.SHORT_TERM_LIABILITY, "user"))
            myDao.insert(EntryDto("Credit Card 2", 0f, EntryType.SHORT_TERM_LIABILITY, "james"))
            val list = myDao.getUserData("james")
            assertEquals(1, list.size)
            assertEquals("james", list[0].owner)
        }
    }

    @Test
    fun `save data saves every entry`() {
        runBlocking {
            myDao.saveUserData(defaultListNoHeaders)
            val realSize = myDao.getUserData("user").size

            assertEquals(defaultListNoHeaders.size, realSize)
        }
    }

    @Test
    fun `delete user data does not delete other owner's entries`() {
        runBlocking {
            myDao.saveUserData(defaultListNoHeaders)
            myDao.deleteUserData("james")
            val userSize = myDao.getUserData("user").size
            assertEquals(defaultListNoHeaders.size, userSize)
        }
    }

    @Test
    fun `delete user data deletes all data of specified owner`() {
        runBlocking {
            myDao.saveUserData(defaultListNoHeaders)
            myDao.deleteUserData("user")
            val userSize = myDao.getUserData("user").size
            assertEquals(0, userSize)
        }
    }
}