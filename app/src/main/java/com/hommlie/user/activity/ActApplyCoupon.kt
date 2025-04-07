package com.hommlie.user.activity

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hommlie.user.adapter.CouponAdapter
import com.hommlie.user.api.ApiClient
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActivityActApplyCouponBinding
import com.hommlie.user.model.Coupns
import com.hommlie.user.model.Coupon
import com.hommlie.user.utils.Common
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActApplyCoupon : BaseActivity() {

    private lateinit var binding: ActivityActApplyCouponBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CouponAdapter
    companion object {
        var selectedCouponName: String = ""
        var couponPrice:String =""
    }

    override fun setLayout(): View =binding.root

    override fun initView() {
        binding=ActivityActApplyCouponBinding.inflate(layoutInflater)

        selectedCouponName=""
        couponPrice=""

        recyclerView=binding.rvCoupons

        binding.ivBack.setOnClickListener { finish() }

        binding.tvApply.setOnClickListener {
            val isComeFromSelectAddress =
                intent.getBooleanExtra("isComeFromSelectCouponCode", false)
            if (isComeFromSelectAddress) {
                val intent = Intent(this@ActApplyCoupon, ActCheckout::class.java)
                intent.putExtra("couponcode",binding.edtCouponcode.text.toString())
                setResult(503, intent)
                finish()
            }
        }

        getCoupons()
    }


    private fun getCoupons(){
        Common.showLoadingProgress(this@ActApplyCoupon)
        val call =ApiClient.getClient.getCoupons()

        call.enqueue(object : Callback<Coupns>{
            override fun onResponse(call: Call<Coupns>, response: Response<Coupns>) {
                if (response.isSuccessful){
                    Common.dismissLoadingProgress()
                    if (response.body()!=null){
                        if (response.body()!!.status==1){
                            val data= response.body()!!.data
                            setUpRecylerView(data)
                        }else{
                            binding.ivNoData.visibility=View.VISIBLE
                        }
                    }else{
                        binding.ivNoData.visibility=View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<Coupns>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.showErrorFullMsg(this@ActApplyCoupon,"Please try again after some time")
            }

        })
    }

    private fun setUpRecylerView(data: List<Coupon>){
        adapter= CouponAdapter(data)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

    }



}