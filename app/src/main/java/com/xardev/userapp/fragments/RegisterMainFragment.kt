package com.xardev.userapp.fragments

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.xardev.userapp.R
import com.xardev.userapp.data.User
import com.xardev.userapp.databinding.FragmentRegisterMainBinding

class RegisterMainFragment : Fragment() {

    lateinit var binder: FragmentRegisterMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binder = DataBindingUtil.inflate(inflater,R.layout.fragment_register_main, container, false)

        binder.btnNext.setOnClickListener {
            if(binder.inputName.text.toString().isNotBlank()){
                binder.inputLayoutName.error = null
                if (binder.inputEmail.text.toString().isNotBlank()){

                    val name = binder.inputName.text.toString()
                    val email = binder.inputEmail.text.toString()

                    val p = Patterns.EMAIL_ADDRESS

                    if (p.matcher(email).matches()){

                        val user = User(id = null, name =  name, email = email)
                        val bundle = bundleOf("user" to user)

                        findNavController().navigate(
                            R.id.registerPhotoFragment,
                            bundle
                        )


                        binder.inputLayoutEmail.error = null

                    }else binder.inputLayoutEmail.error = "Please enter a valid email address."

                }else binder.inputLayoutEmail.error = "Please enter your email."

            }else
                binder.inputLayoutName.error = "Please enter your name."

        }

        return binder.root
    }
}