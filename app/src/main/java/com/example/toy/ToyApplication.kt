package com.example.toy

import android.app.Application
import android.os.Parcel
import androidx.fragment.app.FragmentActivity
import com.example.network.APIWorker
import com.example.network.ApiSource

class ToyApplication: Application(), ApiSource {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .build()
    }

    override fun apiWorker(): APIWorker {
        return appComponent.apiWorker()
    }
}

fun FragmentActivity.app(): ToyApplication = applicationContext as ToyApplication