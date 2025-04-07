package com.hommlie.user.base


import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.hommlie.user.R

import java.util.*

abstract class BaseAdaptor<T, binding : ViewBinding>(
    val context: Activity,
    private var items: ArrayList<T>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var binding: binding? = null
    abstract fun onBindData(holder: RecyclerView.ViewHolder?, `val`: T, position: Int)
    abstract fun setItemLayout(): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        binding = getBinding(parent)

        return ViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindData(holder, items[position], position)

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    abstract fun getBinding(parent: ViewGroup): binding

    internal inner class ViewHolder(val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root)


//    private fun setAnimation(view: View, position: Int) {
//        val animation = AnimationUtils.loadAnimation(view.context, R.anim.item_fade_in_scale)
//
//        // Apply a delay between items based on position
//        val delay = position * 100L // 100ms delay between each item (adjustable)
//        animation.startOffset = delay
//
//        view.startAnimation(animation)
//    }
}