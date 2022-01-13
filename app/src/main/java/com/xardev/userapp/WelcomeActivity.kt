package com.xardev.userapp

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.xardev.userapp.databinding.ActivityWelcomeBinding
import com.xardev.userapp.utils.DataStoreManager
import com.xardev.userapp.viewmodels.WelcomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: WelcomeViewModel

    private lateinit var binder : ActivityWelcomeBinding

    val dsManager: DataStoreManager = DataStoreManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        binder.lifecycleOwner = this

        lifecycleScope.launchWhenStarted {
            dsManager.isFirstLaunch().collect {

            }
        }

        binder.btnStart.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    private fun animateView(view: View) {
        val translateY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 300f, 0f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)

        ObjectAnimator.ofPropertyValuesHolder(
            translateY,
            alpha
        ).apply {
            setTarget(view)
            interpolator = AccelerateDecelerateInterpolator()
            duration = 1000
            startDelay = 2050
            start()
        }

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

    }
}