package com.example.projeto1.retrofit

import retrofit2.Call
import retrofit2.http.*

interface LoginEndPoints {
    @FormUrlEncoded
    @POST("post/postLogin.php")
    fun postLogin(@Field("email") email: String, @Field("password") password: String): Call<LoginOutputPost>

    @FormUrlEncoded
    @POST("post/postRegister.php")
    fun postRegister(@Field("email") email: String, @Field("password") password: String): Call<LoginOutputPost>
}