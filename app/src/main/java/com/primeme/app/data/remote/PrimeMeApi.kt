package com.primeme.app.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

data class ApiGoal(
    val id: Int,
    val title: String,
    val progress: Int
)

data class CreateGoalRequest(
    val title: String
)

interface PrimeMeApi {

    @GET("api/goals")
    suspend fun getGoals(): List<ApiGoal>

    @POST("api/goals")
    suspend fun createGoal(
        @Body request: CreateGoalRequest
    ): ApiGoal
}

object PrimeMeApiClient {

    private const val BASE_URL = "https://primeme-api-7314-hjg8dvgcdbcdbtcm.southafricanorth-01.azurewebsites.net/"

    val api: PrimeMeApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PrimeMeApi::class.java)
    }
}
