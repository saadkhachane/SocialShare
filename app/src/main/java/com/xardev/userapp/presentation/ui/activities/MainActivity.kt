package com.xardev.userapp.presentation.ui.activities

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.xardev.userapp.R
import com.xardev.userapp.presentation.adapters.SocialFullRecyclerAdapter
import com.xardev.userapp.databinding.ActivityMainBinding
import com.xardev.userapp.core.utils.DataStoreManager
import com.xardev.userapp.core.utils.Result.*
import com.xardev.userapp.core.utils.isLoading
import com.xardev.userapp.domain.model.SocialProfile
import com.xardev.userapp.presentation.adapters.SocialRecyclerAdapter
import com.xardev.userapp.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "here"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var binder : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        window.enterTransition = Slide(Gravity.BOTTOM)

        super.onCreate(savedInstanceState)
        binder = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binder.lifecycleOwner = this


    }


}