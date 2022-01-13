package com.xardev.userapp.repos

import com.xardev.userapp.data.User
import com.xardev.userapp.data.local.UserDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterRepository @Inject constructor(
    var userDao: UserDao
) : RegisterRepositoryMock{

    override suspend fun addUser(user: User) {
        userDao.addUser(user)
    }

    override suspend fun getUser(id: Long) : Flow<User?> {
       return userDao.getUserById(id)
    }

    override suspend fun userExists(email: String): Boolean {
        return userDao.userExists(email).toBoolean()
    }
}

fun Int.toBoolean() : Boolean{
    return this == 1
}

interface RegisterRepositoryMock {

    suspend fun addUser(user: User)

    suspend fun getUser(id: Long): Flow<User?>

    suspend fun userExists(email: String): Boolean

}