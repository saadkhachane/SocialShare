package com.xardev.userapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.core.utils.Result.*
import com.xardev.userapp.core.utils.isLoading
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.domain.use_case.UpdateUserUseCase
import com.xardev.userapp.domain.use_case.UploadUserImageUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private const val TAG = "RegisterPhotoFragmentVi"


class MainPhotoFragmentViewModel @Inject constructor(
    var uploadUserImage: UploadUserImageUseCase,
    var updateUserUseCase: UpdateUserUseCase
) : ViewModel() {

    private var _result: MutableSharedFlow<Result<*>> = MutableSharedFlow()
    var result = _result.asSharedFlow()

    fun uploadImage(file: File) {

        viewModelScope.launch(Dispatchers.IO) {

            uploadUserImage(file)
                .collect { result ->

                    when(result) {

                        is Loading -> { _result.emit(value = result) }
                        is Success -> { _result.emit(value = result) }
                        is Failure -> { _result.emit(value = result) }

                    }

                }

        }

    }

    fun updateUser(user: User) {

        viewModelScope.launch(Dispatchers.IO) {

            updateUserUseCase(user)
                .collect { result ->

                    when(result) {

                        is Loading -> { _result.emit(value = result) }
                        is Success -> { _result.emit(value = result) }
                        is Failure -> { _result.emit(value = result) }

                    }

                }

        }

    }


}