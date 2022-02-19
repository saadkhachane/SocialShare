package com.xardev.userapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.core.utils.isLoading
import com.xardev.userapp.domain.use_case.GetUserUseCase
import com.xardev.userapp.domain.use_case.UpdateUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainViewModel"


class MainViewModel @Inject constructor(
    val getUserUseCase : GetUserUseCase,
    val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private var _result: MutableSharedFlow<Result<*>> = MutableSharedFlow()
    var result: SharedFlow<Result<*>> = _result

    private var _user: MutableSharedFlow<User?> = MutableSharedFlow()
    var user: SharedFlow<User?> = _user

    fun getUser(id: String) {

        viewModelScope.launch(Dispatchers.IO) {

            getUserUseCase(id)
                .collect { result ->

                    when(result) {

                        is Result.Loading -> { _result.emit(result)}
                        is Result.Success -> {
                            _user.emit(result.value as User)
                            _result.emit(result)
                        }
                        is Result.Failure -> { _result.emit(result) }

                    }

                }

        }
    }


    fun updateUser(user: User) {

        viewModelScope.launch(Dispatchers.IO) {

            updateUserUseCase(user)
                .collect { result ->

                    when(result) {

                        is Result.Loading -> { _result.emit(result)}
                        is Result.Success -> {
                            _result.emit(result)
                        }
                        is Result.Failure -> { _result.emit(result) }

                    }

                }

        }
    }

}