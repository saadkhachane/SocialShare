package com.xardev.userapp.domain.repos

import com.xardev.userapp.domain.model.User
import com.xardev.userapp.data.local.UserDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginRepository @Inject constructor(
    val userDao: UserDao
) {

     fun getUserByEmail(email: String) : Flow<User?>{
        return userDao.getUserByEmail(email)
    }


}