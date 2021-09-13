package com.example.toy

import android.app.Application
import com.example.database.DatabaseModule
import com.example.database.DatabaseSource
import com.example.network.ApiSource
import com.example.toy.di.AppComponent
import com.example.toy.di.DaggerAppComponent

class ToyApplication: Application(), ApiSource, DatabaseSource {

    private lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory().create(
            DatabaseModule(this)
        )
    }

    override fun apiWorker() = appComponent.apiWorker()

    override fun dao() = appComponent.dao()
}