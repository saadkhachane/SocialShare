package com.xardev.userapp

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.util.LayoutDirection
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.animation.AnticipateOvershootInterpolator
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xardev.userapp.adapters.SocialAdapter
import com.xardev.userapp.databinding.ActivityMainBinding
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
    private val adapter : SocialAdapter by lazy {
        SocialAdapter(this, null)
    }

    val dsManager: DataStoreManager = DataStoreManager(this)


    override fun onCreate(savedInstanceState: Bundle?) {

        window.enterTransition = Slide(Gravity.BOTTOM)

        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binder.lifecycleOwner = this

        setupRecycler()
        animateViews(binder)
    }

    private fun setupRecycler() {
        binder.recyclerView.layoutManager = LinearLayoutManager(this)
        binder.recyclerView.adapter = adapter
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
            //dsManager.setSessionActive(false)
        }
        super.onStop()
    }

    private fun checkSession() {
        lifecycleScope.launchWhenResumed {
            dsManager.isFirstLaunch().collect {
                if (it == null || it == true){
                    //Log.d(TAG, "state 1: ${lifecycle.currentState}")
                    if(lifecycle.currentState == Lifecycle.State.RESUMED)
                        startActivity(Intent(this@MainActivity, RegisterActivity::class.java))
                }else {

                    dsManager.isSessionActive().collect { b ->
                        if (b == null || b == false){
                            //Log.d(TAG, "state 2: ${lifecycle.currentState}")
                            if(lifecycle.currentState == Lifecycle.State.RESUMED)
                                startActivity( Intent(this@MainActivity, LoginActivity::class.java) )
                        }
                    }

                }
            }
        }


    }

    private fun animateViews(binder: ActivityMainBinding) {

        lifecycleScope.launchWhenCreated {

            delay(300)
            animateView(binder.headBack, 320f, 0f, 0f, 1f)
            delay(300)
            animateView(binder.welcomeMsg, 50f, 0f, 0f, 1f)
            delay(100)
            animateView(binder.name, 50f, 0f, 0f, 1f)
            animatePopView(binder.menu)
            animatePopView(binder.imageLayout)
            delay(100)
            animatePopView(binder.image)
            delay(100)
            animateView(binder.qrLayout, 150f, 0f, 0f, 1f)
            animateView(binder.qrShadow, 150f, 0f, 0f, .3f)
            delay(100)
            animateView(binder.listTitle, 150f, 0f, 0f, 1f)
            delay(100)
            animateView(binder.recyclerView, 150f, 0f, 0f, 1f)

        }

    }


    private fun animateView(view: View, translateFrom: Float,  translateTo: Float, alphaFrom: Float, alphaTo: Float) {
        val translateY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, translateFrom, translateTo)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, alphaFrom, alphaTo)

        ObjectAnimator.ofPropertyValuesHolder(
            view,
            translateY,
            alpha
        ).apply {
            interpolator = AnticipateOvershootInterpolator()
            duration = 1000
            start()
        }

    }

    private fun animatePopView(view: View) {
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f, 1f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)

        ObjectAnimator.ofPropertyValuesHolder(
            view,
            scaleX,
            scaleY,
            alpha
        ).apply {
            interpolator = AnticipateOvershootInterpolator()
            duration = 1000
            start()
        }

    }
}