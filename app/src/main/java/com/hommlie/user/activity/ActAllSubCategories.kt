package com.hommlie.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.hommlie.user.R
import com.hommlie.user.adapter.SubCateAdapter
import com.hommlie.user.api.ApiClient
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.databinding.ActAllSubCategoriesBinding
import com.hommlie.user.databinding.RowViewallBinding
import com.hommlie.user.model.ProductDataItem
import com.hommlie.user.model.SubCategoriesResponse
import com.hommlie.user.model.SubcategoryItem
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.alertErrorOrValidationDialog
import com.hommlie.user.utils.Common.dismissLoadingProgress
import com.hommlie.user.utils.Common.filterHttps
import com.hommlie.user.utils.Common.showLoadingProgress
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.set


class ActAllSubCategories : BaseActivity() {
    private lateinit var allSubCategoriesBinding: ActAllSubCategoriesBinding
    var cateId = ""
    var cartSubList: ArrayList<SubcategoryItem>? = ArrayList()
    var productdata: ArrayList<ProductDataItem>? = ArrayList()
    var linearLayoutManager: GridLayoutManager? = null
    private var productAllDataAdapter: BaseAdaptor<ProductDataItem, RowViewallBinding>? = null
   // var videoView : VideoView? =null

    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )

    override fun setLayout(): View = allSubCategoriesBinding.root

    override fun initView() {
        allSubCategoriesBinding = ActAllSubCategoriesBinding.inflate(layoutInflater)

       // window.statusBarColor = ContextCompat.getColor(this@ActAllSubCategories, R.color.colorPrimary)

        window.decorView.systemUiVisibility = 0
      //  window.statusBarColor = Color.WHITE

        linearLayoutManager = GridLayoutManager(this@ActAllSubCategories,2, GridLayoutManager.VERTICAL, false)

        allSubCategoriesBinding.ivBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.no_animation,R.anim.slide_out)
            setResult(RESULT_OK)
        }
        allSubCategoriesBinding.tvNoDataFound.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.no_animation,R.anim.slide_out)
            setResult(RESULT_OK)
        }

        val extras = intent.extras
        val cat_id = extras!!.getString("cat_id")
        val categoryName = extras.getString("categoryName")

//        if (extras.getString("categoryGraphic").toString()=="null"){
//            allSubCategoriesBinding.ivBannerImage.visibility=View.GONE
//        }else {
//            Glide.with(this@ActAllSubCategories)
//                .load(extras.getString("categoryGraphic").toString())
//                .placeholder(R.drawable.ic_placeholder)
//                .into(allSubCategoriesBinding.ivBannerImage)
//        }

        allSubCategoriesBinding.nsRvProduct.viewTreeObserver.addOnScrollChangedListener(scrollListener)


        allSubCategoriesBinding.searchCard.setOnClickListener { openActivity(ActSearch::class.java) }

//       videoView=allSubCategoriesBinding.videoview
//        //val videoUri = Uri.parse("https://techmonkshub.info/hommly/salon_demo_video.mp4")
//        val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.dummy_video)
//        videoView!!.setVideoURI(videoUri)
//        videoView!!.setOnPreparedListener { mp ->
//            mp.setVolume(0f, 0f)
//            videoView!!.start()
//        }
//        videoView!!.setOnCompletionListener { vp->videoView!!.start() }
//        videoView!!.start()

        allSubCategoriesBinding.Subcategoriename.text = categoryName.toString()
        cateId = intent.getStringExtra("cat_id")!!

        if (Common.isCheckNetwork(this@ActAllSubCategories)) {
            if (cat_id != null) {
                callApiSubCategories(cat_id)
            }
        } else {
            alertErrorOrValidationDialog(
                this@ActAllSubCategories,
                resources.getString(R.string.no_internet)
            )
        }
    }

    // TODO API SUB CATEGORIES CALL
    private fun callApiSubCategories(cat_id: String) {
        showLoadingProgress(this@ActAllSubCategories)
        val map = HashMap<String, String>()
        map["cat_id"] = cat_id
        val call = ApiClient.getClient.getSubCategoriesDetail(map)
        call.enqueue(object : Callback<SubCategoriesResponse> {
            override fun onResponse(
                call: Call<SubCategoriesResponse>,
                response: Response<SubCategoriesResponse>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        if (restResponce.data?.subcategory?.size!! > 0) {

                            val filteredSubCategories = restResponce.data.subcategory
//                                .filter {
//                                it.innersubcategory?.size != 1
//                            }

                            val rating = restResponce.data.mainCategoryData?.totalReviews
                            val temprating_inK = rating?.toDouble()?.div(1000)?.let { String.format("%.1f", it) }
                            allSubCategoriesBinding.tvReview.text = "${restResponce.data.mainCategoryData?.avgRating} ( ${temprating_inK}k bookings )"

                            Glide.with(this@ActAllSubCategories)
                                .load(restResponce.data.mainCategoryData?.motionGraphics)
                                .placeholder(R.drawable.ic_placeholder)
                                .into(allSubCategoriesBinding.ivBannerImage)

                            cartSubList?.clear() // Clear the existing list if necessary
                            cartSubList?.addAll(filteredSubCategories)

                            // Filter subcategories where `innerdata.product != 1` and add to productdata
                            val remainingSubCategories  = restResponce.data.subcategory
//                                .filter{
//                                it.innersubcategory?.size == 1
//                            }
                            productdata?.clear()
                            productdata?.addAll(remainingSubCategories.mapNotNull { subcategoryItem ->
                                // Assuming you have a way to convert `innersubcategory` to `ViewAllDataItem`
                                subcategoryItem.innersubcategory?.firstOrNull()?.let { viewAllDataItem ->
                                    // Map the first item in innersubcategory to ViewAllDataItem
                                    viewAllDataItem
                                }
                            })



                           // cartSubList = restResponce.data.subcategory
                            if (!cartSubList.isNullOrEmpty()) {
                                allSubCategoriesBinding.nsRvProduct.visibility = View.VISIBLE
                                allSubCategoriesBinding.rvSubcate.visibility = View.VISIBLE
                                allSubCategoriesBinding.tvNoDataFound.visibility = View.GONE
                                val adapter = SubCateAdapter(cartSubList!!)
                                allSubCategoriesBinding.rvSubcate.layoutManager = LinearLayoutManager(this@ActAllSubCategories, LinearLayoutManager.VERTICAL, false)
                                allSubCategoriesBinding.rvSubcate.adapter = adapter
                            }else{
                                allSubCategoriesBinding.tvNoDataFound.visibility=View.VISIBLE
                                allSubCategoriesBinding.nsRvProduct.visibility = View.GONE
                                allSubCategoriesBinding.rvSubcate.visibility = View.GONE
                            }
                            if (!productdata.isNullOrEmpty()) {
                            //    allSubCategoriesBinding.nsRvProduct.visibility = View.VISIBLE
                                allSubCategoriesBinding.tvNoDataFound.visibility = View.GONE
                            //    allSubCategoriesBinding.view1.visibility=View.VISIBLE
                               // loadProductData(productdata!!) not required thats why
                            }
                        } else {
                            allSubCategoriesBinding.rvSubcate.visibility = View.GONE
                            allSubCategoriesBinding.tvNoDataFound.visibility = View.VISIBLE
                            dismissLoadingProgress()
                           // alertErrorOrValidationDialog(this@ActAllSubCategories, restResponce.message.toString())
                        }
                    }else{
                        allSubCategoriesBinding.rvSubcate.visibility = View.GONE
                        allSubCategoriesBinding.tvNoDataFound.visibility = View.VISIBLE
                        dismissLoadingProgress()
                        //alertErrorOrValidationDialog(this@ActAllSubCategories, restResponce.message.toString())
                    }
                }else{
                    Common.dismissLoadingProgress()
                    when (response.code()) {
                        404 -> {
                            Common.alertErrorOrValidationDialog(this@ActAllSubCategories, "Resource not found.")
                        }
                        500 -> {
                            Common.alertErrorOrValidationDialog(this@ActAllSubCategories, "Server error. Please try again later.")
                        }
                        else -> {
                            Common.alertErrorOrValidationDialog(this@ActAllSubCategories, "Unexpected error: ${response.code()}")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SubCategoriesResponse>, t: Throwable) {
                dismissLoadingProgress()
                val errorMessage = when (t) {
                    is java.net.UnknownHostException -> "No internet connection. Please check your network."
                    is java.net.SocketTimeoutException -> "Request timed out. Please try again."
                    is java.io.IOException -> "Network error occurred. Please try again."
                    else -> "Something went wrong. Please try again."
                }

                // Display the error message
                Common.alertErrorOrValidationDialog(this@ActAllSubCategories, errorMessage)

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
                this@ActAllSubCategories,
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
                    if(productAllDataList[position].productPrice!!.toDouble().equals(productAllDataList[position].discountedPrice!!.toDouble())){
                        binding.tvProductDisprice.visibility = View.GONE
                    }

                        binding.tvProductPrice.text ="\u20b9"+productAllDataList[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+productAllDataList[position].productPrice

                    Glide.with(this@ActAllSubCategories)
                        .load(filterHttps(productAllDataList[position].productimage?.imageUrl))
                        .placeholder(R.drawable.ic_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                    holder?.itemView?.setOnClickListener {
                        val intent = Intent(this@ActAllSubCategories, ActProductDetails::class.java)
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

//        allSubCategoriesBinding.rvProduct.apply {
//            layoutManager = linearLayoutManager
//            itemAnimator = DefaultItemAnimator()
//            adapter = productAllDataAdapter
//        }  when use this funtion so uncomment these lines
    }


    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActAllSubCategories, false)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.no_animation,R.anim.slide_out)
    }


    private val scrollListener = ViewTreeObserver.OnScrollChangedListener {
        val visibleRect = Rect()
        val isVisible = allSubCategoriesBinding.ivBannerImage.getGlobalVisibleRect(visibleRect)

        if (isVisible) {
            allSubCategoriesBinding.Subcategoriename.visibility = View.GONE
            allSubCategoriesBinding.clSubcategoriename.background = null
            allSubCategoriesBinding.view.background = null
        } else {
            allSubCategoriesBinding.Subcategoriename.visibility = View.VISIBLE
            allSubCategoriesBinding.clSubcategoriename.setBackgroundColor(getColor(R.color.white))
            allSubCategoriesBinding.view.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_line_color))
        }


    }





}