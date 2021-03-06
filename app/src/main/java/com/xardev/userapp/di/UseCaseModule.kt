package com.xardev.userapp.di

import com.xardev.userapp.core.utils.DataStoreManager
import com.xardev.userapp.data.repos.MainRepositoryImpl
import com.xardev.userapp.data.repos.RegisterRepositoryImpl
import com.xardev.userapp.domain.use_case.AddUserUseCase
import com.xardev.userapp.domain.use_case.GetUserUseCase
import com.xardev.userapp.domain.use_case.UpdateUserUseCase
import com.xardev.userapp.domain.use_case.UploadUserImageUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UseCaseModule {

    @Singleton
    @Provides
    fun provideAddUserUseCase(repo : RegisterRepositoryImpl, dsManager : DataStoreManager)
    : AddUserUseCase{
        return AddUserUseCase(repo, dsManager)
    }

    @Singleton
    @Provides
    fun provideGetUserUseCase(repo : MainRepositoryImpl)
            : GetUserUseCase {
        return GetUserUseCase(repo)
    }

    @Singleton
    @Provides
    fun provideUploadUserUseCase(repo : RegisterRepositoryImpl)
            : UploadUserImageUseCase {
        return UploadUserImageUseCase(repo)
    }

    @Singleton
    @Provides
    fun provideUpdateUserUseCase(repo : MainRepositoryImpl, dsManager : DataStoreManager)
            : UpdateUserUseCase {
        return UpdateUserUseCase(repo, dsManager)
    }

}