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

    @FormUrlEncoded
    @POST("post/getICR.php")
    fun getICR(@Field("identifier") identifier: Int): Call<ICROutput>

    @FormUrlEncoded
    @POST("post/getISF.php")
    fun getISF(@Field("identifier") identifier: Int): Call<ISFOutput>

    @FormUrlEncoded
    @POST("post/getErrorCHO.php")
    fun getErrorCHO(@Field("identifier") identifier: Int, @Field("inf") inf: Int, @Field("sup") sup: Int, @Field("target") target: Int): Call<ErrorCHOOutput>

    @FormUrlEncoded
    @POST("post/postBolus.php")
    fun postBolus(@Field("identifier") identifier: Int, @Field("cho") cho: Int, @Field("glicemia") glicemia: Int, @Field("glicemiatarget") glicemiatarget: Int, @Field("error") error: Float): Call<BolusOutput>

    @FormUrlEncoded
    @POST("post/postGlicemia.php")
    fun postGlicemia(@Field("identifier") identifier: Int, @Field("value") value: Int, @Field("inf") inf: Int, @Field("sup") sup: Int, @Field("target") target: Int): Call<ValuesOutput>

    @FormUrlEncoded
    @POST("post/postTime.php")
    fun postTime(@Field("identifier") identifier: Int, @Field("paCHO") paCHO: Int, @Field("paHour") paHour: String, @Field("aCHO") aCHO: Int, @Field("aHour") aHour: String, @Field("lCHO") lCHO: Int, @Field("lHour") lHour: String, @Field("jCHO") jCHO: Int, @Field("jHour") jHour: String): Call<ValuesOutput>

    @FormUrlEncoded
    @POST("post/getGlicemia.php")
    fun getGlicemia(@Field("identifier") identifier: Int): Call<List<GlicemiaOutput>>
}