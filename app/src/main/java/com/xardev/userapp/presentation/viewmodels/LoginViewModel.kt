package com.xardev.userapp.presentation.viewmodels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.data.repos.LoginRepository
import com.xardev.userapp.core.utils.DataStoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


class LoginViewModel @Inject constructor(
    val loginRepo: LoginRepository,
    var dsManager: DataStoreManager
) : ViewModel() {

    private var _isLoading = MutableStateFlow(false)
    var isLoading: StateFlow<Boolean> = _isLoading

    private var _result: MutableStateFlow<Result<*>> = MutableStateFlow(Result.Success(null))
    var result: StateFlow<Result<*>> = _result

    private var _isRememberEnabled = MutableStateFlow(false)
    var isRememberEnabled: StateFlow<Boolean> = _isRememberEnabled

    init {
        getRememberEnabled()
    }

    fun getUserByEmail(email: String) {

        if (email.isNotEmpty()) {
            val p = Patterns.EMAIL_ADDRESS

            if (p.matcher(email).matches()) {
                Result
                viewModelScope.launch {
                    _isLoading.value = true
                    delay(1500)

                    loginRepo.getUserByEmail(email)
                        .onStart { _isLoading.value = true }
                        .collect {
                            _isLoading.value = false
                            if (it != null) _result.value = Result.Success(it)
                            else _result.value =
                                Result.Failure<Throwable>(Throwable(message = "User Not Found!"))
                        }
                }

            } else {
                _result.value =
                    Result.Failure<Throwable>(Throwable(message = "Please enter a valid email address."))

            }

        } else {
            _result.value =
                Result.Failure<Throwable>(Throwable(message = "Please enter your email address."))

        }

    }

    fun getRememberEnabled() {
        viewModelScope.launch {
            dsManager.isRememberEnabled().collect {
                _isRememberEnabled.value = it!!
            }
        }

    }

    fun setRememberEnabled(b: Boolean) {
        viewModelScope.launch {
            dsManager.setRememberEnabled(b)
        }

    }


}