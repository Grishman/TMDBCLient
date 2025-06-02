package com.example.tmdbclient.di

import com.example.tmdbclient.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

private const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
private const val TIMEOUT_SECONDS = 15L

val networkModule = module {

    single<Moshi> {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory()) // For Kotlin reflection (if not using Moshi codegen for all types)
            .build()
    }

    // Provides a singleton OkHttpClient instance
    single<OkHttpClient> {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) { // Only log in debug builds
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain -> // Interceptor to add API key to every request
                val originalRequest = chain.request()
                val originalHttpUrl = originalRequest.url

                val url = originalHttpUrl.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.tmdbApiKey)
                    .build()

                val requestBuilder = originalRequest.newBuilder().url(url)
                chain.proceed(requestBuilder.build())
            }
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    // Provides a singleton Retrofit instance
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl(TMDB_BASE_URL)
            .client(get<OkHttpClient>()) // Koin will inject the OkHttpClient defined above
            .addConverterFactory(MoshiConverterFactory.create(get<Moshi>())) // Koin will inject Moshi
            .build()
    }

    // Provides a singleton TmdbApiService instance
    single<TmdbApiService> {
        get<Retrofit>().create(TmdbApiService::class.java) // Koin will inject Retrofit
    }
}