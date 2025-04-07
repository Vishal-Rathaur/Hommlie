package com.hommlie.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.databinding.ActOrderDetailsBinding
import com.hommlie.user.databinding.RemoveItemDialogBinding
import com.hommlie.user.databinding.RowOrderdetailsproductBinding
import com.hommlie.user.model.*
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.filterHttps
import com.hommlie.user.utils.SharePreference
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ActOrderDetails : BaseActivity() {
    private lateinit var orderDetailsBinding: ActOrderDetailsBinding
    var currency: String = ""
    var currencyPosition: String = ""
    var orderDetailsList: OrderInfo? = null
    var orderstatus: String  = ""
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )

    override fun setLayout(): View = orderDetailsBinding.root

    override fun initView() {
        orderDetailsBinding = ActOrderDetailsBinding.inflate(layoutInflater)
        if (Common.isCheckNetwork(this@ActOrderDetails)) {
            if (SharePreference.getBooleanPref(this@ActOrderDetails, SharePreference.isLogin)) {
                callApiOrderDetail()
            } else {
                openActivity(ActSignUp::class.java)
                this.finish()
            }
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActOrderDetails,
                resources.getString(R.string.no_internet)
            )
        }
        currency = SharePreference.getStringPref(this@ActOrderDetails, SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(
                this@ActOrderDetails,
                SharePreference.CurrencyPosition
            )!!
        orderDetailsBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }

        orderDetailsBinding.tvorderid.setOnClickListener {
            val text = orderDetailsBinding.tvorderid.text.toString().trim()
            copyTextToClipboard(text)
        }
        orderDetailsBinding.btnReport.setOnClickListener {
            Common.getToast(this@ActOrderDetails,"Report will send on your email")
        }

    }

    //TODO API ORDER DETAILS CALL
    private fun callApiOrderDetail() {
        Common.showLoadingProgress(this@ActOrderDetails)
        val hasmap = HashMap<String, String>()
        hasmap["order_number"] = intent.getStringExtra("orderId").toString()
        val call = ApiClient.getClient.getOrderDetails(hasmap)
        call.enqueue(object : Callback<OrderDetailsResponse> {
            override fun onResponse(call: Call<OrderDetailsResponse>, response: Response<OrderDetailsResponse>) {
                if (response.isSuccessful) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        orderDetailsList = restResponce.orderInfo
                        Common.getLog("zzzzz",restResponce.orderInfo.toString())

                        loadOrderDetails(orderDetailsList!!)
                        restResponce.orderData?.let { loadOrderProductDetails(it) }

                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.showErrorFullMsg(
                            this@ActOrderDetails,
                            restResponce.message.toString()
                        )
                    }
                }else{
                    Common.dismissLoadingProgress()
                    Common.showErrorFullMsg(this@ActOrderDetails,"Please try again after some time")
                    finish()
                    overridePendingTransition(R.anim.no_animation,R.anim.slide_out)
                }
            }


            override fun onFailure(call: Call<OrderDetailsResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(this@ActOrderDetails, resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SET ORDER PRODUCT DETAILS DATA
    private fun loadOrderProductDetails(orderData: ArrayList<OrderDataItem>) {
        lateinit var binding: RowOrderdetailsproductBinding
        val viewAllDataAdapter =
            object : BaseAdaptor<OrderDataItem, RowOrderdetailsproductBinding>(
                this@ActOrderDetails,
                orderData
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n", "InflateParams",
                    "UseCompatLoadingForDrawables"
                )
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: OrderDataItem,
                    position: Int
                ) {
                    Glide.with(this@ActOrderDetails)
                        .load(filterHttps(orderData[position].imageUrl))
                        .placeholder(R.drawable.ic_placeholder)
                        .into(binding.ivCartitemm)
                    binding.ivCartitemm.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    binding.tvcateitemname.text = orderData[position].productName
                    binding.tvcartitemqty.text = "Qty: " + orderData[position].qty.toString()
                    binding.btnTrackOrder.setOnClickListener {
                        Log.e(
                            "order_id--->",
                            orderData[position].id.toString()
                        )
                        val intent = Intent(this@ActOrderDetails, ActTrackOrder::class.java)
                        intent.putExtra("orderId", orderData[position].id.toString()
                        )
                        intent.putExtra("att", orderData[position].attribute.toString()
                        )
                        startActivity(intent)
                    }
                    binding.btnCancel.setOnClickListener {
                        if (orderstatus == "6") {
                           Common.getToast(this@ActOrderDetails,"Order Cancelled")
                        }else{
                            cancelOrderDialog(orderData[position].id.toString())
                        }
                    }

                    binding.swipe.isSwipeEnabled = true // make it true to enable

                    val qty = orderData[position].qty
                    val price = orderData[position].price?.toDouble()
                    val totalpriceqty = price!! * qty!!
                    if (orderData[position].variation == null) {
                        binding.tvcartitemsize.text = "-"
                    } else {
                        binding.tvcartitemsize.text =
                            orderData[position].attribute + ": " + orderData[position].variation
                    }
                  //  if (orderstatus == "6") {
                        binding.swipe.isSwipeEnabled = false // make false if not want to use
                  //  }
                    when (orderstatus) {
                        "6" -> {
                            binding.swipe.isSwipeEnabled = false
//                            binding.tvorderstatus.visibility = View.VISIBLE
//                            binding.tvorderstatus.text = getString(R.string.order_cancelled)
                            orderDetailsBinding.btnReport.isClickable=false
                            orderDetailsBinding.btnReport.text="Order Cancelled"
                            orderDetailsBinding.btnReport.backgroundTintList = ContextCompat.getColorStateList(this@ActOrderDetails, R.color.light_red_logout)
                            orderDetailsBinding.btnReport.setTextColor(ContextCompat.getColor(this@ActOrderDetails, R.color.red_logout))

                        }
                        else -> {
                            binding.tvorderstatus.visibility = View.GONE
                        }
                    }

                    if (currencyPosition == "left") {

                        binding.tvcartitemprice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    totalpriceqty
                                )
                            )
                        binding.tvshippingcost.text = currency.plus(
                            String.format(
                                Locale.US,
                                "%,.2f",
                                orderData[position].shippingCost!!.toDouble()
                            )
                        )
                        binding.tvtax.text = currency.plus(
                            String.format(
                                Locale.US,
                                "%,.2f",
                                orderData[position].tax!!.toString().toDouble()
                            )
                        )
//                        if (orderData[position].discountAmount == null) {
//                            binding.tvdiscout.text = currency.plus(
//                                String.format(
//                                    Locale.US,
//                                    "%,.2f",
//                                    0.toDouble()
//                                )
//                            )
//                        } else {
//                            binding.tvdiscout.text = currency.plus(
//                                String.format(
//                                    Locale.US,
//                                    "%,.2f",
//                                    orderData[position].discountAmount!!.toDouble()
//                                )
//                            )
//                        }
                        val discountAmount = orderData[position].discountAmount
                        val formattedDiscount = if (discountAmount.isNullOrEmpty()) {
                            String.format(Locale.US, "%,.2f", 0.0)
                        } else {
                            String.format(Locale.US, "%,.2f", discountAmount.toDouble())
                        }
                        binding.tvdiscout.text = currency.plus(formattedDiscount)

                    } else {
                        binding.tvcartitemprice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                totalpriceqty
                            )) + "" + currency
                        binding.tvshippingcost.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                orderData[position].shippingCost!!.toDouble()
                            )) + "" + currency
                        binding.tvtax.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                orderData[position].tax!!.toString().toDouble()
                            )) + "" + currency
                        if (orderData[position].discountAmount == null) {
                            binding.tvdiscout.text =
                                (String.format(
                                    Locale.US,
                                    "%,.2f",
                                    0.toDouble()
                                )) + "" + currency
                        } else {
                            binding.tvdiscout.text =
                                (String.format(
                                    Locale.US,
                                    "%,.2f",
                                    orderData[position].discountAmount!!.toDouble()
                                )) + "" + currency
                        }
                    }

                  /*  binding.tvmore.setOnClickListener {
                        val dialog = BottomSheetDialog(this@ActOrderDetails)
                        if (Common.isCheckNetwork(this@ActOrderDetails)) {
                            val view = layoutInflater.inflate(R.layout.row_bottomsheetorderdetails, null)
                            dialog.window?.setBackgroundDrawable(getDrawable(R.color.tr))
                            val btnCancelorder = view.findViewById<TextView>(R.id.tvcancelorder)
                            val btnTrackorder = view.findViewById<TextView>(R.id.tvtrackorder)
                            val btnCancel = view.findViewById<TextView>(R.id.tvCancel)
                            val btnRetuenRequest = view.findViewById<TextView>(R.id.tvReturnReq)
                            val viewreturnrequest = view.findViewById<View>(R.id.view2)
                            if (orderstatus == "4") {
                                btnRetuenRequest.visibility = View.VISIBLE
                                viewreturnrequest.visibility = View.VISIBLE
                            }else{
                                btnRetuenRequest.visibility = View.GONE
                                viewreturnrequest.visibility = View.GONE
                            }
                            if (orderstatus == "7") {
                                btnRetuenRequest.visibility = View.GONE
                                viewreturnrequest.visibility = View.GONE
                            }
                            btnCancel.setOnClickListener {
                                dialog.dismiss()
                                onResume()
                            }
                            btnCancelorder.setOnClickListener {
                                cancelOrderDialog(orderData[position].id?:0)
                                dialog.dismiss()
                            }
                            btnRetuenRequest.setOnClickListener {
                                Log.e(
                                    "order_id--->",
                                    orderData[position].id.toString()
                                )
                                val intent =
                                    Intent(this@ActOrderDetails, ActRetuenRequest::class.java)
                                intent.putExtra(
                                    "order_id",
                                    orderData[position].id.toString()
                                )
                                intent.putExtra(
                                    "att",
                                    orderData[position].attribute.toString()
                                )
                                startActivity(intent)
                                dialog.dismiss()
                            }
                            btnTrackorder.setOnClickListener {
                                Log.e(
                                    "order_id--->",
                                    orderData[position].id.toString()
                                )
                                val intent = Intent(this@ActOrderDetails, ActTrackOrder::class.java)
                                intent.putExtra(
                                    "orderId",
                                    orderData[position].id.toString()
                                )
                                intent.putExtra(
                                    "att",
                                    orderData[position].attribute.toString()
                                )
                                startActivity(intent)
                                dialog.dismiss()
                            }
                            dialog.setCancelable(false)
                            dialog.setContentView(view)
                            dialog.show()
                        } else {
                            Common.alertErrorOrValidationDialog(
                                this@ActOrderDetails,
                                resources.getString(R.string.no_internet)
                            )
                            dialog.dismiss()
                        }
                    }  */

                    if (orderData.size==position+1){
                        binding.viewL.visibility=View.INVISIBLE
                    }

                    if (orderstatus=="6"){
                        binding.cl3.visibility=View.GONE
                    }
                    if (orderstatus=="4" || orderstatus=="3" || orderstatus=="2"){
                        binding.btnCancel.visibility=View.GONE
                    }

                }

                override fun setItemLayout(): Int {
                    return R.layout.row_orderdetailsproduct
                }

                override fun getBinding(parent: ViewGroup): RowOrderdetailsproductBinding {
                    binding = RowOrderdetailsproductBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        orderDetailsBinding.rvorderproduct.apply {
            if (orderData.size > 0) {
                orderDetailsBinding.rvorderproduct.visibility = View.VISIBLE
                orderDetailsBinding.tvNoDataFound.visibility = View.GONE
                layoutManager =
                    LinearLayoutManager(this@ActOrderDetails, LinearLayoutManager.VERTICAL, false)
                itemAnimator = DefaultItemAnimator()
                adapter = viewAllDataAdapter
            } else {
                orderDetailsBinding.rvorderproduct.visibility = View.VISIBLE
                orderDetailsBinding.tvNoDataFound.visibility = View.GONE
            }
        }
    }


    fun cancelOrderDialog(orderId: String) {
        val removeDialogBinding = RemoveItemDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this@ActOrderDetails)
        dialog.setContentView(removeDialogBinding.root)
        removeDialogBinding.tvRemoveTitle.text = resources.getString(R.string.cancel_product)
        removeDialogBinding.tvAlertMessage.text = resources.getString(R.string.cancel_product_desc)
        removeDialogBinding.btnProceed.setOnClickListener {
            if (Common.isCheckNetwork(this@ActOrderDetails)) {
                dialog.dismiss()
                cancelorder(orderId)
            } else {
                Common.alertErrorOrValidationDialog(this@ActOrderDetails, resources.getString(R.string.no_internet))
            }
        }
        removeDialogBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    //TODO API ORDER CANCEL CALL
    private fun cancelorder(id: String) {
        Common.showLoadingProgress(this@ActOrderDetails)
        val hasmap = HashMap<String, String>()
      //  hasmap["user_id"] = SharePreference.getStringPref(this@ActOrderDetails, SharePreference.userId)!!
        hasmap["id"] = id.toString()
    //    hasmap["status"] = "5"
        Log.e("TOken ", SharePreference.getStringPref(this@ActOrderDetails,SharePreference.token).toString())
        val call = ApiClient.getClient.getCancelOrder(hasmap,SharePreference.getStringPref(this@ActOrderDetails,SharePreference.token).toString())
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                Common.dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.status == 1) {
                       // Common.isAddOrUpdated = true
                        callApiOrderDetail()
                    } else {
                        Common.showErrorFullMsg(
                            this@ActOrderDetails,
                            response.body()?.message.toString()
                        )
                    }
                } else {
                    Common.alertErrorOrValidationDialog(
                        this@ActOrderDetails,
                        resources.getString(R.string.error_msg)
                    )
                }
            }
            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActOrderDetails,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SET ORDER DETAILS DATA
    @SuppressLint("SetTextI18n")
    private fun loadOrderDetails(orderDetailsList: OrderInfo) {
        if (!orderDetailsList.equals(0)) {
            orderDetailsBinding.tvorderid.visibility = View.VISIBLE
            orderDetailsBinding.tvpaymenttype.visibility = View.VISIBLE
            orderDetailsBinding.tvorderdate.visibility = View.VISIBLE
            orderDetailsBinding.tvusermailid.visibility = View.VISIBLE
            orderDetailsBinding.tvphone.visibility = View.VISIBLE
            orderDetailsBinding.tvareaaddress.visibility = View.VISIBLE
            orderDetailsBinding.tvUserName.visibility = View.VISIBLE
            orderDetailsBinding.tvsubtotal.visibility = View.VISIBLE
            orderDetailsBinding.tvtaxtotal.visibility = View.VISIBLE
            orderDetailsBinding.tvshippingtotal.visibility = View.VISIBLE
            orderDetailsBinding.tvtotal.visibility = View.VISIBLE
            orderDetailsBinding.tvdiscounttotal.visibility = View.VISIBLE
            orderDetailsBinding.tvorderid.text = orderDetailsList.orderNumber+"  "

            orderstatus = orderDetailsList.status.toString()

            Common.getToast(this@ActOrderDetails, orderstatus)


            when (orderDetailsList.paymentType) {
                1 -> {
                    orderDetailsBinding.tvpaymenttype.text = "Cash"
                }
                2 -> {
                    orderDetailsBinding.tvpaymenttype.text = "Wallet"
                }
                3 -> {
                    orderDetailsBinding.tvpaymenttype.text = "RazorPay"
                }
                4 -> {
                    orderDetailsBinding.tvpaymenttype.text = "Stripe"
                }
                5 -> {
                    orderDetailsBinding.tvpaymenttype.text = "Flutterwave"
                }
                6 -> {
                    orderDetailsBinding.tvpaymenttype.text = "Paystack"
                }
            }
            orderDetailsBinding.tvorderdate.text = orderDetailsList.date?.let { Common.getDate(it) }
            orderDetailsBinding.tvusermailid.text = orderDetailsList.email
            orderDetailsBinding.tvphone.text = orderDetailsList.mobile
            when {
                orderDetailsList.landmark == null -> {
                    orderDetailsBinding.tvareaaddress.text =
                        "" + orderDetailsList.streetAddress + "-" + orderDetailsList.pincode
                }
                orderDetailsList.streetAddress == null -> {
                    orderDetailsList.landmark + "-" + orderDetailsList.pincode
                }
                orderDetailsList.pincode == null -> {
                    orderDetailsList.landmark + " " + orderDetailsList.streetAddress
                }
                else -> {
                    orderDetailsBinding.tvareaaddress.text =
                        orderDetailsList.landmark + " " + orderDetailsList.streetAddress + "-" + orderDetailsList.pincode
                }
            }
            if (orderDetailsList.orderNotes == null) {
                orderDetailsBinding.clNote.visibility = View.GONE
            } else {
                orderDetailsBinding.edNote.setText(orderDetailsList.orderNotes)
            }
            orderDetailsBinding.tvUserName.text = orderDetailsList.fullName
            if (currencyPosition == "left") {

                orderDetailsBinding.tvsubtotal.text =orderDetailsList.subtotal

                orderDetailsBinding.tvtaxtotal.text = orderDetailsList.tax

                orderDetailsBinding.tvshippingtotal.text =orderDetailsList.shippingCost
                if (orderDetailsList.discountAmount != null) {
                    orderDetailsBinding.tvdiscounttotal.text = orderDetailsList.discountAmount
                } else {
                    orderDetailsBinding.tvdiscounttotal.text = "0.00"
                }
                orderDetailsBinding.tvtotal.text =orderDetailsList.grandTotal
            } else {
                orderDetailsBinding.tvsubtotal.text = orderDetailsList.subtotal
                orderDetailsBinding.tvtaxtotal.text = orderDetailsList.tax!!.toString()
                orderDetailsBinding.tvshippingtotal.text = orderDetailsList.shippingCost

                if (orderDetailsList.discountAmount != null) {
                    orderDetailsBinding.tvdiscounttotal.text =orderDetailsList.discountAmount
                } else {
                    orderDetailsBinding.tvdiscounttotal.text = "0.00"
                }

                orderDetailsBinding.tvtotal.text = orderDetailsList.grandTotal
            }
        } else {
            orderDetailsBinding.tvorderid.visibility = View.GONE
            orderDetailsBinding.tvpaymenttype.visibility = View.GONE
            orderDetailsBinding.tvorderdate.visibility = View.GONE
            orderDetailsBinding.tvusermailid.visibility = View.GONE
            orderDetailsBinding.tvphone.visibility = View.GONE
            orderDetailsBinding.tvareaaddress.visibility = View.GONE
            orderDetailsBinding.tvUserName.visibility = View.GONE
            orderDetailsBinding.tvsubtotal.visibility = View.GONE
            orderDetailsBinding.tvtaxtotal.visibility = View.GONE
            orderDetailsBinding.tvshippingtotal.visibility = View.GONE
            orderDetailsBinding.tvtotal.visibility = View.GONE
        }

        if (orderDetailsList.grandTotal!!.contains("N",ignoreCase = true)){
            val price = orderDetailsList.subtotal?.toDouble()
            val tax   = orderDetailsList.tax?.toDouble()
            val ship  = orderDetailsList.shippingCost?.toDouble()
            val total = price!!+ tax!! +ship!!

            if (orderDetailsList.discountAmount!!.contains("N",ignoreCase = true)){
                orderDetailsBinding.tvtotal.text="\u20b9 "+total.toString()
            }else{
                val discount = orderDetailsList.discountAmount?.toDouble()
                orderDetailsBinding.tvtotal.text="\u20b9 "+(total-discount!!).toString()
            }





        }else{
            orderDetailsBinding.tvtotal.text=orderDetailsList.grandTotal
        }
    }

    override fun onResume() {
        super.onResume()
        if (Common.isCheckNetwork(this@ActOrderDetails)) {
            if (SharePreference.getBooleanPref(this@ActOrderDetails, SharePreference.isLogin)) {
                callApiOrderDetail()
            } else {
                openActivity(ActSignUp::class.java)
                this.finish()
            }
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActOrderDetails,
                resources.getString(R.string.no_internet)
            )
        }
    }


    private fun copyTextToClipboard(text: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("", text)
        clipboardManager.setPrimaryClip(clipData)
       // Common.getToast(this@ActOrderDetails,text)
    }

    override fun onBackPressed() {
      //  super.onBackPressed()
    }

}