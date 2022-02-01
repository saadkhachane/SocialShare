package com.xardev.userapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.xardev.userapp.data.User
import com.xardev.userapp.data.local.DataStoreManager
import com.xardev.userapp.repos.RegisterRepositoryImpl
import com.xardev.userapp.utils.Result
import com.xardev.userapp.utils.Result.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.File
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "RegisterResultFragment"

class RegisterResultFragmentViewModel @Inject constructor(
    var repo: RegisterRepositoryImpl,
    var dsManager: DataStoreManager
): ViewModel() {

    private var _isLoading : MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isLoading = _isLoading.asStateFlow()

    private var _result: MutableStateFlow<Result<*>> = MutableStateFlow(Success(null))
    var result: StateFlow<Result<*>> = _result

    fun addUserToServer(user: User) {

        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                _isLoading.value = true

                val jsonUser = Gson().toJson(user).toString()
                val requestBody = jsonUser.toRequestBody(
                    "application/json; charset=utf-8".toMediaTypeOrNull())


                repo.addUserToServer(requestBody)
                    .enqueue(object : Callback<ResponseBody>{

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {

                            if ( response.isSuccessful && response.body() != null){
                                //Log.d(TAG, "onResponse: " + response.body()?.string())

                                kotlin.runCatching {
                                    val jsonRes = JSONObject(response.body()!!.string())
                                    val success = jsonRes.getString("success")
                                    val error = jsonRes.getString("error")
                                    val userId = jsonRes.getString("user_id")

                                    if ( success.equals("true",true)){
                                        _result.value = Success(jsonRes)

                                        viewModelScope.launch {

                                            if (!userId.isNullOrEmpty()){
                                                dsManager.setUserId(userId)
                                                dsManager.setSessionActive(true)
                                                dsManager.setFirstLaunch(false)

                                                Log.d(TAG, "onResponse: $jsonRes")
                                            }
                                        }

                                    }else {
                                        _result.value = Result.Failure<Throwable>(Throwable(error))
                                    }
                                }.onFailure {
                                    Log.d(TAG, "onFailure : ${it.message}")
                                }

                            }else {
                                _result.value = Result.Failure<Throwable>(Throwable("Unknown error."))
                            }

                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.d(TAG, "onFailure: ${t.message}")

                                _result.value = Result.Failure<Throwable>(Throwable(t))

                        }
                    })

                _isLoading.value = false

            }.onFailure {
                _isLoading.value = false
                _result.value = Result.Failure<Throwable>(it)
            }
        }
    }


}