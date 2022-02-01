package com.xardev.userapp.repos

import androidx.room.RoomDatabase
import com.xardev.userapp.data.User
import com.xardev.userapp.data.local.UserDao
import com.xardev.userapp.data.remote.ApiService
import com.xardev.userapp.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody
import retrofit2.Call
import javax.inject.Inject

class MainRepository @Inject constructor(
    val userDao: UserDao,
    val api: ApiService
) {

    fun getUserFromServer(id: String): Call<User> {
        return api.getUser(Constants.API_KEY, id)
    }


}