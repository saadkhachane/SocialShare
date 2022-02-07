package com.xardev.userapp.presentation.ui.activities

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.xardev.userapp.R
import com.xardev.userapp.presentation.adapters.SocialRecyclerAdapter
import com.xardev.userapp.databinding.ActivityMainBinding
import com.xardev.userapp.core.utils.DataStoreManager
import com.xardev.userapp.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "here"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    private lateinit var binder : ActivityMainBinding
    private val adapter : SocialRecyclerAdapter by lazy {
        SocialRecyclerAdapter(this, null)
    }

    @Inject
    lateinit var dsManager: DataStoreManager


    override fun onCreate(savedInstanceState: Bundle?) {

        window.enterTransition = Slide(Gravity.BOTTOM)

        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binder.lifecycleOwner = this

        setupRecycler()
        setCollectors()

        animateViews(binder)
    }

    private fun setCollectors() {
        lifecycleScope.launchWhenCreated {

            launch {
                dsManager.getUserId()
                    .collect {
                    if (it != null)
                        viewModel.getUserFromServer(it)
                }
            }

            launch {

                viewModel.user
                    .collect {
                        if (it != null){
                            binder.name.text = it.name

                            Glide.with(this@MainActivity)
                                .asBitmap()
                                .load(it.img)
                                .into(binder.image)

                        }
                    }

            }

        }

    }

    private fun setupRecycler() {
        binder.recyclerView.layoutManager = LinearLayoutManager(this)
        binder.recyclerView.adapter = adapter
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