package com.example.toy

import com.example.network.ApiSource
import com.example.network.NetworkModule
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface AppComponent : ApiSource