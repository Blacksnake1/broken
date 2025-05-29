package com.nekosoft.brokenglass.network

import okhttp3.ConnectionPool
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitClient {
    companion object {
        private var instance: Retrofit? = null
        fun getInstance(): Retrofit? {
            val okkHttpClient = OkHttpClient.Builder()
                .callTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .connectionPool(ConnectionPool(0, 5, TimeUnit.MINUTES))
                .protocols(listOf(Protocol.HTTP_1_1))
                .build()
            if (instance == null) {
                instance = Retrofit.Builder()
                    .client(okkHttpClient)
                    .baseUrl("https://raw.githubusercontent.com/ConfigNeko/BrokenScreenApi/main/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return instance
        }

    }
}