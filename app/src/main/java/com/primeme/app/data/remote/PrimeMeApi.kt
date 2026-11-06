package com.primeme.app.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Tasks
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.Path
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
    @DELETE("api/goals/{id}")
    suspend fun deleteGoal(@Path("id") id: Int): retrofit2.Response<Unit>

    @PATCH("api/goals/{id}/complete")
    suspend fun completeGoal(
        @Path("id") id: Int
    ): ApiGoal


    @GET("api/goals")
    suspend fun getGoals(): List<ApiGoal>

    @POST("api/goals")
    suspend fun createGoal(
        @Body request: CreateGoalRequest
    ): ApiGoal
}

object PrimeMeApiClient {

    private const val BASE_URL = "https://primeme-api-7314-hjg8dvgcdbcdbtcm.southafricanorth-01.azurewebsites.net/"

    private val authenticatedClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val user = FirebaseAuth.getInstance().currentUser
                ?: throw java.io.IOException("Sign in required")
            val token = try {
                Tasks.await(user.getIdToken(false)).token
                    ?: throw java.io.IOException("Missing Firebase ID token")
            } catch (exception: Exception) {
                throw java.io.IOException("Unable to obtain Firebase ID token", exception)
            }
            chain.proceed(chain.request().newBuilder()
                .header("Authorization", "Bearer $token").build())
        }.build()

    val api: PrimeMeApi by lazy {
        Retrofit.Builder()
            .client(authenticatedClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PrimeMeApi::class.java)
    }
}
