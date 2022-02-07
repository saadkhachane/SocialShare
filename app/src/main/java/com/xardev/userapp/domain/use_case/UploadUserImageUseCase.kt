package com.xardev.userapp.domain.use_case

import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.core.utils.Result.Failure
import com.xardev.userapp.core.utils.Result.Success
import com.xardev.userapp.domain.repos.RegisterRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.awaitResponse
import java.io.File

class UploadUserImageUseCase(
     val repo: RegisterRepositoryImpl
) {

    suspend operator fun invoke(file: File) : Flow<Result<*>> {
        return flow {

            emit(Result.Loading<Boolean>(true))

            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val fileBody = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = repo.uploadUserImage(fileBody).awaitResponse()

            if (response.isSuccessful) {

                response.body()?.let { body ->
                    emit(Success(body.string()))
                }

            }else {

                response.errorBody()?.let {  body ->
                    emit(Failure<Throwable>(Throwable(body.string())))
                }
            }

            emit(Result.Loading<Boolean>(false))

        }


    }
}