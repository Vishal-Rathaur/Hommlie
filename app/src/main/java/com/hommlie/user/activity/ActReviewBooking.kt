package com.hommlie.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.databinding.ActivityActReviewBookingBinding
import com.hommlie.user.databinding.RowChekoutBinding
import com.hommlie.user.model.CheckOutDataItem
import com.hommlie.user.model.InitiatePaymentResponse
import com.hommlie.user.model.PaymentSingleResponse
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.dismissLoadingProgress
import com.hommlie.user.utils.SharePreference
import com.hommlie.user.utils.SharePreference.Companion.getStringPref
import com.hommlie.user.utils.SharePreference.Companion.userEmail
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import java.util.Locale
import kotlin.collections.HashMap

class ActReviewBooking : BaseActivity(), PaymentResultWithDataListener {//PaymentResultListener {

    private lateinit var reviewBinding: ActivityActReviewBookingBinding

    private var strGetData = ""
    var currency: String = ""
    var currencyPosition: String = ""
    var subtotal: Double = 0.0
    var tax: Double = 0.0
    var checkoutprice: Int = 0
    var discountsum: String = ""
    var price: Double = 0.0
    var type: String = ""
    var fname: String = ""
    var lname:String=""
    var landmark: String = ""
    var email: String = ""
    var pincode: String = ""
    var mobile: String = ""
    var streetAddress: String = ""
    var coupon_name: String = ""
    var resellMargin: String = ""
    var finalPrice: String = ""
    var resellOrderFlag: String = ""
    var vendorid: String = ""
    var discountAmount: String = ""
    var orderNote: String = ""
    var highShippingCharge = 0.0;
    var size:String=""
    var paymentType:String=""
    var paymetntypeId : String =""
    var date:String=""
    var desiredDate:String=""
    var logoimg = ""
    var desiredTime:String=""
    var latitude : String =""
    var longitude: String=""
    var coupon_id : String =""

    override fun setLayout(): View =reviewBinding.root

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initView() {
        reviewBinding=ActivityActReviewBookingBinding.inflate(layoutInflater)

        fname = intent.getStringExtra("fname")!!
       // lname =  intent.getStringExtra("lname")!!
        landmark = intent.getStringExtra("landmark") ?: ""
        mobile = intent.getStringExtra("mobile")!!
        orderNote = intent.getStringExtra("order_notes") ?: "0"
        price = intent.getStringExtra("grand_total")!!.toDouble()
        pincode = intent.getStringExtra("pincode")!!
        streetAddress = intent.getStringExtra("street_address")!!
        coupon_name = intent.getStringExtra("coupon_name") ?: "0"
        discountAmount = intent.getStringExtra("discount_amount") ?: "0.0"
        resellMargin = intent.getStringExtra("resell_margin")!!
        finalPrice = intent.getStringExtra("final_price")!!
        resellOrderFlag = intent.getStringExtra("reselling_order_flag")!!
        size=intent.getStringExtra("size")!!
        paymentType=intent.getStringExtra("paymentType")!!
        paymetntypeId= intent.getStringExtra("payment_typeId").toString()
        date=intent.getStringExtra("date")!!
        vendorid = intent.getStringExtra("vendorid")!!
        email=intent.getStringExtra("email")!!
        tax=intent.getStringExtra("tax")!!.toDouble()
        subtotal=intent.getStringExtra("subTotal")!!.toDouble()
        desiredDate=intent.getStringExtra("desiredDate")!!
        desiredTime=intent.getStringExtra("desiredTime")!!
        latitude=intent.getStringExtra("latitude")!!
        longitude=intent.getStringExtra("longitude")!!
        coupon_id = intent.getStringExtra("coupon_id")!!

        reviewBinding.tvAddress.text= "$streetAddress, $landmark, $pincode"
        reviewBinding.tvSize.text=size
        reviewBinding.tvName.text= "$fname"
        reviewBinding.tvMobile.text="Mob : $mobile"
        reviewBinding.tvEmail.text="Email : $email"
        reviewBinding.tvEditSelectDateTime.text=date
        reviewBinding.tvSelectedPaymentmethod.text=paymentType
        reviewBinding.tvSelectedDateTime.text=date
        reviewBinding.tvTotalValue.text ="\u20b9 "+  String.format("%.2f", price)
        reviewBinding.tvTaxValue.text="\u20b9 "+ tax.toString()
        reviewBinding.tvPriceValue.text="\u20b9 "+ subtotal.toString()
        reviewBinding.tvDeliverychargeValue.text="\u20b9 "+ discountAmount
        reviewBinding.tvCashbackAmount.text="\u20b9"+discountAmount+" cashback"



        if (intent.hasExtra("innersubcategoryData")) {
            val myList = intent.getParcelableArrayExtra("innersubcategoryData")
            val reviewDataList = myList?.mapNotNull { it as? CheckOutDataItem }
            loadCheckOutItem(reviewDataList)
            loadCheckOutDetails()
        }


        reviewBinding.btnProccedtoPayment.setOnClickListener {
            paynow()
        }

        reviewBinding.ivBack.setOnClickListener {
            finish()
        }

        reviewBinding.tvRemoveCoupon.setOnClickListener {
            price=price.toDouble()+discountAmount.toDouble()
            reviewBinding.tvCashbackAmount.text="no coupon applied"
            reviewBinding.tvDeliverychargeValue.text="0.0"
            reviewBinding.tvTotalValue.text = String.format("%.2f", price)
            reviewBinding.tvRemoveCoupon.setTextColor(getColor(R.color.subtext))
        }




    }

    //TODO SET CHECKOUT OTHER DETAILS
    @SuppressLint("SetTextI18n")
    private fun loadCheckOutDetails() {

//        if (currencyPosition == "left") {
//            reviewBinding.tvTotalValue.text = currency.plus(
//                String.format(Locale.US, "%,.02f", price.toDouble())
//            )
//            reviewBinding.tvTaxValue.text = currency.plus(
//                String.format(Locale.US, "%,.02f",tax.toDouble())
//            )
//            reviewBinding.tvDeliverychargeValue.text = currency + "" + discountAmount.toString()
//
//            reviewBinding.tvTotalValue.text =
//                currency.plus(String.format(Locale.US, "%,.02f", price.toDouble()))
//
//        } else {
//            reviewBinding.tvPriceValue.text =
//                String.format(Locale.US, "%,.02f", subtotal.toDouble()) + "" + currency
//            reviewBinding.tvTaxValue.text =
//                String.format(Locale.US, "%,.02f", tax.toDouble()) + "" + currency
//            reviewBinding.tvDeliverychargeValue.text = discountAmount.toString() + "" + currency
//                        reviewBinding.tvTotalValue.text =
//                String.format(Locale.US, "%,.02f", price) + "" + currency
//        }

    }

    private fun loadCheckOutItem(checkOutDataList: List<CheckOutDataItem>?) {
        discountsum = if (checkOutDataList == null) {
            "0"
        } else {
            (checkOutDataList.sumOf { it.discountAmount?.toDouble() ?: 0.0 }).toString()
        }

    //    reviewBinding.btnProccedtoPayment.setOnClickListener {
//            if (reviewBinding.tvAddress.text.toString().isNullOrEmpty()) {
//                Common.showErrorFullMsg(
//                    this@ActReviewBooking,
//                    resources.getString(R.string.select_your_address)
//                )
//            } else {
//                val intent = Intent(this@ActReviewBooking, ActBookingSuccess::class.java)
//                intent.putExtra("email", email)
//                intent.putExtra("fname", fname)
//                intent.putExtra("lname", lname)
//                intent.putExtra("landmark", landmark)
//                intent.putExtra("mobile", mobile)
//                //   intent.putExtra("order_notes", checkoutBinding.edtordernote.text.toString())
//                intent.putExtra("pincode", pincode)
//                intent.putExtra("street_address", streetAddress)
//                intent.putExtra("coupon_name", coupon_name)
//              //  intent.putExtra("resell_margin", marginEarned.toString())
//                intent.putExtra("final_price", finalPrice.toString())
//             //   intent.putExtra("reselling_order_flag", resellingOrderFlag)
//                intent.putExtra("discount_amount", discountsum).removeExtra("$")
//                intent.putExtra("grand_total", price.toDouble().toString())
//                intent.putExtra("vendorid", vendorid)
//                Log.d("qqqqqq", "total " + price.toString())
//                Log.d("qqqqqq", "final price " + finalPrice.toString())
//
//                startActivity(intent)
//            }
   //         paynow()
   //     }

        lateinit var binding: RowChekoutBinding
        val wishListDataAdapter =
            object : BaseAdaptor<CheckOutDataItem, RowChekoutBinding>(
                this@ActReviewBooking,
                checkOutDataList as ArrayList<CheckOutDataItem>
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: CheckOutDataItem,
                    position: Int
                ) {
                    binding.tvTitle.text = checkOutDataList!![position].productName

                    if (checkOutDataList[position].attribute == null || checkOutDataList[position].variation == null) {
                        //  binding.tvcartitemsize.text = "-"
                    } else {
//                            binding.tvcartitemsize.text =
//                                checkOutDataList[position].attribute + " : " + checkOutDataList[position].variation
                    }
                    val qty = checkOutDataList[position].qty
                    val qtyprice = checkOutDataList[position].price?.toDouble()
                    val totalpriceqty = qtyprice!! * qty!!
                    binding.tvTotalItem.text =
                            //"Qty: " +
                        ""+checkOutDataList[position].qty
                    Log.d("tax", checkOutDataList[position].tax!!.toString())

                    val shippingCost =
                        if (checkOutDataList[position].shippingCost == "Free Shipping") {
                            "0.0"
                        } else {
                            checkOutDataList[position].shippingCost
                        }

                    if (currencyPosition == "left") {
                        binding.tvCurrentPrice.text ="\u20B9"+
                                currency.plus(
                                    String.format(
                                        Locale.US,
                                        "%,.02f",
                                        totalpriceqty
                                    )
                                )
//                            binding.tvtax.text =
//                                currency.plus(
//                                    String.format(
//                                        Locale.US,
//                                        "%,.02f",
//                                        checkOutDataList[position].tax!!.toString().toDouble()
//                                    )
//                                )
                        /*binding.tvshippingcost.text =
                        currency.plus(
                            String.format(
                                Locale.US,
                                "%,.02f",
                                shippingCost?.toDouble()
                            )
                        )*/

//                            if (highShippingCharge != shippingCost?.toDouble()) {
//                                binding.tvshippingcost.text = "0.00" + currency
//                            } else {
//                                if (highShippingFlag == 0) {
//                                    binding.tvshippingcost.text = currency +
//                                            (String.format(
//                                                Locale.US,
//                                                "%,.02f",
//                                                shippingCost?.toDouble()
//                                            ))
//                                    highShippingFlag = 1;
//                                } else {
//                                    binding.tvshippingcost.text = currency + "0.00"
//                                }
//                            }

                        if (checkOutDataList[position].discountAmount == null) {
                            binding.tvOldPrice.text =
                                currency.plus(
                                    String.format(
                                        Locale.US,
                                        "%,.02f",
                                        0.toDouble()
                                    )
                                )
                        } else {
                            binding.tvOldPrice.text =
                                currency.plus(
                                    String.format(
                                        Locale.US,
                                        "%,.02f",
                                        checkOutDataList[position].discountAmount!!.toDouble()
                                    )
                                )
                        }
                    } else {
                        binding.tvCurrentPrice.text ="\u20B9"+
                                (String.format(
                                    Locale.US,
                                    "%,.02f",
                                    totalpriceqty
                                )) + "" + currency
//                            binding.tvtax.text =
//                                (String.format(
//                                    Locale.US,
//                                    "%,.02f",
//                                    checkOutDataList[position].tax!!.toString().toDouble()
//                                )) + "" + currency
//                            binding.tvshippingcost.text =
//                                (String.format(
//                                    Locale.US,
//                                    "%,.02f",
//                                    shippingCost?.toDouble()
//                                )) + "" + currency

                        if (checkOutDataList[position].discountAmount == null) {
                            binding.tvOldPrice.text =
                                (String.format(
                                    Locale.US,
                                    "%,.02f",
                                    0.toDouble()
                                )) + "" + currency

                        } else {
                            binding.tvOldPrice.text =
                                (String.format(
                                    Locale.US,
                                    "%,.02f",
                                    checkOutDataList[position].discountAmount!!.toDouble()
                                )) + "" + currency
                        }
                    }
//                        Glide.with(this@ActCheckout)
//                            .load(filterHttps(checkOutDataList[position].imageUrl))
//                            .into(binding.ivCartitemm)
                    // binding.detailsCard.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                    vendorid = checkOutDataList[position].vendorId.toString()
                    holder?.itemView?.setOnClickListener {
                        Log.e(
                            "product_id--->",
                            checkOutDataList[position].productId.toString()
                        )
                        val intent = Intent(this@ActReviewBooking, ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            checkOutDataList[position].productId.toString()
                        )
                        startActivity(intent)
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_chekout
                }

                override fun getBinding(parent: ViewGroup): RowChekoutBinding {
                    binding = RowChekoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }

        reviewBinding.rvReviewdata.apply {
            layoutManager =
                LinearLayoutManager(this@ActReviewBooking, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = wishListDataAdapter
        }
    }


    private fun callApiOrder(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActReviewBooking)
        val call = ApiClient.getClient.setOrderPayment(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    Common.isAddOrUpdated = true
                    if (response.body()?.status == 1) {
                        Common.showSuccessFullMsg(
                            this@ActReviewBooking,
                            response.body()?.message.toString()
                        )
                       // openActivity(ActPaymentSuccessFull::class.java)
                        ActMain.cardItemCount.value=0
                        val intent=Intent(this@ActReviewBooking,ActPaymentSuccessFull::class.java)
                        intent.putExtra("order_number",response.body()?.orderNumber.toString())
                        intent.putExtra("grand_total",hasmap["grand_total"].toString())
                        intent.putExtra("desired_date",hasmap["desired_date"].toString())
                        intent.putExtra("desired_time",hasmap["desired_time"].toString())
                        startActivity(intent)
                        finish()
                        finishAffinity()
                    } else {
                        Common.showErrorFullMsg(
                            this@ActReviewBooking,
                            response.body()?.message.toString()
                        )
                    }
                } else if(response.code() == 500){
                    Common.dismissLoadingProgress()
                    val intent=Intent(this@ActReviewBooking,ActPaymentSuccessFull::class.java)
                    intent.putExtra("order_number",response.body()?.orderNumber.toString())
                    intent.putExtra("grand_total",hasmap["grand_total"].toString())
                    intent.putExtra("desired_date",hasmap["desired_date"].toString())
                    intent.putExtra("desired_time",hasmap["desired_time"].toString())
                    startActivity(intent)
                    finish()
                    finishAffinity()
//                    Common.showErrorFullMsg(
//                        this@ActReviewBooking, "Error code 500"
//                    )
                }else {
                    Common.dismissLoadingProgress()
                    Common.getLog("orderapi", "code - " + response.code())
                    Common.getLog("orderapi", "body - " + response.body())
                    Common.alertErrorOrValidationDialog(
                        this@ActReviewBooking,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActReviewBooking,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    private fun paynow() {
        if (paymetntypeId == "1") {
            if (Common.isCheckNetwork(this@ActReviewBooking)) {
                Common.showLoadingProgress(this@ActReviewBooking)
                val hasmap = HashMap<String, String>()
                hasmap["user_id"] = SharePreference.getStringPref(this@ActReviewBooking, SharePreference.userId)!!
                hasmap["payment_type"] = "1"
                hasmap["payment_id"] = "cash on delivery"
                hasmap["grand_total"] =price.toString()
                hasmap["discount_amount"] = intent.getStringExtra("discount_amount") ?: "0"
                hasmap["coupon_name"] = intent.getStringExtra("coupon_name") ?: ""
                hasmap["order_notes"] = intent.getStringExtra("order_notes") ?: "0"
                hasmap["full_name"] = intent.getStringExtra("fname")!!
                hasmap["email"] = email
                hasmap["mobile"] = intent.getStringExtra("mobile")!!
                hasmap["landmark"] = intent.getStringExtra("landmark") ?: ""
                hasmap["street_address"] = intent.getStringExtra("street_address")!!
                hasmap["pincode"] = intent.getStringExtra("pincode")!!
                hasmap["latitude"]=latitude
                hasmap["longitude"]=longitude
                hasmap["desired_time"]=desiredTime
                hasmap["desired_date"]=desiredDate

//                hasmap["resell_margin"] = intent.getStringExtra("resell_margin")!!
//                hasmap["final_price"] = intent.getStringExtra("final_price")!!
//                hasmap["reselling_order_flag"] = intent.getStringExtra("reselling_order_flag")!!
//                hasmap["vendor_id"] = intent.getStringExtra("vendorid")!!
//                hasmap["isSchedule"]="0"

                Log.d("HadMap", hasmap.toString())

                callApiOrder(hasmap)
            } else {
                Common.alertErrorOrValidationDialog(
                    this@ActReviewBooking,
                    resources.getString(R.string.no_internet)
                )
            }
        }else if (paymetntypeId == "3" ){
            if (Common.isCheckNetwork(this@ActReviewBooking)) {
                initiatePayment(price,coupon_id,"INR")
              //  startPayment()
            } else {
                Common.alertErrorOrValidationDialog(
                    this@ActReviewBooking,
                    resources.getString(R.string.no_internet)
                )
            }
        }else{

        }
    }



    //TODO START RAZORPAY PAYMENT
    private fun startPayment(orderId: String?) {
        Common.getLog("test", price.toString())
        val activity: Activity = this
        val co = Checkout()
        try {
            co.setKeyID("rzp_live_ziQnMov0ucEqGL")
            val amount = 1!!.toDouble() * 100
            Common.getLog("test", amount.toString())
            val options = JSONObject()
            options.put("name", resources.getString(R.string.app_name))
            options.put("description", resources.getString(R.string.order_payment))
            options.put("image", logoimg)
            options.put("currency", "INR")
            options.put("order_id",orderId)
            options.put("amount", String.format(Locale.US, "%d", amount.toLong()))
            val prefill = JSONObject()
            prefill.put("email", getStringPref(this@ActReviewBooking, userEmail))
            prefill.put(
                "contact", getStringPref(
                    this@ActReviewBooking,
                    SharePreference.userMobile
                )
            )
            options.put("prefill", prefill)
            val theme = JSONObject()
            theme.put("color", "#366ed4")
            options.put("theme", theme)
            co.open(activity, options)
        } catch (e: Exception) {
            dismissLoadingProgress()
            Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }


    //TODO RAZORPAY SUCCESS METHOD
    override fun onPaymentSuccess(razorpayPaymentId: String?,paymentData: PaymentData) {
        try {
        /*    Common.showLoadingProgress(this@ActReviewBooking)
            val hasmap = HashMap<String, String>()
            hasmap["user_id"] =
                SharePreference.getStringPref(this@ActReviewBooking, SharePreference.userId)!!
            hasmap["payment_type"] = "1"
            hasmap["payment_id"] = "cash on delivery"
            hasmap["grand_total"] =price.toString()
            hasmap["discount_amount"] = intent.getStringExtra("discount_amount") ?: "0"
            hasmap["coupon_name"] = intent.getStringExtra("coupon_name") ?: ""
            hasmap["order_notes"] = intent.getStringExtra("order_notes") ?: "0"
            hasmap["full_name"] = intent.getStringExtra("fname")!!
            hasmap["email"] = email
            hasmap["mobile"] = intent.getStringExtra("mobile")!!
            hasmap["landmark"] = intent.getStringExtra("landmark") ?: ""
            hasmap["street_address"] = intent.getStringExtra("street_address")!!
            hasmap["pincode"] = intent.getStringExtra("pincode")!!
            hasmap["latitude"]=latitude
            hasmap["longitude"]=longitude
            hasmap["desired_time"]=desiredTime
            hasmap["desired_date"]=desiredDate

            Log.d("HadMap", hasmap.toString())

            callApiOrder(hasmap) */

            val hasmap = HashMap<String, String>()
            hasmap["razorpay_order_id"]   = paymentData.orderId
            hasmap["razorpay_payment_id"] = razorpayPaymentId.toString()
            hasmap["razorpay_signature"]  = paymentData.signature
            verifyPayment(hasmap)

        } catch (e: Exception) {
            Log.e("Exception", "Exception in onPaymentSuccess", e)
            Common.getToast(this@ActReviewBooking,"exception")
        }
    }

    override fun onPaymentError(p0: Int, p1: String?,paymentData: PaymentData) {
        Common.showErrorFullMsg(this@ActReviewBooking,"Payment Failed")
        Common.getToast(this@ActReviewBooking,"failed")
    }


    fun verifyPayment(hasmap: HashMap<String, String>) {
        Common.getToast(this@ActReviewBooking,"going for verify payment")
        Common.showLoadingProgress(this@ActReviewBooking)
       val call = ApiClient.getClient.verifyPayment(hasmap)
        call.enqueue(object : Callback<PaymentSingleResponse>{
            override fun onResponse(call: Call<PaymentSingleResponse>, response: Response<PaymentSingleResponse>) {
                if(response.isSuccessful){
                    val restResponse = response.body()
                    if (restResponse!=null){
                        if (restResponse.status==1){
                            Common.dismissLoadingProgress()
                            showDialog(hasmap,restResponse.message.toString())
                        }else{
                            Common.dismissLoadingProgress()
                            Common.getToast(this@ActReviewBooking,"1")
                        }
                    }else{
                        Common.dismissLoadingProgress()
                        Common.getToast(this@ActReviewBooking,"2")
                    }
                }else{
                    Common.dismissLoadingProgress()
                    Common.getToast(this@ActReviewBooking,"3")
                }
            }

            override fun onFailure(call: Call<PaymentSingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.getToast(this@ActReviewBooking,"4")
            }

        })
    }

    private fun showDialog(hasmap: HashMap<String, String>,message : String) {
        val dialog = Dialog(this@ActReviewBooking)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.payment_verifying_dailog)

        val status = dialog.findViewById(R.id.tv_status) as TextView
        val orderId = dialog.findViewById(R.id.tv_orderId) as TextView
        val paymentId = dialog.findViewById(R.id.tv_paymentId) as TextView
        val yesBtn = dialog.findViewById(R.id.btn_ok) as TextView

        status.text = message + hasmap["razorpay_order_id"]
        orderId.text = hasmap["razorpay_order_id"]
        paymentId.text = hasmap["razorpay_payment_id"]

        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }


    private fun initiatePayment(price: Double, coupon_id: String, currency: String) {
        Common.showLoadingProgress(this@ActReviewBooking)
         val hashMap = HashMap<String,String>()
        hashMap["amount"] = price.toString()
       // hashMap["coupon_id"] = coupon_id
        hashMap["currency"] = currency

        val call = ApiClient.getClient.initiatePayment(hashMap)
        call.enqueue(object : Callback<InitiatePaymentResponse>{
            override fun onResponse(call: Call<InitiatePaymentResponse>, response: Response<InitiatePaymentResponse>) {
                if (response.isSuccessful){
                    val restResponse = response.body()
                    if (restResponse!=null){
                        if (restResponse.status==1){
                            Common.dismissLoadingProgress()
                            startPayment(restResponse.data?.orderId)
                        }else{
                            Common.dismissLoadingProgress()
                            Common.alertErrorOrValidationDialog(this@ActReviewBooking,"Payment failed to Intiate, please try again after some time.")
                        }
                    }else{
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(this@ActReviewBooking,"Payment failed to Intiate, please try again after some time.")
                    }
                }else{
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(this@ActReviewBooking,"Payment failed to Intiate, please try again after some time.")
                }
            }

            override fun onFailure(call: Call<InitiatePaymentResponse>, t: Throwable) {
               Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(this@ActReviewBooking,"Payment failed to Intiate, please try again after some time.")
            }

        })
    }

}

