package com.xardev.userapp.repos

import com.xardev.userapp.data.User
import com.xardev.userapp.data.local.UserDao
import com.xardev.userapp.data.remote.ApiService
import com.xardev.userapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    var userDao: UserDao,
    var api: ApiService
) : RegisterRepositoryMock{

    override suspend fun addUser(user: User) {
        userDao.addUser(user)
    }

    override suspend fun getUser(id: Long) : Flow<User?> {
       return userDao.getUserById(id)
    }

    override suspend fun userExists(email: String): Boolean {
        return userDao.userExists(email).toBoolean()
    }

    fun addUserToServer(user: RequestBody): Call<ResponseBody> {
        return api.addUser(Constants.API_KEY, user)
    }

    fun userExistsInServer(user: RequestBody): Call<ResponseBody> {
        return api.userExists(Constants.API_KEY, user)
    }

    fun uploadUserImage( file : MultipartBody.Part ): Call<ResponseBody> {
        return api.uploadImage(Constants.API_KEY, file)
    }
}

fun Int.toBoolean() : Boolean{
    return this == 1
}

interface RegisterRepositoryMock {

    suspend fun addUser(user: User)

    suspend fun getUser(id: Long): Flow<User?>

    suspend fun userExists(email: String): Boolean

}