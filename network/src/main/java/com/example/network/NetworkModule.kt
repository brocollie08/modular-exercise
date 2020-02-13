package com.example.network

import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NetworkModule {
    @Singleton
    @Provides
    fun providesWorker(): APIWorker = APIWorker()
}
