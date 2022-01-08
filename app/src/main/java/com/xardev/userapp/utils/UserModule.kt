package com.xardev.userapp.utils

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xardev.userapp.data.UserDatabase
import com.xardev.userapp.data.api.QrApiService
import com.xardev.userapp.data.local.UserDao
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
class UserModule {

    @Singleton
    @Provides
    fun provideDao(@ApplicationContext context: Context) : UserDao {

        val db = Room.databaseBuilder(
            context,
            UserDatabase::class.java,
            "user.db"
        ).build()

        return db.userDao()
    }

    @Singleton
    @Provides
    fun provideApi(@ApplicationContext context: Context) : QrApiService {

        val retrofit = Retrofit.Builder()
            .baseUrl("")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(QrApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideDsManager(@ApplicationContext context: Context) : DataStoreManager {
        return DataStoreManager(context)
    }




}