package com.example.projeto1.retrofit

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface UserProfileEndPoints {
    @FormUrlEncoded
    @POST("post/emptyProfile.php")
    fun checkEmptyUserProfile(@Field("identifier") identifier: Int): Call<UserProfileOutput>

    /*
    @FormUrlEncoded
    @POST("post/postRegister.php")
    fun postRegister(@Field("email") email: String, @Field("password") password: String): Call<UserProfileOutput>
    */
}