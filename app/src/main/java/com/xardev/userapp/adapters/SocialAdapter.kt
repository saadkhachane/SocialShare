package com.xardev.userapp.adapters

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.xardev.userapp.R
import com.xardev.userapp.data.User
import com.xardev.userapp.databinding.SocialItemBinding

class SocialAdapter(var context: Context, var user: User?) : RecyclerView.Adapter<SocialAdapter.SocialViewHolder>() {

    val list = listOf("","","")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialViewHolder {
        val binder : SocialItemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.social_item, parent, false )
        return SocialViewHolder(binder)
    }

    override fun onBindViewHolder(holder: SocialViewHolder, position: Int) {
        val binder = holder.binder

        animateItem(binder.root, position)

        when(position) {
            0 -> binder.root.background = context.getDrawable(R.drawable.social_item_1)
            list.size - 1 -> binder.root.background = context.getDrawable(R.drawable.social_item_3)
            else -> binder.root.background = context.getDrawable(R.drawable.social_item_2)
        }

        binder.add.setOnClickListener {
            binder.inputLayout.visibility = View.VISIBLE
            binder.btnFinish.visibility = View.VISIBLE
        }
        binder.root.setOnClickListener {
            binder.inputLayout.visibility = View.VISIBLE
            binder.btnFinish.visibility = View.VISIBLE
        }
        binder.btnFinish.setOnClickListener {
            binder.inputLayout.visibility = View.GONE
            binder.btnFinish.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class SocialViewHolder(var binder: SocialItemBinding) : RecyclerView.ViewHolder(binder.root){

    }

    private fun animateItem(view: View, pos: Int){

        val translateY = PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, 50f, 0f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)

        ObjectAnimator.ofPropertyValuesHolder(
            view,
            translateY,
            alpha
        ).also {
            it.duration = 500
            it.interpolator = DecelerateInterpolator()
            it.startDelay = (pos * 300).toLong()
            it.start()
        }

    }
}