package com.xardev.userapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.xardev.userapp.data.User
import com.xardev.userapp.data.local.DataStoreManager
import com.xardev.userapp.repos.MainRepository
import com.xardev.userapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "MainViewModel"

class MainViewModel @Inject constructor(
    val repo: MainRepository,
    val dsManager: DataStoreManager
) : ViewModel() {

    private var _isLoading : MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isLoading : StateFlow<Boolean> = _isLoading

    private var _result: MutableStateFlow<Result<*>> = MutableStateFlow(Result.Success(null))
    var result: StateFlow<Result<*>> = _result

    private var _user: MutableStateFlow<User?> = MutableStateFlow(null)
    var user: StateFlow<User?> = _user

    fun getUser(email: String) {

        viewModelScope.launch(Dispatchers.IO) {
            repo.userDao.getUserByEmail(email)
                .collect {
                    if (it != null){
                        _user.value = it
                    }
                }
        }
    }

    fun getUserFromServer(id: String) {

        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                _isLoading.value = true


                repo.getUserFromServer(id)
                    .enqueue(object : Callback<User> {

                        override fun onResponse(call: Call<User>, response: Response<User>) {

                            if ( response.isSuccessful && response.body() != null){
                                //Log.d(TAG, "onResponse: " + response.body()?.string())

                                kotlin.runCatching {
                                    val user = response.body()

                                    _user.value = user
                                    _result.value = Result.Success(user)

                                }.onFailure {
                                    Log.d(TAG, "onFailure : ${it.message}")
                                }

                            }else {
                                _result.value = Result.Failure<Throwable>(Throwable("Unknown error."))
                            }

                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
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