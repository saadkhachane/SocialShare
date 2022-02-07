package com.xardev.userapp.domain.repos

import com.xardev.userapp.domain.model.User
import com.xardev.userapp.data.local.UserDao
import com.xardev.userapp.data.remote.ApiService
import com.xardev.userapp.core.Constants
import retrofit2.Call
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    val userDao: UserDao,
    val api: ApiService
) {

    fun getUserFromServer(id: String): Call<User> {
        return api.getUser(Constants.API_KEY, id)
    }


}