package com.xardev.userapp.presentation.ui.fragments

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.net.Uri
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
import com.xardev.userapp.presentation.ui.activities.MainActivity
import com.xardev.userapp.R
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.databinding.FragmentRegisterResultBinding
import com.xardev.userapp.core.utils.DataStoreManager
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.presentation.viewmodels.RegisterResultFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "here"

@AndroidEntryPoint
class RegisterResultFragment : Fragment() {

    var user : User? = null
    var img_path : String? = null

    @Inject
    lateinit var viewModel: RegisterResultFragmentViewModel

    @Inject
    lateinit var dsManager: DataStoreManager

    lateinit var binder: FragmentRegisterResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = arguments?.get("user") as User
        img_path = arguments?.get("img_path") as String
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binder = DataBindingUtil.inflate(inflater,R.layout.fragment_register_result, container, false)

        if (user != null){
            binder.name.text = "${user!!.name}!"
            binder.image.setImageURI( Uri.parse(img_path) )

            setCollectors()
            setClickListeners()
            viewModel.addUser(user!!)

        }else {
            findNavController().navigateUp()
        }


        return binder.root
    }

    private fun setClickListeners() {
        binder.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
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
                        if (it is Result.Success){
                            if (it.value != null){
                                binder.resultAnim.setAnimation(R.raw.success)
                                binder.resultAnim.playAnimation()
                                setLoading()
                                delay(5000)
                                startActivity(Intent(activity, MainActivity::class.java))
                                activity?.finish()
                            }
                        }else if (it is Result.Failure){
                            Log.d(TAG, "Failure: ${it.error.message} ")

                            binder.congratulation.text = "Oups.."
                            binder.title.text = "What Happened?"
                            binder.subtitle.text = it.error.message

                            binder.resultAnim.setAnimation(R.raw.failure)
                            binder.resultAnim.playAnimation()

                            binder.btnBack.visibility = View.VISIBLE
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
            animateView(binder.btnBack)
            animateView(binder.btnBackShadow)
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