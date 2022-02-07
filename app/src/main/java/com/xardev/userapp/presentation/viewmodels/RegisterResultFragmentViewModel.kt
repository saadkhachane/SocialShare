package com.xardev.userapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.core.utils.Result.*
import com.xardev.userapp.core.utils.isLoading
import com.xardev.userapp.domain.use_case.AddUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.*
import javax.inject.Inject

private const val TAG = "RegisterResultFragment"


class RegisterResultFragmentViewModel @Inject constructor(
    val addUserUseCase: AddUserUseCase
): ViewModel() {

    private var _isLoading : MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isLoading = _isLoading.asStateFlow()

    private var _result: MutableStateFlow<Result<*>> = MutableStateFlow(Success(null))
    var result: StateFlow<Result<*>> = _result

    fun addUser(user: User) {

        viewModelScope.launch(Dispatchers.IO) {

            addUserUseCase(user)
                .collect { result ->

                    when(result) {

                        is Loading -> _isLoading.value = result.isLoading
                        is Success -> _result.value = result
                        is Failure -> _result.value = result

                    }
                }
        }
    }


}