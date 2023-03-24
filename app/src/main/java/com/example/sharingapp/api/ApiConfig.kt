package com.example.sharingapp.api

import androidx.viewbinding.BuildConfig

class ApiConfig {

    companion object {
        const val BASE_URL = "https://story-api.dicoding.dev/v1/"

        fun getApiService(): ApiService {
            val loggingInterceptor = if (BuildConfig.DEBUG) {
                okhttp3.logging.HttpLoggingInterceptor().apply {
                    level = okhttp3.logging.HttpLoggingInterceptor.Level.BODY
                }
            } else {
                okhttp3.logging.HttpLoggingInterceptor().apply {
                    level = okhttp3.logging.HttpLoggingInterceptor.Level.NONE
                }
            }

            val client = okhttp3.OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }

}