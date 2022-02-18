package com.xardev.userapp.data.repos

import com.google.gson.Gson
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.data.local.UserDao
import com.xardev.userapp.data.remote.ApiService
import com.xardev.userapp.core.Constants
import com.xardev.userapp.core.utils.DataStoreManager
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.core.utils.Result.*
import com.xardev.userapp.domain.repos.RegisterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.awaitResponse
import java.io.File
import java.io.IOException
import javax.inject.Inject

class RegisterRepositoryImpl @Inject constructor(
    val userDao: UserDao,
    val api: ApiService,
    val dsManager: DataStoreManager
) : RegisterRepository {

    override suspend fun addUser(user: User): Flow<Result<*>> {
        return flow {

            kotlin.runCatching {

                emit(Loading<Boolean>(true))

                val jsonUser = Gson().toJson(user).toString()
                val requestBody = jsonUser.toRequestBody(
                    "application/json; charset=utf-8".toMediaTypeOrNull())

                val response = api.addUser(Constants.API_KEY, requestBody).awaitResponse()

                if ( response.isSuccessful ){

                    response.body()?.let { body ->

                        val jsonRes = JSONObject(body.string())
                        val success = jsonRes.getString("success")
                        val error = jsonRes.getString("error")
                        val userId = jsonRes.getString("user_id")

                        if ( success.equals("true",true) ){

                            user.id = userId

                            userDao.addUser( user.toUserEntity() )

                            dsManager.setUserId(userId)
                            dsManager.setSessionActive(true)
                            dsManager.setFirstLaunch(false)

                            emit(Loading<Boolean>(false))
                            emit(Success(user))

                        }else {
                            emit(Loading<Boolean>(false))
                            emit(Failure<Throwable>(Throwable(error)))
                        }

                    }

                }else {
                    emit(Loading<Boolean>(false))
                    emit(Failure<Throwable>(Throwable("Unknown error.")))
                }


            }.onFailure {
                emit(Loading<Boolean>(false))
                emit(Failure<Throwable>(it))
            }

        }
    }

    override suspend fun uploadImage(file : File): Flow<Result<*>> {
        return flow {

            try {
            emit(Loading<Boolean>(true))

            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val fileBody = MultipartBody.Part.createFormData("file", file.name, requestFile)


                val response = api.uploadImage(Constants.API_KEY, fileBody).awaitResponse()

                if (response.isSuccessful) {

                    response.body()?.let { body ->
                        emit(Success(body.string()))
                    }

                }else {

                    response.errorBody()?.let {  body ->
                        emit(Failure<Throwable>(Throwable(body.string())))
                    }
                }
            }catch (e: IOException) {
                emit(Failure<Throwable>(Throwable("Please check you internet connection and try again.")))
            }
            catch (e: Exception) {
                emit(Failure<Throwable>(e))
            }


            emit(Loading<Boolean>(false))

        }
    }
}

fun Int.toBoolean() : Boolean{
    return this == 1
}