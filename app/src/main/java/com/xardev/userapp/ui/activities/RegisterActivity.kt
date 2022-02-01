package com.xardev.userapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.xardev.userapp.R
import com.xardev.userapp.databinding.ActivityRegisterBinding
import com.xardev.userapp.data.local.DataStoreManager
import com.xardev.userapp.viewmodels.WelcomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: WelcomeViewModel

    private lateinit var binder : ActivityRegisterBinding

    val dsManager: DataStoreManager = DataStoreManager(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_register)
        binder.lifecycleOwner = this

        lifecycleScope.launchWhenStarted {
            dsManager.isFirstLaunch().collect {

            }
        }
    }
}