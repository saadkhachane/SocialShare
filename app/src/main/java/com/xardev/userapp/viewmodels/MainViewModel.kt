package com.xardev.userapp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xardev.userapp.data.User
import com.xardev.userapp.repos.MainRepository
import com.xardev.userapp.repos.WelcomeRepoMock
import com.xardev.userapp.repos.WelcomeRepository
import com.xardev.userapp.utils.DataStoreManager
import com.xardev.userapp.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainViewModel @Inject constructor(
    var repo: MainRepository
) : ViewModel() {

    private var _isLoading : MutableStateFlow<Boolean> = MutableStateFlow(false)
    var isLoading : StateFlow<Boolean> = _isLoading

    private var _result: MutableStateFlow<Result<*>> = MutableStateFlow(Result.Success(null))
    var result: StateFlow<Result<*>> = _result

    private var _user: MutableStateFlow<User?> = MutableStateFlow(null)
    var user: StateFlow<User?> = _user

    fun getUser(email: String) {

        viewModelScope.launch(Dispatchers.IO) {
            repo.userDao.getUserByEmail(email)
                .collect {
                    if (it != null){
                        _user.value = it
                    }
                }
        }

    }

}