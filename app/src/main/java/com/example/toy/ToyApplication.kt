package com.example.toy

import android.app.Application
import android.os.Parcel
import androidx.fragment.app.FragmentActivity
import com.example.database.Database
import com.example.database.DatabaseModule
import com.example.database.DatabaseSource
import com.example.database.MyDao
import com.example.network.APIWorker
import com.example.network.ApiSource

class ToyApplication: Application(), ApiSource, DatabaseSource {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory().create(
            DatabaseModule(this)
        )
    }

    override fun apiWorker(): APIWorker {
        return appComponent.apiWorker()
    }

    override fun dao(): MyDao {
        return appComponent.dao()
    }
}