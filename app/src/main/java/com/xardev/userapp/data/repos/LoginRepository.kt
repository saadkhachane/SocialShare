package com.xardev.userapp.data.repos

import com.xardev.userapp.domain.model.User
import com.xardev.userapp.data.local.UserDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginRepository @Inject constructor(
    val userDao: UserDao
) {

     fun getUserByEmail(email: String) : Flow<User?>{
        return flow{

        }
    }


}