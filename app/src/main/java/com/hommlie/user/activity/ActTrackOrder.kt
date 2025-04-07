package com.hommlie.user.activity

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import kotlinx.coroutines.*
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActTrackOrderBinding
import com.hommlie.user.databinding.RemoveItemDialogBinding
import com.hommlie.user.model.TrackOrderInfo
import com.hommlie.user.model.TrackOrderResponse
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.filterHttps
import com.hommlie.user.utils.Common.getDateTime
import com.hommlie.user.utils.SharePreference
import com.hommlie.user.utils.StepProgressView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ActTrackOrder : BaseActivity() {
    private lateinit var trackOrderBinding: ActTrackOrderBinding
    var trackOrderDetailsList: TrackOrderInfo? = null
    var currency: String = ""

    var orderId : String = ""
    var currencyPosition: String = ""
    private var pos = 0
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )

    var desiredDate:String=""
    var desiredTime:String=""

    override fun setLayout(): View = trackOrderBinding.root

    override fun initView() {
        trackOrderBinding = ActTrackOrderBinding.inflate(layoutInflater)
        trackOrderBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        if (Common.isCheckNetwork(this@ActTrackOrder)) {
            if (SharePreference.getBooleanPref(this@ActTrackOrder, SharePreference.isLogin)) {
                orderId = intent.getStringExtra("orderId").toString()
                callApiTrackDetail()
            } else {
                openActivity(ActSignUp::class.java)
                this.finish()
            }
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActTrackOrder,
                resources.getString(R.string.no_internet)
            )
        }
        currency = SharePreference.getStringPref(this@ActTrackOrder, SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(
                this@ActTrackOrder,
                SharePreference.CurrencyPosition
            )!!



    }




    //TODO CALL TRACK ORDER DETAIL API
    private fun callApiTrackDetail() {
        Common.showLoadingProgress(this@ActTrackOrder)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] = SharePreference.getStringPref(this@ActTrackOrder, SharePreference.userId)!!
        hasmap["id"] = orderId
        val call = ApiClient.getClient.getTrackOrder(hasmap)
        call.enqueue(object : Callback<TrackOrderResponse> {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onResponse(
                call: Call<TrackOrderResponse>,
                response: Response<TrackOrderResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        trackOrderDetailsList = restResponce.orderInfo
                       Common.getLog("ttttttttt",restResponce.orderInfo.toString())
                        loadTrackOrderDetails(trackOrderDetailsList!!)
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActTrackOrder,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<TrackOrderResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(this@ActTrackOrder, resources.getString(R.string.error_msg))
            }
        })
    }

    //TODO TRACK ORDER DETAILS DATA SET
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    private fun loadTrackOrderDetails(trackOrderDetailsList: TrackOrderInfo) {
        if (!trackOrderDetailsList.equals(0)) {
            trackOrderBinding.clordertrack.visibility = View.GONE
            trackOrderBinding.ivOrderTrack.visibility = View.VISIBLE
            trackOrderBinding.tvTrackOrderqty.visibility = View.VISIBLE
            trackOrderBinding.tvordertrackname.visibility = View.VISIBLE
            trackOrderBinding.tvOrderProductSize.visibility = View.VISIBLE
            trackOrderBinding.tvcartitemprice.visibility = View.VISIBLE
            trackOrderBinding.tvcartqtytitle.visibility = View.VISIBLE
            trackOrderBinding.tvcartitemprice.visibility = View.VISIBLE
            Glide.with(this@ActTrackOrder)
                .load(filterHttps(trackOrderDetailsList.image)).placeholder(R.drawable.ic_placeholder).into(trackOrderBinding.ivOrderTrack)
            trackOrderBinding.ivOrderTrack.setBackgroundColor(Color.parseColor(colorArray[pos % 6]))
           /* if (trackOrderDetailsList.orderStatus == 1) {
                trackOrderBinding.ivOrderPlaced.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderConfirmed.setImageResource(R.drawable.ic_round)
                trackOrderBinding.ivOrderShipped.setImageResource(R.drawable.ic_round)
                trackOrderBinding.ivOrderDelivery.setImageResource(R.drawable.ic_round)
                trackOrderBinding.view.setBackgroundColor(getColor(R.color.medium_gray))
                trackOrderBinding.view1.setBackgroundColor(getColor(R.color.medium_gray))
                trackOrderBinding.view2.setBackgroundColor(getColor(R.color.medium_gray))
                trackOrderBinding.tvOrderPlacedDate.visibility = View.VISIBLE
//                if (trackOrderDetailsList.createdAt.toString()==null) {
//                    trackOrderBinding.tvOrderPlacedDate.text =
//                        getDateTime(trackOrderDetailsList.createdAt.toString())
//                }
                trackOrderBinding.tvOrderConfirmedDate.visibility = View.GONE
                trackOrderBinding.tvOrderShippedDate.visibility = View.GONE
                trackOrderBinding.btnAddReview.visibility = View.GONE
                trackOrderBinding.tvOrderDeliveryDate.visibility = View.GONE
            } else if (trackOrderDetailsList.orderStatus == 2) {
                trackOrderBinding.ivOrderPlaced.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderConfirmed.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderShipped.setImageResource(R.drawable.ic_round)
                trackOrderBinding.ivOrderDelivery.setImageResource(R.drawable.ic_round)
                trackOrderBinding.view.setBackgroundColor(getColor(R.color.green))
                trackOrderBinding.view1.setBackgroundColor(getColor(R.color.medium_gray))
                trackOrderBinding.view2.setBackgroundColor(getColor(R.color.medium_gray))
//                if (trackOrderDetailsList.createdAt.toString()==null) {
//                    trackOrderBinding.tvOrderPlacedDate.text =
//                        getDateTime(trackOrderDetailsList.createdAt.toString())
//                    trackOrderBinding.tvOrderConfirmedDate.text =
//                        getDateTime(trackOrderDetailsList.confirmedAt.toString())
//                }
                trackOrderBinding.tvOrderConfirmedDate.visibility = View.VISIBLE
                trackOrderBinding.tvOrderShippedDate.visibility = View.GONE
                trackOrderBinding.tvOrderDeliveryDate.visibility = View.GONE
                trackOrderBinding.btnAddReview.visibility = View.GONE
            } else if (trackOrderDetailsList.orderStatus == 3) {
                trackOrderBinding.ivOrderPlaced.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderConfirmed.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderShipped.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderDelivery.setImageResource(R.drawable.ic_round)
                trackOrderBinding.view.setBackgroundColor(getColor(R.color.green))
                trackOrderBinding.view1.setBackgroundColor(getColor(R.color.green))
                trackOrderBinding.view2.setBackgroundColor(getColor(R.color.medium_gray))
//                if (trackOrderDetailsList.createdAt.toString()==null) {
//                    trackOrderBinding.tvOrderPlacedDate.text =
//                        getDateTime(trackOrderDetailsList.createdAt.toString())
//                    trackOrderBinding.tvOrderShippedDate.text =
//                        getDateTime(trackOrderDetailsList.shippedAt.toString())
//                    trackOrderBinding.tvOrderConfirmedDate.text =
//                        getDateTime(trackOrderDetailsList.confirmedAt.toString())
//                }
                trackOrderBinding.tvOrderConfirmedDate.visibility = View.VISIBLE
                trackOrderBinding.tvOrderShippedDate.visibility = View.VISIBLE
                trackOrderBinding.tvOrderDeliveryDate.visibility = View.GONE
                trackOrderBinding.btnAddReview.visibility = View.GONE
            } else if (trackOrderDetailsList.orderStatus == 4 || trackOrderDetailsList.orderStatus == 7 ||
                trackOrderDetailsList.orderStatus == 8 || trackOrderDetailsList.orderStatus == 9 || trackOrderDetailsList.orderStatus == 10
            ) {
                trackOrderBinding.ivOrderPlaced.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderConfirmed.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderShipped.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.ivOrderDelivery.setImageResource(R.drawable.ic_green_round)
                trackOrderBinding.view.setBackgroundColor(getColor(R.color.green))
                trackOrderBinding.view1.setBackgroundColor(getColor(R.color.green))
                trackOrderBinding.view2.setBackgroundColor(getColor(R.color.green))
//                if (trackOrderDetailsList.createdAt.toString()==null) {
//                    trackOrderBinding.tvOrderPlacedDate.text =
//                        getDateTime(trackOrderDetailsList.createdAt.toString())
//                    trackOrderBinding.tvOrderShippedDate.text =
//                        getDateTime(trackOrderDetailsList.shippedAt.toString())
//                    trackOrderBinding.tvOrderConfirmedDate.text =
//                        getDateTime(trackOrderDetailsList.confirmedAt.toString())
//                    trackOrderBinding.tvOrderDeliveryDate.text =
//                        getDateTime(trackOrderDetailsList.deliveredAt.toString())
//                }
                trackOrderBinding.tvOrderConfirmedDate.visibility = View.VISIBLE
                trackOrderBinding.tvOrderShippedDate.visibility = View.VISIBLE
                trackOrderBinding.tvOrderDeliveryDate.visibility = View.VISIBLE
                trackOrderBinding.btnAddReview.visibility = View.VISIBLE
                trackOrderBinding.btnAddReview.setOnClickListener {
                    val intent = Intent(this@ActTrackOrder, ActWriteReview::class.java)
                    intent.putExtra("proName", trackOrderDetailsList.productName.toString())
                    intent.putExtra("proID", trackOrderDetailsList.orderNumber.toString())
                //    intent.putExtra("vendorsID", trackOrderDetailsList.vendorId.toString())
                    intent.putExtra("proImage", trackOrderDetailsList.image.toString())
                    startActivity(intent)
                }
            }   */

            trackOrderBinding.tvTrackOrderqty.text = trackOrderDetailsList.quantity.toString()
            trackOrderBinding.tvordertrackname.text = trackOrderDetailsList.productName
            if (trackOrderDetailsList.variation?.isEmpty() == true) {
                trackOrderBinding.tvOrderProductSize.text = "-"
            } else {
                trackOrderBinding.tvOrderProductSize.text =
                    intent.getStringExtra("att") ?: "Size" + ": " + trackOrderDetailsList.variation
            }
            if (currencyPosition == "left") {

                trackOrderBinding.tvcartitemprice.text =
                    currency.plus(
                        String.format(
                            Locale.US,
                            "%,.2f",
                            trackOrderDetailsList.price!!.toDouble()
                        )
                    )
            } else {
                trackOrderBinding.tvcartitemprice.text =
                    (String.format(
                        Locale.US,
                        "%,.2f",
                        trackOrderDetailsList.price!!.toDouble()
                    )) + "" + currency
            }

            trackOrderBinding.tvPaymentType.text= if(trackOrderDetailsList.paymentType==1)"Cash" else "Online"
            trackOrderBinding.tvdate.text = trackOrderDetailsList.desiredDate?:"No date selected"
            trackOrderBinding.tvtime.text = trackOrderDetailsList.desiredTime?:"No time selected"
            trackOrderBinding.orderplacedat.text = "Order placed at : "+trackOrderDetailsList.date
            trackOrderBinding.tvOrderId.text = "Order Id : "+trackOrderDetailsList.orderNumber

            if (trackOrderDetailsList.orderStatus==4){
                trackOrderBinding.viewline3.visibility=View.GONE
                trackOrderBinding.btnCancel.visibility=View.GONE
                trackOrderBinding.btnResechedule.visibility=View.GONE
             //   trackOrderBinding.btnAddReview.visibility=View.VISIBLE
            }

            trackOrderBinding.btnCancel.setOnClickListener {
                if (trackOrderDetailsList.orderStatus == 2 || trackOrderDetailsList.orderStatus == 3) {
                    if (trackOrderDetailsList.orderStatus==2){
                        Common.getToast(this@ActTrackOrder,"Service Dispatched")
                    }else{
                        Common.getToast(this@ActTrackOrder,"Service Onsite")
                    }
                } else{
                    cancelOrderDialog(orderId, resources.getString(R.string.cancel_product), resources.getString(R.string.cancel_product_desc), "cancel")
                }
            }

            trackOrderBinding.btnResechedule.setOnClickListener {
                if (trackOrderDetailsList.orderStatus == 2 || trackOrderDetailsList.orderStatus == 3) {
                    if (trackOrderDetailsList.orderStatus==2){
                        Common.getToast(this@ActTrackOrder,"Service Dispatched")
                    }else{
                        Common.getToast(this@ActTrackOrder,"Service Onsite")
                    }
                } else {
                    val intent = Intent(this@ActTrackOrder, ActSelectTimeSlot::class.java)
                    intent.putExtra("isComeFromSelectDateandTimeMethod", true)
                    dateAndtimeDataSet.launch(intent)
                    CoroutineScope(Dispatchers.Main).launch { delay(500)
                        cancelOrderDialog(orderId, "Resechedule Service", "Are you sure want to rechedule service?", "resechedule")
                    }
                }
            }
            if (trackOrderDetailsList.orderStatus==6){
                trackOrderBinding.viewline3.visibility=View.GONE
                trackOrderBinding.btnCancel.visibility=View.GONE
                trackOrderBinding.btnResechedule.visibility=View.GONE
                trackOrderBinding.tvPaymentType.text="Order Cancelled"
                trackOrderBinding.tvPaymentType.backgroundTintList = ContextCompat.getColorStateList(this@ActTrackOrder, R.color.red_logout)
            }

            if (trackOrderDetailsList.orderStatus == 2 || trackOrderDetailsList.orderStatus == 3) {
                trackOrderBinding.tvCancel.backgroundTintList = ContextCompat.getColorStateList(this@ActTrackOrder, R.color.light_red_logout)
                trackOrderBinding.tvReschedule.backgroundTintList = ContextCompat.getColorStateList(this@ActTrackOrder, R.color.light_color_quantom_orange)
            }


        } else {
            trackOrderBinding.clordertrack.visibility = View.GONE
            trackOrderBinding.ivOrderTrack.visibility = View.GONE
            trackOrderBinding.tvTrackOrderqty.visibility = View.GONE
            trackOrderBinding.tvordertrackname.visibility = View.GONE
            trackOrderBinding.tvOrderProductSize.visibility = View.GONE
            trackOrderBinding.tvcartitemprice.visibility = View.GONE
            trackOrderBinding.tvcartqtytitle.visibility = View.GONE
            trackOrderBinding.btnAddReview.visibility = View.GONE
        }

        trackOrderBinding.stepProgressView.animateToStep(trackOrderDetailsList.orderStatus)
    }

    override fun onResume() {
        super.onResume()
       // callApiTrackDetail()
    }

    @SuppressLint("SetTextI18n")
    val dateAndtimeDataSet =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 502) {
                Common.getToast(this@ActTrackOrder,result.data?.getStringExtra("date")!!.substring(3))
                Common.getToast(this@ActTrackOrder,result.data?.getStringExtra("time").toString())
                desiredDate= result.data?.getStringExtra("date")!!.substring(3)
                desiredTime=result.data?.getStringExtra("time").toString()
                //    paymentType = result.data?.getStringExtra("paymentType").toString()
            } else {
//                    checkoutBinding.tvUserName.visibility = View.GONE
//                    checkoutBinding.tvUserphone.visibility = View.GONE

            }
        }

    private fun animateStep(activeStep: ImageView, activeLine: View, nextStep: ImageView) {
        // Fill current step
        activeStep.setBackgroundResource(R.drawable.ic_circle)

        // Animate the line
        val animator = ObjectAnimator.ofFloat(activeLine, "scaleX", 0f, 1f)
        animator.duration = 500
        animator.start()

        // Delay filling the next step
        activeLine.postDelayed({
            nextStep.setBackgroundResource(R.drawable.ic_circle)
        }, 500)
    }


    private fun cancelorder(id: String) {
        Common.showLoadingProgress(this@ActTrackOrder)
        val hasmap = HashMap<String, String>()
        hasmap["id"] = id.toString()
        Log.e("TOken ", SharePreference.getStringPref(this@ActTrackOrder,SharePreference.token).toString())
        val call = ApiClient.getClient.getCancelOrder(hasmap,SharePreference.getStringPref(this@ActTrackOrder,SharePreference.token).toString())
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                Common.dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.status == 1) {
                        callApiTrackDetail()
                    } else {
                        Common.showErrorFullMsg(this@ActTrackOrder, response.body()?.message.toString())
                    }
                } else {
                    Common.alertErrorOrValidationDialog(this@ActTrackOrder, resources.getString(R.string.error_msg))
                }
            }
            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(this@ActTrackOrder, resources.getString(R.string.error_msg))
            }
        })
    }

    private fun recheduleOrder(id: String) {
        Common.showLoadingProgress(this@ActTrackOrder)
        val hasmap = HashMap<String, String>()
        hasmap["id"] = id.toString()
        hasmap["desired_time"] = desiredTime
        hasmap["desired_date"] = desiredDate
        Log.e("TOken ", SharePreference.getStringPref(this@ActTrackOrder,SharePreference.token).toString())
        val call = ApiClient.getClient.rescheduleOrder(hasmap,SharePreference.getStringPref(this@ActTrackOrder,SharePreference.token).toString())
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(call: Call<SingleResponse>, response: Response<SingleResponse>) {
                Common.dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.status == 1) {
                        callApiTrackDetail()
                    } else {
                        Common.showErrorFullMsg(this@ActTrackOrder, response.body()?.message.toString())
                    }
                } else {
                    Common.alertErrorOrValidationDialog(this@ActTrackOrder, resources.getString(R.string.error_msg))
                }
            }
            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(this@ActTrackOrder, resources.getString(R.string.error_msg))
            }
        })
    }

    fun cancelOrderDialog(orderId: String,title : String,message : String,type : String) {
        val removeDialogBinding = RemoveItemDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this@ActTrackOrder)
        dialog.setContentView(removeDialogBinding.root)
        removeDialogBinding.tvRemoveTitle.text = title
        removeDialogBinding.tvAlertMessage.text = message
        removeDialogBinding.btnProceed.setOnClickListener {
            if (Common.isCheckNetwork(this@ActTrackOrder)) {
                dialog.dismiss()
                if (type=="cancel") {
                    cancelorder(orderId)
                }else{
                    if (!desiredDate.isNullOrEmpty()&&!desiredTime.isNullOrEmpty()) {
                        recheduleOrder(orderId)
                    }else{
                        Common.getToast(this@ActTrackOrder,"Please select desired date & time")
                    }
                }
                //callApiOrderDetail()
            } else {
                Common.alertErrorOrValidationDialog(
                    this@ActTrackOrder,
                    resources.getString(R.string.no_internet)
                )
            }
        }
        removeDialogBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


}

