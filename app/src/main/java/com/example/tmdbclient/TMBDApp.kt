package com.example.tmdbclient

import android.app.Application
import com.example.tmdbclient.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.module.Module

class TMBDApp : Application(), KoinComponent {

    val koinModules: List<Module> = listOf(
        networkModule,
    )

    override fun onCreate() {
        super.onCreate()
        initDI()
    }

    private fun initDI() {
        startKoin {
            androidContext(this@TMBDApp)
            modules(koinModules)
        }
    }
}