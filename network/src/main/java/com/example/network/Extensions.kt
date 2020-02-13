package com.example.network

import android.app.Activity

inline fun <reified T> Activity.dependencySource() = application as T