package com.xardev.userapp.domain.repos

import com.xardev.userapp.domain.model.User
import kotlinx.coroutines.flow.Flow
import java.io.File
import com.xardev.userapp.core.utils.Result as Result

interface RegisterRepository {

    suspend fun addUser(user: User) : Flow<Result<*>>

    suspend fun uploadImage(file: File) : Flow<Result<*>>
}
