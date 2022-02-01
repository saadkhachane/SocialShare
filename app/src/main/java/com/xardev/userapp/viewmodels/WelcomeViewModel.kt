package com.xardev.userapp.viewmodels

import androidx.lifecycle.ViewModel
import com.xardev.userapp.repos.WelcomeRepository
import javax.inject.Inject


class WelcomeViewModel @Inject constructor(
    repo: WelcomeRepository
) : ViewModel() {



}