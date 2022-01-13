package com.xardev.userapp.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.xardev.userapp.R
import com.xardev.userapp.data.User
import com.xardev.userapp.databinding.FragmentRegisterInfoBinding
import com.xardev.userapp.databinding.FragmentRegisterMainBinding
import com.xardev.userapp.databinding.FragmentRegisterSuccessBinding
import com.xardev.userapp.utils.DataStoreManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

private const val TAG = "here"

@AndroidEntryPoint
class RegisterSuccessFragment : Fragment() {

    var user : User? = null

//    @Inject
//    lateinit var viewModel: MainViewModel

    val dsManager: DataStoreManager = DataStoreManager(requireContext())

    lateinit var binder: FragmentRegisterSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = arguments?.get("user") as User
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binder = DataBindingUtil.inflate(inflater,R.layout.fragment_register_success, container, false)

        if (user != null){

            binder.name.text = "${user!!.name}!"

        }else {
            findNavController().navigateUp()
        }

        setLoading()


        return binder.root
    }

    private fun setLoading() {
        var progress = 0

        lifecycleScope.launchWhenCreated {
            for(i in 0..99){
                delay(30)
                progress++
                binder.progressBar.progress = progress
                Log.d(TAG, "loading: $progress ")
            }

            Log.d(TAG, "setLoading: start")
            binder.progressBar.hide()

            animatePopView(binder.trophy)
            delay(1000)
            animateTrophy(binder.trophy)
            delay(1000)
            animateView(binder.congratulation)
            animateView(binder.name)
            delay(300)
            animatePopView(binder.image)
            animatePopView(binder.imageLayout)
            delay(300)
            animateView(binder.title)
            animateView(binder.subtitle)
        }

    }

    private fun animateView(view: View) {
        val translateY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 100f, 0f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)

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

    private fun animateTrophy(view: View) {
        val translateY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y,  0f)

        ObjectAnimator.ofPropertyValuesHolder(
            view,
            translateY
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