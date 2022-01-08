package com.xardev.userapp.repos

import androidx.room.RoomDatabase
import com.xardev.userapp.data.local.UserDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class MainRepository @Inject constructor(
    var userDao: UserDao
) {

    init {
    }


}