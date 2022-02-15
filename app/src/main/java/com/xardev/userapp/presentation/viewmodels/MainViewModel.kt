package com.xardev.userapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.core.utils.isLoading
import com.xardev.userapp.domain.use_case.GetUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainViewModel"


class MainViewModel @Inject constructor(
    val getUserUseCase : GetUserUseCase
) : ViewModel() {

    private var _result: MutableStateFlow<Result<*>> = MutableStateFlow(Result.Success(null))
    var result: StateFlow<Result<*>> = _result

    private var _user: MutableStateFlow<User?> = MutableStateFlow(null)
    var user: StateFlow<User?> = _user

    fun getUser(id: String) {

        viewModelScope.launch(Dispatchers.IO) {

            getUserUseCase(id)
                .collect { result ->

                    when(result) {

                        is Result.Loading -> { _result.value = result}
                        is Result.Success -> {
                            _user.value = result.value as User
                            _result.value = result
                        }
                        is Result.Failure -> { _result.value = result }

                    }

                }

        }
    }

}