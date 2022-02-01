package com.xardev.userapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xardev.userapp.repos.RegisterRepositoryImpl
import com.xardev.userapp.utils.Result
import com.xardev.userapp.utils.Result.Success
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.File
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "RegisterPhotoFragmentVi"

class RegisterPhotoFragmentViewModel @Inject constructor(
    var repo: RegisterRepositoryImpl
) : ViewModel() {

    private var _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isLoading = _isLoading.asStateFlow()

    private var _result: MutableSharedFlow<Result<*>> = MutableSharedFlow()
    var result = _result.asSharedFlow()

    suspend fun uploadImage(file: File) {

        val requestfile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val fileBody = MultipartBody.Part.createFormData("file", file.name, requestfile)

        _isLoading.value = true

        repo.uploadUserImage(fileBody)
            .enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    _isLoading.value = false

                    if (response.isSuccessful) {

                        val res = response.body()?.string()

                        viewModelScope.launch {
                            _result.emit(Success(res))
                        }


                    }

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d(TAG, "onFailure: ${t.message}")

                    _isLoading.value = false

                    viewModelScope.launch {
                        _result.emit(
                            Result.Failure<Throwable>(
                                t
                            )
                        )
                    }

                }

            })

    }


}