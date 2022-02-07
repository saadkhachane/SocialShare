package com.xardev.userapp.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.domain.repos.RegisterRepositoryImpl
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.core.utils.Result.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.*
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "RegisterPhotoFragmentVi"

class RegisterMainFragmentViewModel @Inject constructor(
    var repo: RegisterRepositoryImpl
): ViewModel() {

    private var _isLoading : MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isLoading = _isLoading.asStateFlow()

    private var _result: MutableStateFlow<Result<*>> = MutableStateFlow(Result.Success(null))
    var result: StateFlow<Result<*>> = _result

    fun userExistsInServer(user: User) {

        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                _isLoading.value = true

                val jsonUser = Gson().toJson(user).toString()
                val requestBody = jsonUser.toRequestBody(
                    "application/json; charset=utf-8".toMediaTypeOrNull())


                repo.userExistsInServer(requestBody)
                    .enqueue(object : Callback<ResponseBody>{

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {

                            val res = response.body()?.string()
                            Log.d(TAG, "onResponse: ${res}")

                            if (res.equals("false",true)){
                                _result.value = Success(res)
                            }else {
                                _result.value = Result.Failure<Throwable>(Throwable("A user already found with the same email."))
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