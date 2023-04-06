package com.shahinsha.mvvm.network

import com.shahinsha.mvvm.model.AppData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {

    @GET("v6/latest/{currency}")
    suspend fun getAppData(
        @Path("currency") currency: String
    ): AppData

    companion object {
        private const val BASE_URL = "https://open.er-api.com/"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}

