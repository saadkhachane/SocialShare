package com.xardev.userapp.repos

import android.util.Log
import com.xardev.userapp.data.User
import com.xardev.userapp.data.local.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception
import javax.inject.Inject

class LoginRepository @Inject constructor(
    val userDao: UserDao
) {

     fun getUserByEmail(email: String) : Flow<User?>{
        return userDao.getUserByEmail(email)
    }


}