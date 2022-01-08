package com.xardev.userapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.xardev.userapp.utils.DataStoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

class MainActivity : AppCompatActivity() {

    val dsManager : DataStoreManager by lazy {
        DataStoreManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onStart() {
        // Session Check
        checkSession()

        super.onStart()
    }

    override fun onStop() {
        lifecycleScope.launchWhenCreated {
            dsManager.setSessionActive(false)
        }
        super.onStop()
    }

    private fun checkSession() {
        lifecycleScope.launchWhenCreated {
            dsManager.isSessionActive().collect {
                if (!it!!){
                    startActivity( Intent(this@MainActivity, LoginActivity::class.java) )
                }
            }
        }
    }
}