package com.hommlie.user.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hommlie.user.R
import com.hommlie.user.activity.ActViewAll
import com.hommlie.user.activity.Act_inner_all_sub_category
import com.hommlie.user.model.SubcategoryItem
import com.hommlie.user.utils.Common
import okhttp3.internal.assertThreadHoldsLock

class SubCateAdapter(private val mList: ArrayList<SubcategoryItem>) :
    RecyclerView.Adapter<SubCateAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
          //  .inflate(R.layout.row_all_sub_categories, parent, false)
            .inflate(R.layout.row_subcategory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ItemsViewModel = mList[position]
      //  setAnimation(holder.itemView,position)
        holder.textView.text = ItemsViewModel.subcategoryName
        holder.subcat_titile.text = ItemsViewModel.subcategory_title
        holder.subcat_sub_title.text = ItemsViewModel.subcategory_sub_title

        Glide.with(holder.imageView).load(ItemsViewModel.subcatImage).placeholder(R.drawable.placeholder_homevideo).into(holder.imageView)

        val isVisible: Boolean = ItemsViewModel.expand
      //  holder.recyclerView.visibility = if (isVisible) View.VISIBLE else View.GONE
        holder.itemView.setOnClickListener {
            if (ItemsViewModel.innersubcategory.isNullOrEmpty()) {
              //  holder.recyclerView.isClickable = false
              //  Common.getToast(holder.itemView.context as Activity,ItemsViewModel.subcatId.toString())
                val intent= Intent(holder.itemView.context , ActViewAll::class.java )
                intent.putExtra("subcategory_name",ItemsViewModel.subcategoryName)
                intent.putExtra("subcategory_id",ItemsViewModel.subcatId.toString())
                holder.itemView.context.startActivity(intent)
            } else {
//                ItemsViewModel.expand = !ItemsViewModel.expand
//                Log.d("isVisible", isVisible.toString())
//                notifyItemChanged(position)
                Common.getToast(holder.itemView.context as Activity,"No data found")
            }
        }
//        val adapter: SubInnerCateAdapter? = ItemsViewModel.innersubcategory?.let {
//            SubInnerCateAdapter(
//                it
//            )
//        }
//        holder.recyclerView.adapter = adapter
//        holder.recyclerView.layoutManager =
//            LinearLayoutManager(holder.recyclerView.context, LinearLayoutManager.VERTICAL, false)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.tv_category_name)
        val imageView:ImageView=itemView.findViewById(R.id.ivsearchproduct)
        val subcat_titile: TextView = itemView.findViewById(R.id.tv_category_starting_price)
        val subcat_sub_title: TextView = itemView.findViewById(R.id.tv_category_subtext)

    }


    private fun setAnimation(view: View, position: Int) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.item_slide_down)
        val delay = position * 50L
        animation.startOffset = delay

        view.startAnimation(animation)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }


}