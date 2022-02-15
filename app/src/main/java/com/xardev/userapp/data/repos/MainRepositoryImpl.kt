package com.xardev.userapp.data.repos

import com.google.gson.Gson
import com.xardev.userapp.data.local.UserDao
import com.xardev.userapp.data.remote.ApiService
import com.xardev.userapp.core.Constants
import com.xardev.userapp.core.utils.DataStoreManager
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.core.utils.Result.*
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.domain.repos.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.awaitResponse
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    val userDao: UserDao,
    private val api: ApiService
) : MainRepository {


    override suspend fun getUser(id: String): Flow<Result<*>> {
        return flow {

            emit( Loading(true) )

            var user = userDao.getUserById(id)
                .toUser()
                .apply {
                    socialProfileList = userDao.getSocialProfiles().map { it.toSocialProfile() }
                }

            kotlin.runCatching {

                val userDto = api.getUser(Constants.API_KEY, id)

                userDao.updateUser( userDto.toUser().toUserEntity() )
                userDao.addSocialProfiles( userDto.social_profile.map { it.toSocialProfileEntity() } )

                user = userDao.getUserById(userDto.id)
                    .toUser()
                    .apply {
                        socialProfileList = userDao.getSocialProfiles().map { it.toSocialProfile() }
                    }

            }.onFailure {
                emit( Success(user) )
                emit( Failure<Throwable>(it) )
                emit( Loading<Boolean>(false) )
            }.onSuccess {
                emit( Success(user) )
                emit( Loading<Boolean>(false) )
            }

        }

    }

    override suspend fun updateUser(user: User): Flow<Result<*>> {

        return flow {

            kotlin.runCatching {

                emit(Loading<Boolean>(true))

                val jsonUser = Gson().toJson(user).toString()
                val requestBody = jsonUser.toRequestBody(
                    "application/json; charset=utf-8".toMediaTypeOrNull())

                val response = api.updateUser(Constants.API_KEY, requestBody).awaitResponse()

                if ( response.isSuccessful ){

                    response.body()?.let { body ->

                        val jsonRes = JSONObject(body.string())
                        val success = jsonRes.getString("success")
                        val error = jsonRes.getString("error")
                        val userId = jsonRes.getString("user_id")

                        if ( success.equals("true",true) ){

                            user.id = userId

                            userDao.updateUser( user.toUserEntity() )

                            emit(Loading<Boolean>(false))
                            emit(Success("ok"))

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


}