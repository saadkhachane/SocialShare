package com.xardev.userapp.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.xardev.userapp.R
import com.xardev.userapp.RegisterActivity
import com.xardev.userapp.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    lateinit var binder: FragmentWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binder = DataBindingUtil.inflate(inflater,R.layout.fragment_welcome, container, false)

        binder.btnStart.setOnClickListener {
            findNavController().navigate(R.id.registerMainFragment)
        }

        return binder.root
    }
}