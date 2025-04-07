package com.hommlie.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.databinding.ActivitySellerMarginBinding
import com.hommlie.user.databinding.RowSellerMarginBinding
import com.hommlie.user.model.SellerMarginResponse
import com.hommlie.user.model.SellerMargingDataItem
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.getCurrentLanguage
import com.hommlie.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap
import java.util.Locale

class ActSellerMargin : AppCompatActivity() {

    private lateinit var actSellerMarginBinding: ActivitySellerMarginBinding

    private var sellerMarginDataList = ArrayList<SellerMargingDataItem>()
    var currency: String = ""
    var currencyPosition: String = ""
    private var wishListDataAdapter: BaseAdaptor<SellerMargingDataItem, RowSellerMarginBinding>? =
        null
    private var linearlayoutManager: LinearLayoutManager? = null
    private var currentPage = 1
    var totalPages: Int = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var pastVisibleItems = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actSellerMarginBinding = ActivitySellerMarginBinding.inflate(layoutInflater)
        val view = actSellerMarginBinding.root
        setContentView(view)
        initViews()
    }

    private fun initViews() {

        linearlayoutManager =
            LinearLayoutManager(
                this@ActSellerMargin,
                LinearLayoutManager.VERTICAL,
                false
            )
        loadOrderHistory(sellerMarginDataList)

        currency = SharePreference.getStringPref(this@ActSellerMargin, SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(this@ActSellerMargin, SharePreference.CurrencyPosition)!!

        if (Common.isCheckNetwork(this@ActSellerMargin)) {
            if (SharePreference.getBooleanPref(this@ActSellerMargin, SharePreference.isLogin)) {
                callApiOrderSellerMargin()

            } else {
                val intent = Intent(this, ActSignUp::class.java)
                startActivity(intent)

            }
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActSellerMargin,
                resources.getString(R.string.no_internet)
            )
        }

        actSellerMarginBinding.ivBack.setOnClickListener{
            finish()
        }





        actSellerMarginBinding.rvOrderlist.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = linearlayoutManager!!.childCount
                    totalItemCount = linearlayoutManager!!.itemCount
                    pastVisibleItems = linearlayoutManager!!.findFirstVisibleItemPosition()
                    if (currentPage < totalPages) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            currentPage += 1
                            if (Common.isCheckNetwork(this@ActSellerMargin)) {
                                callApiOrderSellerMargin()
                            } else {
                                Common.alertErrorOrValidationDialog(
                                    this@ActSellerMargin,
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        }
                    }
                }
            }
        })

    }



    private fun callApiOrderSellerMargin() {
        Common.showLoadingProgress(this@ActSellerMargin)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(this@ActSellerMargin, SharePreference.userId)!!

        val call = ApiClient.getClient.getResellerList(currentPage.toString(), hasmap)
        call.enqueue(object : Callback<SellerMarginResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SellerMarginResponse>,
                response: Response<SellerMarginResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        if (currentPage == 1) {
                            sellerMarginDataList.clear()
                        }
                        restResponce.data?.data?.let {
                            sellerMarginDataList.addAll(it)
                            Log.d("list", sellerMarginDataList.toString())
                        }
                        this@ActSellerMargin.currentPage =
                            restResponce.data?.currentPage!!.toInt()
                        this@ActSellerMargin.totalPages =
                            restResponce.data.lastPage!!.toInt()
                        loadOrderHistory(sellerMarginDataList)
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActSellerMargin,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SellerMarginResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActSellerMargin,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }



    private fun loadOrderHistory(SellerMargingDataList: ArrayList<SellerMargingDataItem>) {
        lateinit var binding: RowSellerMarginBinding

        wishListDataAdapter =
            object : BaseAdaptor<SellerMargingDataItem, RowSellerMarginBinding>(
                this,
                SellerMargingDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: SellerMargingDataItem,
                    position: Int
                ) {

                    binding.tvorderid.text = SellerMargingDataList[position].orderNumber
                    when (SellerMargingDataList[position].paymentType) {
                        1 -> {
                            binding.tvpaymenttype.text = "Cash"
                        }
                        2 -> {
                            binding.tvpaymenttype.text = "Wallet"
                        }
                        3 -> {
                            binding.tvpaymenttype.text = "RazorPay"
                        }
                        4 -> {
                            binding.tvpaymenttype.text = "Stripe"
                        }
                        5 -> {
                            binding.tvpaymenttype.text = "Flutterwave"
                        }
                        6 -> {
                            binding.tvpaymenttype.text = "Paystack"
                        }
                    }

                    binding.tvorderdate.text = SellerMargingDataList[position].date?.let {
                        Common.getDate(
                            it
                        )
                    }
                    if (currencyPosition == "left") {
                        binding.tvMarginEarned.text =
                            currency.plus(
                                SellerMargingDataList[position].resellMargin?.let {
                                    String.format(
                                        Locale.US,
                                        "%,.2f",
                                        it.toDouble()
                                    )
                                }
                            )
                    } else {
                        binding.tvMarginEarned.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                SellerMargingDataList[position].resellMargin!!.toDouble()
                            )) + "" + currency
                    }
                    holder?.itemView?.setOnClickListener {
                        Log.e(
                            "order_number--->",
                            SellerMargingDataList[position].orderNumber.toString()
                        )
                        val intent = Intent(context, ActOrderDetails::class.java)      ////////
                        intent.putExtra(
                            "order_number",
                            SellerMargingDataList[position].orderNumber.toString()
                        )
                        startActivity(intent)
                    }
                }


                override fun setItemLayout(): Int {
                    return R.layout.row_seller_margin
                }

                override fun getBinding(parent: ViewGroup): RowSellerMarginBinding {
                    binding = RowSellerMarginBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        if (!isFinishing) {
            actSellerMarginBinding.rvOrderlist.apply {
                if (SellerMargingDataList.size > 0) {
                    actSellerMarginBinding.rvOrderlist.visibility = View.VISIBLE
                    actSellerMarginBinding.tvNoDataFound.visibility = View.GONE

                    layoutManager =
                        linearlayoutManager
                    itemAnimator = DefaultItemAnimator()
                    adapter = wishListDataAdapter
                } else {
                    actSellerMarginBinding.rvOrderlist.visibility = View.GONE
                    actSellerMarginBinding.tvNoDataFound.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(this, false)
        if (Common.isCheckNetwork(this)) {
            if (SharePreference.getBooleanPref(this, SharePreference.isLogin)) {
                callApiOrderSellerMargin()
            } else {
                val intent = Intent(this, ActSignUp::class.java)
                startActivity(intent)

            }
        } else {
            Common.alertErrorOrValidationDialog(
                this,
                resources.getString(R.string.no_internet)
            )
        }
    }


    //Call api order details



}