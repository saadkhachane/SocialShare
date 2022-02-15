package com.xardev.userapp.presentation.ui.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xardev.userapp.R
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.databinding.FragmentRegisterMainBinding
import com.xardev.userapp.core.utils.Result
import com.xardev.userapp.presentation.viewmodels.RegisterMainFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class RegisterMainFragment : Fragment() {

    lateinit var binder: FragmentRegisterMainBinding

    @Inject
    lateinit var viewModel: RegisterMainFragmentViewModel

    var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binder = DataBindingUtil.inflate(inflater,R.layout.fragment_register_main, container, false)


        setClickListeners()

        // For Future versions..
        //setCollectors()

        return binder.root
    }


    private fun setClickListeners() {

        binder.btnNext.setOnClickListener {

            if(binder.inputName.text.toString().isNotBlank()){
                binder.inputLayoutName.error = null

                if (binder.inputEmail.text.toString().isNotBlank()){

                    val name = binder.inputName.text.toString()
                    val email = binder.inputEmail.text.toString()

                    val p = Patterns.EMAIL_ADDRESS

                    if (p.matcher(email).matches()){

                        user = User(id = "null", name =  name, email = email)

                        val bundle = bundleOf("user" to user)

                        findNavController().navigate(
                            R.id.action_registerMainFragment_to_registerPhotoFragment,
                            bundle
                        )

                        binder.inputLayoutEmail.error = null


                    }else binder.inputLayoutEmail.error = "Please enter a valid email address."

                }else binder.inputLayoutEmail.error = "Please enter your email."

            }else
                binder.inputLayoutName.error = "Please enter your name."

        }
    }

    private fun setCollectors() {

        lifecycleScope.launchWhenStarted {
            viewModel.isLoading
                .collect {
                    binder.btnNext.isEnabled = !it
                }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.result
                .collect {
                    if (it is Result.Success){

                        if (user != null){
                            val bundle = bundleOf("user" to user)

                            findNavController().navigate(
                                R.id.registerPhotoFragment,
                                bundle
                            )

                            binder.inputLayoutEmail.error = null
                        }

                    }else if(it is Result.Failure){
                        Snackbar.make(binder.root, it.error.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()

                    }
                }
        }

    }
}