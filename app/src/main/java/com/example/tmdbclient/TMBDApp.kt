package com.example.tmdbclient

import android.app.Application
import com.example.tmdbclient.di.networkModule
import com.example.tmdbclient.di.repositoryModule
import com.example.tmdbclient.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.logger.Level

class TMBDApp : Application(), KoinComponent {

    val koinModules: List<Module> = listOf(
        networkModule,
        repositoryModule,
        viewModelModule,
    )

    override fun onCreate() {
        super.onCreate()
        initDI()
    }

    private fun initDI() {
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.ERROR)
            androidContext(this@TMBDApp)
            modules(koinModules)
        }
    }
}