package com.hommlie.user.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.hommlie.user.R
import com.hommlie.user.databinding.ActProductDetailsBinding
import com.hommlie.user.databinding.RowProductsizeBinding
import com.hommlie.user.model.DynamicVariations
import java.util.*
import kotlin.collections.ArrayList

class VariationAdapter(
    private val productDetailsBinding: ActProductDetailsBinding,
    private val context: Activity,
    private val attributeItems: ArrayList<DynamicVariations>,
    private val itemClick: (Int, String) -> Unit
) : RecyclerView.Adapter<VariationAdapter.RowProductViewHolder>() {

    private var isFirstItemSelected = false

    inner class RowProductViewHolder(private val binding: RowProductsizeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) = with(binding) {
            val item = attributeItems[position]

            productDetailsBinding.apply {
                rvproductSize.visibility = View.VISIBLE
                tvWhoseSize.visibility = View.VISIBLE
                rvbhkSize.visibility = View.VISIBLE
//                bhkDescription.visibility = View.GONE
//                packageDescription.visibility = View.VISIBLE
            }

            tvproductsizeS.text = item.attributeName

//            if (item.isSelected) {
//                tvproductsizeS.background = ResourcesCompat.getDrawable(context.resources, R.drawable.size_gray_border, null)
//                tvproductsizeS.setTextColor(ContextCompat.getColor(context, R.color.white))
//            } else {
//                tvproductsizeS.background = ResourcesCompat.getDrawable(context.resources, R.drawable.transparent_back_5sdp, null)
//                tvproductsizeS.setTextColor(ContextCompat.getColor(context, R.color.color_545454))
//            }
//
//            itemView.setOnClickListener {
//                // Update selection state
//                attributeItems.forEachIndexed { index, variationItem ->
//                    variationItem.isSelected = index == position
//                }
//                notifyDataSetChanged() // Refresh the list to reflect the updated selection state
//                itemClick(position, "ItemClick")
//            }

            // Automatically select the first item with delay to avoid IllegalStateException
            if (position == 0 && !isFirstItemSelected) {
                isFirstItemSelected = true
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
        return attributeItems.size
    }
}
