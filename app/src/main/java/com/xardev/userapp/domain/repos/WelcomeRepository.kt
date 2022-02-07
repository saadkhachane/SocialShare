package com.xardev.userapp.domain.repos

import com.xardev.userapp.data.local.UserDao
import javax.inject.Inject

class WelcomeRepository @Inject constructor(
    var userDao: UserDao
): WelcomeRepoMock{


}

interface WelcomeRepoMock {


}