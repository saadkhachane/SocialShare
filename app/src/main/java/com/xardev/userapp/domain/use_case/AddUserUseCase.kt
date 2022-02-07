package com.xardev.userapp.domain.use_case

import com.google.gson.Gson
import com.xardev.userapp.core.utils.DataStoreManager
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.core.utils.Result.*
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.domain.repos.RegisterRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.awaitResponse

class AddUserUseCase(
    val repo: RegisterRepositoryImpl,
    val dsManager: DataStoreManager
) {

    suspend operator fun invoke(user: User): Flow<Result<*>> {

        return flow {

            kotlin.runCatching {

                emit( Loading<Boolean>(true) )

                val jsonUser = Gson().toJson(user).toString()
                val requestBody = jsonUser.toRequestBody(
                    "application/json; charset=utf-8".toMediaTypeOrNull())

                val response = repo.addUserToServer(requestBody).awaitResponse()

                if ( response.isSuccessful ){

                    response.body()?.let { body ->

                        val jsonRes = JSONObject(body.string())
                        val success = jsonRes.getString("success")
                        val error = jsonRes.getString("error")
                        val userId = jsonRes.getString("user_id")

                        if ( success.equals("true",true) ){

                            user.id = userId

                            repo.addUser( user.toUserEntity() )

                            dsManager.setUserId(userId)
                            dsManager.setSessionActive(true)
                            dsManager.setFirstLaunch(false)

                            emit( Loading<Boolean>(false) )
                            emit( Success(user) )

                        }else {
                            emit( Loading<Boolean>(false) )
                            emit( Failure<Throwable>(Throwable(error)) )
                        }

                    }

                }else {
                    emit( Loading<Boolean>(false) )
                    emit( Failure<Throwable>(Throwable("Unknown error.")) )
                }


            }.onFailure {
                emit( Loading<Boolean>(false) )
                emit( Failure<Throwable>(it) )
            }

        }



    }

}