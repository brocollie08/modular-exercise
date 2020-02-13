package com.example.feature

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.example.network.dependencySource
import kotlinx.android.synthetic.main.feature_main.*
import java.io.Serializable

class FeatureActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.feature_main)

        val worker = DaggerFeatureComponent.factory().create(
            dependencySource()
        ).apiWorker

        text_view.text = worker.getStuff()
    }
}