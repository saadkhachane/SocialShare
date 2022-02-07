package com.xardev.userapp.core.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class DataStoreManager(val context: Context) {

    companion object{
        private val Context.datastore by preferencesDataStore("Global")
        val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch")
        val REMEMBER_KEY = booleanPreferencesKey("remember")
        val EMAIL_KEY = stringPreferencesKey("email")
        val USERID_KEY = stringPreferencesKey("id")
        val SESSION_KEY = booleanPreferencesKey("session")
    }

    // Session Info
    fun isSessionActive() : Flow<Boolean?>{
        return context.datastore.data
            .map {
               it[SESSION_KEY]
            }.flowOn(Dispatchers.IO)
    }

    suspend fun setSessionActive(b: Boolean){
        context.datastore.edit {
            it[SESSION_KEY] = b
        }
    }

    // Remember Login Info
    fun isRememberEnabled() : Flow<Boolean?>{
         return context.datastore.data
             .map {
                 it[REMEMBER_KEY]
             }
    }

    suspend fun setRememberEnabled(b: Boolean){
        context.datastore.edit {
            it[REMEMBER_KEY] = b
        }
    }

    // Email Info
    fun getEmail() : Flow<String?>{
        return context.datastore.data
            .map {
                it[EMAIL_KEY]
            }
    }

    suspend fun setEmail(email: String){
        context.datastore.edit {
            it[EMAIL_KEY] = email
        }
    }


    // User Id
    fun getUserId() : Flow<String?>{
        return context.datastore.data
            .map {
                it[USERID_KEY]
            }
    }

    suspend fun setUserId(id: String){
        context.datastore.edit {
            it[USERID_KEY] = id
        }
    }
    // First Launch Info
    fun isFirstLaunch() : Flow<Boolean?> {
        return context.datastore
            .data.map {
                it[FIRST_LAUNCH_KEY]
            }
    }

    suspend fun setFirstLaunch(b: Boolean){
        context.datastore
            .edit {
                it[FIRST_LAUNCH_KEY] = b
            }
    }

}