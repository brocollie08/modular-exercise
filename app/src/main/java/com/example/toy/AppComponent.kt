package com.example.toy

import com.example.database.DatabaseModule
import com.example.database.DatabaseSource
import com.example.network.ApiSource
import com.example.network.NetworkModule
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, DatabaseModule::class])
interface AppComponent : ApiSource, DatabaseSource {
    @Component.Factory
    interface Factory {
        fun create(databaseModule: DatabaseModule) : AppComponent
    }
}