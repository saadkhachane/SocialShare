package com.xardev.userapp.presentation.adapters

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xardev.userapp.R
import com.xardev.userapp.databinding.SocialItemBinding
import com.xardev.userapp.domain.model.SocialProfile

class SocialRecyclerAdapter(var context: Context)
    : RecyclerView.Adapter<SocialRecyclerAdapter.SocialViewHolder>() {

    private var list : List<SocialProfile> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocialViewHolder {
        val binder : SocialItemBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.social_item, parent, false )
        return SocialViewHolder(binder)
    }

    override fun onBindViewHolder(holder: SocialViewHolder, position: Int) {
        val binder = holder.binder

        if (list.isNotEmpty() ){

            val sp = list[position]

            if (position < list.size -1 ) {

                Glide.with(context)
                    .asBitmap()
                    .load(sp.icon)
                    .into(binder.icon)

                binder.icon.setOnClickListener {

                    if (!sp.profile.isNullOrEmpty()){
                        if (!sp.profile!!.startsWith("http://") && !sp.profile!!.startsWith("https://"))
                            sp.profile = "http://" + sp.profile

                        context.startActivity(
                            Intent(Intent.ACTION_VIEW,
                                Uri.parse(sp.profile)),
                        )

                    }
                }

            }else {

                binder.icon.setImageDrawable(context.getDrawable(R.drawable.ic_add_social))

                binder.icon.setOnClickListener {



                }

            }


        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    class SocialViewHolder(var binder: SocialItemBinding) : RecyclerView.ViewHolder(binder.root){

    }

    fun updateList(list: ArrayList<SocialProfile>) {
        list.add( SocialProfile("", "", "", null) )
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