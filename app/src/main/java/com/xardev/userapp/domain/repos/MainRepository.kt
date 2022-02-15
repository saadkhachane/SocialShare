package com.xardev.userapp.domain.repos

import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    suspend fun getUser(id: String) : Flow<Result<*>>

    suspend fun updateUser(user: User)  : Flow<Result<*>>

}
