package com.xardev.userapp.data.remote

import com.xardev.userapp.data.remote.dto.UserDto
import com.xardev.userapp.domain.model.User
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

    @GET("api/userExists.php")
    fun userExists(
        @Query("secretKey") apiKey: String,
        @Body user: RequestBody
    ) : Call<ResponseBody>

    @GET("api/getUser.php")
    suspend fun getUser(
        @Query("secretKey") apiKey: String,
        @Query("id")  id: String
    ) : UserDto


}