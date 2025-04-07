package com.hommlie.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.databinding.ActWalletBinding
import com.hommlie.user.databinding.RowTransactionhistoryBinding
import com.hommlie.user.model.*
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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class ActWallet : BaseActivity(), PaymentResultWithDataListener {
    private lateinit var actWalletBinding: ActWalletBinding
    private var walletList = ArrayList<Transaction>()
    private var viewAllDataAdapter: BaseAdaptor<Transaction, RowTransactionhistoryBinding>? = null
    private var manager: LinearLayoutManager? = null

    var pastVisibleItems = 0

    var selectedAmount : MutableLiveData<Int> = MutableLiveData(500)


    override fun setLayout(): View = actWalletBinding.root

    override fun initView() {
        actWalletBinding = ActWalletBinding.inflate(layoutInflater)
        actWalletBinding.ivBack.setOnClickListener {
            finish()
        }
        manager = LinearLayoutManager(this@ActWallet, LinearLayoutManager.VERTICAL, false)

        loadWalletDetails(walletList)

        if (Common.isCheckNetwork(this@ActWallet)) {
            if (SharePreference.getBooleanPref(this@ActWallet, SharePreference.isLogin)) {
                callApiWallet()
            } else {
                openActivity(ActSignUp::class.java)
                this.finish()
            }
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActWallet,
                resources.getString(R.string.no_internet)
            )
        }

        actWalletBinding.edtAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // Called before the text is changed
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // Called as the text is being changed
            }

            override fun afterTextChanged(editable: Editable?) {
                val amountText = editable.toString()
                if (amountText.isNotEmpty()&& amountText.all { it.isDigit() }) {
                    try {
                        val amount = amountText.toInt()
                        if (selectedAmount.value != amount) {
                            selectedAmount.value = amount
                            if (amountText.length <= actWalletBinding.edtAmount.text.length) {
                                actWalletBinding.edtAmount.setSelection(amountText.length)
                            }
                        }
                        if (amount<=99){
                            actWalletBinding.cardAddBalance.setCardBackgroundColor(ContextCompat.getColor(this@ActWallet, R.color.btn_disable))
                            actWalletBinding.cardAddBalance.isEnabled=false
                        }else {
                            actWalletBinding.cardAddBalance.setCardBackgroundColor(ContextCompat.getColor(this@ActWallet, R.color.serviceHeadingcolor))
                            actWalletBinding.cardAddBalance.isEnabled = true
                        }
                    } catch (e: NumberFormatException) {
                        actWalletBinding.cardAddBalance.setCardBackgroundColor(ContextCompat.getColor(this@ActWallet, R.color.btn_disable))
                        actWalletBinding.cardAddBalance.isEnabled=false
                    }
                }else{
                    actWalletBinding.cardAddBalance.setCardBackgroundColor(ContextCompat.getColor(this@ActWallet, R.color.btn_disable))
                    actWalletBinding.cardAddBalance.isEnabled=false

                }
            }
        })


        selectedAmount.observe(this, Observer { amount ->
            actWalletBinding.edtAmount.setText(amount.toString())
        })

        actWalletBinding.tvWalletAmount.setText("\u20b9"+SharePreference.getDoublePref(this@ActWallet,SharePreference.wallet))

        actWalletBinding.cardAddBalance.setOnClickListener {
            initiatePayment(selectedAmount.value.toString())
        }
        actWalletBinding.clSeeall.setOnClickListener {
            val intent = Intent(this@ActWallet,ActTransaction::class.java)
            startActivity(intent)
        }

        actWalletBinding.card1.setOnClickListener {
            selectedAmount.value = 500
            actWalletBinding.edtAmount.setSelection(actWalletBinding.edtAmount.text.length)
            actWalletBinding.card1.strokeColor = ContextCompat.getColor(this, R.color.serviceHeadingcolor)
            actWalletBinding.card2.strokeColor = ContextCompat.getColor(this, R.color.gray_line_color)
            actWalletBinding.card3.strokeColor = ContextCompat.getColor(this, R.color.gray_line_color)
            actWalletBinding.card4.strokeColor = ContextCompat.getColor(this, R.color.gray_line_color)
            actWalletBinding.card1.setCardBackgroundColor(ContextCompat.getColor(this,R.color.light_serviceHeadingcolor))
            actWalletBinding.card2.setCardBackgroundColor(ContextCompat.getColor(this,R.color.white))
            actWalletBinding.card3.setCardBackgroundColor(ContextCompat.getColor(this,R.color.white))
            actWalletBinding.card4.setCardBackgroundColor(ContextCompat.getColor(this,R.color.white))
        }
        actWalletBinding.card2.setOnClickListener {
            selectedAmount.value = 1000
            actWalletBinding.edtAmount.setSelection(actWalletBinding.edtAmount.text.length)
            actWalletBinding.card1.strokeColor = ContextCompat.getColor(this, R.color.gray_line_color)
            actWalletBinding.card2.strokeColor = ContextCompat.getColor(this, R.color.serviceHeadingcolor)
            actWalletBinding.card3.strokeColor = ContextCompat.getColor(this, R.color.gray_line_color)
            actWalletBinding.card4.strokeColor = ContextCompat.getColor(this, R.color.gray_line_color)
            actWalletBinding.card1.setCardBackgroundColor(ContextCompat.getColor(this,R.color.white))
            actWalletBinding.card2.setCardBackgroundColor(ContextCompat.getColor(this,R.color.light_serviceHeadingcolor))
            actWalletBinding.card3.setCardBackgroundColor(ContextCompat.getColor(this,R.color.white))
            actWalletBinding.card4.setCardBackgroundColor(ContextCompat.getColor(this,R.color.white))
        }
        actWalletBinding.card3.setOnClickListener {
            selectedAmount.value = 2000
            actWalletBinding.edtAmount.setSelection(actWalletBinding.edtAmount.text.length)
            actWalletBinding.card1.strokeColor = ContextCompat.getColor(this, R.color.gray_line_color)
            actWalletBinding.card2.strokeColor = ContextCompat.getColor(this, R.color.gray_line_color)
            actWalletBinding.card3.strokeColor = ContextCompat.getColor(this, R.color.serviceHeadingcolor)
            actWalletBinding.card4.strokeColor = ContextCompat.getColor(this, R.color.gray_line_color)
            actWalletBinding.card1.setCardBackgroundColor(ContextCompat.getColor(this,R.color.white))
            actWalletBinding.card2.setCardBackgroundColor(ContextCompat.getColor(this,R.color.white))
            actWalletBinding.card3.setCardBackgroundColor(ContextCompat.getColor(this,R.color.light_serviceHeadingcolor))
            actWalletBinding.card4.setCardBackgroundColor(ContextCompat.getColor(this,R.color.white))
        }
        actWalletBinding.card4.setOnClickListener {
            selectedAmount.value = 5000
            actWalletBinding.edtAmount.setSelection(actWalletBinding.edtAmount.text.length)
            actWalletBinding.card1.strokeColor = ContextCompat.getColor(this, R.color.gray_line_color)
            actWalletBinding.card2.strokeColor = ContextCompat.getColor(this, R.color.gray_line_color)
            actWalletBinding.card3.strokeColor = ContextCompat.getColor(this, R.color.gray_line_color)
            actWalletBinding.card4.strokeColor = ContextCompat.getColor(this, R.color.serviceHeadingcolor)
            actWalletBinding.card1.setCardBackgroundColor(ContextCompat.getColor(this,R.color.white))
            actWalletBinding.card2.setCardBackgroundColor(ContextCompat.getColor(this,R.color.white))
            actWalletBinding.card3.setCardBackgroundColor(ContextCompat.getColor(this,R.color.white))
            actWalletBinding.card4.setCardBackgroundColor(ContextCompat.getColor(this,R.color.light_serviceHeadingcolor))
        }

    }


    //TODO CALL WALLET API
    private fun callApiWallet() {
        // Show a loading indicator
        Common.showLoadingProgress(this@ActWallet)

        // Prepare the request parameters
        val hasmap = HashMap<String, String>()
            hasmap["userId"] = getStringPref(this@ActWallet, SharePreference.userId)!!

        // Make the API call
        ApiClient.getClient.getWallet(hasmap).enqueue(object : Callback<WalletResponse> {

            override fun onResponse(call: Call<WalletResponse>, response: Response<WalletResponse>) {
                dismissLoadingProgress()
                if (response.isSuccessful) {
                    val walletResponse = response.body()
                    if (walletResponse?.status == 1) {
                        // Populate the wallet list and load details
                        if (walletResponse.transactions!=null){
                            if (walletResponse.transactions.size>5){
                                walletList.clear()
                                walletList.addAll(walletResponse.transactions.take(5))
                                actWalletBinding.clSeeall.visibility = View.VISIBLE
                            }else{
                                walletList.clear()
                                walletList.addAll(walletResponse.transactions)
                                actWalletBinding.clSeeall.visibility = View.GONE
                            }
                        }else{
                            actWalletBinding.cardRecentTrans.visibility=View.GONE
                        }

                        SharePreference.setDoublePref(this@ActWallet, SharePreference.wallet,walletResponse.wallet.balance)
                        actWalletBinding.tvWalletAmount.setText("\u20b9"+SharePreference.getDoublePref(this@ActWallet,SharePreference.wallet))

                        loadWalletDetails(walletList)
                    } else {
                        // Handle API response with status 0
                       // Common.showErrorFullMsg(this@ActWallet, walletResponse?.message ?: getString(R.string.error_msg))
                    }
                } else {
                    // Handle non-200 HTTP response codes
                    Common.showErrorFullMsg(
                        this@ActWallet,
                        response.message() ?: getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<WalletResponse>, t: Throwable) {
                // Dismiss the loading indicator and show an error message
                Common.dismissLoadingProgress()
                Common.showErrorFullMsg(
                    this@ActWallet,
                    t.localizedMessage ?: getString(R.string.error_msg)
                )
            }
        })
    }


    //TODO WALLET DATA SET
    private fun loadWalletDetails(walletList: ArrayList<Transaction>) {
        lateinit var binding: RowTransactionhistoryBinding
        viewAllDataAdapter =
            object : BaseAdaptor<Transaction, RowTransactionhistoryBinding>(this@ActWallet, walletList) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n", "UseCompatLoadingForDrawables")
                override fun onBindData(holder: RecyclerView.ViewHolder?, `val`: Transaction, position: Int) {


                    binding.tvwalletdate.text = Common.formatDateTime(walletList[position].createdAt)
                    if (walletList[position].transactionType=="credit") {
                        binding.tvDeliveryprice.text = "\u002B\u20b9${walletList[position].amount}"
                        binding.tvwalletName.text = "Cash Deposited"
                        binding.tvDeliveryprice.setTextColor(ContextCompat.getColor(this@ActWallet,R.color.quantum_googgreen500))
                    }else{
                        binding.tvDeliveryprice.text = "\u002D\u20b9${walletList[position].amount}"
                        binding.tvwalletName.text = "Service Booked #"
                        binding.tvDeliveryprice.setTextColor(ContextCompat.getColor(this@ActWallet,R.color.home_header))
                    }

                }

                override fun setItemLayout(): Int {
                    return R.layout.row_notification
                }

                override fun getBinding(parent: ViewGroup): RowTransactionhistoryBinding {
                    binding = RowTransactionhistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    return binding
                }
            }
        actWalletBinding.rvRecenttrans.apply {
            if (walletList.size > 0) {
                actWalletBinding.cardRecentTrans.visibility = View.VISIBLE
          //      actWalletBinding.tvNoDataFound.visibility = View.GONE
                layoutManager = manager
                adapter = viewAllDataAdapter
            } else {
                actWalletBinding.cardRecentTrans.visibility = View.GONE
           //     actWalletBinding.tvNoDataFound.visibility = View.VISIBLE
            }
        }
    }



    //TODO START RAZORPAY PAYMENT
    private fun startPayment(orderId: String?) {
        Common.getLog("test", selectedAmount.value.toString())
        val activity: Activity = this
        val co = Checkout()
        try {
            co.setKeyID("rzp_live_ziQnMov0ucEqGL")
            val amount = selectedAmount.value!!.toDouble() * 100
            Common.getLog("test", amount.toString())
            val options = JSONObject()
            options.put("name", resources.getString(R.string.app_name))
            options.put("description", resources.getString(R.string.order_payment))
            options.put("image", resources.getDrawable(R.drawable.hommlie_home))
            options.put("currency", "INR")
            options.put("order_id",orderId)
            options.put("amount", String.format(Locale.US, "%d", amount.toLong()))
            val prefill = JSONObject()
            prefill.put("email", getStringPref(this@ActWallet, userEmail))
            prefill.put("contact", getStringPref(this@ActWallet, SharePreference.userMobile))
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


    private fun initiatePayment(price: String) {
        Common.showLoadingProgress(this@ActWallet)
        val hashMap = HashMap<String,String>()
        hashMap["amount"] = price
        hashMap["currency"] = "INR"

        val call = ApiClient.getClient.initiatePayment(hashMap)
        call.enqueue(object : Callback<InitiatePaymentResponse>{
            override fun onResponse(call: Call<InitiatePaymentResponse>, response: Response<InitiatePaymentResponse>) {
                if (response.isSuccessful){
                    val restResponse = response.body()
                    if (restResponse!=null){
                        if (restResponse.status==1){
                            dismissLoadingProgress()
                            startPayment(restResponse.data?.orderId)
                        }else{
                            dismissLoadingProgress()
                            Common.alertErrorOrValidationDialog(this@ActWallet,"Payment failed to Intiate, please try again after some time.")
                        }
                    }else{
                        dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(this@ActWallet,"Payment failed to Intiate, please try again after some time.")
                    }
                }else{
                    dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(this@ActWallet,"Payment failed to Intiate, please try again after some time.")
                }
            }

            override fun onFailure(call: Call<InitiatePaymentResponse>, t: Throwable) {
                dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(this@ActWallet,"Payment failed to Intiate, please try again after some time.")
            }

        })
    }

    private fun callApiOrder(hasmap:HashMap<String, String>) {
        Common.showLoadingProgress(this)
        val call = ApiClient.getClient.confirmRecharge(hasmap)
        Log.e("user_id",hasmap["userId"].toString())
        Log.e("payment_id",hasmap["payment_id"].toString())
        Log.e("amount",hasmap["amount"].toString())
        Log.e("description",hasmap["description"].toString())
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    Common.isAddOrUpdated = true
                    if (response.body()?.status == 1) {
                        Common.showSuccessFullMsg(this@ActWallet, response.body()?.message.toString())
                       callApiWallet()
                    } else {
                        Common.showErrorFullMsg(
                            this@ActWallet,
                            response.body()?.message.toString()+"1"
                        )
                    }
                } else {
                    dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                        this@ActWallet,
                        resources.getString(R.string.error_msg)+"2"
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActWallet,
                    resources.getString(R.string.error_msg)+"3"
                )
            }
        })
    }




    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActWallet, false)
        if (Common.isCheckNetwork(this@ActWallet)) {
            if (SharePreference.getBooleanPref(this@ActWallet, SharePreference.isLogin)) {
                 //   callApiWallet()
            } else {
                openActivity(ActSignUp::class.java)
                this.finish()
            }
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActWallet,
                resources.getString(R.string.no_internet)
            )
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData) {
        try{

            val hasmap = HashMap<String, String>()
            hasmap["userId"] = getStringPref(this@ActWallet, SharePreference.userId)!!
           // hasmap["payment_type"] = "Razor Pay"
            hasmap["payment_id"] = razorpayPaymentId.toString()
            hasmap["amount"] = selectedAmount.value.toString()
            hasmap["description"] = "adding money to wallet"
            callApiOrder(hasmap)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        try {
            //Toast.makeText(this, "${p2.}", Toast.LENGTH_LONG).show()
            dismissLoadingProgress()
        } catch (e: Exception) {
            Log.e("Exception", e.message, e)
        }
    }
}