package com.example.tmdbclient.di

import com.example.tmdbclient.data.repository.MovieRepository
import com.example.tmdbclient.data.repository.MovieRepositoryImpl
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val repositoryModule = module {

    single<MovieRepository> {
        MovieRepositoryImpl(apiService = get(), dispatcher = Dispatchers.IO)
    }

}