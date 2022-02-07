package com.xardev.userapp.presentation.ui.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.xardev.userapp.R
import com.xardev.userapp.domain.model.User
import com.xardev.userapp.databinding.FragmentRegisterInfoBinding

class RegisterInfoFragment : Fragment() {

    var user : User? = null
    var img_path : String? = null

    lateinit var binder: FragmentRegisterInfoBinding
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
        binder = DataBindingUtil.inflate(inflater,R.layout.fragment_register_info, container, false)

        if (user != null){

            binder.name.text = "${user!!.name}!"
            binder.image.setImageURI( Uri.parse(img_path) )
            binder.imageView.visibility = View.INVISIBLE

        }else {
            findNavController().navigateUp()
        }


        binder.btnFinish.setOnClickListener {

            user?.phone = binder.inputPhone.text.toString()
            user?.work = binder.inputWork.text.toString()
            user?.bio = binder.inputBio.text.toString()

            val bundle = bundleOf(
                "user" to user,
                "img_path" to img_path
            )

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