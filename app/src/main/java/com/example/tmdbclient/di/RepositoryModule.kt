package com.example.tmdbclient.di

import com.example.tmdbclient.data.repository.MovieRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<MovieRepository> {
        MovieRepository(apiService = get())
    }

}