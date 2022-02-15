package com.xardev.userapp.domain.use_case

import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.data.repos.MainRepositoryImpl
import kotlinx.coroutines.flow.Flow

class GetUserUseCase(
    val repo: MainRepositoryImpl
) {

    suspend operator fun invoke(id: String) : Flow<Result<*>> {

        return repo.getUser(id)

    }

}