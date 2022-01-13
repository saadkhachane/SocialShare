package com.xardev.userapp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.xardev.userapp.repos.MainRepository
import com.xardev.userapp.repos.WelcomeRepoMock
import com.xardev.userapp.repos.WelcomeRepository
import com.xardev.userapp.utils.DataStoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class MainViewModel @Inject constructor(
    repo: MainRepository
) : ViewModel() {



}