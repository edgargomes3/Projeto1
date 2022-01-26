package com.example.projeto1.retrofit

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ValuesEndPoints {
    @FormUrlEncoded
    @POST("post/postICR.php")
    fun postICR(@Field("identifier") identifier: Int, @Field("value") value: Int): Call<ValuesOutput>

    @FormUrlEncoded
    @POST("post/postISF.php")
    fun postISF(@Field("identifier") identifier: Int, @Field("value") value: Int): Call<ValuesOutput>
}