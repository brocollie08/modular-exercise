package com.example.database

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(private val application: Application) {
    @Provides
    @Singleton
    fun providesDao(): MyDao = Room.databaseBuilder(application, Database::class.java, "mydb").build().userDao()
}