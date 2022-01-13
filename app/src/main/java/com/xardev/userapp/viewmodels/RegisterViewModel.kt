package com.xardev.userapp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xardev.userapp.data.User
import com.xardev.userapp.repos.*
import com.xardev.userapp.utils.DataStoreManager
import com.xardev.userapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


class RegisterViewModel @Inject constructor(
    var repo: RegisterRepository
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