package com.xardev.userapp.presentation.ui.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.Toast
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
import com.xardev.userapp.R
import com.xardev.userapp.core.utils.DataStoreManager
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.core.utils.isLoading
import com.xardev.userapp.databinding.FragmentMainBinding
import com.xardev.userapp.domain.model.SocialProfile
import com.xardev.userapp.presentation.adapters.SocialRecyclerAdapter
import com.xardev.userapp.presentation.ui.activities.LoginActivity
import com.xardev.userapp.presentation.ui.activities.RegisterActivity
import com.xardev.userapp.presentation.viewmodels.MainViewModel
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

    private var editing = false
    private var justCreated = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binder = DataBindingUtil.inflate(inflater,R.layout.fragment_main, container, false)
        binder.lifecycleOwner = this
        adapter = context?.let { SocialRecyclerAdapter(it) }!!

        setupRecycler()
        setCollectors()
        setSwipeListener()
        setTouchListeners()
        setClickListeners()
        setupLoading(binder.loading)

        loadData()

        justCreated = true

        return binder.root
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListeners() {
        binder.btnLinkedin.setOnTouchListener { v, e ->

            if ( e.action == MotionEvent.ACTION_DOWN ) {
                v.animate()
                    .scaleX(0.97f)
                    .scaleY(0.97f)
                    .start()

                binder.btnLinkedinShadow.animate()
                    .scaleX(0.97f)
                    .scaleY(0.97f)
                    .start()

            } else if( e.action == MotionEvent.ACTION_UP ){
                v.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .start()

                binder.btnLinkedinShadow.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .start()
            }

            v.performClick()
            false
        }

        binder.btnEditProfile.setOnTouchListener { v, e ->

                if ( e.action == MotionEvent.ACTION_DOWN ) {
                    v.animate()
                        .scaleX(0.97f)
                        .scaleY(0.97f)
                        .start()

                    binder.btnEditProfileShadow.animate()
                        .scaleX(0.97f)
                        .scaleY(0.97f)
                        .start()

                } else if( e.action == MotionEvent.ACTION_UP ){
                    v.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .start()

                    binder.btnEditProfileShadow.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .start()
                }


            //v.performClick()
            false
        }
    }

    private fun setClickListeners() {
        binder.btnEditPhoto.setOnClickListener {

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

        binder.btnEditVisibility.setOnClickListener {

        }

        binder.btnEditProfile.setOnClickListener {
            editing = !editing

            if(editing) {
                binder.btnEditProfileText.text = "Save Profile"

            }else {
                binder.btnEditProfileText.text = "Edit Profile"
                user?.let {
                    user!!.bio = binder.inputBio.text.toString()
                    user!!.work = binder.inputJob.text.toString()
                    user!!.phone = binder.inputPhone.text.toString()
                    viewModel.updateUser(user!!)
                }
            }

            showHideViews()
        }

    }

    private fun showHideViews() {
        if(editing){
            hideView(binder.txtBio)
            showView(binder.inputLayoutBio)
            hideView(binder.txtJob)
            showView(binder.inputLayoutJob)
            hideView(binder.txtPhone)
            showView(binder.inputLayoutPhone)
        }else {
            showView(binder.txtBio)
            hideView(binder.inputLayoutBio)
            showView(binder.txtJob)
            hideView(binder.inputLayoutJob)
            showView(binder.txtPhone)
            hideView(binder.inputLayoutPhone)
        }
    }

    private fun hideView(view: View) {
        TransitionManager.beginDelayedTransition(binder.root as ViewGroup?, AutoTransition())
        view.visibility = View.GONE

    }

    private fun showView(view: View) {
        TransitionManager.beginDelayedTransition(binder.root as ViewGroup?, AutoTransition())
        view.visibility = View.VISIBLE

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
                    it?.let{
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

                                        if(justCreated)
                                        animateViews(binder)
                                        justCreated = false

                                    }

                                }

                            }
                            is Result.Success -> {
                                if (result.value == "ok"){
                                    loadData()
                                }
                            }
                            is Result.Failure -> {
                                Toast.makeText(requireContext(), result.error.message, Toast.LENGTH_LONG).show()
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
            animateView(binder.btnEditProfile, 150f, 0f, 0f, 1f)
            animateView(binder.btnEditProfileShadow, 150f, 0f, 0f, 0.2f)
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
        lifecycleScope.launch(Dispatchers.IO) {
            loadingAnimator = ObjectAnimator.ofPropertyValuesHolder(
                view,
                alpha
            ).apply {
                duration = 500
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }
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