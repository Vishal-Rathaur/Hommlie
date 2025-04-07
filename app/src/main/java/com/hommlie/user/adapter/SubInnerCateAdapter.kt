package com.hommlie.user.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hommlie.user.R
import com.hommlie.user.activity.ActViewAll
import com.hommlie.user.model.InnerSubcat
import java.util.ArrayList

class SubInnerCateAdapter(private var mList: List<InnerSubcat>) : RecyclerView.Adapter<SubInnerCateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_all_inner_sub_categories, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val innersubcategory = mList[position]

        Glide.with(holder.imageView).load(innersubcategory.innersubcategory_image).placeholder(R.drawable.g2).into(holder.imageView)

       // holder.textView.text = innersubcategory.innersubcategoryName
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ActViewAll::class.java)
            intent.putExtra("innersubcategory_id", innersubcategory.id.toString())
            intent.putExtra(
                "title",
                innersubcategory.innersubcategory_name.toString()
            )
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
       // val textView: TextView = itemView.findViewById(R.id.tvinnersubcateitemname)
        val imageView:ImageView=itemView.findViewById(R.id.ivCategories)
    }
}