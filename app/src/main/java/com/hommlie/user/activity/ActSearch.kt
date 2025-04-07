package com.hommlie.user.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.databinding.ActSearchBinding
import com.hommlie.user.databinding.RowSearchBinding
import com.hommlie.user.model.*
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.SharePreference
import com.hommlie.user.utils.SharePreference.Companion.getBooleanPref
import com.hommlie.user.utils.SharePreference.Companion.getStringPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class ActSearch : BaseActivity() {
    private lateinit var searchBinding: ActSearchBinding
    var searchList: ArrayList<SearchDataItem> = ArrayList()
    var tempsearchList: ArrayList<SearchDataItem>? = null
    private var viewAllDataAdapter: BaseAdaptor<SearchDataItem, RowSearchBinding>? = null

    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )
    override fun setLayout(): View = searchBinding.root

    override fun initView() {
        tempsearchList = ArrayList()
        searchBinding = ActSearchBinding.inflate(layoutInflater)
        searchBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        if (Common.isCheckNetwork(this@ActSearch)) {
            callApiSearchList()
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActSearch,
                resources.getString(R.string.no_internet)
            )
        }

        searchBinding.ivClear.setOnClickListener {
            searchBinding.edSearch.text=null
        }

        //TODO EDITTEXT VIEW DATA CHANGED METHOD
        searchBinding.edSearch.doAfterTextChanged {
            it?.let {
                searchList.clear()
                val searchText = it.toString()!!.toLowerCase()
                if (searchText.isNotEmpty()) {
                    searchBinding.rvsearch.visibility = View.VISIBLE
                    searchBinding.tvNoDataFound.visibility = View.GONE
                    tempsearchList?.filter {
                        it.productName?.toLowerCase()?.contains(searchText) ?: false
                    }?.let {
                        searchList.addAll(it)
                    }
                    searchBinding.ivClear.visibility=View.VISIBLE
                } else {
                    searchBinding.rvsearch.visibility = View.GONE
                    searchBinding.tvNoDataFound.visibility = View.VISIBLE
                    tempsearchList?.let { searchList.addAll(it) }
                    searchBinding.ivClear.visibility=View.GONE
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    loadSearchList(searchList)
                }, 100L)
            }
        }
    }

    //TODO CALL SEARCH API
    private fun callApiSearchList() {
        Common.showLoadingProgress(this@ActSearch)
        val hasmap = HashMap<String, String>()
        if (getBooleanPref(this@ActSearch, SharePreference.isLogin)) {
            hasmap["user_id"] = getStringPref(this@ActSearch, SharePreference.userId)!!
        } else {
            hasmap["user_id"] = ""
        }
        val call = ApiClient.getClient.getSearchProducts()
        call.enqueue(object : Callback<SearchProductResponse> {
            override fun onResponse(
                call: Call<SearchProductResponse>,
                response: Response<SearchProductResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        restResponce.data?.let {
                            searchList.addAll(it)
                            tempsearchList?.addAll(it)
                        }
                        loadSearchList(searchList)
                        openKeyboardWithCoroutine(this@ActSearch, searchBinding.edSearch)
                        if (intent.getStringExtra("voicesearch").toString()!="null"){
                            searchBinding.edSearch.setText(intent.getStringExtra("voicesearch").toString())
                        }
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActSearch,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<SearchProductResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActSearch,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SEARCH DATA SET
    private fun loadSearchList(tempsearchList: ArrayList<SearchDataItem>) {
        lateinit var binding: RowSearchBinding
        viewAllDataAdapter =
            object : BaseAdaptor<SearchDataItem, RowSearchBinding>(
                this@ActSearch,
                tempsearchList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: SearchDataItem,
                    position: Int
                ) {
                    binding.tvsearch.text = tempsearchList[position].productName
                    Glide.with(this@ActSearch)
                        .load(
                            Common.filterHttps(tempsearchList[position].productimage?.imageUrl)).into(binding.ivsearchproduct)
                    binding.ivsearchproduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                    val averageRating = 4.7f
                    displayDynamicStars(binding.ivRate,averageRating)


                    val productPrice = tempsearchList[position].productPrice?:0.0
                    val discountedPrice = tempsearchList[position].discountedPrice?.toDoubleOrNull()?:0.0

                    if (productPrice != null && discountedPrice != null && productPrice == discountedPrice) {
                        binding.tvProductDisprice.visibility = View.GONE
                        binding.tvdiscountpercentage.visibility=View.GONE
                        binding.ivDownarrow.visibility=View.GONE
                    } else {
                        binding.tvProductDisprice.visibility = View.VISIBLE
                        binding.tvdiscountpercentage.visibility=View.VISIBLE
                        binding.ivDownarrow.visibility=View.VISIBLE
                   //     binding.tvdiscountpercentage.text=(calculateDiscountPercentage(productPrice!!,discountedPrice!!)).toString()+"% off"
                        binding.tvdiscountpercentage.text = if (productPrice != null && discountedPrice != null) {
                            "${calculateDiscountPercentage(productPrice, discountedPrice)}% off"
                        } else {
                            "Price details unavailable"
                        }


                    }

                    binding.tvProductPrice.text ="\u20b9"+discountedPrice
                    binding.tvProductDisprice.text ="\u20b9"+productPrice



                    holder?.itemView?.setOnClickListener {
                        val intent = Intent(this@ActSearch, ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            tempsearchList[position].productimage?.productId.toString()
                        )
                        startActivity(intent)
                    }

                }


                override fun setItemLayout(): Int {
                    return R.layout.row_gravity
                }

                override fun getBinding(parent: ViewGroup): RowSearchBinding {
                    binding = RowSearchBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }

        searchBinding.rvsearch.apply {
            layoutManager =
                LinearLayoutManager(this@ActSearch, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = viewAllDataAdapter
        }
    }



    fun openKeyboardWithCoroutine(context: Context, view: View) {
        (context as? AppCompatActivity)?.lifecycleScope?.launch {
            delay(200)

            withContext(Dispatchers.Main) {
                view.requestFocus()
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        }
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

    fun calculateDiscountPercentage(originalPrice: Double?, discountedPrice: Double?): String {
        if (originalPrice == null || discountedPrice == null || originalPrice <= 0 || discountedPrice < 0) {
            return "Invalid prices"
        }

        val discount = originalPrice - discountedPrice
        val discountPercentage = (discount / originalPrice) * 100

        // Format to two decimal places
        return discountPercentage.toInt().toString()
    }



}