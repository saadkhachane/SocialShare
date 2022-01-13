package com.xardev.userapp.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xardev.userapp.R
import com.xardev.userapp.data.User
import com.xardev.userapp.databinding.FragmentRegisterResultBinding
import com.xardev.userapp.utils.DataStoreManager
import com.xardev.userapp.utils.Result
import com.xardev.userapp.utils.exceptionOrNull
import com.xardev.userapp.viewmodels.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "here"

@AndroidEntryPoint
class RegisterResultFragment : Fragment() {

    var user : User? = null

    @Inject
    lateinit var viewModel: RegisterViewModel

    lateinit var dsManager: DataStoreManager

    lateinit var binder: FragmentRegisterResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = arguments?.get("user") as User
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binder = DataBindingUtil.inflate(inflater,R.layout.fragment_register_result, container, false)
        dsManager = DataStoreManager(context)

        if (user != null){
            binder.name.text = "${user!!.name}!"

            setCollectors()
            viewModel.addUser(user!!)

        }else {
            findNavController().navigateUp()
        }


        return binder.root
    }

    private fun setCollectors() {
        lifecycleScope.launchWhenStarted {

            launch {
                viewModel.isLoading
                    .collect{
                        if (it) binder.progressBar.show()
                        else binder.progressBar.hide()
                    }
            }

            launch {
                viewModel.result
                    .collect {
                        if (it is Result.Success<*>){
                            if (it != null){
                                dsManager.setSessionActive(true)
                                dsManager.setFirstLaunch(false)
                                user?.email?.let { it1 -> dsManager.setEmail(it1) }

                                Log.d(TAG, "Success: ${it.value} ")
                                binder.resultAnim.setAnimation(R.raw.success)
                                binder.resultAnim.playAnimation()
                                setLoading()
                            }
                        }else if (it is Result.Failure<*>){
                            Log.d(TAG, "Failure: ${it.error.message} ")

                            binder.congratulation.text = "Oups.."
                            binder.title.text = "Something happened."
                            binder.subtitle.text = it.error.message

                            binder.resultAnim.setAnimation(R.raw.failure)
                            binder.resultAnim.playAnimation()
                            setLoading()
                        }

                    }
            }


        }

    }

    private fun setLoading() {

        lifecycleScope.launchWhenCreated {

            animatePopView(binder.resultAnim)
            delay(1000)
            animateTrophy(binder.resultAnim)
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