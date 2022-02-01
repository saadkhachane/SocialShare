package com.xardev.userapp.data.remote

import com.xardev.userapp.data.User
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("api/uploadImage.php")
    fun uploadImage(
        @Query("secretKey") apiKey: String,
        @Part file : MultipartBody.Part
    ) : Call<ResponseBody>

    @POST("api/addUser.php")
    fun addUser(
        @Query("secretKey") apiKey: String,
        @Body user: RequestBody
    ) : Call<ResponseBody>

    @POST("api/updateUser.php")
    fun updateUser(
        @Query("secretKey") apiKey: String,
        @Body user: RequestBody
    ) : Call<ResponseBody>

    @POST("api/userExists.php")
    fun userExists(
        @Query("secretKey") apiKey: String,
        @Body user: RequestBody
    ) : Call<ResponseBody>

    @GET("api/getUser.php")
    fun getUser(
        @Query("secretKey") apiKey: String,
        @Query("id")  id: String
    ) : Call<User>


}