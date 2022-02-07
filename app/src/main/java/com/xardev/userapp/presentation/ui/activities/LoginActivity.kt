package com.xardev.userapp.presentation.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.xardev.userapp.R
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.databinding.ActivityLoginBinding
import com.xardev.userapp.core.utils.DataStoreManager
import com.xardev.userapp.core.utils.exceptionOrNull
import com.xardev.userapp.core.utils.getOrNull
import com.xardev.userapp.core.utils.isSuccess
import com.xardev.userapp.presentation.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {


    @Inject
    lateinit var dsManager : DataStoreManager

    @Inject
    lateinit var viewModel : LoginViewModel

    lateinit var binder : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binder.lifecycleOwner = this

        binder.viewModel = viewModel

        setCollectors()

        binder.signinBtn.setOnClickListener {
            viewModel.getUserByEmail(binder.emailTxt.text.toString())
        }

    }

    private fun setCollectors() {
        lifecycleScope.launchWhenStarted {
            viewModel.result.collect { r ->
                if(r.isSuccess){
                    val user = r.getOrNull() as User?

                    if (user != null){
                        dsManager.setSessionActive(true)
                        dsManager.setEmail(user.email)

                        startActivity( Intent(this@LoginActivity, MainActivity::class.java) )
                    }

                }else {
                        val exception = r.exceptionOrNull() as Throwable?

                        if(exception?.message != null)
                        Snackbar.make(binder.root, exception.message.toString(), Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.isLoading.collect { isLoading ->
                binder.loading.visibility = if(isLoading) View.VISIBLE else View.GONE
            }
        }

    }

    override fun onStart() {
        // Session Check
        checkSession()

        super.onStart()
    }

    private fun checkSession() {
        lifecycleScope.launchWhenCreated {
            dsManager.isSessionActive().collect {
                if (it!!){
                    startActivity( Intent(this@LoginActivity, MainActivity::class.java) )
                }
            }
        }
    }
}