package com.hommlie.user.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.hommlie.user.R
import com.hommlie.user.databinding.RowProductsizeBinding

class AttributeAdapter(
    private val context: Context,
    private val attributeNames: List<String?>,
    private val itemClick: (String) -> Unit
) : RecyclerView.Adapter<AttributeAdapter.AttributeViewHolder>() {

    private var selectedPosition: Int = -1

    init {

        if (attributeNames.isNotEmpty()) {
            selectedPosition = 0
        }
    }

    inner class AttributeViewHolder(private val binding: RowProductsizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val attributeName = attributeNames[position]
            binding.tvproductsizeS.text = attributeName

            if (position == selectedPosition) {
                binding.tvproductsizeS.background = ResourcesCompat.getDrawable(context.resources, R.drawable.size_gray_border, null)
                binding.tvproductsizeS.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                binding.tvproductsizeS.background = ResourcesCompat.getDrawable(context.resources, R.drawable.transparent_back_5sdp, null)
                binding.tvproductsizeS.setTextColor(ContextCompat.getColor(context, R.color.color_545454))
            }

            binding.root.setOnClickListener {
                if (selectedPosition != position) {
                    notifyItemChanged(selectedPosition)
                    selectedPosition = position
                    notifyItemChanged(selectedPosition)
                    itemClick(attributeName.toString())
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributeViewHolder {
        val binding = RowProductsizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttributeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttributeViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = attributeNames.size

    fun selectFirstItem() {
        if (attributeNames.isNotEmpty()) {
            val previousPosition = selectedPosition
            selectedPosition = 0
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            // Optionally trigger item click
            itemClick(attributeNames[0].toString())
        }
    }
}
