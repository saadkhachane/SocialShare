package com.xardev.userapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.xardev.userapp.databinding.ActivityMainBinding
import com.xardev.userapp.databinding.ActivityWelcomeBinding
import com.xardev.userapp.utils.DataStoreManager
import com.xardev.userapp.viewmodels.MainViewModel
import com.xardev.userapp.viewmodels.WelcomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

private const val TAG = "here"

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    private lateinit var binder : ActivityMainBinding

    val dsManager: DataStoreManager = DataStoreManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binder.lifecycleOwner = this

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

        // Session Check
        checkSession()
    }

    override fun onStop() {
        lifecycleScope.launchWhenCreated {
            dsManager.setSessionActive(false)
        }
        super.onStop()
    }

    private fun checkSession() {
        lifecycleScope.launchWhenResumed {
            dsManager.isFirstLaunch().collect {
                if (it == null || it == true){
                    Log.d(TAG, "state 1: ${lifecycle.currentState}")
                    if(lifecycle.currentState == Lifecycle.State.RESUMED)
                        startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
                }else {

                    dsManager.isSessionActive().collect { b ->
                        if (b != null || b == false){
                            Log.d(TAG, "state 2: ${lifecycle.currentState}")
                            if(lifecycle.currentState == Lifecycle.State.RESUMED)
                                startActivity( Intent(this@MainActivity, LoginActivity::class.java) )
                        }
                    }

                }
            }
        }


    }
}