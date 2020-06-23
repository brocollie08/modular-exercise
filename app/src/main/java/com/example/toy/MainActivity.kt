package com.example.toy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(tool_bar)
    }

    override fun onStart() {
        super.onStart()
        tool_bar.run {
            setSupportActionBar(tool_bar)
            title = getString(R.string.app_name)
        }
    }
}
