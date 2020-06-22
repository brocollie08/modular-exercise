package com.example.network

import android.os.Parcel
import android.os.Parcelable

class APIWorker {
    suspend fun calculate(assets: List<Float>, liabilities: List<Float>): Triple<Float, Float, Float> {
        val totalAssets = assets.sum()
        val totalLiabilities = liabilities.sum()
        return Triple(totalAssets, totalLiabilities, totalAssets-totalLiabilities)
    }
}