package com.xardev.userapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.xardev.userapp.data.User
import com.xardev.userapp.repos.*
import com.xardev.userapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "RegisterViewModel"

class RegisterViewModel @Inject constructor(
    var repo: RegisterRepositoryImpl
) : ViewModel() {

    private var _isLoading : MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isLoading : StateFlow<Boolean> = _isLoading

    private var _result: MutableStateFlow<Result<*>> = MutableStateFlow(Result.Success(null))
    var result: StateFlow<Result<*>> = _result

    fun addUser(user: User) {

        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                _isLoading.value = true

                if (!repo.userExists(user.email)){
                    repo.addUser(user)

                    _result.value = Result.Success(user)
                }else {
                    _result.value = Result.Failure<Throwable>(
                        Throwable(message = "Another account with the same email was found. try to change your email!")
                    )
                }
                _isLoading.value = false

            }.onFailure {
                _isLoading.value = false
                _result.value = Result.Failure<Throwable>(
                    it
                )

            }
        }
    }


}