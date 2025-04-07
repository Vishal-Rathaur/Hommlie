package com.hommlie.user.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.hommlie.user.R
import com.hommlie.user.activity.ActMain
import com.hommlie.user.activity.ActOrderDetails
import com.hommlie.user.activity.ActSignUp
import com.hommlie.user.activity.ActTrackOrder
import com.hommlie.user.activity.ActWriteReview
import com.hommlie.user.api.ApiClient
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.base.BaseFragment
import com.hommlie.user.databinding.FragOrderHistoryBinding
import com.hommlie.user.databinding.RowOrderBinding
import com.hommlie.user.model.OrderHistoryDataItem
import com.hommlie.user.model.OrderHistoryResponse
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.filterHttps
import com.hommlie.user.utils.Common.getCurrentLanguage
import com.hommlie.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryFragment : BaseFragment<FragOrderHistoryBinding>() {
    private lateinit var fragOrderHistoryBinding: FragOrderHistoryBinding
    private var orderHistoryDataList = ArrayList<OrderHistoryDataItem>()
    var currency: String = ""
    var currencyPosition: String = ""
    private var wishListDataAdapter: BaseAdaptor<OrderHistoryDataItem, RowOrderBinding>? =
        null
    private var linearlayoutManager: LinearLayoutManager? = null
    private var currentPage = 1
    var total_pages: Int = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var pastVisibleItems = 0

    override fun initView(view: View) {
        fragOrderHistoryBinding = FragOrderHistoryBinding.bind(view)
      //  retainInstance = true

        requireActivity().window.decorView.systemUiVisibility = 0
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)

        linearlayoutManager =
            LinearLayoutManager(
                requireActivity(),
                LinearLayoutManager.VERTICAL,
                false
            )

        fragOrderHistoryBinding.rvOrderlist.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var lastScrollY = 0
            private var isMenuVisible = true

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val activity = activity as? ActMain

                // Check if the user is scrolling down or up
                if (dy > 0) {
                    // User is scrolling down
                    if (isMenuVisible) {
                        activity?.hideMenu()
                        isMenuVisible = false
                    }
                } else if (dy < 0) {
                    // User is scrolling up
                    if (!isMenuVisible) {
                        activity?.showMenu()
                        isMenuVisible = true
                    }
                }

                lastScrollY += dy
            }
        })


        loadOrderHistory(orderHistoryDataList)

        currency = SharePreference.getStringPref(requireActivity(), SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(requireActivity(), SharePreference.CurrencyPosition)!!

        if (Common.isCheckNetwork(requireActivity())) {
            if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
                callApiOrderHistory()
            } else {
                openActivity(ActSignUp::class.java)
                requireActivity().finish()
            }
        } else {
            Common.alertErrorOrValidationDialog(
                requireActivity(),
                resources.getString(R.string.no_internet)
            )
        }
        fragOrderHistoryBinding.rvOrderlist.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = linearlayoutManager!!.childCount
                    totalItemCount = linearlayoutManager!!.itemCount
                    pastVisibleItems = linearlayoutManager!!.findFirstVisibleItemPosition()
                    if (currentPage < total_pages) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            currentPage += 1
                            if (Common.isCheckNetwork(requireActivity())) {
                                callApiOrderHistory()
                            } else {
                                Common.alertErrorOrValidationDialog(
                                    requireActivity(),
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        }
                    }
                }
            }
        })
    }



    override fun getBinding(): FragOrderHistoryBinding {
        fragOrderHistoryBinding = FragOrderHistoryBinding.inflate(layoutInflater)
        return fragOrderHistoryBinding
    }

    //TODO API ORDER HISTORY CALL
    private fun callApiOrderHistory() {
        Common.showLoadingProgress(requireActivity())
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(requireActivity(), SharePreference.userId)!!

        val call = ApiClient.getClient.getOrderHistory(currentPage.toString(), hasmap)
        call.enqueue(object : Callback<OrderHistoryResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<OrderHistoryResponse>,
                response: Response<OrderHistoryResponse>
            ) {
                if (!isAdded) return
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        if (currentPage == 1) {
                            orderHistoryDataList.clear()
                        }

                        restResponce.data?.let {  //.data?
                            orderHistoryDataList.addAll(it)
                            Log.d("list", orderHistoryDataList.toString());

                        }
                      //  this@OrderHistoryFragment.currentPage = restResponce.data?.currentPage!!.toInt()
                       // this@OrderHistoryFragment.total_pages = restResponce.data.lastPage!!.toInt()
                        loadOrderHistory(orderHistoryDataList)
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                      //  Common.alertErrorOrValidationDialog(requireActivity(), restResponce.message.toString())
                    }
                }else{
                    Common.dismissLoadingProgress()
                    when (response.code()) {
                        404 -> {
                            Common.alertErrorOrValidationDialog(requireActivity(), "Resource not found.")
                        }
                        500 -> {
                            Common.alertErrorOrValidationDialog(requireActivity(), "Server error. Please try again later.")
                        }
                        else -> {
                            Common.alertErrorOrValidationDialog(requireActivity(), "Unexpected error: ${response.code()}")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<OrderHistoryResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                val errorMessage = when (t) {
                    is java.net.UnknownHostException -> "No internet connection. Please check your network."
                    is java.net.SocketTimeoutException -> "Request timed out. Please try again."
                    is java.io.IOException -> "Network error occurred. Please try again."
                    else -> "Something went wrong. Please try again."
                }

                // Display the error message
                Common.alertErrorOrValidationDialog(requireActivity(), errorMessage)

                // Optionally, log the error for debugging purposes
                Log.e("API_ERROR", "onFailure: ${t.localizedMessage}", t)
            }

        })
    }

    //TODO SET ORDER HISTORY DATA
    private fun loadOrderHistory(orderHistoryDataList: ArrayList<OrderHistoryDataItem>) {

        lateinit var binding: RowOrderBinding
        wishListDataAdapter =
            object : BaseAdaptor<OrderHistoryDataItem, RowOrderBinding>(
                requireActivity(),
                orderHistoryDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: OrderHistoryDataItem,
                    position: Int
                ) {
                  //  holder?.let { setAnimation(it.itemView, position) }
                    binding.tvOrderId.text = "Order Id: "+orderHistoryDataList[position].orderNumber
//                    when (orderHistoryDataList[position].paymentType) {
//                        1 -> {
//                            binding.tvpaymenttype.text = "Cash"
//                        }
//                        2 -> {
//                            binding.tvpaymenttype.text = "Wallet"
//                        }
//                        3 -> {
//                            binding.tvpaymenttype.text = "RazorPay"
//                        }
//                        4 -> {
//                            binding.tvpaymenttype.text = "Stripe"
//                        }
//                        5 -> {
//                            binding.tvpaymenttype.text = "Flutterwave"
//                        }
//                        6 -> {
//                            binding.tvpaymenttype.text = "Paystack"
//                        }
//                    }
                    binding.tvPlacedTime.text ="Accepted at : "+ orderHistoryDataList[position].date?.let {
                        Common.getDate(it.substringBefore(" "))
                    }

                    if (orderHistoryDataList[position].order_status==4){
                        binding.llrating.visibility=View.VISIBLE
                    }else{
                        binding.llrating.visibility=View.INVISIBLE
                        binding.tvRatetext.visibility=View.GONE
                    }

                    binding.llrating.setOnClickListener {
                        val intent = Intent(requireActivity(),ActWriteReview::class.java)
                        intent.putExtra("proName", orderHistoryDataList[position].productName.toString())
                        intent.putExtra("proID", orderHistoryDataList[position].product_id.toString())
                        intent.putExtra("order_id", orderHistoryDataList[position].id.toString())
                        intent.putExtra("proImage", orderHistoryDataList[position].image.toString())
                        startActivity(intent)
                    }

                    Glide.with(requireActivity())
                        .load(filterHttps(orderHistoryDataList[position].image))
                        .transition(DrawableTransitionOptions.withCrossFade(100))
                        .placeholder(R.drawable.placeholder)
                        .into(binding.ivProduct)
                    binding.tvProductName.text=orderHistoryDataList[position].productName

                    binding.llRatestar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                        ratingBar.progress = rating.toInt()
                        Toast.makeText(requireActivity(), "You rated: $rating stars", Toast.LENGTH_SHORT).show()
                    }

                    binding.trackCard.setOnClickListener {
                        val orderId = orderHistoryDataList[position].orderNumber
                        Log.d("Fragment", "Order ID: $orderId")

                        val intent = Intent(requireActivity(), ActOrderDetails::class.java)
                        intent.putExtra("orderId", orderId.toString())
                        startActivity(intent)
                    }
                    binding.tvTrackOrder.setOnClickListener {
                        val id = orderHistoryDataList[position].id
                        Log.d("Fragment", "Order ID: $id")

                        val intent = Intent(requireActivity(), ActTrackOrder::class.java)
                        intent.putExtra("orderId", id.toString())
                        startActivity(intent)
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_order
                }

                override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
                    super.onViewDetachedFromWindow(holder)
                    holder.itemView.clearAnimation()
                }

                override fun getBinding(parent: ViewGroup): RowOrderBinding {
                    binding = RowOrderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        if (isAdded) {
            fragOrderHistoryBinding.rvOrderlist.apply {
                if (orderHistoryDataList.size > 0) {
                    fragOrderHistoryBinding.rvOrderlist.visibility = View.VISIBLE
                    fragOrderHistoryBinding.tvNoDataFound.visibility = View.GONE

                    layoutManager =
                        linearlayoutManager
                    itemAnimator = DefaultItemAnimator()
                    adapter = wishListDataAdapter
                } else {
                    fragOrderHistoryBinding.rvOrderlist.visibility = View.GONE
                    fragOrderHistoryBinding.tvNoDataFound.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setAnimation(view: View, position: Int) {
        val animation = AnimationUtils.loadAnimation(view.context, R.anim.item_fade_in_scale)

        // Apply a delay between items based on position
        val delay = position * 50L // 100ms delay between each item (adjustable)
        animation.startOffset = delay

        view.startAnimation(animation)
    }
    override fun onResume() {
        super.onResume()
        getCurrentLanguage(requireActivity(), false)

    }
}