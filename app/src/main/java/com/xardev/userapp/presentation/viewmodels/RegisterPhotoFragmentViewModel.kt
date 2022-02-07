package com.xardev.userapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.core.utils.Result.*
import com.xardev.userapp.core.utils.isLoading
import com.xardev.userapp.domain.use_case.UploadUserImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private const val TAG = "RegisterPhotoFragmentVi"


class RegisterPhotoFragmentViewModel @Inject constructor(
    var uploadUserImage: UploadUserImageUseCase
) : ViewModel() {

    private var _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isLoading = _isLoading.asStateFlow()

    private var _result: MutableSharedFlow<Result<*>> = MutableSharedFlow()
    var result = _result.asSharedFlow()

    fun uploadImage(file: File) {

        viewModelScope.launch {

            uploadUserImage(file)
                .collect { result ->

                    when(result) {

                        is Loading -> { _isLoading.value = result.isLoading }
                        is Success -> { _result.emit(value = result) }
                        is Failure -> { _result.emit(value = result) }

                    }

                }

        }

    }


}