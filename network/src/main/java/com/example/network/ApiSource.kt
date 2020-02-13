package com.example.network

import com.example.network.APIWorker

interface ApiSource {
    fun apiWorker(): APIWorker
}