package com.hommlie.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.databinding.ActViewAllBinding
import com.hommlie.user.databinding.RowViewallBinding
import com.hommlie.user.model.*
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.SharePreference
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ActViewAll : BaseActivity() {

    private lateinit var viewAllBinding: ActViewAllBinding
    private var viewAllDataList = ArrayList<ViewAllDataItem>()
    private var filterAllDataList = ArrayList<FilterDataItem>()
    private var productAllDataList = ArrayList<ProductDataItem>()
    var currency: String = ""
    var currencyPosition: String = ""
    private var viewAllDataAdapter: BaseAdaptor<ViewAllDataItem, RowViewallBinding>? =
        null
    private var filterAllDataAdapter: BaseAdaptor<FilterDataItem, RowViewallBinding>? =
        null
    private var productAllDataAdapter: BaseAdaptor<ProductDataItem, RowViewallBinding>? =
        null
    private var gridLayoutManager: GridLayoutManager? = null
    private var gridLayoutManagerFilter: GridLayoutManager? = null
    private var gridLayoutManagerProduct: LinearLayoutManager? = null
    private var pos = 0
    private var currentPage = 1
    var total_pages: Int = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    var pastVisibleItems = 0
    var subcatId =""
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )

    override fun setLayout(): View = viewAllBinding.root

    override fun initView() {
        viewAllBinding = ActViewAllBinding.inflate(layoutInflater)
        gridLayoutManager = GridLayoutManager(this@ActViewAll, 1, GridLayoutManager.VERTICAL, false)
        gridLayoutManagerFilter =
            GridLayoutManager(this@ActViewAll, 1, GridLayoutManager.VERTICAL, false)
        gridLayoutManagerProduct =
            LinearLayoutManager(this@ActViewAll,LinearLayoutManager.VERTICAL, false)
        currency = SharePreference.getStringPref(this@ActViewAll, SharePreference.Currency)!!
        currencyPosition =
            SharePreference.getStringPref(this@ActViewAll, SharePreference.CurrencyPosition)!!
        var title: String? = intent.getStringExtra("title")
        var type: String = ""
        viewAllBinding.tvviewall.text = title.toString()
        loadFeaturedProducts(viewAllDataList)
      //  loadFilter(filterAllDataList)
        loadProductData(productAllDataList)
        if (Common.isCheckNetwork(this@ActViewAll)) {
            if (title == "Cleaning Services") {
                viewAllBinding.rvFilterall.visibility = View.GONE
                viewAllBinding.rvProduct.visibility = View.GONE
                viewAllBinding.rvViewall.visibility = View.VISIBLE
                viewAllData(currentPage.toString(),"cleaning_services")
            } else if (title == "Pest Control") {
                viewAllBinding.rvFilterall.visibility = View.GONE
                viewAllBinding.rvProduct.visibility = View.GONE
                viewAllBinding.rvViewall.visibility = View.VISIBLE
               // title = "new_products"
                viewAllData(currentPage.toString(), "pest_control")
            } else if (title == "Most Booked Service") {
                viewAllBinding.rvFilterall.visibility = View.GONE
                viewAllBinding.rvProduct.visibility = View.GONE
                viewAllBinding.rvViewall.visibility = View.VISIBLE
              //  title = "hot_products"
                viewAllData(currentPage.toString(), "most_booked_services")
            }else if (title == "Mosquito Mesh") {
                viewAllBinding.rvFilterall.visibility = View.GONE
                viewAllBinding.rvProduct.visibility = View.GONE
                viewAllBinding.rvViewall.visibility = View.VISIBLE
                //  title = "hot_products"
                viewAllData(currentPage.toString(), "mosquito_mesh")
            }else if (title == "Safety-Pro Netting") {
                viewAllBinding.rvFilterall.visibility = View.GONE
                viewAllBinding.rvProduct.visibility = View.GONE
                viewAllBinding.rvViewall.visibility = View.VISIBLE
                //  title = "hot_products"
                viewAllData(currentPage.toString(), "safety_pro_netting")
            }
            else {
                title = intent.getStringExtra("subcategory_name").toString()
                subcatId =intent.getStringExtra("subcategory_id").toString()
                viewAllBinding.tvviewall.text = title.toString()
                productData(currentPage.toString())
                Common.getToast(this@ActViewAll,subcatId)
            }
        } else {
            Common.alertErrorOrValidationDialog(
                this,
                resources.getString(R.string.no_internet)
            )
        }

        viewAllBinding.ivBack.setOnClickListener {
            finish()
        }
        viewAllBinding.tvNoDataFound.setOnClickListener {
            finish()
        }
        viewAllBinding.searchCard.setOnClickListener { openActivity(ActSearch::class.java) }
        viewAllBinding.ivFilter.setOnClickListener {
            filterAllDataList.clear()
            val dialog = BottomSheetDialog(this@ActViewAll)
            if (Common.isCheckNetwork(this@ActViewAll)) {
                val view =
                    layoutInflater.inflate(R.layout.row_bottomsheetsortby, null)
                val latest = view.findViewById<TextView>(R.id.tvlatest)
                val pricelowtohigh = view.findViewById<TextView>(R.id.tvpricelowtohigh)
                val pricehightolow = view.findViewById<TextView>(R.id.tvpricehightolow)
                val rattinglowtohigh = view.findViewById<TextView>(R.id.tvrattinglowtohigh)
                val rattinghightolow = view.findViewById<TextView>(R.id.tvrattinghightolow)
                val close = view.findViewById<ImageView>(R.id.iv_close)

                latest.setOnClickListener {
                    type = "new"
                    viewAllBinding.rvFilterall.visibility = View.VISIBLE
                    viewAllBinding.rvViewall.visibility = View.GONE
                    viewAllBinding.rvProduct.visibility = View.GONE
                    if (Common.isCheckNetwork(this@ActViewAll)) {
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {
                            callApiFilter(
                                SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!, type
                            )
                        } else {
                            openActivity(ActSignUp::class.java)
                            finish()
                        }
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            resources.getString(R.string.no_internet)
                        )
                    }
                    dialog.dismiss()
                }
                pricelowtohigh.setOnClickListener {
                    type = "price-low-to-high"
                    viewAllBinding.rvFilterall.visibility = View.VISIBLE
                    viewAllBinding.rvViewall.visibility = View.GONE
                    viewAllBinding.rvProduct.visibility = View.GONE
                    if (Common.isCheckNetwork(this@ActViewAll)) {
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {
                            callApiFilter(
                                SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!, type
                            )
                        } else {
                            openActivity(ActSignUp::class.java)
                            finish()
                        }
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            resources.getString(R.string.no_internet)
                        )
                    }
                    dialog.dismiss()
                }
                pricehightolow.setOnClickListener {
                    type = "price-high-to-low"
                    viewAllBinding.rvFilterall.visibility = View.VISIBLE
                    viewAllBinding.rvViewall.visibility = View.GONE
                    viewAllBinding.rvProduct.visibility = View.GONE
                    if (Common.isCheckNetwork(this@ActViewAll)) {
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {

                            callApiFilter(
                                SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!, type
                            )
                        } else {
                            openActivity(ActSignUp::class.java)
                            finish()
                        }
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            resources.getString(R.string.no_internet)
                        )
                    }
                    dialog.dismiss()
                }
                rattinglowtohigh.setOnClickListener {
                    type = "ratting-low-to-high"
                    viewAllBinding.rvFilterall.visibility = View.VISIBLE
                    viewAllBinding.rvViewall.visibility = View.GONE
                    viewAllBinding.rvProduct.visibility = View.GONE
                    if (Common.isCheckNetwork(this@ActViewAll)) {
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {
                            callApiFilter(
                                SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!, type
                            )
                        } else {
                            openActivity(ActSignUp::class.java)
                            finish()
                        }
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            resources.getString(R.string.no_internet)
                        )
                    }
                    dialog.dismiss()
                }
                rattinghightolow.setOnClickListener {
                    type = "ratting-high-to-low"
                    viewAllBinding.rvFilterall.visibility = View.VISIBLE
                    viewAllBinding.rvViewall.visibility = View.GONE
                    viewAllBinding.rvProduct.visibility = View.GONE
                    if (Common.isCheckNetwork(this@ActViewAll)) {
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {

                            callApiFilter(
                                SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!, type
                            )
                        } else {
                            openActivity(ActSignUp::class.java)
                            finish()
                        }
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            resources.getString(R.string.no_internet)
                        )
                    }
                    dialog.dismiss()
                }
                close.setOnClickListener { dialog.dismiss() }
                dialog.setCancelable(false)
                dialog.setContentView(view)
                dialog.show()
            } else {
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.no_internet)
                )
                dialog.dismiss()
            }
        }

        viewAllBinding.rvViewall.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager!!.childCount
                    totalItemCount = gridLayoutManager!!.itemCount
                    pastVisibleItems = gridLayoutManager!!.findFirstVisibleItemPosition()
                    if (currentPage < total_pages) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            currentPage += 1
                            viewAllData(currentPage.toString(), title.toString())
                        }
                    }
                }
            }
        })
        viewAllBinding.rvFilterall.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManagerFilter!!.childCount
                    totalItemCount = gridLayoutManagerFilter!!.itemCount
                    pastVisibleItems = gridLayoutManagerFilter!!.findFirstVisibleItemPosition()
                    if (currentPage < total_pages) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            currentPage += 1
                            callApiFilter(currentPage.toString(), type)
                        }
                    }
                }
            }
        })
        viewAllBinding.rvProduct.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManagerProduct!!.childCount
                    totalItemCount = gridLayoutManagerProduct!!.itemCount
                    pastVisibleItems = gridLayoutManagerProduct!!.findFirstVisibleItemPosition()
                    if (currentPage < total_pages) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            currentPage += 1
                            productData(currentPage.toString())
                        }
                    }
                }
            }
        })
    }

    //TODO CALL PRODUCT API
    private fun productData(currentPage: String) {
        Log.v("PRODUCT API 1",currentPage)
        viewAllBinding.rvProduct.visibility = View.VISIBLE
        viewAllBinding.rvViewall.visibility = View.GONE
        viewAllBinding.rvFilterall.visibility = View.GONE
        Common.showLoadingProgress(this@ActViewAll)
        val hasmap = HashMap<String, String>()
      //  hasmap["user_id"] = SharePreference.getStringPref(this@ActViewAll, SharePreference.userId)!!
        hasmap["subcategory_id"] = //["innersubcategory_id"]
                                subcatId

        val call = ApiClient.getClient.getProduct(hasmap)
        call.enqueue(object : Callback<ProductResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                Common.dismissLoadingProgress()
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        if (currentPage == "1") {
                            productAllDataList.clear()
                        }
                        restResponce.data?.let {
                            productAllDataList.addAll(it)
                        }
//                        this@ActViewAll.currentPage = restResponce.data?.currentPage!!.toInt()
//                        this@ActViewAll.total_pages = restResponce.data.lastPage!!.toInt()
                        productAllDataAdapter?.notifyDataSetChanged()
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()

                        if (restResponce.message == "No data found") {
                            viewAllBinding.tvNoDataFound.visibility = View.VISIBLE
                        }
                    }
                }else {
                    viewAllBinding.tvNoDataFound.visibility=View.VISIBLE
                    when (response.code()) {
                        404 -> {
                            Common.alertErrorOrValidationDialog(this@ActViewAll, "Resource not found.")
                        }
                        500 -> {
                            Common.alertErrorOrValidationDialog(this@ActViewAll, "Server error. Please try again later.")
                        }
                        else -> {
                            Common.alertErrorOrValidationDialog(this@ActViewAll, "Unexpected error: ${response.code()}")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                viewAllBinding.tvNoDataFound.visibility=View.VISIBLE
                val errorMessage = when (t) {
                    is java.net.UnknownHostException -> "No internet connection. Please check your network."
                    is java.net.SocketTimeoutException -> "Request timed out. Please try again."
                    is java.io.IOException -> "Network error occurred. Please try again."
                    else -> "Something went wrong. Please try again."
                }

                // Display the error message
                Common.alertErrorOrValidationDialog(this@ActViewAll, errorMessage)

                // Optionally, log the error for debugging purposes
                Log.e("API_ERROR", "onFailure: ${t.localizedMessage}", t)
            }
        })
    }

    //TODO PRODUCT DATA SET
    private fun loadProductData(productAllDataList: ArrayList<ProductDataItem>) {
        lateinit var binding: RowViewallBinding
        productAllDataAdapter =
            object : BaseAdaptor<ProductDataItem, RowViewallBinding>(
                this@ActViewAll,
                productAllDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: ProductDataItem,
                    position: Int
                ) {

                 /*   if (productAllDataList[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            productAllDataList[position].rattings?.get(0)?.avgRatting?.toDouble()
                                .toString()
                    }

                    if (productAllDataList.get(position).isWishlist == 0) {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_dislike,
                                null
                            )
                        )
                    } else {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_like,
                                null
                            )
                        )
                    }

                    binding.ivwishlist.setOnClickListener {
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {
                            if (productAllDataList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    productAllDataList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(this@ActViewAll)) {
                                    callApiFavouriteProduct(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActViewAll,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }

                            if (productAllDataList[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    productAllDataList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(this@ActViewAll)) {
                                    callApiRemoveFavouriteProduct(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActViewAll,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }


                        } else {
                            openActivity(ActSignUp::class.java)
                            finish()
                        }
                    }  */

                    binding.tvProductName.text = productAllDataList[position].productName
//                    if(productAllDataList[position].productPrice!!.toDouble().equals(productAllDataList[position].discountedPrice!!.toDouble())){
//                        binding.tvProductDisprice.visibility = View.GONE
//                    }
                    val productPrice = productAllDataList[position].productPrice?.toDoubleOrNull()
                    val discountedPrice = productAllDataList[position].discountedPrice?.toDoubleOrNull()

                    if (productPrice != null && discountedPrice != null && productPrice == discountedPrice) {
                        binding.tvProductDisprice.visibility = View.GONE
                        binding.tvdiscountpercentage.visibility=View.GONE
                        binding.ivDownarrow.visibility=View.GONE
                    } else {
                        binding.tvProductDisprice.visibility = View.VISIBLE
                        binding.tvdiscountpercentage.visibility=View.VISIBLE
                        binding.ivDownarrow.visibility=View.VISIBLE
                        binding.tvdiscountpercentage.text=(calculateDiscountPercentage(productPrice!!,discountedPrice!!)).toString()+"% off"
                    }

                    if (currencyPosition == "left") {

                        binding.tvProductPrice.text ="\u20b9"+productAllDataList[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+ productAllDataList[position].productPrice
                    } else {
                        binding.tvProductPrice.text ="\u20b9"+productAllDataList[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+productAllDataList[position].productPrice
                    }
                    Glide.with(this@ActViewAll)
                        .load(filterHttps(productAllDataList[position].productimage?.imageUrl))
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                    val averageRating = 4.6f
                  //  displayDynamicStars(binding.ivRate,averageRating)

                    productAllDataList[position].rating?.let { displayDynamicStars(binding.ivRate, it)
                        binding.tvRatePro.text=it.toString()}

                    holder?.itemView?.setOnClickListener {
                        val intent = Intent(this@ActViewAll, ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            productAllDataList[position].productimage?.productId.toString()
                        )
                        startActivity(intent)
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_featuredproduct
                }

                override fun getBinding(parent: ViewGroup): RowViewallBinding {
                    binding = RowViewallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }

        viewAllBinding.rvProduct.apply {
            layoutManager = gridLayoutManagerProduct
            itemAnimator = DefaultItemAnimator()
            adapter = productAllDataAdapter
        }
    }

    //TODO CALL FILTER API
    private fun callApiFilter(userId: String, type: String) {
        Common.showLoadingProgress(this@ActViewAll)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] = userId
        hasmap["type"] = type
        val call = ApiClient.getClient.getFilter(currentPage.toString(), hasmap)
        call.enqueue(object : Callback<GetFilterResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<GetFilterResponse>,
                response: Response<GetFilterResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        if (currentPage == 1) {
                            filterAllDataList.clear()
                        }
                        restResponce.data?.data?.let {
                            filterAllDataList.addAll(it)
                        }
                        this@ActViewAll.total_pages = restResponce.data?.lastPage!!.toInt()
                        filterAllDataAdapter?.notifyDataSetChanged()
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()

                        if (restResponce.message == "No data found") {
                            viewAllBinding.tvNoDataFound.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetFilterResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO FILTER DATA SET
    private fun loadFilter(filterAllDataList: ArrayList<FilterDataItem>) {
        lateinit var binding: RowViewallBinding
        filterAllDataAdapter =
            object : BaseAdaptor<FilterDataItem, RowViewallBinding>(
                this@ActViewAll,
                filterAllDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: FilterDataItem,
                    position: Int
                ) {
                   /*  if (filterAllDataList[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            filterAllDataList[position].rattings?.get(0)?.avgRatting?.toDouble()
                                .toString()
                    }
                    if (filterAllDataList.get(position).isWishlist == 0) {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_dislike,
                                null
                            )
                        )
                    } else {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_like,
                                null
                            )
                        )
                    }

                    binding.ivwishlist.setOnClickListener {
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {
                            if (filterAllDataList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    filterAllDataList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(this@ActViewAll)) {
                                    callApiFavouriteFilter(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActViewAll,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }

                            if (filterAllDataList[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    filterAllDataList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(this@ActViewAll)) {
                                    callApiRemoveFavouriteFilter(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActViewAll,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }

                        } else {
                            openActivity(ActSignUp::class.java)
                            finish()
                        }
                    }  */

                    binding.tvProductName.text = filterAllDataList[position].productName
                    if (currencyPosition == "left") {

                        binding.tvProductPrice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    filterAllDataList[position].productPrice!!.toDouble()
                                )
                            )
                        binding.tvProductDisprice.text =
                            currency.plus(
                                String.format(
                                    Locale.US,
                                    "%,.2f",
                                    filterAllDataList[position].discountedPrice!!.toDouble()
                                )
                            )
                    } else {
                        binding.tvProductPrice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                filterAllDataList[position].productPrice!!.toDouble()
                            )) + "" + currency
                        binding.tvProductDisprice.text =
                            (String.format(
                                Locale.US,
                                "%,.2f",
                                filterAllDataList[position].discountedPrice!!.toDouble()
                            )) + "" + currency
                    }
                    Glide.with(this@ActViewAll)
                        .load(filterHttps(filterAllDataList[position].productimage?.imageUrl))
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                    holder?.itemView?.setOnClickListener {
                        val intent = Intent(this@ActViewAll, ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            filterAllDataList[position].productimage?.productId.toString()
                        )
                        startActivity(intent)
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_featuredproduct
                }

                override fun getBinding(parent: ViewGroup): RowViewallBinding {
                    binding = RowViewallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }

        viewAllBinding.rvFilterall.apply {
            layoutManager = gridLayoutManagerFilter
            itemAnimator = DefaultItemAnimator()
            adapter = filterAllDataAdapter
        }
    }

    //TODO CALL VIEW ALL API
    private fun viewAllData(currentPage: String, title: String) {
        Log.v("aaaaa",currentPage + "--" +title)
        Common.showLoadingProgress(this@ActViewAll)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] = SharePreference.getStringPref(this@ActViewAll, SharePreference.userId)!!
        hasmap["type"] = title
        val call = ApiClient.getClient.setViewAllListing(currentPage, hasmap)
        call.enqueue(object : Callback<ViewAllListResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ViewAllListResponse>,
                response: Response<ViewAllListResponse>
            ) {
                Common.dismissLoadingProgress()
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        if (currentPage == "1") {
                            viewAllDataList.clear()
                        }
                        restResponce.alldata?.let {
                            viewAllDataList.addAll(it)
                        }
                     //   this@ActViewAll.total_pages = restResponce.alldata?.lastPage!!.toInt()
                        viewAllDataAdapter?.notifyDataSetChanged()
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()

                        if (restResponce.message == "No data found") {
                            viewAllBinding.tvNoDataFound.visibility = View.VISIBLE
                        }
                    }
                }else {
                    viewAllBinding.tvNoDataFound.visibility=View.VISIBLE
                    when (response.code()) {
                        404 -> {
                            Common.alertErrorOrValidationDialog(this@ActViewAll, "Resource not found.")
                        }
                        500 -> {
                            Common.alertErrorOrValidationDialog(this@ActViewAll, "Server error. Please try again later.")
                        }
                        else -> {
                            Common.alertErrorOrValidationDialog(this@ActViewAll, "Unexpected error: ${response.code()}")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ViewAllListResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                viewAllBinding.tvNoDataFound.visibility=View.VISIBLE
                val errorMessage = when (t) {
                    is java.net.UnknownHostException -> "No internet connection. Please check your network."
                    is java.net.SocketTimeoutException -> "Request timed out. Please try again."
                    is java.io.IOException -> "Network error occurred. Please try again."
                    else -> "Something went wrong. Please try again."
                }

                // Display the error message
                Common.alertErrorOrValidationDialog(this@ActViewAll, errorMessage)

                // Optionally, log the error for debugging purposes
                Log.e("API_ERROR", "onFailure: ${t.localizedMessage}", t)
            }
        })
    }

    //TODO VIEW ALL DATA SET
    private fun loadFeaturedProducts(viewAllDataList: ArrayList<ViewAllDataItem>) {
        lateinit var binding: RowViewallBinding
        viewAllDataAdapter =
            object : BaseAdaptor<ViewAllDataItem, RowViewallBinding>(
                this@ActViewAll,
                viewAllDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: ViewAllDataItem,
                    position: Int
                ) {
                 /*   if (viewAllDataList[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            viewAllDataList[position].rattings?.get(0)?.avgRatting?.toDouble()
                                .toString()
                    }
                    if (viewAllDataList[position].isWishlist == 0) {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_dislike,
                                null
                            )
                        )
                    } else {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_like,
                                null
                            )
                        )
                    }

                    binding.ivwishlist.setOnClickListener {
                        if (SharePreference.getBooleanPref(
                                this@ActViewAll,
                                SharePreference.isLogin
                            )
                        ) {
                            if (viewAllDataList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    viewAllDataList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(this@ActViewAll)) {
                                    callApiFavourite(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActViewAll,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                            if (viewAllDataList[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    viewAllDataList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActViewAll,
                                    SharePreference.userId
                                )!!

                                if (Common.isCheckNetwork(this@ActViewAll)) {
                                    callApiRemoveFavourite(map, position)
                                } else {
                                    Common.alertErrorOrValidationDialog(
                                        this@ActViewAll,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                        } else {
                            openActivity(ActSignUp::class.java)
                            finish()
                        }
                    }  */
                    binding.tvProductName.text = viewAllDataList[position].productName
                    if (currencyPosition == "left") {

                        binding.tvProductPrice.text ="\u20b9"+viewAllDataList[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+ viewAllDataList[position].productPrice
                    } else {
                        binding.tvProductPrice.text ="\u20b9"+viewAllDataList[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+ viewAllDataList[position].productPrice
                    }
                    Glide.with(this@ActViewAll)
                        .load(filterHttps(viewAllDataList[position].productimage?.imageUrl))
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))


                    viewAllDataList[position].rating?.let { displayDynamicStars(binding.ivRate, it)
                        binding.tvRatePro.text=it.toString()}

                    holder?.itemView?.setOnClickListener {
                        val intent = Intent(this@ActViewAll, ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            viewAllDataList[position].productimage?.productId.toString()
                        )
                        startActivity(intent)
                    }
                }
                override fun setItemLayout(): Int {
                    return R.layout.row_featuredproduct
                }
                override fun getBinding(parent: ViewGroup): RowViewallBinding {
                    binding = RowViewallBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        viewAllBinding.rvViewall.apply {
            layoutManager = gridLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = viewAllDataAdapter
        }
    }

    //TODO CALL API REMOVE FAVOURITE
    private fun callApiRemoveFavourite(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(this@ActViewAll)
        val call = ApiClient.getClient.setRemoveFromWishList(map)
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {
                        Common.dismissLoadingProgress()
                        viewAllDataList[position].isWishlist = 0
                        viewAllDataAdapter!!.notifyItemChanged(position)

                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO CALL FAVOURITE API
    private fun callApiFavourite(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(this@ActViewAll)
        val call = ApiClient.getClient.setAddToWishList(map)
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {
                        Common.dismissLoadingProgress()
                        viewAllDataList[position].isWishlist = 1
                        viewAllDataAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO CALL API REMOVE FAVOURITE
    private fun callApiRemoveFavouriteProduct(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(this@ActViewAll)
        val call = ApiClient.getClient.setRemoveFromWishList(map)
        Log.e("remove-->", Gson().toJson(map))
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {

                        Common.dismissLoadingProgress()
                        productAllDataList[position].isWishlist = 0
                        productAllDataAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO CALL FAVOURITE API
    private fun callApiFavouriteProduct(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(this@ActViewAll)
        val call = ApiClient.getClient.setAddToWishList(map)
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {
                        Common.dismissLoadingProgress()
                        productAllDataList[position].isWishlist = 1
                        productAllDataAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            restResponse.message
                        )
                    }
                }
            }
            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO CALL API REMOVE FAVOURITE
    private fun callApiRemoveFavouriteFilter(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(this@ActViewAll)
        val call = ApiClient.getClient.setRemoveFromWishList(map)
        Log.e("remove-->", Gson().toJson(map))
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {

                        Common.dismissLoadingProgress()
                        filterAllDataList[position].isWishlist = 0
                        filterAllDataAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            restResponse.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO CALL FAVOURITE API
    private fun callApiFavouriteFilter(map: HashMap<String, String>, position: Int) {
        Common.showLoadingProgress(this@ActViewAll)
        val call = ApiClient.getClient.setAddToWishList(map)
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status == 1) {
                        Common.dismissLoadingProgress()
                        filterAllDataList[position].isWishlist = 1
                        filterAllDataAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActViewAll,
                            restResponse.message
                        )
                    }
                }
            }
            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActViewAll,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    fun filterHttps(url: String?): String? {
        var str = url
        val oldValue = "https"
        val newValue = "http"
        val output = str?.replace(oldValue,newValue)
        return output
    }



    private fun displayDynamicStars(rating1: LinearLayout, rating: Float) {
        rating1.removeAllViews()

        for (i in 0 until 5) {
            val starView = ImageView(this)

            // Combine empty and filled stars into a LayerDrawable
            val emptyDrawable = getDrawable(R.drawable.ic_star_empty)
            val filledDrawable = ClipDrawable(getDrawable(R.drawable.ic_filled_star), Gravity.LEFT, ClipDrawable.HORIZONTAL)

            // Set the level (fill percentage) for each star
            val starLevel = calculateStarFillLevel(rating - i)
            filledDrawable.level = starLevel

            // Apply LayerDrawable combining empty and filled stars
            val layerDrawable = LayerDrawable(arrayOf(emptyDrawable, filledDrawable))
            starView.setImageDrawable(layerDrawable)

            // Set star size
            val params = LinearLayout.LayoutParams(45, 45) // Width x Height
            params.setMargins(4, 0, 4, 0)
            starView.layoutParams = params

            // Add the starView to the LinearLayout
            rating1.addView(starView)
        }
    }

    /**
     * Calculates the fill level for a star:
     * - 0 -> empty
     * - 10000 -> full
     * - Any intermediate value for fractional fill
     */
    private fun calculateStarFillLevel(value: Float): Int {
        return when {
            value >= 1 -> 10000  // Fully filled star
            value > 0 -> (value * 10000).toInt()  // Partially filled star
            else -> 0  // Empty star
        }
    }

    fun calculateDiscountPercentage(originalPrice: Double, discountedPrice: Double): Int {
        if (originalPrice <= 0 || discountedPrice < 0) {
            throw IllegalArgumentException("Prices must be positive and original price should be greater than 0")
        }
        val discount = originalPrice - discountedPrice
        val discountPercentage = (discount / originalPrice) * 100
        return discountPercentage.toInt() // Round to 2 decimal places
    }


}