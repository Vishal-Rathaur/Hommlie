package com.hommlie.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.databinding.ActCheckoutBinding
import com.hommlie.user.databinding.RowChekoutBinding
import com.hommlie.user.model.CheckOutData
import com.hommlie.user.model.CheckOutDataItem
import com.hommlie.user.model.GetCheckOutResponse
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.alertErrorOrValidationDialog
import com.hommlie.user.utils.Common.dismissLoadingProgress
import com.hommlie.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class ActCheckout : BaseActivity() {
    private lateinit var checkoutBinding: ActCheckoutBinding

    private lateinit var lottieAnimationView: LottieAnimationView

    private var mediaPlayer: MediaPlayer? = null

    private var checkOutDataList: ArrayList<CheckOutDataItem>? = null
    var currency: String = ""
    var currencyPosition: String = ""
    var subtotal: Int = 0
    var tax: Int = 0
    var checkoutprice: Int = 0
    var discountsum: String = ""
    var price: Double = 0.0
    var fname: String = ""
    var landmark: String = ""
    var pincode: String = ""
    var lname: String = ""
    var email: String = ""
    var mobile: String = ""
    var streetAddress: String = ""
    var coupon_name: String = ""
    var coupon_id : String = ""
    var marginEarned: Double = 0.0
    var vendorid: String = ""
    var value: Double = 0.0
    var sum: Double = 0.0
    var finalPrice: Double = 0.0
    var resellingOrderFlag = "no"
    var paymentType:String=""
    var paymetntypeId:String=""
    var couponAmount:Double=0.0
    var desiredDate:String=""
    var desiredTime:String=""
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )
    var highShippingCharge = 0.0;
    var highShippingFlag = 0;
    var orginalPrice=0.0
    var latitude =""
    var longitude=""

    override fun setLayout(): View = checkoutBinding.root

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun initView() {
        checkoutBinding = ActCheckoutBinding.inflate(layoutInflater)

        lottieAnimationView = checkoutBinding.lottieAnimationView

        mediaPlayer = MediaPlayer.create(this, R.raw.booking_succesfull)

//        checkoutBinding.tvapplycoupon.visibility = View.GONE
//        checkoutBinding.clCoupon.visibility = View.GONE
//        currency = SharePreference.getStringPref(this@ActCheckout, SharePreference.Currency)!!
//        currencyPosition =
//            SharePreference.getStringPref(this@ActCheckout, SharePreference.CurrencyPosition)!!
//        checkoutBinding.btnaddress.setOnClickListener { openActivity(ActAddress::class.java) }
        checkoutBinding.ivBack.setOnClickListener { finish() }
        if (SharePreference.getBooleanPref(this@ActCheckout, SharePreference.isLogin)) {
            if (Common.isCheckNetwork(this@ActCheckout)) {
                val map = HashMap<String, String>()
                map["user_id"] = SharePreference.getStringPref(
                    this@ActCheckout,
                    SharePreference.userId
                )!!
                map["coupon_name"]=""
                callApiCheckoutitem(map)
            } else {
                alertErrorOrValidationDialog(
                    this@ActCheckout,
                    resources.getString(R.string.no_internet)
                )
            }
        } else {
            openActivity(ActSignUp::class.java)
            finish()
            finishAffinity()
        }
        checkoutBinding.addressCard.setOnClickListener {
            val intent = Intent(this@ActCheckout, ActAddress::class.java)
            intent.putExtra("isComeFromSelectAddress", true)
            addressDataSet.launch(intent)
        }
        checkoutBinding.accountCard.setOnClickListener {
            val intent = Intent(this@ActCheckout, ActAddress::class.java)
            intent.putExtra("isComeFromSelectAddress", true)
            addressDataSet.launch(intent)
        }
        checkoutBinding.paymentmethodCard.setOnClickListener {
            val intent = Intent(this@ActCheckout, ActPaymentMethod::class.java)
            intent.putExtra("isComeFromSelectPaymentMethod", true)
            paymentTypeDataSet.launch(intent)
        }
        checkoutBinding.timeslotCard.setOnClickListener {
            val intent = Intent(this@ActCheckout, ActSelectTimeSlot::class.java)
            intent.putExtra("isComeFromSelectDateandTimeMethod", true)
            dateAndtimeDataSet.launch(intent)
        }
        checkoutBinding.tvViewAllCoupons.setOnClickListener {
            val intent = Intent(this@ActCheckout, ActApplyCoupon::class.java)
            intent.putExtra("isComeFromSelectCouponCode", true)
            couponDataSet.launch(intent)
        }
        checkoutBinding.unlockcouponCard.setOnClickListener {
            val intent = Intent(this@ActCheckout, ActApplyCoupon::class.java)
            intent.putExtra("isComeFromSelectCouponCode", true)
            couponDataSet.launch(intent)
        }
        checkoutBinding.tvRemoveCoupon.setOnClickListener{
            checkoutBinding.tvExplorenow.text=""
            checkoutBinding.tvApplied.text=""
            couponAmount=0.0

        }

        //        checkoutBinding.clhaveacoupon.setOnClickListener {
//            if (!SharePreference.getBooleanPref(this@ActCheckout, SharePreference.isCoupon)) {
//                SharePreference.setBooleanPref(this@ActCheckout, SharePreference.isCoupon, true)
//                checkoutBinding.tvapplycoupon.visibility = View.GONE
//                checkoutBinding.clCoupon.visibility = View.GONE
//            } else {
//                checkoutBinding.tvapplycoupon.visibility = View.VISIBLE
//                checkoutBinding.clCoupon.visibility = View.VISIBLE
//            }
//        }
//        //TODO RESELLER BUTTON
//        checkoutBinding.btnResellerMargin.setOnClickListener {
//            checkoutBinding.llResellLayout.visibility = View.VISIBLE
//
//            checkoutBinding.tvMarginEarned.visibility = View.VISIBLE
//            checkoutBinding.tvMarginEarnedTitle.visibility = View.VISIBLE
//            checkoutBinding.tvFinalPrice.visibility = View.VISIBLE
//            checkoutBinding.viewBottomOfMarginEarned.visibility = View.VISIBLE
//            checkoutBinding.tvFinalPriceTitle.visibility = View.VISIBLE
//
//
//            checkoutBinding.tvMarginEarned.text =  marginEarned.toString()
//            finalPrice = price + marginEarned
//            checkoutBinding.tvFinalPrice.text =  finalPrice.toString()
//
//            Common.getToast(this@ActCheckout,"Reselling price applied successfully " + marginEarned)
//        }
//        //TODO RELLER NO
//        checkoutBinding.btnNo.setOnClickListener {
//            resellingOrderFlag = "no"
//            checkoutBinding.llResellLayout.visibility = View.GONE
//            checkoutBinding.btnNo.setBackgroundColor(Color.parseColor("#7F0061"))
//            checkoutBinding.btnYes.setBackgroundColor(Color.parseColor("#bcbcbc"))
//
//            checkoutBinding.tvMarginEarned.visibility = View.GONE
//            checkoutBinding.tvMarginEarnedTitle.visibility = View.GONE
//            checkoutBinding.tvFinalPrice.visibility = View.GONE
//            checkoutBinding.viewBottomOfMarginEarned.visibility = View.GONE
//            checkoutBinding.tvFinalPriceTitle.visibility = View.GONE
//
//            checkoutBinding.edtresellprice.text.clear()
//            checkoutBinding.tvyourmargin.text = "Your Margin: "
//
//        }
//        //TODO  RELLER YES
//        checkoutBinding.btnYes.setOnClickListener {
//            resellingOrderFlag = "yes"
//            checkoutBinding.llResellLayout.visibility = View.VISIBLE
//            checkoutBinding.btnYes.setBackgroundColor(Color.parseColor("#7F0061"))
//            checkoutBinding.btnNo.setBackgroundColor(Color.parseColor("#bcbcbc"))
//        }
//        //TODO APPLY COUPON
//        checkoutBinding.btnapplycoupon.setOnClickListener {
//            if (checkoutBinding.btnapplycoupon.text.toString() == resources.getString(R.string.apply)
//            ) {
//                if (checkoutBinding.edtcouponcode.text.isNotEmpty()) {
//                    val map = HashMap<String, String>()
//                    map["user_id"] = SharePreference.getStringPref(
//                        this@ActCheckout,
//                        SharePreference.userId
//                    )!!
//                    map["coupon_name"] = checkoutBinding.edtcouponcode.text.toString()
//                    callApiCheckoutitem(map)
//                    checkoutBinding.edtcouponcode.requestFocus()
//                    checkoutBinding.btnapplycoupon.setTextColor(getColor(R.color.red))
//                    checkoutBinding.btnapplycoupon.text = resources.getString(R.string.remove)
//                    coupon_name = checkoutBinding.edtcouponcode.text.toString()
//                } else {
//                    alertErrorOrValidationDialog(
//                        this@ActCheckout,
//                        resources.getString(R.string.coupan_code_validation)
//                    )
//                }
//            } else if (checkoutBinding.btnapplycoupon.text.toString() == resources.getString(R.string.remove)) {
//                checkoutBinding.edtcouponcode.setText("")
//                checkoutBinding.btnapplycoupon.text = resources.getString(R.string.apply)
//                checkoutBinding.tvdiscounttotal.text = currency + "0.00"
//                checkoutBinding.tvtotal.text = price.toString()
//                checkoutBinding.btnapplycoupon.setTextColor(getColor(R.color.Blackcolor))
//                val map = HashMap<String, String>()
//                map["user_id"] = SharePreference.getStringPref(
//                    this@ActCheckout,
//                    SharePreference.userId
//                )!!
//                callApiCheckoutitem(map)
//                coupon_name = ""
//            }
//        }
//
//        checkoutBinding.edtresellprice.addTextChangedListener(textWatcher)
//        checkoutBinding.tvtotal.addTextChangedListener(textWatcher)
//
//    }
//
//    private val textWatcher = object : TextWatcher {
//        override fun afterTextChanged(s: Editable?) {
//           // val number: Float = java.lang.Float.valueOf(edt.getText().toString())
//         // checkoutBinding.tvtotal.text = s
//        }
//        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//        }
//        @SuppressLint("SetTextI18n")
//        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//          //  Common.getToast(this@ActCheckout,"ontextchanged")
//         // checkoutBinding.tvtotal.text = s as Editable?
//
//            val text: String = checkoutBinding.edtresellprice.getText().toString()
//            if (!text.isEmpty()) try {
//                value = text.toDouble()
//
//                sum = price + value
//                Common.getLog("aaaaaa","" + value )
//
//                marginEarned =  value - price
//                Common.getLog("aaaaaa","retrn value" + sum.absoluteValue )
//
//                if(marginEarned.absoluteValue == 0.0 ){
//                    checkoutBinding.tvyourmargin.text = "Your margin: " + marginEarned.toString()
//                    checkoutBinding.tvyourmargin.setTextColor(Color.parseColor("#ff0000"))
//                    //price = resellprice.toString().toDouble()
//                } else if(marginEarned < 0.0){
//                    checkoutBinding.tvyourmargin.text = "Your margin: " + marginEarned.toString()
//                    checkoutBinding.tvyourmargin.setTextColor(Color.parseColor("#ff0000"))
//                   // price = resellprice.toString().toDouble()
//                }else{
//                    checkoutBinding.tvyourmargin.text = "Your margin: " + marginEarned.toString()
//                    checkoutBinding.tvyourmargin.setTextColor(Color.parseColor("#34c759"))
//
//                }
//
//                // it means it is double
//            } catch (e1: Exception) {
//                // this means it is not double
//                e1.printStackTrace()
//            }
//        }
    }

        //TODO AP CHECK OUT API
        private fun callApiCheckoutitem(map: HashMap<String, String>) {
            Common.showLoadingProgress(this@ActCheckout)
            val call = ApiClient.getClient.getCheckOut(map)
            call.enqueue(object : Callback<GetCheckOutResponse> {
                @SuppressLint("SetTextI18n")
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onResponse(
                    call: Call<GetCheckOutResponse>,
                    response: Response<GetCheckOutResponse>
                ) {
                    Log.e("code", response.code().toString())
                    Log.e("status", response.body().toString())
                    if (response.code() == 200) {
                        val restResponce = response.body()!!
                        if (restResponce.status == 1) {
                            dismissLoadingProgress()
                            checkOutDataList = restResponce.checkoutdata

                            checkOutDataList?.forEach {
                                if (it.shippingCost?.toDouble()!! > highShippingCharge) {
                                    highShippingCharge = it.shippingCost.toDouble();
                                }
                            }

                            checkOutDataList?.let {
                                loadCheckOutItem(it)
                            }
                            restResponce.data?.let {
                                loadCheckOutDetails(it)
                            }
                        } else if (restResponce.status == 0) {
                            dismissLoadingProgress()
                           // checkoutBinding.appliedcouponCard.text = resources.getString(R.string.apply)checkoutBinding.edtcouponcode.setText("")
                            checkoutBinding.tvRemoveCoupon.setTextColor(getColor(R.color.color_FF3269))
                            alertErrorOrValidationDialog(
                                this@ActCheckout,
                                restResponce.message.toString()
                            )
                        }
                    }
                }

                override fun onFailure(call: Call<GetCheckOutResponse>, t: Throwable) {
                    dismissLoadingProgress()
                    Log.e("error", t.message.toString())
                    alertErrorOrValidationDialog(
                        this@ActCheckout,
                        resources.getString(R.string.error_msg)
                    )
                }
            })
        }

        //TODO SET CHECKOUT DATA
        private fun loadCheckOutItem(checkOutDataList: ArrayList<CheckOutDataItem>) {
            discountsum = if (checkOutDataList == null) {
                "0"
            } else {
                (checkOutDataList.sumOf { it.discountAmount?.toDouble() ?: 0.0 }).toString()
            }

            checkoutBinding.btnProccedtoPayment.setOnClickListener {
                if (checkoutBinding.tvAddress.text.toString().isNullOrEmpty()) {
                    Common.showErrorFullMsg(
                        this@ActCheckout,
                        resources.getString(R.string.select_your_address)
                    )
                }else if (checkoutBinding.tvSelectedDateTime.text.toString().isNullOrEmpty()){
                    Common.showErrorFullMsg(
                        this@ActCheckout,"Please select desized order date")
                }else if (checkoutBinding.tvSelectedPaymentmethod.text.toString().isNullOrEmpty()){
                    Common.showErrorFullMsg(
                        this@ActCheckout,"Please select payment method")
                }
                else {
                    val intent = Intent(this@ActCheckout, ActReviewBooking::class.java)
                    intent.putExtra("email", email)
                    intent.putExtra("fname", fname)
                 //   intent.putExtra("lname", lname)
                    intent.putExtra("landmark", landmark)
                    intent.putExtra("mobile", mobile)
                 //   intent.putExtra("order_notes", checkoutBinding.edtordernote.text.toString())
                    intent.putExtra("pincode", pincode)
                    intent.putExtra("street_address", streetAddress)
                    intent.putExtra("coupon_name", coupon_name)
                    intent.putExtra("coupon_id",coupon_id)
                    intent.putExtra("resell_margin", marginEarned.toString())
                    intent.putExtra("final_price", finalPrice.toString())
                    intent.putExtra("reselling_order_flag", resellingOrderFlag)
                    intent.putExtra("discount_amount", couponAmount.toString())
                    intent.putExtra("grand_total", orginalPrice.toDouble().toString())
                    intent.putExtra("vendorid", vendorid)
                    intent.putExtra("size",checkoutBinding.tvSize.text.toString())
                    intent.putExtra("reviewData",checkOutDataList)
                    intent.putExtra("paymentType",paymentType)
                    intent.putExtra("payment_typeId",paymetntypeId)
                    intent.putExtra("date",checkoutBinding.tvSelectedDateTime.text.toString())
                    intent.putExtra("tax",tax.toString())
                    intent.putExtra("subTotal",subtotal.toString())
                    intent.putExtra("desiredDate",desiredDate)
                    intent.putExtra("desiredTime",desiredTime)
                    intent.putExtra("latitude",latitude)
                    intent.putExtra("longitude",longitude)
                    Log.d("qqqqqq", "resell flag " + resellingOrderFlag.toString())
                    Log.d("qqqqqq", "total " + price.toString())
                    Log.d("qqqqqq", "margin earned " + marginEarned.toString())
                    Log.d("qqqqqq", "final price " + finalPrice.toString())

                 //   Common.getToast(this@ActCheckout,)

                    startActivity(intent)
                }
            }

            lateinit var binding: RowChekoutBinding
            val wishListDataAdapter =
                object : BaseAdaptor<CheckOutDataItem, RowChekoutBinding>(
                    this@ActCheckout,
                    checkOutDataList
                ) {
                    @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                    override fun onBindData(
                        holder: RecyclerView.ViewHolder?,
                        `val`: CheckOutDataItem,
                        position: Int
                    ) {
                        binding.tvTitle.text = checkOutDataList[position].productName
                        checkoutBinding.tvSize.text=checkOutDataList[position].variation
                        binding.tvOldPrice.text=checkOutDataList[position].variation

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
                           //     binding.tvOldPrice.text = currency.plus(String.format(Locale.US, "%,.02f", 0.toDouble()))
                            } else {
                              //  binding.tvOldPrice.text = currency.plus(String.format(Locale.US, "%,.02f", checkOutDataList[position].discountAmount!!.toDouble()))
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
                         //       binding.tvOldPrice.text = (String.format(Locale.US, "%,.02f", 0.toDouble()) + "" + currency

                            } else {
                           //     binding.tvOldPrice.text = (String.format(Locale.US, "%,.02f", checkOutDataList[position].discountAmount!!.toDouble())) + "" + currency
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
                            val intent = Intent(this@ActCheckout, ActProductDetails::class.java)
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
            checkoutBinding.rvCheckoutdata.apply {
                layoutManager =
                    LinearLayoutManager(this@ActCheckout, LinearLayoutManager.VERTICAL, false)
                itemAnimator = DefaultItemAnimator()
                adapter = wishListDataAdapter
            }
        }

        //TODO SET CHECKOUT OTHER DETAILS
        @SuppressLint("SetTextI18n")
        private fun loadCheckOutDetails(checkOutList: CheckOutData) {
            subtotal = checkOutList.subtotal!!
            tax = checkOutList.tax?.toDouble()?.toInt()!!
            val disc = Math.round(discountsum.toDouble()).toInt()

            val shippingCost = if (checkOutList.shippingCost == "Free Shipping") {
                "0.0"
            } else {
                checkOutList.shippingCost
            }
            price = checkOutList.subtotal + checkOutList.tax.toString()
                .toDouble() + highShippingCharge
            checkoutprice = checkOutList.subtotal

            if (currencyPosition == "left") {
                checkoutBinding.tvPriceValue.text ="\u20b9 "+ subtotal.toDouble().toString()
                checkoutBinding.tvTaxValue.text ="\u20b9 "+ String.format("%.2f",checkOutList.tax.toDouble())
                checkoutBinding.tvDeliverychargeValue.text = "\u20b9 "+ currency + "" + highShippingCharge.toString()
                checkoutBinding.tvTotalValue.text ="\u20b9 "+ String.format("%.2f", price)
                orginalPrice=price
            } else {
                checkoutBinding.tvPriceValue.text ="\u20b9 "+ subtotal.toDouble().toString()
                checkoutBinding.tvTaxValue.text ="\u20b9 "+ String.format("%.2f",checkOutList.tax.toDouble())
                checkoutBinding.tvDeliverychargeValue.text ="\u20b9 "+ highShippingCharge.toString()
                checkoutBinding.tvTotalValue.text ="\u20b9 "+ String.format("%.2f", price)
                orginalPrice=price
            }
        }


        //TODO SET ADDRESS TO CHECK OUT PAGE
        @SuppressLint("SetTextI18n")
        val addressDataSet =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == 500) {
//                    checkoutBinding.tvUserName.visibility = View.VISIBLE
//                    checkoutBinding.tvUserphone.visibility = View.VISIBLE
//                    checkoutBinding.tvareaaddress.visibility = View.VISIBLE
                    checkoutBinding.tvEmail.visibility = View.VISIBLE
                    checkoutBinding.tvAddress.visibility = View.VISIBLE
                    checkoutBinding.tvName.visibility = View.VISIBLE
                    checkoutBinding.tvMobile.visibility = View.VISIBLE
                    checkoutBinding.tvName.text =
                        result.data?.getStringExtra("FirstName")
                    checkoutBinding.tvAddress.text =
                        result.data?.getStringExtra("StreetAddress") + " " + result.data?.getStringExtra(
                            "Landmark"
                        ) + "-" + result.data?.getStringExtra(
                            "Pincode"
                        )
                    checkoutBinding.tvMobile.text = "Mob : " + result.data?.getStringExtra("Mobile")
                    checkoutBinding.tvEmail.text = "Email : " + result.data?.getStringExtra("Email")
                    email = result.data?.getStringExtra("Email").toString()
                    fname = result.data?.getStringExtra("FirstName").toString()
                   // lname = result.data?.getStringExtra("LastName").toString()
                    mobile = result.data?.getStringExtra("Mobile").toString()
                    landmark = result.data?.getStringExtra("Landmark").toString()
                    streetAddress = result.data?.getStringExtra("StreetAddress").toString()
                    latitude = result.data?.getStringExtra("latitude").toString()
                    longitude =result.data?.getStringExtra("longitude").toString()
                    pincode = result.data?.getStringExtra("Pincode").toString()
                } else {
                    checkoutBinding.tvName.visibility = View.GONE
                    checkoutBinding.tvMobile.visibility = View.GONE
                    checkoutBinding.tvEmail.visibility = View.GONE
                    checkoutBinding.tvAddress.visibility = View.GONE
//                    checkoutBinding.clAddressselect.visibility = View.GONE
//                    checkoutBinding.btnaddress.visibility = View.VISIBLE
//                    checkoutBinding.tveditaddress.visibility = View.GONE
                }
            }



    //TODO SET PaymentType TO CHECK OUT PAGE
    @SuppressLint("SetTextI18n")
    val paymentTypeDataSet =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 501) {
//                    checkoutBinding.tvUserName.visibility = View.VISIBLE
//                    checkoutBinding.tvUserphone.visibility = View.VISIBLE
//                    checkoutBinding.tvareaaddress.visibility = View.VISIBLE
//                    checkoutBinding.tvusermailid.visibility = View.VISIBLE
//                    checkoutBinding.clAddressselect.visibility = View.VISIBLE
//                    checkoutBinding.btnaddress.visibility = View.GONE
//                    checkoutBinding.tvAddress.visibility = View.VISIBLE
                checkoutBinding.tvSelectedPaymentmethod.text =
                    result.data?.getStringExtra("paymentType")
                paymentType = result.data?.getStringExtra("paymentType").toString()
                paymetntypeId= result.data?.getStringExtra("payment_typeId").toString()
            } else {
//                    checkoutBinding.tvUserName.visibility = View.GONE
//                    checkoutBinding.tvUserphone.visibility = View.GONE
//                    checkoutBinding.tvareaaddress.visibility = View.GONE
//                    checkoutBinding.tvusermailid.visibility = View.GONE
//                    checkoutBinding.clAddressselect.visibility = View.GONE
//                    checkoutBinding.btnaddress.visibility = View.VISIBLE
//                    checkoutBinding.tveditaddress.visibility = View.GONE
            }
        }

    //TODO SET DateandTime TO CHECK OUT PAGE
    @SuppressLint("SetTextI18n")
    val dateAndtimeDataSet =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 502) {
//                    checkoutBinding.tvUserName.visibility = View.VISIBLE
//                    checkoutBinding.tvUserphone.visibility = View.VISIBLE
                checkoutBinding.tvSelectedDateTime.text =
                    result.data?.getStringExtra("date")+"  "+result.data?.getStringExtra("time")
                desiredDate= result.data?.getStringExtra("date")!!.substring(3)
                    desiredTime=result.data?.getStringExtra("time").toString()
            //    paymentType = result.data?.getStringExtra("paymentType").toString()
            } else {
//                    checkoutBinding.tvUserName.visibility = View.GONE
//                    checkoutBinding.tvUserphone.visibility = View.GONE

            }
        }


    //TODO SET coupon TO CHECK OUT PAGE
    @SuppressLint("SetTextI18n")
    val couponDataSet =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 503) {
//                    checkoutBinding.tvUserName.visibility = View.VISIBLE
//                    checkoutBinding.tvUserphone.visibility = View.VISIBLE
                checkoutBinding.tvApplied.text =
                    result.data?.getStringExtra("couponcode")
                checkoutBinding.tvExplorenow.text =
                    result.data?.getStringExtra("couponcode")
                val temp = result.data?.getStringExtra("couponamount")?.toDouble() ?:0.0
                couponAmount = (subtotal.toDouble()*temp)/100
                coupon_name = result.data?.getStringExtra("couponcode").toString()
                coupon_id = result.data?.getStringExtra("coupon_id").toString()
                checkoutBinding.tvRemoveCoupon.setTextColor(getColor(R.color.color_FF3269) )
                //price=price-couponAmount
                orginalPrice=price
                orginalPrice=orginalPrice-couponAmount
                checkoutBinding.tvTotalValue.text=  "\u20b9 "+ String.format("%.2f", orginalPrice)
                checkoutBinding.tvDeliverychargeValue.text= String.format("%.2f", couponAmount)
//                val dialogFragment = HalfScreenDialogFragment()
//                dialogFragment.show(supportFragmentManager, "HalfScreenDialogFragment")
//                dialogFragment.isCancelable=false
                lottieAnimationView.visibility=View.VISIBLE
                lottieAnimationView.playAnimation()
                if (mediaPlayer != null) {
                    mediaPlayer?.start()
                } else {
                    Log.e("MediaPlayer", "MediaPlayer is null. Cannot start playback.")
                }

            } else {
//                    checkoutBinding.tvUserName.visibility = View.GONE
//                    checkoutBinding.tvUserphone.visibility = View.GONE

            }
        }


    fun exampleFunction(data: String) {
        lottieAnimationView.visibility=View.VISIBLE
        lottieAnimationView.playAnimation()
      //  Toast.makeText(this, "Data from Fragment: $data", Toast.LENGTH_SHORT).show()
    }



        override fun onResume() {
            super.onResume()
            Common.getCurrentLanguage(this@ActCheckout, false)
        }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

}