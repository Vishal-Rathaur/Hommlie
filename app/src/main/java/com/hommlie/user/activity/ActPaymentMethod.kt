package com.hommlie.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flutterwave.raveandroid.RavePayActivity
import com.flutterwave.raveandroid.RaveUiManager
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants
import com.google.gson.Gson
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.PaymentUrlResponse
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.databinding.ActPaymentMethodBinding
import com.hommlie.user.databinding.RowPaymentBinding
import com.hommlie.user.model.*
import com.hommlie.user.utils.CallBackSuccess
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.alertErrorOrValidationDialog
import com.hommlie.user.utils.Common.dismissLoadingProgress
import com.hommlie.user.utils.Common.getToast
import com.hommlie.user.utils.Common.isCheckNetwork
import com.hommlie.user.utils.Common.showErrorFullMsg
import com.hommlie.user.utils.Common.showLoadingProgress
import com.hommlie.user.utils.Common.showSuccessFullMsg
import com.hommlie.user.utils.NewStripeDataController
import com.hommlie.user.utils.SharePreference
import com.hommlie.user.utils.SharePreference.Companion.getStringPref
import com.hommlie.user.utils.SharePreference.Companion.userEmail
import com.hommlie.user.utils.SharePreference.Companion.userId
import com.hommlie.user.utils.SharePreference.Companion.userName
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import com.stripe.android.model.Token
import com.hommlie.user.utils.Common.getLog
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.round
import kotlin.math.roundToInt


class ActPaymentMethod : BaseActivity(), PaymentResultListener, CallBackSuccess {
    private lateinit var actPaymentMethodBinding: ActPaymentMethodBinding
    private var paymentDataList: ArrayList<PaymentlistItem>? = null
    var currency: String = ""
    var currencyPosition: String = ""
    private var strGetData = ""
    var walletAmount: String = ""
    var logoimg = ""
    private var strRezorPayKey = ""
    var newStripeDataController: NewStripeDataController? = null
    var callBackSuccess: CallBackSuccess? = null
    var price: Double = 0.0
    var type: String = ""
    var fname: String = ""
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
    var flutterWaveKey: String = ""
    var stripeKey: String = ""
    var encryptionKey: String = ""
    var payStackKey: String = ""
    var doubleBackToExitPressedOnce = false
    override fun setLayout(): View = actPaymentMethodBinding.root

    override fun initView() {
        actPaymentMethodBinding = ActPaymentMethodBinding.inflate(layoutInflater)
        callBackSuccess = this@ActPaymentMethod
        Checkout.preload(this@ActPaymentMethod)
        actPaymentMethodBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        currency =
            getStringPref(this@ActPaymentMethod, SharePreference.Currency)!!
        currencyPosition =
            getStringPref(
                this@ActPaymentMethod,
                SharePreference.CurrencyPosition
            )!!

        if (isCheckNetwork(this@ActPaymentMethod)) {
            if (SharePreference.getBooleanPref(this@ActPaymentMethod, SharePreference.isLogin)) {
                callApiPaymentDetail()
            } else {
                openActivity(ActSignUp::class.java)
                this.finish()
            }
        } else {
            alertErrorOrValidationDialog(
                this@ActPaymentMethod,
                resources.getString(R.string.no_internet)
            )
        }
        actPaymentMethodBinding.btnProccedtoPayment.setOnClickListener {
            paynow()
        }
//        fname = intent.getStringExtra("fname")!! + " " + intent.getStringExtra("lname")
//        landmark = intent.getStringExtra("landmark") ?: ""
//        mobile = intent.getStringExtra("mobile")!!
//        orderNote = intent.getStringExtra("order_notes") ?: "0"
//        price = intent.getStringExtra("grand_total")!!.toDouble()
//        pincode = intent.getStringExtra("pincode")!!
//        streetAddress = intent.getStringExtra("street_address")!!
//        coupon_name = intent.getStringExtra("coupon_name") ?: "0"
//        discountAmount = intent.getStringExtra("discount_amount") ?: "0.0"
//        resellMargin = intent.getStringExtra("resell_margin")!!
//        finalPrice = intent.getStringExtra("final_price")!!
//        resellOrderFlag = intent.getStringExtra("reselling_order_flag")!!
//        vendorid = intent.getStringExtra("vendorid")!!
    }

    //TODO CALL PAYMENT DETAILS API
    private fun callApiPaymentDetail() {
        showLoadingProgress(this@ActPaymentMethod)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            getStringPref(this@ActPaymentMethod, userId)!!
        val call = ApiClient.getClient.getPaymentList(hasmap)
        call.enqueue(object : Callback<PaymentListResponse> {
            override fun onResponse(
                call: Call<PaymentListResponse>,
                response: Response<PaymentListResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        dismissLoadingProgress()
                        actPaymentMethodBinding.rvpaymentlist.visibility = View.VISIBLE
                        paymentDataList = restResponce.paymentlist
                        walletAmount = restResponce.walletamount.toString()
                        loadPaymentDetails(paymentDataList!!, walletAmount)
                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        actPaymentMethodBinding.rvpaymentlist.visibility = GONE
                        alertErrorOrValidationDialog(
                            this@ActPaymentMethod,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<PaymentListResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActPaymentMethod,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO PAYMENT DETAILS DATA SET
    private fun loadPaymentDetails(
        paymentDataList: ArrayList<PaymentlistItem>,
        walletamount: String?
    ) {
        lateinit var binding: RowPaymentBinding
        val viewAllDataAdapter =
            object : BaseAdaptor<PaymentlistItem, RowPaymentBinding>(
                this@ActPaymentMethod,
                paymentDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: PaymentlistItem,
                    position: Int
                ) {
                    holder?.itemView?.setOnClickListener {
                        for (item in paymentDataList) {
                            item.isSelect = false
                        }
                        strGetData = paymentDataList[position].paymentName.toString()
                        Log.e("paymentname", strGetData)

                        paymentDataList[position].isSelect = true
                        Handler(Looper.getMainLooper()).postDelayed({
                            loadPaymentDetails(
                                paymentDataList, walletamount
                            )
                        }, 10L)
                        val isComeFromSelectAddress =
                            intent.getBooleanExtra("isComeFromSelectPaymentMethod", false)

                        if (isComeFromSelectAddress) {
                            val intent = Intent(this@ActPaymentMethod, ActCheckout::class.java)
                            intent.putExtra("paymentType", paymentDataList[position].paymentName)
                            intent.putExtra("payment_typeId",paymentDataList[position].id.toString())
                            setResult(501, intent)
                            finish()
                        }
                    }
                    binding.tvpaymenttype.text = paymentDataList[position].paymentName
                    if (paymentDataList[position].isSelect == true) {
                        binding.ivCheck.visibility = View.VISIBLE
                    } else {
                        binding.ivCheck.visibility = View.GONE
                    }

                    when (paymentDataList[position].paymentName) {
                        "Wallet" -> {
                            binding.ivPayment.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.ic_wallet,
                                    null
                                )!!
                            )

                            if (walletAmount.isEmpty()) {
                                if (currencyPosition == "left") {
                                    binding.tvpaymenttype.text =
                                        paymentDataList[position].paymentName + "(" +
                                                currency.plus(
                                                    String.format(
                                                        Locale.US,
                                                        "%,.2f",
                                                        0.toDouble()
                                                    )
                                                ) + ")"
                                } else {
                                    binding.tvpaymenttype.text =
                                        paymentDataList[position].paymentName + "(" + String.format(
                                            Locale.US,
                                            "%,.2f",
                                            0.toDouble()
                                        ) + currency + ")"
                                }
                            } else {
                                if (currencyPosition == "left") {
                                    binding.tvpaymenttype.text =
                                        paymentDataList[position].paymentName + "(" +
                                                currency.plus(
                                                    String.format(
                                                        Locale.US,
                                                        "%,.2f",
                                                        walletamount!!.toDouble()
                                                    )
                                                ) + ")"
                                } else {
                                    binding.tvpaymenttype.text =
                                        paymentDataList[position].paymentName + "(" + String.format(
                                            Locale.US,
                                            "%,.2f",
                                            walletamount!!.toDouble()
                                        ) + currency + ")"
                                }
                            }
                        }

                        "Cash on delivery" -> {
                            binding.ivPayment.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.ic_cod,
                                    null
                                )!!
                            )
                            binding.tvpaymenttype.text = "Cash on delivery"

                        }

                        "RazorPay" -> {
                            if (paymentDataList[position].environment == 1) {
                                strRezorPayKey = paymentDataList[position].testPublicKey!!
                            } else {
                                strRezorPayKey = paymentDataList[position].livePublicKey!!
                            }
                            binding.ivPayment.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.ic_rezorpaypayment,
                                    null
                                )!!
                            )
                            binding.tvpaymenttype.text = "RazorPay"
                        }

                        "Stripe" -> {
                            newStripeDataController =
                                if (paymentDataList[position].environment == 1) {
                                    stripeKey = paymentDataList[position].testPublicKey!!
                                    NewStripeDataController(
                                        this@ActPaymentMethod,
                                        stripeKey
                                    )
                                } else {
                                    stripeKey = paymentDataList[position].testPublicKey!!
                                    NewStripeDataController(
                                        this@ActPaymentMethod,
                                        stripeKey
                                    )
                                }
                            binding.ivPayment.setImageDrawable(
                                ResourcesCompat.getDrawable(
                                    resources,
                                    R.drawable.ic_stripepayment,
                                    null
                                )!!
                            )
                            binding.tvpaymenttype.text = "Stripe"
                        }

                        "Flutterwave" -> {
                            encryptionKey = paymentDataList[position].encryptionKey!!
                            if (paymentDataList[position].environment == 1) {
                                flutterWaveKey = paymentDataList[position].testPublicKey!!

                            } else {
                                flutterWaveKey = paymentDataList[position].livePublicKey!!
                            }
//                            binding.ivPayment.setImageDrawable(
//                                ResourcesCompat.getDrawable(
//                                    resources,
//                                    R.drawable.ic_flutterwavepayment,
//                                    null
//                                )!!
//                            )
                            binding.tvpaymenttype.text = "Flutterwave"
                        }

                        "Paystack" -> {
                            if (paymentDataList[position].environment == 1) {
                                payStackKey = paymentDataList[position].testPublicKey!!

                            } else {
                                payStackKey = paymentDataList[position].livePublicKey!!
                            }
//                            binding.ivPayment.setImageDrawable(
//                                ResourcesCompat.getDrawable(
//                                    resources,
//                                    R.drawable.ic_paystackpayment,
//                                    null
//                                )!!
//                            )
                            binding.tvpaymenttype.text = "Paystack"
                        }
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_payment
                }

                override fun getBinding(parent: ViewGroup): RowPaymentBinding {
                    binding = RowPaymentBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }

        actPaymentMethodBinding.rvpaymentlist.apply {
            if (paymentDataList.size > 0) {
                actPaymentMethodBinding.rvpaymentlist.visibility = View.VISIBLE
                actPaymentMethodBinding.tvNoDataFound.visibility = View.GONE
                layoutManager =
                    LinearLayoutManager(
                        this@ActPaymentMethod,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                itemAnimator = DefaultItemAnimator()
                adapter = viewAllDataAdapter
            } else {
                actPaymentMethodBinding.rvpaymentlist.visibility = GONE
                actPaymentMethodBinding.tvNoDataFound.visibility = View.VISIBLE
            }
        }
    }

    //TODO ORDER PAYMENT METHOD CALL
    private fun paynow() {
        if (strGetData == "Cash on delivery") {
            if (isCheckNetwork(this@ActPaymentMethod)) {
                showLoadingProgress(this@ActPaymentMethod)
                val hasmap = HashMap<String, String>()
                hasmap["user_id"] = getStringPref(this@ActPaymentMethod, userId)!!
                hasmap["email"] = getStringPref(this@ActPaymentMethod, userEmail)!!
                hasmap["full_name"] =
                    intent.getStringExtra("fname")!! + " " + intent.getStringExtra("lname")
                hasmap["landmark"] = intent.getStringExtra("landmark") ?: ""
                hasmap["mobile"] = intent.getStringExtra("mobile")!!
                hasmap["order_notes"] = intent.getStringExtra("order_notes") ?: "0"
                hasmap["grand_total"] = intent.getStringExtra("grand_total")!!
                hasmap["payment_type"] = "1"
                hasmap["pincode"] = intent.getStringExtra("pincode")!!
                hasmap["street_address"] = intent.getStringExtra("street_address")!!
                hasmap["coupon_name"] = intent.getStringExtra("coupon_name") ?: "0"
                hasmap["discount_amount"] = intent.getStringExtra("discount_amount") ?: "0"
                hasmap["resell_margin"] = intent.getStringExtra("resell_margin")!!
                hasmap["final_price"] =intent.getStringExtra("final_price")!!
                hasmap["reselling_order_flag"] = intent.getStringExtra("reselling_order_flag")!!
                hasmap["vendor_id"] = intent.getStringExtra("vendorid")!!
                Log.d("HadMap", hasmap.toString())
                callApiOrder(hasmap)
            } else {
                alertErrorOrValidationDialog(
                    this@ActPaymentMethod,
                    resources.getString(R.string.no_internet)
                )
            }
        } else if (strGetData == "Wallet") {
            if (walletAmount.toDouble() >= intent.getStringExtra("grand_total")!!
                    .toDouble()
            ) {
                if (isCheckNetwork(this@ActPaymentMethod)) {
                    showLoadingProgress(this@ActPaymentMethod)
                    val hasmap = HashMap<String, String>()
                    hasmap["user_id"] = getStringPref(this@ActPaymentMethod, userId)!!
                    hasmap["email"] = getStringPref(this@ActPaymentMethod, userEmail)!!
                    hasmap["full_name"] =
                        intent.getStringExtra("fname")!! + " " + intent.getStringExtra("lname")
                    hasmap["landmark"] = intent.getStringExtra("landmark") ?: ""
                    hasmap["mobile"] = intent.getStringExtra("mobile")!!
                    hasmap["order_notes"] = intent.getStringExtra("order_notes") ?: "0"
                    hasmap["grand_total"] = intent.getStringExtra("grand_total")!!
                    hasmap["payment_type"] = "2"
                    hasmap["pincode"] = intent.getStringExtra("pincode")!!
                    hasmap["street_address"] = intent.getStringExtra("street_address")!!
                    hasmap["coupon_name"] = intent.getStringExtra("coupon_name") ?: "0"
                    hasmap["discount_amount"] = intent.getStringExtra("discount_amount")!!
                    hasmap["vendor_id"] = intent.getStringExtra("vendorid")!!
                    Log.d("HadMap", hasmap.toString())
                    callApiOrder(hasmap)
                } else {
                    alertErrorOrValidationDialog(
                        this@ActPaymentMethod,
                        resources.getString(R.string.no_internet)
                    )
                }
            } else {
                showErrorFullMsg(
                    this@ActPaymentMethod,
                    "You don't have sufficient amount in your wallet to place this order."
                )
            }
        } else if (strGetData == "RazorPay") {
            if (isCheckNetwork(this@ActPaymentMethod)) {
                showLoadingProgress(this@ActPaymentMethod)
                startPayment()
            } else {
                alertErrorOrValidationDialog(
                    this@ActPaymentMethod,
                    resources.getString(R.string.no_internet)
                )
            }
        } else if (strGetData == "Stripe") {
            val intent = Intent(this@ActPaymentMethod, ActCardInfo::class.java)
            intent.putExtra("public_key", stripeKey)
            intent.putExtra("fname", fname)
            intent.putExtra("landmark", landmark)
            intent.putExtra("mobile", mobile)
            intent.putExtra("order_notes", orderNote)
            intent.putExtra("pincode", pincode)
            intent.putExtra("street_address", streetAddress)
            intent.putExtra("coupon_name", coupon_name)
            intent.putExtra("discount_amount", discountAmount)
            intent.putExtra("grand_total", price.toString())
            intent.putExtra("vendorid", vendorid)
            startActivity(intent)
            Log.e("data", intent.toString())
        } else if (strGetData == "Flutterwave") {
            flutterWavePayment()
        } else if (strGetData == "Paystack") {
            val amount = intent.getStringExtra("grand_total") ?: "0.00"
            val totalAmount = round(amount.toDouble()).times(100).roundToInt()
            val i = Intent(this@ActPaymentMethod, ActPayStack::class.java)
            i.putExtra(
                "email",
                getStringPref(
                    this@ActPaymentMethod,
                    userEmail
                )
            )
            i.putExtra("public_key", payStackKey)
            i.putExtra("amount", totalAmount.toString())
            startActivityForResult(i, 500)
        } else if (strGetData == "Credit and Debit") {
            if (isCheckNetwork(this@ActPaymentMethod)) {
                showLoadingProgress(this@ActPaymentMethod)
                val hasmap = HashMap<String, String>()
                hasmap["user_id"] = getStringPref(this@ActPaymentMethod, userId)!!
                hasmap["email"] = getStringPref(this@ActPaymentMethod, userEmail)!!
                hasmap["full_name"] =
                    intent.getStringExtra("fname")!! + " " + intent.getStringExtra("lname")
                hasmap["landmark"] = intent.getStringExtra("landmark") ?: ""
                hasmap["mobile"] = intent.getStringExtra("mobile")!!
                hasmap["order_notes"] = intent.getStringExtra("order_notes") ?: "0"
                hasmap["grand_total"] = intent.getStringExtra("grand_total")!!
                hasmap["payment_type"] = "1"
                hasmap["pincode"] = intent.getStringExtra("pincode")!!
                hasmap["street_address"] = intent.getStringExtra("street_address")!!
                hasmap["coupon_name"] = intent.getStringExtra("coupon_name") ?: "0"
                hasmap["discount_amount"] = intent.getStringExtra("discount_amount") ?: "0"
                hasmap["vendor_id"] = intent.getStringExtra("vendorid")!!
                Log.d("HadMap", hasmap.toString())
                val url = "https://ikksa.com/wepayment.php?grand_total="+intent.getStringExtra("grand_total")
                //val url = "https://www.google.co.in/"
                loadwebView(url,hasmap)

            } else {
                alertErrorOrValidationDialog(
                    this@ActPaymentMethod,
                    resources.getString(R.string.no_internet)
                )
            }
        } else if (strGetData == "") {
            showErrorFullMsg(
                this@ActPaymentMethod,
                resources.getString(R.string.payment_type_selection_error)
            )
        }
    }

    //TODO START FLUTTER WAVE PAYMENT
    private fun flutterWavePayment() {
        RaveUiManager(this).setAmount(intent.getStringExtra("grand_total")!!.toDouble())
            .setEmail(getStringPref(this@ActPaymentMethod, userEmail))
            .setfName(getStringPref(this@ActPaymentMethod, userName))
            .setlName(getStringPref(this@ActPaymentMethod, userName))
            .setPublicKey(flutterWaveKey)
            .setEncryptionKey(encryptionKey)
            .setCountry("NG")
            .setCurrency("NGN")
            .setTxRef(System.currentTimeMillis().toString() + "Ref")
            .setPhoneNumber(getStringPref(this@ActPaymentMethod, SharePreference.userMobile), false)
            .acceptMpesaPayments(true)
            .acceptBankTransferPayments(true, true)
            .acceptAccountPayments(true)
            .acceptSaBankPayments(true)
            .acceptBankTransferPayments(true)
            .acceptCardPayments(true)
            .onStagingEnv(false)
            .allowSaveCardFeature(true, false)
            .withTheme(R.style.DefaultPayTheme)
            .initialize()
    }

    //TODO START RAZORPAY PAYMENT
    private fun startPayment() {
        Common.getLog("test", intent.getStringExtra("grand_total").toString())
        val activity: Activity = this
        val co = Checkout()
        try {
            co.setKeyID(strRezorPayKey)
            val amount = intent.getStringExtra("grand_total")!!.toDouble() * 100
            Common.getLog("test", amount.toString())
            val options = JSONObject()
            options.put("name", resources.getString(R.string.app_name))
            options.put("description", resources.getString(R.string.order_payment))
            options.put("image", logoimg)
            options.put("currency", "INR")
            options.put("amount", String.format(Locale.US, "%d", amount.toLong()))
            val prefill = JSONObject()
            prefill.put("email", getStringPref(this@ActPaymentMethod, userEmail))
            prefill.put(
                "contact", getStringPref(
                    this@ActPaymentMethod,
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
    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try {
            val hasmap = HashMap<String, String>()
            hasmap["user_id"] = getStringPref(this@ActPaymentMethod, userId)!!
            hasmap["email"] = getStringPref(this@ActPaymentMethod, userEmail)!!
            hasmap["full_name"] =
                intent.getStringExtra("fname")!! + " " + intent.getStringExtra("lname")
            hasmap["landmark"] = intent.getStringExtra("landmark") ?: ""
            hasmap["mobile"] = intent.getStringExtra("mobile")!!
            hasmap["order_notes"] = intent.getStringExtra("order_notes") ?: "0"
            hasmap["grand_total"] = intent.getStringExtra("grand_total")!!
            hasmap["razorpay_payment_id"] = razorpayPaymentId!!
            hasmap["payment_type"] = "3"
            hasmap["pincode"] = intent.getStringExtra("pincode")!!
            hasmap["street_address"] = intent.getStringExtra("street_address")!!
            hasmap["coupon_name"] = intent.getStringExtra("coupon_name") ?: "0"
            hasmap["discount_amount"] = intent.getStringExtra("discount_amount")!!
            hasmap["vendor_id"] = intent.getStringExtra("vendorid")!!
            callApiOrder(hasmap)
        } catch (e: Exception) {
            Log.e("Exception", "Exception in onPaymentSuccess", e)
        }
    }

    //TODO ERROE MESSAHE PAYMENT GETAWAY
    override fun onPaymentError(errorCode: Int, response: String?) {
        try {
            Toast.makeText(this, "$response", Toast.LENGTH_LONG).show()
            dismissLoadingProgress()
        } catch (e: Exception) {
            Log.e("Exception", e.message, e)
        }
    }

    //TODO STRAT STRIPE PAYMENT GATEWAY
    override fun onstart() {
        showLoadingProgress(this@ActPaymentMethod)
    }

    //TODO SUCCESS STRIPE PAYMENT
    override fun success(token: Token?) {
        try {
            val hasmap = HashMap<String, String>()
            hasmap["user_id"] = getStringPref(this@ActPaymentMethod, userId)!!
            hasmap["email"] = getStringPref(this@ActPaymentMethod, userEmail)!!
            hasmap["full_name"] =
                intent.getStringExtra("fname")!! + " " + intent.getStringExtra("lname")
            hasmap["landmark"] = intent.getStringExtra("landmark") ?: ""
            hasmap["mobile"] = intent.getStringExtra("mobile")!!
            hasmap["order_notes"] = intent.getStringExtra("order_notes") ?: "0"
            hasmap["grand_total"] = intent.getStringExtra("grand_total")!!
            hasmap["stripeToken"] = token!!.id
            hasmap["stripeEmail"] = getStringPref(this@ActPaymentMethod, userEmail)!!
            hasmap["payment_type"] = "4"
            hasmap["pincode"] = intent.getStringExtra("pincode")!!
            hasmap["street_address"] = intent.getStringExtra("street_address")!!
            hasmap["coupon_name"] = intent.getStringExtra("coupon_name") ?: "0"
            hasmap["discount_amount"] = intent.getStringExtra("discount_amount")!!
            hasmap["vendor_id"] = intent.getStringExtra("vendorid")!!
            callApiOrder(hasmap)

        } catch (e: Exception) {
            Log.e("Exception", "Exception in onPaymentSuccess", e)
        }
    }

    //TODO STRIPE PAYEMNT FAILER
    override fun failer(error: java.lang.Exception?) {
        dismissLoadingProgress()
        getToast(this@ActPaymentMethod, error?.message.toString())
        Log.e("error", error?.message.toString())
    }


    //TODO CALL ORDER API
    private fun callApiOrder(hasmap: HashMap<String, String>) {
        showLoadingProgress(this@ActPaymentMethod)
        val call = ApiClient.getClient.setOrderPayment(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    Common.isAddOrUpdated = true
                    if (response.body()?.status == 1) {
                        showSuccessFullMsg(
                            this@ActPaymentMethod,
                            response.body()?.message.toString()
                        )
                        openActivity(ActPaymentSuccessFull::class.java)
                    } else {
                        showErrorFullMsg(
                            this@ActPaymentMethod,
                            response.body()?.message.toString()
                        )
                    }
                } else if(response.code() == 500){
                    dismissLoadingProgress()
                    showErrorFullMsg(
                        this@ActPaymentMethod,"Error code 500")
                }else {
                    dismissLoadingProgress()
                    getLog("orderapi","code - " + response.code())
                    getLog("orderapi","body - " + response.body())
                    alertErrorOrValidationDialog(
                        this@ActPaymentMethod,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActPaymentMethod,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO ACTIVITY RESULT
    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == 500) {
                showLoadingProgress(this@ActPaymentMethod)
                val id = data?.getStringExtra("id")
                Log.e("PAystackId",id.toString())
                val hasmap = HashMap<String, String>()
                hasmap["user_id"] = getStringPref(this@ActPaymentMethod, userId)!!
                hasmap["email"] = getStringPref(this@ActPaymentMethod, userEmail)!!
                hasmap["full_name"] = intent.getStringExtra("fname")!! + " " + intent.getStringExtra("lname")
                hasmap["landmark"] = intent.getStringExtra("landmark") ?: ""
                hasmap["mobile"] = intent.getStringExtra("mobile")!!
                hasmap["order_notes"] = intent.getStringExtra("order_notes") ?: "0"
                hasmap["grand_total"] = intent.getStringExtra("grand_total")!!
                hasmap["payment_id"] = id.toString()
                hasmap["payment_type"] = "6"
                hasmap["pincode"] = intent.getStringExtra("pincode")!!
                hasmap["street_address"] = intent.getStringExtra("street_address")!!
                hasmap["coupon_name"] = intent.getStringExtra("coupon_name") ?: "0"
                hasmap["discount_amount"] = intent.getStringExtra("discount_amount")!!
                hasmap["vendor_id"] = intent.getStringExtra("vendorid")!!
                callApiOrder(hasmap)
            }
        } else if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            when (resultCode) {
                RavePayActivity.RESULT_SUCCESS -> {
                    Toast.makeText(this, "Transaction Successful", Toast.LENGTH_SHORT).show()
                    val message: String? = data.getStringExtra("response")
                    Log.e("message", message.toString())
                    val json = Gson().fromJson(message, FlutterWaveResponse::class.java)
                    val dataValue = json.data
                    val id = dataValue?.flwRef
                    showLoadingProgress(this@ActPaymentMethod)
                    val hasmap = HashMap<String, String>()
                    hasmap["user_id"] = getStringPref(this@ActPaymentMethod, userId)!!
                    hasmap["email"] = getStringPref(this@ActPaymentMethod, userEmail)!!
                    hasmap["full_name"] =
                        intent.getStringExtra("fname")!! + " " + intent.getStringExtra("lname")
                    hasmap["landmark"] = intent.getStringExtra("landmark") ?: ""
                    hasmap["mobile"] = intent.getStringExtra("mobile")!!
                    hasmap["order_notes"] = intent.getStringExtra("order_notes") ?: "0"
                    hasmap["grand_total"] = intent.getStringExtra("grand_total")!!
                    hasmap["razorpay_payment_id"] = id.toString()
                    hasmap["stripeEmail"] = getStringPref(this@ActPaymentMethod, userEmail)!!
                    hasmap["payment_type"] = "5"
                    hasmap["pincode"] = intent.getStringExtra("pincode")!!
                    hasmap["street_address"] = intent.getStringExtra("street_address")!!
                    hasmap["coupon_name"] = intent.getStringExtra("coupon_name") ?: "0"
                    hasmap["discount_amount"] = intent.getStringExtra("discount_amount")!!
                    hasmap["vendor_id"] = intent.getStringExtra("vendorid")!!
                    callApiOrder(hasmap)
                }
                RavePayActivity.RESULT_ERROR -> {
                    Toast.makeText(this, "An Error Occur", Toast.LENGTH_SHORT).show()
                }
                RavePayActivity.RESULT_CANCELLED -> {
                    Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActPaymentMethod, false)
    }


    fun getpaymenturl(map: java.util.HashMap<String, String>){
        showLoadingProgress(this@ActPaymentMethod)
        val call = ApiClient.getClient2.getPaymentUrl(map)
        call.enqueue(object : Callback<PaymentUrlResponse> {
            override fun onResponse(call: Call<PaymentUrlResponse>,
                                    response: Response<PaymentUrlResponse>) {
                if (response.code() == 200) {
                    val restResponce: PaymentUrlResponse = response.body()!!
                        dismissLoadingProgress()
                        //val url = "https://ikksa.com/wepayment.php?grand_total=10"//restResponce.redirectUrl!!

                        actPaymentMethodBinding.rvpaymentlist.visibility = View.GONE
                        actPaymentMethodBinding.btnProccedtoPayment .visibility = View.GONE
                        actPaymentMethodBinding.webView.visibility = View.VISIBLE
                       // loadwebView(url,map)
                        // call other api or action
                    }
                }

            override fun onFailure(call: Call<PaymentUrlResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActPaymentMethod,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (actPaymentMethodBinding.webView.canGoBack()) {
                    actPaymentMethodBinding.webView.goBack()
                } else {
                    showToastToExit()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun showToastToExit() {
        Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_SHORT).show()

        when {
            doubleBackToExitPressedOnce -> {
                onBackPressed()
            }
            else -> {
                doubleBackToExitPressedOnce = true
                showToast(getString(R.string.back_again_to_exit))
                Handler(Looper.myLooper()!!).postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isOnline(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun loadwebView(url:String,hasmap: java.util.HashMap<String, String>){

        actPaymentMethodBinding.rvpaymentlist.visibility = GONE
        actPaymentMethodBinding.btnProccedtoPayment .visibility = GONE
        actPaymentMethodBinding.webView.visibility = View.VISIBLE

        actPaymentMethodBinding.webView.settings.setJavaScriptEnabled(true)
        actPaymentMethodBinding.webView.getSettings().setDomStorageEnabled(true); // Add this
        actPaymentMethodBinding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        actPaymentMethodBinding.webView.loadUrl(url)
        Log.v("Base Url -> ----",""+url)
        actPaymentMethodBinding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null) {
                    view?.loadUrl(url)
                    Log.v("Url loading -> ----"," "+url)
                }
                return true
            }

            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                Log.v("Current screen -> ----",""+url)
                if (url != null) {
                    val paymentDone : Boolean = "paid" in url
                    val paymentFail : Boolean = "failed" in url
                    if(paymentDone){
                        Log.v("Payment done -> ----"," "+url)
                        val mUurlParse = url.toHttpUrlOrNull()
                        if (mUurlParse != null) {
                            val mStatus = mUurlParse.queryParameter("status")
                            val mMsg = mUurlParse.queryParameter("message")
                            showSuccessFullMsg(this@ActPaymentMethod," " +mMsg )
                        }
                        callApiOrder(hasmap)
                    }else if(paymentFail){
                        Log.v("Payment Fail -> ----"," "+url)
                        // https://ikksa.com/paymentSuccess.php?id=10e5c887-ad68-4d6c-b905-f987973a3755&status=failed&amount=9900&message=Unable+to+process+the+purchase+transaction+%28Test+Environment%29
                        val mUurlParse = url.toHttpUrlOrNull()
                        if (mUurlParse != null) {
                            val mStatus = mUurlParse.queryParameter("status")
                            val mMsg = mUurlParse.queryParameter("message")
                            showErrorFullMsg(this@ActPaymentMethod," " +mMsg )
                        }

                        actPaymentMethodBinding.webView.visibility = GONE
                        actPaymentMethodBinding.btnProccedtoPayment .visibility = VISIBLE
                        actPaymentMethodBinding.rvpaymentlist.visibility = VISIBLE

                    }else{
                        Log.v("Other screen -> ----"," "+url)

                    }
                }
                super.doUpdateVisitedHistory(view, url, isReload)
            }
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                Common.showLoadingProgress(this@ActPaymentMethod)
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                dismissLoadingProgress()
                super.onPageFinished(view, url)
            }

            @SuppressLint("NewApi")
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                val errorMessage = "Got Error! $error"
                showToast(errorMessage)
                /*infoTV.text = errorMessage
                setProgressDialogVisibility(false)*/
                super.onReceivedError(view, request, error)
            }

        }
    }



}
