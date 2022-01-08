package com.xardev.userapp.repos

import com.xardev.userapp.data.local.UserDao
import javax.inject.Inject

class RegisterRepository @Inject constructor(
    var userDao: UserDao
) {



}