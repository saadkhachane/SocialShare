package com.xardev.userapp.domain.use_case

import com.xardev.userapp.core.utils.DataStoreManager
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.core.utils.Result.*
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.data.repos.RegisterRepositoryImpl
import kotlinx.coroutines.flow.Flow

class AddUserUseCase(
    val repo: RegisterRepositoryImpl,
    val dsManager: DataStoreManager
) {

    suspend operator fun invoke(user: User): Flow<Result<*>> {

        return repo.addUser(user)

    }

}