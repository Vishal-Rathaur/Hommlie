package com.hommlie.user.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hommlie.user.R
import com.hommlie.user.activity.ActApplyCoupon
import com.hommlie.user.activity.ActCheckout
import com.hommlie.user.databinding.RowCouponsBinding
import com.hommlie.user.model.Coupon
import com.hommlie.user.model.DateTimeModel

class CouponAdapter(
    //private val context: ActApplyCoupon,
    private val data: List<Coupon>
) : RecyclerView.Adapter<CouponAdapter.CouponHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION
    inner class CouponHolder(private val itemBinding: RowCouponsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(
            data: Coupon,
         //   context: Activity,
            position: Int
        ) = with(itemBinding)
        {
            itemBinding.storeTextView.text=data.couponName
            itemBinding.amountTextView.text=data.percentage +"% off"
            itemBinding.validityTextView.text="Validity till : "+data.endDate


            itemView.setOnClickListener {
//                    val isComeFromSelectAddress =
//                        intent.getBooleanExtra("isComeFromSelectCouponCode", false)
                //    if (isComeFromSelectAddress) {
                        val intent = Intent(itemView.context, ActCheckout::class.java)
                        intent.putExtra("couponcode",data.couponName)
                        intent.putExtra("couponamount",data.percentage)
                        intent.putExtra("coupon_id",data.id.toString())
                (itemView.context as Activity). setResult(503, intent)
                (itemView.context as Activity).finish()
               //     }
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CouponHolder {
        val view = RowCouponsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CouponHolder(view)
    }

    override fun onBindViewHolder(holder: CouponHolder, position: Int) {
        holder.bind(data[position], position)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}