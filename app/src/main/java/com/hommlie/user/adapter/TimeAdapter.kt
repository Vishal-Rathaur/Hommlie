package com.hommlie.user.adapter

import android.app.Activity
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hommlie.user.R
import com.hommlie.user.activity.ActSelectTimeSlot
import com.hommlie.user.databinding.RowTimeBinding

class TimeAdapter (
    private val context: ActSelectTimeSlot,
    private val data: List<String>
) : RecyclerView.Adapter<TimeAdapter.TimeHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION
    inner class TimeHolder(private val itemBinding: RowTimeBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(
            data: String,
            context: Activity,
            position: Int
        ) = with(itemBinding)
        {
            itemBinding.tvTime.text=data
            if (position == selectedPosition) {
                itemBinding.tvTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.color249370))
                itemBinding.llTime.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.color249370))
                ActSelectTimeSlot.selectedTime=data
            } else {
                itemBinding.tvTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.color181818))
                itemBinding.llTime.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.color_cardBorder))
            }

            itemBinding.llTime.setOnClickListener {
                notifyItemChanged(selectedPosition)
                selectedPosition =adapterPosition
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeHolder {
        val view = RowTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TimeHolder(view)
    }

    override fun onBindViewHolder(holder: TimeHolder, position: Int) {
        holder.bind(data[position], context, position)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}