package com.xardev.userapp.di

import android.content.Context
import androidx.room.Room
import com.xardev.userapp.data.local.UserDatabase
import com.xardev.userapp.data.remote.ApiService
import com.xardev.userapp.data.local.UserDao
import com.xardev.userapp.data.local.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PersistentModule {

    @Singleton
    @Provides
    fun provideDao(@ApplicationContext context: Context) : UserDao {

        val db = Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "user.db"
        ).fallbackToDestructiveMigration()
            .build()

        return db.userDao()
    }

    @Singleton
    @Provides
    fun provideDsManager(@ApplicationContext context: Context) : DataStoreManager {
        return DataStoreManager(context)
    }




}