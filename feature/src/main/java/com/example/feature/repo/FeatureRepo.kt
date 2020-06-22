package com.example.feature.repo

import com.example.database.Database
import com.example.database.MyDao
import com.example.database.dto.MySealedClass.*
import com.example.database.dto.EntryType
import com.example.network.APIWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FeatureRepo @Inject constructor(
    private val apiWorker: APIWorker,
    private val userDao: MyDao
) {

    //fun addEntry()

    suspend fun saveData(data: List<EntryDto>?) {
        data?.run {
            userDao.saveUserData(this)
        }
    }

    suspend fun loadData(user: String): Flow<List<EntryDto>> {
        val datalist = userDao.getUserData(user)
        return flow { emit(datalist) }
    }

    suspend fun deleteData(user: String) {
        userDao.deleteUserData(user)
    }

    suspend fun calculate(list: List<EntryDto>): Flow<Triple<Float, Float, Float>> {
        val assets = list.filter { it.type == EntryType.LONG_TERM_ASSET || it.type == EntryType.CASH_INVESTMENT }
            .map { it.value ?: 0f }
        val liabilities = list.filter { it.type == EntryType.SHORT_TERM_LIABILITY || it.type == EntryType.LONG_TERM_DEBT }
            .map { it.value ?: 0f }
        val result = apiWorker.calculate(assets, liabilities)
        return flow { emit(result) }
    }
}