package com.hommlie.user.adapter

import android.app.Activity
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hommlie.user.R
import com.hommlie.user.activity.ActSelectTimeSlot
import com.hommlie.user.databinding.RowDateBinding
import com.hommlie.user.model.DateTimeModel

class DateAdapter(
    private val context: ActSelectTimeSlot,
    private val data: List<DateTimeModel>
) : RecyclerView.Adapter<DateAdapter.DateTimeHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION
    inner class DateTimeHolder(private val itemBinding: RowDateBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(
            data: DateTimeModel,
            context: Activity,
            position: Int
        ) = with(itemBinding)
        {
            itemBinding.tvDate.text=data.date
            itemBinding.tvDayName.text=data.day

            if (data.day=="Sun"){
                itemBinding.llDate1.visibility=View.GONE
            }

            if (position == selectedPosition) {
                itemBinding.tvDate.setTextColor(ContextCompat.getColor(itemView.context, R.color.color249370))
                itemBinding.tvDayName.setTextColor(ContextCompat.getColor(itemView.context, R.color.color249370))
                itemBinding.llDate1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.color249370))
                ActSelectTimeSlot.selectedDate=data.day+" "+data.fullDate

            } else {
                itemBinding.tvDate.setTextColor(ContextCompat.getColor(itemView.context, R.color.color181818))
                itemBinding.tvDayName.setTextColor(ContextCompat.getColor(itemView.context, R.color.color181818))
                itemBinding.llDate1.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.color_cardBorder))
            }

            itemBinding.llDate1.setOnClickListener {
                notifyItemChanged(selectedPosition)
                selectedPosition =adapterPosition
                notifyItemChanged(selectedPosition)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateTimeHolder {
        val view = RowDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateTimeHolder(view)
    }

    override fun onBindViewHolder(holder: DateTimeHolder, position: Int) {
        holder.bind(data[position], context, position)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}