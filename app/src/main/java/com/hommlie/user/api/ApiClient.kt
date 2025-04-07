package com.hommlie.user.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiClient {

    val BASE_URL2 = "https://techmonkshub.com/ecom_1/api/"
    val APP_URL   = "https://techmonkshub.info/hommly/api/"
    val APP_URL2  = "https://apps4business.in/ecom/api/"
    val NODE_URL  = "https://www.hommlie.com/hommlieserver/api/"
    val newNodeUrl = "https://techmonkshub.info/hommlieserver/api/"


    //"https://techmonkshub.com/ecom_1/api/"

    var TIMEOUT: Long = 60 * 1.toLong()
    val getClient: ApiInterface
        get() {
            val okHttpClient: OkHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient.Builder().connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            httpClient.addInterceptor(logging)

            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()
                .baseUrl(NODE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            return retrofit.create(ApiInterface::class.java)
        }

    val getClient2: ApiInterface
        get() {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient.Builder().connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            httpClient.addInterceptor(logging)
            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL2)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            return retrofit.create(ApiInterface::class.java)
        }

}