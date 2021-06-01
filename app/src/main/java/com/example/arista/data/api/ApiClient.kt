package com.example.arista.data.api

import android.util.Log
import com.example.arista.utils.Constants.Companion.herokuBaseUrl
import com.example.arista.utils.Constants.Companion.localhostBaseUrl
import com.google.gson.GsonBuilder
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private val okHttpClient by lazy { OkHttpClient() }

    private val retrofit: Retrofit by lazy {
        Log.e("AppClient", "Creating Retrofit Client")
        val builder = Retrofit.Builder()
            .baseUrl(herokuBaseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client: OkHttpClient = okHttpClient.newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .dispatcher(dispatcher)
            .build()
        builder.client(client).build()
    }

    fun <T> createService(tClass: Class<T>?): T {
        return retrofit.create(tClass)
    }
}