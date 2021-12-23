package com.example.projeto1.retrofit

import retrofit2.Call
import retrofit2.http.*

interface LoginEndPoints {
    @FormUrlEncoded
    @POST("login/postLogin.php")
    fun postLogin(@Field("email") email: String, @Field("password") password: String): Call<LoginOutputPost>

    @FormUrlEncoded
    @POST("/api/register/post")
    fun postRegister(@Field("username") username: String, @Field("password") password: String): Call<LoginOutputPost>
}