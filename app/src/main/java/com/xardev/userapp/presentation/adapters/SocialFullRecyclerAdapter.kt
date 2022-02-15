package com.xardev.userapp.presentation.adapters

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xardev.userapp.R
import com.xardev.userapp.databinding.SocialItemBinding
import com.xardev.userapp.databinding.SocialItemFullBinding
import com.xardev.userapp.databinding.SocialItemFullBindingImpl
import com.xardev.userapp.domain.model.SocialProfile

class SocialFullRecyclerAdapter(var context: Context)
    : RecyclerView.Adapter<SocialFullRecyclerAdapter.SocialViewHolder>() {

    private var list : List<SocialProfile> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialViewHolder {
        val binder : SocialItemFullBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.social_item_full, parent, false )
        return SocialViewHolder(binder)
    }

    override fun onBindViewHolder(holder: SocialViewHolder, position: Int) {
        val binder = holder.binder

        if (list.isNotEmpty()){

            var sp = list[position]

            Glide.with(context)
                .asBitmap()
                .load(sp.icon)
                .into(binder.icon)

            binder.title.text = sp.title
            binder.input.setText(sp.profile)

            //animateItem(binder.root, position)

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

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class SocialViewHolder(var binder: SocialItemFullBinding) : RecyclerView.ViewHolder(binder.root){

    }

    fun updateList(list: List<SocialProfile>) {
        this.list = list
        notifyDataSetChanged()
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