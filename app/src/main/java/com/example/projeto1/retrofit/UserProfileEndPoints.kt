package com.example.projeto1.retrofit

import com.example.projeto1.dataclass.Educacao
import com.example.projeto1.dataclass.UserProfileData
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface UserProfileEndPoints {
    @FormUrlEncoded
    @POST("post/emptyProfile.php")
    fun checkEmptyUserProfile(@Field("identifier") identifier: Int): Call<UserProfileOutput>

    @FormUrlEncoded
    @POST("post/saveProfile.php")
    fun saveProfile(@Field("identifier") identifier: Int,
                    @Field("name") name: String,
                    @Field("birthday") birthday: String,
                    @Field("height") height: Int,
                    @Field("genre") genre: String,
                    @Field("postalcode") postalcode: String,
                    @Field("country") country: String,
                    @Field("education") education: Int,
                    @Field("diabetes") diabetes: String,
                    @Field("diagyear") diagyear: Int,
                    @Field("caregiver") caregiver: Boolean,
                    ): Call<UserProfileOutput>

    @FormUrlEncoded
    @POST("post/getProfile.php")
    fun getUserProfile(@Field("identifier") identifier: Int): Call<UserProfileData>

    @GET("get/getEducacao.php")
    fun getEducacoes(): Call<List<Educacao>>

    /*
    @FormUrlEncoded
    @POST("post/postRegister.php")
    fun postRegister(@Field("email") email: String, @Field("password") password: String): Call<UserProfileOutput>
    */
}