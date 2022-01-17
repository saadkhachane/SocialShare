package com.xardev.userapp.fragments

import android.net.Uri
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
import com.xardev.userapp.databinding.FragmentRegisterInfoBinding
import com.xardev.userapp.databinding.FragmentRegisterMainBinding
import java.net.URI

class RegisterInfoFragment : Fragment() {

    var user : User? = null

    lateinit var binder: FragmentRegisterInfoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        user = arguments?.get("user") as User
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binder = DataBindingUtil.inflate(inflater,R.layout.fragment_register_info, container, false)

        if (user != null){

            binder.name.text = "${user!!.name}!"
            binder.image.setImageURI( Uri.parse(user!!.img_url) )
            binder.imageView.visibility = View.INVISIBLE

        }else {
            findNavController().navigateUp()
        }


        binder.btnFinish.setOnClickListener {

            user?.phone = binder.inputPhone.toString()
            user?.work = binder.inputWork.toString()
            user?.bio = binder.inputBio.toString()

            val bundle = bundleOf("user" to user)

            findNavController().navigate(
                R.id.registerResultFragment,
                bundle
            )

        }

        binder.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return binder.root
    }
}