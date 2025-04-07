package com.hommlie.user.adapter

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hommlie.user.R
import com.hommlie.user.activity.ActAllSubCategories
import com.hommlie.user.activity.ActProductDetails
import com.hommlie.user.model.TopbannerItem
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.filterHttps

class SliderAdapter(private val items: ArrayList<TopbannerItem>) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_item, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val imageView: ImageView = holder.itemView.findViewById(R.id.ivBanner)
        Glide.with(imageView.context).load(filterHttps(items[position].imageUrl.toString()))
            .placeholder(R.drawable.placeholder_homevideo)
            .into(imageView)

        holder.itemView.setOnClickListener {
            if (items[position].productId != null) {
                Log.e("product_id--->", items[position].productId.toString())
                val intent = Intent(holder.itemView.context, ActProductDetails::class.java)
                intent.putExtra("product_id", items[position].productId.toString())
                holder.itemView.context.startActivity(intent)
            } else if (items[position].catId != null) {
                Log.e("cat_id--->", items[position].catId.toString())
                val intent =
                    Intent(holder.itemView.context, ActAllSubCategories::class.java).apply {
                        putExtra("cat_id", items[position].catId.toString())
                        putExtra("categoryName", items[position].categoryName)
                    }
                holder.itemView.context.startActivity(intent)
            } else if (items[position].link != null) {
                val webpage: Uri = Uri.parse(items[position].link)
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                if (intent.resolveActivity(holder.itemView.context.packageManager) != null) {
                    holder.itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount() = items.size
}