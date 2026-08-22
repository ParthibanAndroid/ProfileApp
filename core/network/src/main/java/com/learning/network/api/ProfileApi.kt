package com.learning.network.api

import com.learning.network.model.ProfileResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ProfileApi {
    @GET("api/v1/profile/{id}")
    suspend fun getProfile(
        @Path("id") id: String,
    ): ProfileResponse

//    @POST("api/v1/profile")
//    suspend fun createProfile(@Body request: CreateProfileRequest): ProfileResponse
//
//    @PATCH("api/v1/profile/{id}")
//    suspend fun updateProfile(@Path("id") id: String, @Body request: UpdateProfileRequest): ProfileResponse

    @DELETE("api/v1/profile/{id}")
    suspend fun deleteProfile(
        @Path("id") id: String,
    ): ProfileResponse
}
