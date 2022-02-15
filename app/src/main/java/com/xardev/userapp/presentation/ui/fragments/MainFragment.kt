package com.xardev.userapp.presentation.ui.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.snackbar.Snackbar
import com.xardev.userapp.R
import com.xardev.userapp.core.utils.DataStoreManager
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.databinding.FragmentRegisterMainBinding
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.core.utils.isLoading
import com.xardev.userapp.databinding.ActivityMainBinding
import com.xardev.userapp.databinding.FragmentMainBinding
import com.xardev.userapp.domain.model.SocialProfile
import com.xardev.userapp.presentation.adapters.SocialRecyclerAdapter
import com.xardev.userapp.presentation.ui.activities.LoginActivity
import com.xardev.userapp.presentation.ui.activities.RegisterActivity
import com.xardev.userapp.presentation.viewmodels.MainViewModel
import com.xardev.userapp.presentation.viewmodels.RegisterMainFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "here"

@AndroidEntryPoint
class MainFragment : Fragment() {

    @Inject
    lateinit var viewModel: MainViewModel

    private lateinit var binder : FragmentMainBinding
    private lateinit var adapter : SocialRecyclerAdapter

    @Inject
    lateinit var dsManager: DataStoreManager

    var user: User? = null

    lateinit var loadingAnimator : ObjectAnimator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binder = DataBindingUtil.inflate(inflater,R.layout.fragment_main, container, false)
        adapter = context?.let { SocialRecyclerAdapter(it) }!!

        setupRecycler()
        setCollectors()
        setSwipeListener()
        setClickListeners()
        setupLoading(binder.loading)

        lifecycleScope.launchWhenStarted {
            loadData()
        }


        return binder.root
    }

    private fun setClickListeners() {
        binder.btnEditPhoto?.setOnClickListener {

            val bundle = bundleOf(
                "user" to user
            )

            val extras = FragmentNavigatorExtras(binder.image to "profile_image")

            user?.let {
                findNavController().navigate(
                    R.id.action_mainFragment_to_mainPhotoFragment,
                    bundle
                )
            }

        }
    }


    private fun setSwipeListener() {
        binder.refreshLayout.setOnRefreshListener {
            loadData()
        }
    }

    private fun loadData(){
        lifecycleScope.launch(Dispatchers.IO) {
            dsManager.getUserId()
                .collect {
                    if (it != null){
                        launch(Dispatchers.IO) {
                            viewModel.getUser(it)
                        }

                    }

                }
        }
    }

    private fun setCollectors() {
        lifecycleScope.launchWhenCreated {

            launch {

                viewModel.user
                    .collect { user ->
                        if (user != null){

                            this@MainFragment.user = user
                            binder.user = user

                            context?.let {
                                Glide.with(it)
                                    .asBitmap()
                                    .load(user.img)
                                    .into(binder.image)
                            }

                            val l = user.socialProfileList.filter { !it.profile.isNullOrEmpty() }
                            adapter.updateList( l as ArrayList<SocialProfile> )

                        }
                    }

            }

            launch {

                viewModel.result
                    .collect { result ->
                        when(result){

                            is Result.Loading -> {
                                if(binder.refreshLayout.isRefreshing){
                                    binder.refreshLayout.isRefreshing = result.isLoading
                                }else {
                                    if (result.isLoading){
                                        binder.loading.visibility = View.VISIBLE
                                        loadingAnimator.start()
                                    }else {
                                        loadingAnimator.cancel()
                                        binder.loading.visibility = View.GONE
                                        animateViews(binder)
                                    }

                                }

                            }
                            is Result.Success -> {
                                Log.d(TAG, "Success: ${result.value}")
                            }
                            is Result.Failure -> {
                                Log.d(TAG, "Failure: ${result.error.message}")
                            }

                        }
                    }

            }

        }

    }

    private fun setupRecycler() {
        val layoutManager = FlexboxLayoutManager(context)
        layoutManager.flexDirection = FlexDirection.ROW
        layoutManager.justifyContent = JustifyContent.CENTER
        binder.recyclerView.layoutManager = layoutManager
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
                        startActivity(Intent(context, RegisterActivity::class.java))
                }else {

                    dsManager.isSessionActive().collect { b ->
                        if (b == null || b == false){
                            //Log.d(TAG, "state 2: ${lifecycle.currentState}")
                            if(lifecycle.currentState == Lifecycle.State.RESUMED)
                                startActivity( Intent(context, LoginActivity::class.java) )
                        }
                    }

                }
            }
        }


    }

    private fun animateViews(binder: FragmentMainBinding) {

        lifecycleScope.launchWhenCreated {

            delay(500)
            animateView(binder.headBack, 320f, 0f, 0f, 1f)
            delay(500)
            animatePopView(binder.menu)
            delay(150)
            animatePopView(binder.imageLayoutShadow)
            animatePopView(binder.imageLayout)
            animateView(binder.imageLayoutShadow, 0f, 0f, 0f, 0.9f)
            delay(270)
            animatePopView(binder.btnEditPhoto)
            delay(270)
            animateView(binder.name, 50f, 0f, 0f, 1f)
            delay(270)
            animateView(binder.recyclerView, 150f, 0f, 0f, 1f)
            delay(270)
            animateView(binder.cardProfileVisibility, 150f, 0f, 0f, 1f)
            delay(270)
            animateView(binder.cardAbout, 150f, 0f, 0f, 1f)
            animateView(binder.cardAboutShadow, 150f, 0f, 0f, 0.2f)
            delay(270)
            animateView(binder.cardJob, 150f, 0f, 0f, 1f)
            animateView(binder.cardJobShadow, 150f, 0f, 0f, 0.2f)
            delay(270)
            animateView(binder.cardPhone, 150f, 0f, 0f, 1f)
            animateView(binder.cardPhoneShadow, 150f, 0f, 0f, 0.2f)
            delay(270)
            animateView(binder.cardLocation, 150f, 0f, 0f, 1f)
            animateView(binder.cardLocationShadow, 150f, 0f, 0f, 0.2f)
            delay(270)
            animateView(binder.cardQr, 150f, 0f, 0f, 1f)
            animateView(binder.cardQrShadow, 150f, 0f, 0f, 0.2f)
            delay(270)
            animateView(binder.cardLinkedinProfile, 150f, 0f, 0f, 1f)

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
            interpolator = OvershootInterpolator()
            duration = 500
            start()
        }

    }

    private fun setupLoading(view: View) {
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0.4f, 1f)

        ValueAnimator.ofPropertyValuesHolder(
            alpha
        )
        loadingAnimator = ObjectAnimator.ofPropertyValuesHolder(
            view,
            alpha
        ).apply {
            duration = 500
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
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