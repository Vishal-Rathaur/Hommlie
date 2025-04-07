package com.hommlie.user.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.hommlie.user.R
import com.hommlie.user.databinding.ActProductDetailsBinding
import com.hommlie.user.databinding.RowProductsizeBinding
import com.hommlie.user.model.DynamicVariationData

class BhkAdapter(
    private val productDetailsBinding: ActProductDetailsBinding,
    private val context: Activity,
    private val attributeNames: List<DynamicVariationData>,
    private val itemClick: (Int, String) -> Unit
) : RecyclerView.Adapter<BhkAdapter.RowProductViewHolder>() {

    private var selectedPosition: Int = -1 // To keep track of the selected item

    inner class RowProductViewHolder(private val binding: RowProductsizeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) = with(binding) {
            // Set text to display the attribute name
            tvproductsizeS.text = attributeNames[position].variation

            // Default background and text color
            tvproductsizeS.background = ResourcesCompat.getDrawable(context.resources, R.drawable.transparent_back_5sdp, null)
            tvproductsizeS.setTextColor(ContextCompat.getColor(context, R.color.color_545454))

            // Highlight selected item
            if (position == selectedPosition) {
                tvproductsizeS.background = ResourcesCompat.getDrawable(context.resources, R.drawable.size_gray_border, null)
                tvproductsizeS.setTextColor(ContextCompat.getColor(context, R.color.white))
            }

            // Handle item clicks
            itemView.setOnClickListener {
                if (selectedPosition != position) {
                    // Update the selected position and notify the change
                    notifyItemChanged(selectedPosition) // Reset previous selection
                    selectedPosition = position // Set new selection
                    notifyItemChanged(selectedPosition) // Highlight new selection

                    // Notify the activity/fragment about the selection
                    itemClick(position, "ItemClick")
                }
            }

            // Automatically select the first item with a delay to avoid IllegalStateException
            if (position == 0 && selectedPosition == -1) {
                itemView.post {
                    itemClick(position, "ItemClick")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowProductViewHolder {
        val view = RowProductsizeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: RowProductViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return attributeNames.size
    }

    // Method to update selected position externally
    fun updateSelectedPosition(position: Int) {
        val oldPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectedPosition)
    }
}
