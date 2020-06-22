package com.example.database

import androidx.room.*
import androidx.room.Database
import com.example.database.dto.MySealedClass.*

@Database(
    entities = [EntryDto::class],
    version = 1
)
abstract class Database: RoomDatabase() {
    abstract fun userDao() : MyDao
}

@Dao
interface MyDao {
    @Query("SELECT * FROM entries WHERE owner = :user ORDER BY entryId ASC")
    suspend fun getUserData(user: String) : List<EntryDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entryDto: EntryDto)

    @Transaction
    suspend fun saveUserData(uData: List<EntryDto>) {
        for (e in uData) {
            insert(e)
        }
    }

    @Query("DELETE FROM entries WHERE owner = :user")
    suspend fun deleteUserData(user: String)
}
