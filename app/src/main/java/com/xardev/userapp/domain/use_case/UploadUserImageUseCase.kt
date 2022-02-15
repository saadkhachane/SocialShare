package com.xardev.userapp.domain.use_case

import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.data.repos.RegisterRepositoryImpl
import kotlinx.coroutines.flow.Flow
import java.io.File

class UploadUserImageUseCase(
     val repo: RegisterRepositoryImpl
) {

    suspend operator fun invoke(file: File) : Flow<Result<*>> {

        return repo.uploadImage(file)

    }
}