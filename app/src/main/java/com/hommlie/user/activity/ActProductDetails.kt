package com.hommlie.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.lifecycle.Observer
import android.os.Environment
import androidx.activity.viewModels
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator
import com.hommlie.user.R
import com.hommlie.user.adapter.AttributeAdapter
import com.hommlie.user.adapter.MySlideAdapter
import com.hommlie.user.adapter.NewReviewAdaptor
import com.hommlie.user.adapter.VariationAdapter
import com.hommlie.user.adapter.VariationAdaptor
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.databinding.ActProductDetailsBinding
import com.hommlie.user.databinding.RowFeaturedproductBinding
import com.hommlie.user.databinding.RowProductviewpagerBinding
import com.hommlie.user.databinding.SuccessBottomsheetDialogBinding
import com.hommlie.user.model.*
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.alertDialogNoInternet
import com.hommlie.user.utils.Common.alertErrorOrValidationDialog
import com.hommlie.user.utils.Common.dismissLoadingProgress
import com.hommlie.user.utils.Common.filterHttps
import com.hommlie.user.utils.Common.isCheckNetwork
import com.hommlie.user.utils.SharePreference
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Executors
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ActProductDetails : BaseActivity() {

    private lateinit var attributeAdapter: AttributeAdapter
    private lateinit var variationAdapter: VariationAdaptor
    private lateinit var recyclerViewAttributes: RecyclerView
    private lateinit var recyclerViewVariations: RecyclerView
    private lateinit var recylerviewNewReview : RecyclerView
    private lateinit var variationsMap: Map<String, List<DynamicVariationData>>
    private val viewModel: MeasurementViewModel by viewModels()

    private var mediaPlayer: MediaPlayer? = null

    var arrayList: ArrayList<String>? = null
    var horizontal: HorizontalScrollView? = null
    var imageUriArray = ArrayList<Uri>()
    var aa = 0
    private var screenWidth = 0
    private var myView: View? = null
    private val layout: LinearLayout? = null
    private var layMain: LinearLayout? = null

    private var selectedPackageName =""
    private var selectedVariationName =""

    private var currentPrice=""

    private lateinit var productDetailsBinding: ActProductDetailsBinding
    var productDetailsList: ProductDetailsData? = null
    var currency: String = ""
    var currencyPosition: String = ""
    var isAPICalling: Boolean = false
    var taxpercent: String = ""
    var productprice: Double = 0.0
    var addtax: String = ""
    var price: String = ""
    var pos: Int = 0
    var product_url: String = ""
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )

    private var latitude = ""
    private var longitude = ""

    private lateinit var addressLauncher: ActivityResultLauncher<Intent>

    var youtubeVideoURL=""
    private var youTubePlayer: YouTubePlayer? = null

    // Declaring a Bitmap local
    var mImage: Bitmap? = null

    // Declaring a webpath as a string
    val mWebPath =
        "https://media.geeksforgeeks.org/wp-content/uploads/20210224040124/JSBinCollaborativeJavaScriptDebugging6-300x160.png"

    // Declaring and initializing an Executor and a Handler
    val myExecutor = Executors.newSingleThreadExecutor()
    val myHandler = Handler(Looper.getMainLooper())
    var shareImgcaption: String = ""

    //picked image uri will be saved in it
    private var imageUri: Uri? = null
    private var viewAllDataAdapter: BaseAdaptor<RelatedProductsItem, RowFeaturedproductBinding>? =
        null
    var paymenttype: String = ""
    private var productimageDataAdapter: BaseAdaptor<ProductimagesItem, RowProductviewpagerBinding>? =
        null
    private var variationAdaper: VariationAdapter? = null
    private var bhkAdaptor : VariationAdapter? = null
    private var variationList = ArrayList<DynamicVariations>()
    private var bhkVariationList = ArrayList<DynamicVariationData>()
    override fun setLayout(): View = productDetailsBinding.root

    @RequiresApi(Build.VERSION_CODES.M)
    override fun initView() {
        productDetailsBinding = ActProductDetailsBinding.inflate(layoutInflater)

     //   window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

      //  setupProductVariationAdapter(variationList)
       // setupProductbhkAdapter(bhkVariationList)

        productDetailsBinding.searchCard.setOnClickListener { openActivity(ActSearch::class.java) }

        mediaPlayer = MediaPlayer.create(this, R.raw.add_to_cart)

        recyclerViewAttributes = productDetailsBinding.rvproductSize
        recyclerViewVariations = productDetailsBinding.rvbhkSize
        recyclerViewAttributes.layoutManager = GridLayoutManager(this,2, LinearLayoutManager.VERTICAL, false)
        recyclerViewVariations.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recylerviewNewReview = productDetailsBinding.rvReview
        recylerviewNewReview.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        currency =
            SharePreference.getStringPref(this@ActProductDetails, SharePreference.Currency) ?: ""
        currencyPosition = SharePreference.getStringPref(this@ActProductDetails, SharePreference.CurrencyPosition)!!

        productDetailsBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }

        productDetailsBinding.ivSearch.setOnClickListener {
            val intent=Intent(this@ActProductDetails,ActSearch::class.java)
            startActivity(intent)
        }

        productDetailsBinding.ivheart.setOnClickListener {
            val intent = Intent(this@ActProductDetails, ActMain::class.java)
            intent.putExtra("pos", "3")
            startActivity(intent)
            finish()
        }

        productDetailsBinding.edtAddress.setOnClickListener {
            val intent = Intent(this,ActshowMapPicAddress::class.java)
            addressLauncher.launch(intent)
        }

        productDetailsBinding.scroll.viewTreeObserver.addOnScrollChangedListener(scrollListener)

        addressLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Get the address returned from ActAddress
                val address = result.data?.getStringExtra("selectedAddress")
                latitude  =  result.data?.getStringExtra("latitude").toString()
                longitude  =  result.data?.getStringExtra("longitude").toString()
                productDetailsBinding.edtAddress.setText(address+", "+result.data?.getStringExtra("pincode"))
            }
        }

        /*   productDetailsBinding.ivShare.setOnClickListener {
           // shareUrl()
          //  val iGallery = Intent(Intent.ACTION_SEND)
          //  iGallery.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
          //  startActivityForResult(iGallery, 121)

            val bitmap = productDetailsBinding.ivImgShare.getDrawable().toBitmap()
            val bitmpath =
                MediaStore.Images.Media.insertImage(contentResolver, bitmap, "upload", null)

            val uri = Uri.parse(bitmpath)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/png"
            shareIntent.setPackage("com.whatsapp")
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.getStringExtra("" + shareImgcaption)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "yubshine");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "" + shareImgcaption );
            startActivity(Intent.createChooser(shareIntent, "Share Using"))
        }*/


        productDetailsBinding.btnaddtocart.setOnClickListener {
            if (SharePreference.getBooleanPref(this@ActProductDetails, SharePreference.isLogin)) {
                if (productDetailsBinding.btnaddtocart.text!="Add to Cart"){
                    productDetailsBinding.btnaddtocart.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ActProductDetails, R.color.cardview_dark_background))
                }else{ productDetailsList?.let { it1 ->
                    apiaddtocart(it1, selectedPackageName,selectedVariationName)
                    }
                }
            } else {
                openActivity(ActSignUp::class.java)
                this.finish()
            }
        }

        productDetailsBinding.edtDate.setOnClickListener {
            val intent = Intent(this@ActProductDetails, ActSelectTimeSlot::class.java)
            intent.putExtra("isComeFromSelectDateandTimeMethod", true)
            dateAndtimeDataSet.launch(intent)
        }
        productDetailsBinding.edtTime.setOnClickListener {
            val intent = Intent(this@ActProductDetails, ActSelectTimeSlot::class.java)
            intent.putExtra("isComeFromSelectDateandTimeMethod", true)
            dateAndtimeDataSet.launch(intent)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Common.getLog("productdetails", "enter inside activiy result")
        // Check which request we're responding to
        if (requestCode == 121) {
            Common.getLog("productdetails", "came to code 121")
            //  val bitmap = productDetailsBinding.ivProfile.getDrawable().toBitmap()
            //  val bitmpath = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "upload", null)

            //  val uri = Uri.parse(bitmpath)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "image/png"
            shareIntent.setPackage("com.whatsapp")
            //  shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(shareIntent, "Share Using"))

            if (resultCode == Activity.RESULT_OK) {

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Common.getToast(this@ActProductDetails, "No selection")
            }
        } else {
            Common.getLog("productdetails", "came else part")
        }
    }

    fun shareUrl() {
        myExecutor.execute {
            mImage = mLoad(mWebPath)
            myHandler.post {
                // productDetailsBinding.ivShare.setImageBitmap(mImage)

                val path =
                    Environment.getExternalStorageDirectory().toString() + "/" + "Downloads" + "/"
                val uri = Uri.parse(path)
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(uri, "*/*")
                startActivity(intent)

                if (mImage != null) {
                    mSaveMediaToStorage(mImage)
                }
            }
        }
    }

    // Function to establish connection and load image
    private fun mLoad(string: String): Bitmap? {
        val url: URL = mStringToURL(string)!!
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
        }
        return null
    }

    // Function to convert string to URL
    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    // Function to save image on the device.
    // Refer: https://www.geeksforgeeks.org/circular-crop-an-image-and-save-it-to-the-file-in-android/
    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    }


    //TODO CALL PRODUCT DETAILS API
    private fun callApiProductDetail(productId: String) {
        Common.showLoadingProgress(this@ActProductDetails)
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(this@ActProductDetails, SharePreference.userId)!!
        hasmap["product_id"] = productId
        Common.getToast(this@ActProductDetails,productId)
        val call = ApiClient.getClient.getProductDetails(hasmap)
        call.enqueue(object : Callback<GetProductDetailsResponse> {
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onResponse(
                call: Call<GetProductDetailsResponse>,
                response: Response<GetProductDetailsResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        dismissLoadingProgress()
                        productDetailsList = restResponce.data

                        product_url = restResponce.productUrl.toString()

                        if (productDetailsList!!.variations.isNullOrEmpty()){
                            productDetailsBinding.bhkCard.visibility = View.GONE
                            productDetailsBinding.productsizeCard.visibility = View.GONE
                        }else{
                                productDetailsBinding.productsizeCard.visibility = View.VISIBLE
                                productDetailsBinding.bhkCard.visibility = View.VISIBLE
                            processVariationsData(productDetailsList!!.variations!!)
                        }
                        loadProductDetails(productDetailsList!!)
                        restResponce.relatedProducts?.let { loadRelatedProducts(it) }
                        restResponce.vendors?.let { loadVendorsData(it) }
                        /*  productDetailsBinding.clReturnpolicy.setOnClickListener {
                            val intent = Intent(this@ActProductDetails, ActReturnPolicy::class.java)
                            intent.putExtra(
                                "return_policies",
                                restResponce.returnpolicy?.returnPolicies.toString()
                            )
                            startActivity(intent)
                        } */
                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ActProductDetails,
                            restResponce.message.toString()
                        )
                    }
                }else{
                    Common.dismissLoadingProgress()
                    when (response.code()) {
                        404 -> {
                            Common.alertErrorOrValidationDialog(this@ActProductDetails, "Resource not found.")
                        }
                        500 -> {
                            Common.alertErrorOrValidationDialog(this@ActProductDetails, "Server error. Please try again later.")
                        }
                        else -> {
                            Common.alertErrorOrValidationDialog(this@ActProductDetails, "Unexpected error: ${response.code()}")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetProductDetailsResponse>, t: Throwable) {
                dismissLoadingProgress()
                val errorMessage = when (t) {
                    is java.net.UnknownHostException -> "No internet connection. Please check your network."
                    is java.net.SocketTimeoutException -> "Request timed out. Please try again."
                    is java.io.IOException -> "Network error occurred. Please try again."
                    else -> "Something went wrong. Please try again."
                }

                // Display the error message
                Common.alertErrorOrValidationDialog(this@ActProductDetails, errorMessage)

                // Optionally, log the error for debugging purposes
                Log.e("API_ERROR", "onFailure: ${t.localizedMessage}", t)
            }
        })
    }

    //TODO VENDORS DATA SET
    private fun loadVendorsData(vendors: Vendors) {
      /*  productDetailsBinding.tvTitle.text = vendors.name
        Glide.with(this@ActProductDetails)
            .load(filterHttps(vendors.imageUrl)).into(productDetailsBinding.imageSlider)
         productDetailsBinding.ivvendors.setBackgroundColor(Color.parseColor(colorArray[pos % 6]))  */
        if (vendors.rattings == null) {
            productDetailsBinding.tvRating.text =
                "0.0"
        } else {
            productDetailsBinding.tvRating.text =
                vendors.rattings.avgRatting?.toString() ?: ""
        }
    /*    productDetailsBinding.tvvisitstore.setOnClickListener {
            val intent = Intent(this@ActProductDetails, ActVendorsDetails::class.java)
            intent.putExtra("vendor_id", vendors.id?.toString())
            intent.putExtra("vendors_name", vendors.name)
            intent.putExtra("vendors_iv", vendors.imageUrl)
            intent.putExtra("vendors_rate", vendors.rattings?.avgRatting?.toString())
            startActivity(intent)
        } */
    }

    //TODO RELATED PRODUCT DETAILS DATA SET
    private fun loadRelatedProducts(relatedProducts: ArrayList<RelatedProductsItem>) {
        lateinit var binding: RowFeaturedproductBinding
        viewAllDataAdapter =
            object : BaseAdaptor<RelatedProductsItem, RowFeaturedproductBinding>(
                this@ActProductDetails,
                relatedProducts
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: RelatedProductsItem,
                    position: Int
                ) {
                  /*  if (relatedProducts.get(position).isWishlist == 0) {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_btnwishlist,
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
                                this@ActProductDetails,
                                SharePreference.isLogin
                            )
                        ) {
                            if (relatedProducts[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    relatedProducts[position].id!!.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActProductDetails,
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(this@ActProductDetails)) {
                                    //   callApiFavourite(map, position, relatedProducts)
                                } else {
                                    alertErrorOrValidationDialog(
                                        this@ActProductDetails,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                            if (relatedProducts[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    relatedProducts[position].id!!.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    this@ActProductDetails,
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(this@ActProductDetails)) {
                                    //  callApiRemoveFavourite(map, position, relatedProducts)
                                } else {
                                    alertErrorOrValidationDialog(
                                        this@ActProductDetails,
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                        } else {
                            openActivity(ActSignUp::class.java)
                            finish()
                        }
                    }  */
                    if (relatedProducts[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "4.7"
                    } else {
                        binding.tvRatePro.text =
                            relatedProducts[position].rattings?.get(0)?.avgRatting?.toString()
                    }
                    binding.tvProductName.text = relatedProducts[position].productName.toString()

                    val temp=relatedProducts[position].productPrice
                    if (temp!=null) {
                        if (relatedProducts[position].discountedPrice!=null || relatedProducts[position].discountedPrice?.toDouble() ?: 0.0 == temp.toDouble()) {
                            binding.tvProductDisprice.visibility = View.GONE
                        }else{
                            binding.tvProductDisprice.visibility = View.VISIBLE
                        }
                    }else{
                        binding.tvProductDisprice.visibility = View.GONE
                    }


                    if (currencyPosition == "left") {

                        binding.tvProductPrice.text ="\u20b9"+ relatedProducts[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+relatedProducts[position].productPrice
                    } else {
                        binding.tvProductPrice.text ="\u20b9"+relatedProducts[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+ relatedProducts[position].productPrice
                    }
                    val rp = filterHttps(relatedProducts[position].productimage?.imageUrl)
                    Glide.with(this@ActProductDetails)
                        .load(rp)
                        .placeholder(R.drawable.ic_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

//                    binding.tvdiscountpercentage.text=(relatedProducts[position].productPrice?.let {
//                        relatedProducts[position].discountedPrice?.let { it1 ->
//                            calculateDiscountPercentage(
//                                it.toDouble(),
//                                it1.toDouble())
//                        }
//                    }).toString()+"% off"
                    val calculatedDiscount = calculateDiscountPercentage(relatedProducts[position].productPrice?.toDouble()
                        ?: 0.0, relatedProducts[position].discountedPrice?.toDouble() ?: 0.0)

                    if (calculatedDiscount>0&&calculatedDiscount!=null){
                        binding.tvdiscountpercentage.text= calculatedDiscount.toString()+"% off"
                    }else{
                        binding.tvdiscountpercentage.visibility=View.GONE
                        binding.ivDownarrow.visibility =View.GONE
                    }

                  //  displayDynamicStarsforRelatedProducts(binding.ivRate,4.7f)
                    relatedProducts[position].rating?.let { displayDynamicStars(binding.ivRate, it)
                        binding.tvRatePro.text=it.toString()}

                    holder?.itemView?.setOnClickListener {
                        Log.e(
                            "product_id--->",
                            relatedProducts[position].productimage?.productId.toString()
                        )
                        val intent = Intent(this@ActProductDetails, ActProductDetails::class.java)
                        intent.putExtra(
                            "product_id",
                            relatedProducts[position].productimage?.productId.toString()
                        )
                        startActivity(intent)
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_featuredproduct
                }

                override fun getBinding(parent: ViewGroup): RowFeaturedproductBinding {
                    binding = RowFeaturedproductBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        productDetailsBinding.rvstorelist.apply {
            layoutManager =
                GridLayoutManager(this@ActProductDetails, 1, GridLayoutManager.HORIZONTAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = viewAllDataAdapter
        }
    }


    fun calculateDiscountPercentage(originalPrice: Double, discountedPrice: Double): Int {
        if (originalPrice <= 0 || discountedPrice < 0) {
          //  throw IllegalArgumentException("Prices must be positive and original price should be greater than 0")
        }
        val discount = originalPrice - discountedPrice
        val discountPercentage = (discount / originalPrice) * 100
        return discountPercentage.toInt() // Round to 2 decimal places
    }


    //TODO PRODUCT DETAILS DATA SET
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint(
        "ResourceAsColor", "SetTextI18n", "NotifyDataSetChanged",
        "UseCompatLoadingForDrawables"
    )
    private fun loadProductDetails(productDetailsList: ProductDetailsData) {


        productDetailsBinding.tvTitle.text = productDetailsList.productName
        productDetailsBinding.tvviewall.text = productDetailsList.productName
//        productDetailsBinding.tvCurrentPrice.text=productDetailsList.productPrice
//        productDetailsBinding.tvOldPrice.text=productDetailsList.discountedPrice
        productDetailsBinding.tvDes.text =
            "\u00B7 " + productDetailsList.subcategory?.subcategoryName + "\n\u00B7 " + productDetailsList.innersubcategory?.innersubcategoryName
        productDetailsBinding.tvTime.text = productDetailsList.estShippingDays
        productDetailsBinding.tvTotalItem.text = productDetailsList.availableStock

        /*  if (productDetailsList.rattings?.size == 0) {
            productDetailsBinding.tvRating.text =
                "0.0"
        } else {
            productDetailsBinding.tvRating.text =
                productDetailsList.rattings?.get(0)?.avgRatting?.toString()
        }  */

        //paymenttype = productDetailsList.taxType ?: ""


        if (productDetailsList.returnDays == "0") {
            //  productDetailsBinding.clReturnpolicy.visibility = View.GONE
            //   productDetailsBinding.viewreturn.visibility = View.GONE
        } else {
            //   productDetailsBinding.tvreturnpilicy.text =
            productDetailsList.returnDays + " " + getString(R.string.day) + " " + getString(R.string.return_policies)
        }


        val tempProductPrice=productDetailsList.productPrice
        if (tempProductPrice!=null) {
            currentPrice = tempProductPrice
            val tempDiscountPrice=productDetailsList.discountedPrice
            if (tempDiscountPrice!=null){
                currentPrice = tempDiscountPrice
                if (tempProductPrice.toDouble().equals(tempDiscountPrice.toDouble())) {
                    productDetailsBinding.tvOldPrice.visibility = View.GONE
                }
            }
        }

        if (currencyPosition == "left") {
            productDetailsBinding.tvOldPrice.text ="\u20b9"+productDetailsList.productPrice
            productDetailsBinding.tvCurrentPrice.text ="\u20b9"+productDetailsList.discountedPrice
           /*      if (productDetailsList.freeShipping == 1) {
                  productDetailsBinding.tvshoppingcharge.text = currency.plus("0.00")
                  } else if (productDetailsList.freeShipping == 2) {
                productDetailsBinding.tvshoppingcharge.text = currency.plus(
                    String.format(
                        Locale.US,
                        "%,.02f",
                        productDetailsList.shippingCost!!.toDouble()
                    )
                )
            }
                } else {
            productDetailsBinding.tvProductPrice.text =
                (String.format(
                    Locale.US,
                    "%,.02f",
                    productDetailsList.productPrice!!.toDouble()
                )) + "" + currency
            productDetailsBinding.tvProductDisprice.text =
                (String.format(
                    Locale.US,
                    "%,.02f",
                    productDetailsList.discountedPrice!!.toDouble()
                )) + "" + currency
            if (productDetailsList.freeShipping == 1) {
                productDetailsBinding.tvshoppingcharge.text = (String.format(
                    Locale.US,
                    "%,.02f",
                    0.toDouble()
                )) + "" + currency
            } else if (productDetailsList.freeShipping == 2) {
                productDetailsBinding.tvshoppingcharge.text = (String.format(
                    Locale.US,
                    "%,.02f",
                    productDetailsList.shippingCost!!.toDouble()
                )) + "" + currency
                   }
                }  */
            Log.d("isVariation", productDetailsList.isVariation.toString())
            Log.d("availableStock", productDetailsList.availableStock.toString())
          /*  if (productDetailsList.variations?.isNotEmpty() == true) {

                if (productDetailsList.variations[0].qty == "0") {
                    //    productDetailsBinding.tvInstock.text = getString(R.string.outofstock)
                    //  productDetailsBinding.tvInstock.setTextColor(getColor(R.color.red))
                    productDetailsBinding.btnaddtocart.background =
                        getDrawable(R.drawable.round_gray_bg_9)
                    productDetailsBinding.btnaddtocart.isClickable = false
                } else {
//                productDetailsBinding.tvInstock.text = getString(R.string.in_stock)
//                productDetailsBinding.tvInstock.setTextColor(getColor(R.color.green))
                    productDetailsBinding.btnaddtocart.background =
                        getDrawable(R.drawable.round_blue_bg_9)
                    productDetailsBinding.btnaddtocart.isClickable = true
                }
            } else {
                // Handle case when variations list is null or empty
                if (productDetailsList.availableStock == "0") {
//                productDetailsBinding.tvInstock.text = getString(R.string.outofstock)
//                productDetailsBinding.tvInstock.setTextColor(getColor(R.color.red))
                    productDetailsBinding.btnaddtocart.background =
                        getDrawable(R.drawable.round_gray_bg_9)
                    productDetailsBinding.btnaddtocart.isClickable = false
                } else {
//                productDetailsBinding.tvInstock.text = getString(R.string.in_stock)
//                productDetailsBinding.tvInstock.setTextColor(getColor(R.color.green))
                    productDetailsBinding.btnaddtocart.background =
                        getDrawable(R.drawable.round_blue_bg_9)
                    productDetailsBinding.btnaddtocart.isClickable = true
                }
            }  */
          /*  if (productDetailsList.isVariation == 1) {
                if (productDetailsList.variations?.get(0)?.qty!! == "0") {
//                productDetailsBinding.tvInstock.text = getString(R.string.outofstock)
//                productDetailsBinding.tvInstock.setTextColor(getColor(R.color.red))
                    productDetailsBinding.btnaddtocart.background =
                        getDrawable(R.drawable.round_gray_bg_9)
                    productDetailsBinding.btnaddtocart.isClickable = false
                } else {
//                productDetailsBinding.tvInstock.text = getString(R.string.in_stock)
//                productDetailsBinding.tvInstock.setTextColor(getColor(R.color.green))
                    productDetailsBinding.btnaddtocart.background =
                        getDrawable(R.drawable.round_blue_bg_9)
                    productDetailsBinding.btnaddtocart.isClickable = true
                }
            } else if (productDetailsList.isVariation == 0) {
                if (productDetailsList.availableStock == "0") {
//                productDetailsBinding.tvInstock.text = getString(R.string.outofstock)
//                productDetailsBinding.tvInstock.setTextColor(getColor(R.color.red))
                    productDetailsBinding.btnaddtocart.background =
                        getDrawable(R.drawable.round_gray_bg_9)
                    productDetailsBinding.btnaddtocart.isClickable = false
                } else {
//                productDetailsBinding.tvInstock.text = getString(R.string.in_stock)
//                productDetailsBinding.tvInstock.setTextColor(getColor(R.color.green))
                    productDetailsBinding.btnaddtocart.background =
                        getDrawable(R.drawable.round_blue_bg_9)
                    productDetailsBinding.btnaddtocart.isClickable = true
                }
            }  */
          /*  if (productDetailsList.isWishlist == 0) {
                productDetailsBinding.ivwishlist.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_btnwishlist,
                        null
                    )
                )
            } else {
                productDetailsBinding.ivwishlist.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_btnliked,
                        null
                    )
                )
            }
            productDetailsBinding.ivwishlist.setOnClickListener {
                if (SharePreference.getBooleanPref(
                        this@ActProductDetails,
                        SharePreference.isLogin
                    )
                ) {
                    if (productDetailsList.isWishlist == 0) {
                        val map = HashMap<String, String>()
                        map["product_id"] =
                            productDetailsList.id?.toString()!!
                        map["user_id"] = SharePreference.getStringPref(
                            this@ActProductDetails,
                            SharePreference.userId
                        )!!
                        if (isCheckNetwork(this@ActProductDetails)) {
                            //     callApiFavouritePro(map)
                        } else {
                            alertErrorOrValidationDialog(
                                this@ActProductDetails,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    }
                    if (productDetailsList.isWishlist == 1) {
                        val map = HashMap<String, String>()
                        map["product_id"] =
                            productDetailsList.id?.toString()!!
                        map["user_id"] = SharePreference.getStringPref(
                            this@ActProductDetails,
                            SharePreference.userId
                        )!!

                        if (isCheckNetwork(this@ActProductDetails)) {
                            //      callApiRemoveFavouritePro(map)
                        } else {
                            alertErrorOrValidationDialog(
                                this@ActProductDetails,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    }
                } else {
                    openActivity(ActSignUp::class.java)
                    finish()
                }
            } */
            // productDetailsBinding.tvcode.text = productDetailsList.sku

            // because attributes not come
         /*   if (productDetailsList.attribute == null) {
                productDetailsBinding.descriptionCard.visibility = View.GONE
                productDetailsBinding.productsizeCard.visibility = View.GONE
            } else {
                productDetailsBinding.detailsCard.visibility = View.VISIBLE
                productDetailsBinding.tvWhoseSize.text = productDetailsList.attribute
                productDetailsBinding.productsizeCard.visibility = View.VISIBLE
            }*/



//        taxpercent = if(productDetailsList.tax==null){ "0"; }else{ productDetailsList?.tax.toString() }
//        Log.d("Taxper--->", taxpercent)
//        productprice = productDetailsList.productPrice.toDouble()
//        Log.d("Price--->", productprice.toString())
//        Log.d("URL--->", product_url.toString())
//        val tax = productprice * taxpercent.toDouble() / 100
//        Log.d("Tax--->", tax.toString())
//        addtax = if (paymenttype == "amount") {
//            productDetailsList?.tax.toString()
//        } else {
//            tax.toString()
//        }
//        if (currencyPosition == "left") {
//            if (productDetailsList.tax != "0") {
//                //Log.d("tax", productDetailsList.tax)
//                productDetailsBinding.tvaddtax.text =
//                    currency.plus(
//                        String.format(
//                            Locale.US,
//                            "%,.02f",
//                            addtax.toDouble()
//                        )
//                    ) + " " + getString(R.string.add_tax)
//                productDetailsBinding.tvaddtax.setTextColor(getColor(R.color.red))
//            } else {
//                productDetailsBinding.tvaddtax.setTextColor(getColor(R.color.green))
//                productDetailsBinding.tvaddtax.text = getString(R.string.inclusive_all_taxes)
//            }
//        } else {
//            if (productDetailsList.tax != "0") {
//                productDetailsBinding.tvaddtax.text =
//                    (String.format(
//                                Locale.US,
//                                "%,.02f",
//                                addtax.toDouble()
//                            )
//                            ) + currency.plus(" Additional ").plus("Tax")
//                productDetailsBinding.tvaddtax.setTextColor(
//                    ResourcesCompat.getColor(
//                        resources,
//                        R.color.red,
//                        null
//                    )
//                )
//            } else {
//                productDetailsBinding.tvaddtax.setTextColor(
//                    ResourcesCompat.getColor(
//                        resources,
//                        R.color.green,
//                        null
//                    )
//                )
//                productDetailsBinding.tvaddtax.text = "Inclusive All Taxes"
//            }
//        }
//        if (productDetailsList.freeShipping == 1) {
//            productDetailsBinding.tvshoppingcharge.visibility = View.GONE
//            productDetailsBinding.tvshoppingchargetitle.text = "Free Shipping"
//        } else {
//            productDetailsBinding.tvshoppingcharge.visibility = View.VISIBLE
//            if (currencyPosition == "left") {
//                productDetailsBinding.tvshoppingcharge.text =
//                    currency.plus(
//                        String.format(
//                            Locale.US,
//                            "%,.02f",
//                            productDetailsList.shippingCost!!.toDouble()
//                        )
//                    )
//            } else {
//                productDetailsBinding.tvshoppingcharge.text =
//                    (
//                            String.format(
//                                Locale.US,
//                                "%,.02f",
//                                productDetailsList.shippingCost!!.toDouble()
//                            ) + currency
//                            )
//            }

            //   productDetailsBinding.tvshoppingday.text = productDetailsList.estShippingDays + " " + "Day"

            variationList.clear()
            bhkVariationList.clear()
         /*   productDetailsList.variations?.let {
                if (it.size > 0) {
                    for (variation in it) {
                        when (variation.attribute?.id.toString()) {
                            "2" -> variationList.add(variation)
                            "5" -> bhkVariationList.add(variation)
                        }
                    }
                    it.get(0).isSelect = true
                    setupPriceData(it[0])
                }
                bhkAdaptor?.notifyDataSetChanged()
                variationAdaper?.notifyDataSetChanged()
            }  */
            // Assuming productDetailsList.variations contains the list of DynamicVariations
//            productDetailsList.variations?.let { variations ->
//                if (variations.isNotEmpty()) {
//                    for (variation in variations) {
//                        // Safeguard to ensure non-null attributeName
//                        val attributeName = variation.attributeName
//
//                        // Collect names (attribute_name) in variationList if they are unique
//                        if (attributeName != null && variationList.add(attributeName)) {
//                            variationList.add(attributeName)
//                        }
//
//                        // Collect data (DynamicVariationData) in bhkVariationList
//                        val variationData = variation.data
//                        if (variationData != null) {
//                            bhkVariationList.add(variationData)
//                        }
//                    }
//
//                    // Optional: select the first variation data for some processing (e.g., setting price)
//                    bhkVariationList.getOrNull(0)?.let { firstVariationData ->
//                        // Assume setupPriceData takes DynamicVariationData as parameter
//                        setupPriceData(firstVariationData)
//                    }
//
//                    // Notify adapters of data change
//                    bhkAdaptor?.notifyDataSetChanged()
//                    variationAdaper?.notifyDataSetChanged()
//                }
//            }



        /*    if (variationList.size > 0) {
                productDetailsBinding.productsizeCard.visibility = View.VISIBLE
                productDetailsBinding.bhkCard.visibility = View.VISIBLE
            } else {
                productDetailsBinding.bhkCard.visibility = View.GONE
                productDetailsBinding.productsizeCard.visibility = View.GONE
            }  */


          /*  if (productDetailsList.description != null) {
                productDetailsBinding.root.post {
                    moveIndicatorUnderCard(productDetailsBinding.cardAlldesc)
                }
                productDetailsBinding.btnDetail.setOnClickListener{
                    scrollToDetailsCard()
                }
                productDetailsBinding.tvDescription.setOnClickListener {
                    moveIndicatorUnderCard(productDetailsBinding.cardAlldesc)
                    setDescriptionFaqs(productDetailsList.description)
                }
                setDescriptionFaqs(productDetailsList.description)
            }else{
                productDetailsBinding.btnDetail.visibility=View.GONE
            }

            if (productDetailsList.faqs != null) {
                productDetailsBinding.tvFaqss.setOnClickListener {
                    moveIndicatorUnderCard(productDetailsBinding.cardAllfaqss)
                    setDescriptionFaqs(productDetailsList.faqs)
                }
            }else{
                productDetailsBinding.tvFaqss.visibility=View.GONE
            } */



            productDetailsBinding.descriptionnnCard.setOnClickListener {
                val intent = Intent(this@ActProductDetails, ActProductDescription::class.java)
                intent.putExtra("description", productDetailsList.description.toString())
                intent.putExtra("title", "Description")
                startActivity(intent)
            }
            productDetailsBinding.faqsCard.setOnClickListener {
                val intent = Intent(this@ActProductDetails, ActProductDescription::class.java)
                intent.putExtra("description", productDetailsList.faqs.toString())
                intent.putExtra("title", "FAQs")
                startActivity(intent)
            }
            productDetailsBinding.btnDetail.setOnClickListener {
                val intent = Intent(this@ActProductDetails, ActProductDescription::class.java)
                intent.putExtra("description", productDetailsList.description.toString())
                intent.putExtra("title", "Description")
                startActivity(intent)
            }
            productDetailsBinding.custormerReviewCard.setOnClickListener {
                val intent = Intent(this@ActProductDetails, ActReviews::class.java)
                intent.putExtra("product_id", productDetailsList.id.toString())
                startActivity(intent)
            }

//            val imageList = ArrayList<SlideModel>()
//            val videoList = ArrayList<SlideModel>() // for storing all videos
//            for (i in 0 until productDetailsList.productimages?.size!!) {
//               if (productDetailsList.productimages[i].imagetype=="Video"){
//                   val slideVideo=SlideModel(filterHttps(productDetailsList.productimages[i].imageUrl))
//                   videoList.add(slideVideo)
//               }
//                if (productDetailsList.productimages[i].imagetype=="Image"){
//                    val slideModel =
//                        SlideModel(filterHttps(productDetailsList.productimages[i].imageUrl))
//                    imageList.add(slideModel)
//                }
//            }

         //   productDetailsBinding.imageSlider.setImageList(imageList, ScaleTypes.FIT)


            val slideList = ArrayList<MySlideModel>()

            // Populate the slide list with your data
            for (i in 0 until productDetailsList.productimages?.size!!) {
                val imageUrl = filterHttps(productDetailsList.productimages[i].imageUrl)
                val thumbnail = filterHttps(productDetailsList.productimages[i].thumbnail)
                val slideModel = when (productDetailsList.productimages[i].imagetype) {
                   // "Video" -> MySlideModel(imageUrl.toString(), MediaType.VIDEO,thumbnail.toString())
                    "Image" -> MySlideModel(imageUrl.toString(), MediaType.IMAGE,thumbnail.toString())
                    else -> null
                }
                slideModel?.let { slideList.add(it) }
                if (productDetailsList.productimages[i].imagetype=="Video"){
                    youtubeVideoURL=productDetailsList.productimages[i].imageUrl.toString()
                    if (youtubeVideoURL!=""){
                        val videoId = Common.extractYoutubeVideoIdFromUrl(youtubeVideoURL)
                        productDetailsBinding.yotubeVideoView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                            override fun onReady(player: YouTubePlayer) {
//                                if (videoId != null) {
//                                    youTubePlayer.loadVideo(videoId, 0f) // Load the video at 0 seconds
//                                } else {
//                                    // Handle error: invalid URL
//                                }
                                youTubePlayer=player
                                Handler(Looper.getMainLooper()).postDelayed({
                                    videoId?.let {
                                        youTubePlayer?.cueVideo(
                                            it,
                                            0f
                                        ) // Cue the video without autoplay
                                        productDetailsBinding.vedioCard.visibility = View.VISIBLE
                                    } ?: run {
                                        // Handle error: invalid video ID
                                        productDetailsBinding.vedioCard.visibility = View.GONE
                                    }
                                },3000)
                            }
                            override fun onError(player: YouTubePlayer, error: PlayerConstants.PlayerError) {
                                Log.e("YOUTUBE_ERROR", "YouTube Player Error: $error")
                                productDetailsBinding.vedioCard.visibility = View.GONE
                            }
                        })
                    }

                }
            }

            // Set the slide list to the custom image slider
           // productDetailsBinding.imageSlider.setSlideList(slideList)

            val adapter = MySlideAdapter(slideList)
            productDetailsBinding.myviewPager.adapter = adapter
            TabLayoutMediator(productDetailsBinding.tabLayout, productDetailsBinding.myviewPager) { tab, position ->
                // Optionally, set the tab's custom view or text if needed
            }.attach()
          //  productDetailsBinding.myviewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL





            //Picasso.get().load(productDetailsList.productimages[0].imageUrl).into(productDetailsBinding.imageSlider)

//            productDetailsBinding.imageSlider.setItemClickListener(
//                object : ItemClickListener {
//                    override fun onItemSelected(position: Int) {
////                         val intent = Intent(this@ActProductDetails, ActImageSlider::class.java)
////                         intent.putParcelableArrayListExtra("imageList", productDetailsList.productimages)
////                         startActivity(intent)
//                    }
//                })

//            productDetailsBinding.imageSlider.setOnClickListener {
//                loadProductImage(productDetailsList.productimages)
//            }

          /*  if (!productDetailsList.rattings.isNullOrEmpty()){
                if (productDetailsList.rattings.size>6){
                    productDetailsBinding.tvMorereview.visibility=View.VISIBLE
                        val limitedRatings = productDetailsList.rattings.take(5)
                        val adapter = NewReviewAdaptor(this, limitedRatings)
                        recylerviewNewReview.adapter = adapter

                    productDetailsBinding.tvMorereview.setOnClickListener {
                        val intent = Intent(this@ActProductDetails, ActReviews::class.java)
                        intent.putExtra("product_id", productDetailsList.id.toString())
                        startActivity(intent)
                    }
                }else{
                    productDetailsBinding.tvMorereview.visibility=View.GONE
                    val adapter = NewReviewAdaptor(this, productDetailsList.rattings)
                    recylerviewNewReview.adapter = adapter
                }
            }else{
                productDetailsBinding.rvReview.visibility=View.GONE
                productDetailsBinding.tvReview.visibility=View.GONE
                productDetailsBinding.tvMorereview.visibility=View.GONE
            } */

            if (productDetailsList.isForm==1){
                changeVisibilityisForm(true)

                productDetailsBinding.btnBookInspection.setOnClickListener {
                    if (validateFields()){
                        if (Common.isValidEmail(productDetailsBinding.edtEmail.text.toString().trim())){
                            if (Common.isValidMobile(productDetailsBinding.edtMobile.text.toString().trim())){
                                callApiScheduleInspection(productDetailsList.id)
                            }else{
                                Common.showErrorFullMsg(this@ActProductDetails,"Please enter valid mobile number")
                            }
                        }else{
                            Common.showErrorFullMsg(this@ActProductDetails,"Please enter valid email address")
                        }
                    }
                }

            }
        }

        productDetailsList.rating?.let { displayDynamicStars(productDetailsBinding.ivRate, it)
        productDetailsBinding.tvRatePro.text=it.toString()}

        val calculatedDiscount = calculateDiscountPercentage(productDetailsList.productPrice?.toDouble()
            ?: 0.0, productDetailsList.discountedPrice?.toDouble() ?: 0.0)

        if (calculatedDiscount>0&&calculatedDiscount!=null){
            productDetailsBinding.tvdiscountpercentage.text= calculatedDiscount.toString()+"% off"
            productDetailsBinding.tvdiscountpercentage.visibility=View.VISIBLE
            productDetailsBinding.ivDownarrow.visibility =View.VISIBLE
            Common.getToast(this@ActProductDetails,calculatedDiscount.toString()+"1")
        }else{
            productDetailsBinding.tvdiscountpercentage.visibility=View.GONE
            productDetailsBinding.ivDownarrow.visibility =View.GONE
            Common.getToast(this@ActProductDetails,calculatedDiscount.toString()+"2")
        }
    }

    fun setDescriptionFaqs(description_faqs: String) {
        val textView = productDetailsBinding.tvvDescription
        val fullHtmlText = description_faqs ?: "" // Full HTML text

        // Render the full HTML text
        val renderedFullText = Html.fromHtml(fullHtmlText, Html.FROM_HTML_MODE_LEGACY)

        // Initial setup
        textView.text = renderedFullText
        textView.maxLines = 30

        // Post a task to ensure the layout is measured
        textView.post {
            if (textView.layout != null && textView.layout.lineCount > 29) {
                // Extract visible HTML-rendered text up to the 10th line
                val endIndex = textView.layout.getLineEnd(28)
                val visibleHtmlText = fullHtmlText.substring(0, endIndex)

                // Append "Read more" while maintaining HTML formatting
                val spannableText = SpannableStringBuilder()
                    .append(Html.fromHtml(visibleHtmlText, Html.FROM_HTML_MODE_LEGACY))
                    .append(Html.fromHtml("... ", Html.FROM_HTML_MODE_LEGACY))
                    .append(
                        "Read more",
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                this@ActProductDetails,
                                R.color.serviceHeadingcolor
                            )
                        ),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                textView.text = spannableText
            }
        }

        // Handle click to toggle "Read more" and "Hide"
        textView.setOnClickListener {
            if (textView.text.contains("Read more")) {
                // Expand the full text with "Hide" at the end
                val spannableText = SpannableStringBuilder()
                    .append(renderedFullText)
                    .append(Html.fromHtml("   ", Html.FROM_HTML_MODE_LEGACY))
                    .append(
                        "Hide",
                        ForegroundColorSpan(
                            ContextCompat.getColor(
                                this@ActProductDetails,
                                R.color.serviceHeadingcolor
                            )
                        ),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                textView.maxLines = Integer.MAX_VALUE // Expand to show full text
                textView.text = spannableText
            } else if (textView.text.contains("Hide")) {
                // Collapse back to 12 lines with "... Read more"
                textView.maxLines = 30
                textView.text = renderedFullText
                textView.post {
                    if (textView.layout != null && textView.layout.lineCount > 29) {
                        val endIndex = textView.layout.getLineEnd(28)
                        val visibleHtmlText = fullHtmlText.substring(0, endIndex)
                        val spannableText = SpannableStringBuilder()
                            .append(
                                Html.fromHtml(
                                    visibleHtmlText,
                                    Html.FROM_HTML_MODE_LEGACY
                                )
                            )
                            .append(Html.fromHtml("... ", Html.FROM_HTML_MODE_LEGACY))
                            .append(
                                "Read more",
                                ForegroundColorSpan(
                                    ContextCompat.getColor(
                                        this@ActProductDetails,
                                        R.color.serviceHeadingcolor
                                    )
                                ),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )

                        textView.text = spannableText
                    }
                }
            }
        }
    }

    fun moveIndicatorUnderCard(selectedCard: View) {
        // Get the location of the selected card
        val location = IntArray(2)
        selectedCard.getLocationInWindow(location)

        // Convert 10dp to pixels
        val scale = selectedCard.context.resources.displayMetrics.density
        val offset = (10 * scale).toInt()

        // Move the indicator to the location of the selected card with an offset
        productDetailsBinding.tabLayoutDescriptionFaqss.layoutParams =
            (productDetailsBinding.tabLayoutDescriptionFaqss.layoutParams as ViewGroup.MarginLayoutParams).apply {
                leftMargin = location[0] - offset // Adjust margin to show before current position
                width = selectedCard.width
            }
        productDetailsBinding.tabLayoutDescriptionFaqss.requestLayout()
    }


    /*   //TODO CALL API REMOVE FAVOURITE
       private fun callApiRemoveFavourite(
           map: HashMap<String, String>,
           position: Int,
           relatedProducts: ArrayList<RelatedProductsItem>
       ) {
           if (isAPICalling) {
               return
           }
           isAPICalling = true
           Common.showLoadingProgress(this@ActProductDetails)
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
                           dismissLoadingProgress()
                           relatedProducts[position].isWishlist = 0
                           viewAllDataAdapter!!.notifyItemChanged(position)

                       } else if (restResponse.status == 0) {
                           dismissLoadingProgress()
                           alertErrorOrValidationDialog(
                               this@ActProductDetails,
                               restResponse.message
                           )
                       }
                   }
                   isAPICalling = false
               }

               override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                   dismissLoadingProgress()
                   alertErrorOrValidationDialog(
                       this@ActProductDetails,
                       resources.getString(R.string.error_msg)
                   )
                   isAPICalling = false
               }
           })
       }

       //TODO CALL API FAVOURITE
       private fun callApiFavourite(
           map: HashMap<String, String>,
           position: Int,
           relatedProducts: ArrayList<RelatedProductsItem>
       ) {
           if (isAPICalling) {
               return
           }
           isAPICalling = true
           Common.showLoadingProgress(this@ActProductDetails)
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
                           dismissLoadingProgress()
                           relatedProducts[position].isWishlist = 1
                           viewAllDataAdapter!!.notifyItemChanged(position)
                       } else if (restResponse.status == 0) {
                           dismissLoadingProgress()
                           alertErrorOrValidationDialog(
                               this@ActProductDetails,
                               restResponse.message
                           )
                       }
                   }
                   isAPICalling = false
               }

               override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                   dismissLoadingProgress()
                   alertErrorOrValidationDialog(
                       this@ActProductDetails,
                       resources.getString(R.string.error_msg)
                   )
                   isAPICalling = false
               }
           })
       }


       //TODO CALL API REMOVE FAVOURITE
       private fun callApiRemoveFavouritePro(map: HashMap<String, String>) {
           if (isAPICalling) {
               return
           }
           isAPICalling = true
           Common.showLoadingProgress(this@ActProductDetails)
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
                           dismissLoadingProgress()
                           productDetailsList?.isWishlist = 0
                           onResume()
                       } else if (restResponse.status == 0) {
                           dismissLoadingProgress()
                           alertErrorOrValidationDialog(
                               this@ActProductDetails,
                               restResponse.message
                           )
                       }
                   }
                   isAPICalling = false
               }

               override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                   dismissLoadingProgress()
                   alertErrorOrValidationDialog(
                       this@ActProductDetails,
                       resources.getString(R.string.error_msg)
                   )
                   isAPICalling = false
               }
           })
       }

       //TODO CALL API FAVOURITE
       private fun callApiFavouritePro(map: HashMap<String, String>) {
           if (isAPICalling) {
               return
           }
           isAPICalling = true
           Common.showLoadingProgress(this@ActProductDetails)
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
                           dismissLoadingProgress()
                           productDetailsList?.isWishlist = 1
                           onResume()
                       } else if (restResponse.status == 0) {
                           dismissLoadingProgress()
                           alertErrorOrValidationDialog(
                               this@ActProductDetails,
                               restResponse.message
                           )
                       }
                   }
                   isAPICalling = false
               }

               override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                   dismissLoadingProgress()
                   alertErrorOrValidationDialog(
                       this@ActProductDetails,
                       resources.getString(R.string.error_msg)
                   )
                   isAPICalling = false
               }
           })
       }   */

    private fun loadProductImage(productimages: ArrayList<ProductimagesItem>) {
        lateinit var binding: RowProductviewpagerBinding
        productimageDataAdapter =
            object : BaseAdaptor<ProductimagesItem, RowProductviewpagerBinding>(
                this@ActProductDetails,
                productimages
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: ProductimagesItem,
                    position: Int
                ) {

                    Glide.with(this@ActProductDetails)
                        .load(filterHttps(productimages[position].imageUrl))
                        .placeholder(R.drawable.ic_placeholder)
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                }

                override fun setItemLayout(): Int {
                    return R.layout.row_productviewpager
                }

                override fun getBinding(parent: ViewGroup): RowProductviewpagerBinding {
                    binding = RowProductviewpagerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }

        productDetailsBinding.viewPager.apply {
            productDetailsBinding.viewPager.adapter =
                StartScreenAdapter(this@ActProductDetails, productimages)
            productDetailsBinding.tabLayout.setupWithViewPager(
                productDetailsBinding.viewPager,
                true
            )
        }
    }
    class StartScreenAdapter(
        private var mContext: Context,
        private var mImagelist: ArrayList<ProductimagesItem>
    ) :
        PagerAdapter() {
        @SuppressLint("SetTextI18n")
        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(mContext)
            val layout =
                inflater.inflate(R.layout.row_productviewpager, collection, false) as ViewGroup
            val iv: ImageView = layout.findViewById(R.id.ivProduct)
           // val vedioView:VideoView=layout.findViewById(R.id.ivProductVedio)
          //  val ivImgShare: ImageView = layout.findViewById(R.id.ivImgShare)

            Glide.with(mContext).load(filterHttps(mImagelist[position].imageUrl)).placeholder(R.drawable.ic_placeholder).into(iv)
            iv.setOnClickListener {
                val intent = Intent(mContext, ActImageSlider::class.java)
                intent.putParcelableArrayListExtra("imageList", mImagelist)
                mContext.startActivity(intent)
            }
            collection.addView(layout)
            return layout
        }

        override fun destroyItem(
            collection: ViewGroup,
            position: Int,
            view: Any
        ) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int {
            return mImagelist.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }

    private fun apiaddtocart(
        productDetailsList: ProductDetailsData,
        variation: String,
        selectedVariationName: String
    ) {

        var image = ""

        Common.showLoadingProgress(this@ActProductDetails)
       // productprice = productprice
        val attribute: String?

        var price=0.0
        var variationPackagename =""
        var variationVaritionName =""
        if (productDetailsList.variations.isNullOrEmpty()){
            price = productDetailsList.discountedPrice?.toDouble() ?: 0.0
            variationPackagename= ""
            variationVaritionName =""
        }else{
           // price = selectedDisBhkPrice+selectedDisPackagePrice
            price=  selectedDisPackagePrice
            variationPackagename = selectedPackageName
            variationVaritionName = selectedVariationName
        }
        var tax=0.0

        if (productDetailsList.taxType=="amount"){
            tax = productDetailsList.tax?.toDouble() ?: 0.0
        }else  if (productDetailsList.taxType=="percent"){
            tax = (price * (productDetailsList.tax?.toDouble() ?: 0.0)) / 100
            //  addtax = tax.toString()
        }


        productDetailsList.productimages?.firstOrNull { it.imagetype == "Image" }?.let { imageItem ->
            image = imageItem.imageUrl.toString()
        }


  /*  paymenttype = productDetailsList.taxType ?: ""
    if (productDetailsList.taxType == "amount") {
        tax = productDetailsList.tax?.toDouble() ?: 0.0
    } else {
       tax = addtax.toDouble()
    } */
    var shippingcost = ""
    if (productDetailsList.freeShipping == 1) {
        shippingcost = productDetailsList.shippingCost.toString() //"0.00"
    } else if (productDetailsList.freeShipping == 2) {
        shippingcost = productDetailsList.shippingCost.toString()
    }
    val hasmap = HashMap<String, String>()
    hasmap["product_id"] = productDetailsList.id?.toString()!!
    hasmap["user_id"] = SharePreference.getStringPref(this@ActProductDetails, SharePreference.userId)!!
    hasmap["vendor_id"] = productDetailsList.vendorId?.toString()!!
    hasmap["product_name"] = productDetailsList.productName.toString()
    hasmap["qty"] = "1"
    hasmap["price"] = price.toString()
    hasmap["variation"] = selectedVariationId.toString()  //variationPackagename
    hasmap["shipping_cost"] = shippingcost
    hasmap["image"] = image //productDetailsList.productimages?.get(0)?.imageUrl.toString()//imageName!!
    hasmap["tax"] = tax.toString()
    hasmap["attribute"] = selectedAttributeId.toString() //variationVaritionName
    val call = ApiClient.getClient.getAddtocart(hasmap)
    call.enqueue(object : Callback<SingleResponse> {
        override fun onResponse(
            call: Call<SingleResponse>,
            response: Response<SingleResponse>
        ) {
            dismissLoadingProgress()
            if (response.code() == 200) {
                if (response.body()?.status == 1) {
                    Common.isAddOrUpdated = true
                    if (isCheckNetwork(this@ActProductDetails)) {

                        if (mediaPlayer != null) {
                            mediaPlayer?.start()
                        } else {
                            Log.e("MediaPlayer", "MediaPlayer is null. Cannot start playback.")
                        }

                        vibratePhone()
                        successDialogBottomSheet()
                     //   productDetailsBinding.btnaddtocart.text="Added in Cart"
                     //   productDetailsBinding.btnaddtocart.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this@ActProductDetails, R.color.cardview_dark_background))
                          ActMain.cardItemCount.value = ActMain.cardItemCount.value?.plus(1)

                    }
                } else {
                    response.body()?.message?.let {
                        Common.showErrorFullMsg(
                            this@ActProductDetails,
                            it
                        )
                    }
                }
            } else {
                when (response.code()) {
                    404 -> {
                        Common.alertErrorOrValidationDialog(this@ActProductDetails, "Resource not found.")
                    }
                    500 -> {
                        Common.alertErrorOrValidationDialog(this@ActProductDetails, "Server error. Please try again later.")
                    }
                    else -> {
                        Common.alertErrorOrValidationDialog(this@ActProductDetails, "Unexpected error: ${response.code()}")
                    }
                }
            }
        }

        override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
            dismissLoadingProgress()
            val errorMessage = when (t) {
                is java.net.UnknownHostException -> "No internet connection. Please check your network."
                is java.net.SocketTimeoutException -> "Request timed out. Please try again."
                is java.io.IOException -> "Network error occurred. Please try again."
                else -> "Something went wrong. Please try again."
            }

            // Display the error message
            Common.alertErrorOrValidationDialog(this@ActProductDetails, errorMessage)

            // Optionally, log the error for debugging purposes
            Log.e("API_ERROR", "onFailure: ${t.localizedMessage}", t)
        }
    })
}


    fun successDialogBottomSheet() {
        val successDialogBinding = SuccessBottomsheetDialogBinding.inflate(layoutInflater)

        val dialog = BottomSheetDialog(this@ActProductDetails)
        dialog.setContentView(successDialogBinding.root)

        successDialogBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }
        successDialogBinding.btnGotoCart.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@ActProductDetails, ActMain::class.java)
            intent.putExtra("pos", "3")
            startActivity(intent)
            finish()
        }
        successDialogBinding.btncontinueshopping.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this@ActProductDetails, ActMain::class.java)
            intent.putExtra("temp", 0)
            startActivity(intent)
            finish()
        }
        dialog.show()

    }
//
//    @SuppressLint("InflateParams")
//    fun dlgAddtoCartConformationDialog(act: Activity, msg: String?) {
//        var dialog: Dialog? = null
//        try {
//            dialog?.dismiss()
//            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//            dialog.window!!.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.MATCH_PARENT
//            )
//            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            dialog.setCancelable(false)
//            val mInflater = LayoutInflater.from(act)
//            val mView = mInflater.inflate(R.layout.dlg_addtocart, null, false)
//            val textDesc: TextView = mView.findViewById(R.id.tvDesc)
//            textDesc.text = msg
//            val tvContinuation: TextView = mView.findViewById(R.id.tvcontinueshooping)
//            val finalDialog: Dialog = dialog
//            tvContinuation.setOnClickListener {
//                if (isCheckNetwork(this@ActProductDetails)) {
//                    finalDialog.dismiss()
//                    val intent = Intent(this@ActProductDetails, ActMain::class.java)
//                    intent.putExtra("temp", 0)
//                    startActivity(intent)
//                } else {
//                    alertErrorOrValidationDialog(
//                        this@ActProductDetails,
//                        resources.getString(R.string.no_internet)
//                    )
//                }
//            }
//            val tvGoToCart: TextView = mView.findViewById(R.id.tvgotocart)
//            tvGoToCart.setOnClickListener {
//                val intent = Intent(this@ActProductDetails, ActMain::class.java)
//                intent.putExtra("pos", "3")
//                startActivity(intent)
//            }
//            dialog.setContentView(mView)
//            dialog.show()
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
   // }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    private fun setupPriceData(data: DynamicVariationData) {
        selectedVariationName = if (data.variation == "") {
            ""
        } else {
            data.variation ?: ""
        }


        if(data.price!!.toDouble().equals(data.discountedVariationPrice!!.toDouble())){
            productDetailsBinding.tvOldPrice.visibility = View.GONE
        }

        if (currencyPosition == "left") {
            productDetailsBinding.tvCurrentPrice.text ="\u20b9"+data.discountedVariationPrice
            productDetailsBinding.tvOldPrice.text ="\u20b9"+data.price
        } else {
            productDetailsBinding.tvCurrentPrice.text ="\u20b9"+data.discountedVariationPrice

            productDetailsBinding.tvOldPrice.text ="\u20b9"+data.price
        }

        productprice = data.price.toDouble()
        Log.d("clickprice", productprice.toString())
      //  val tax = productprice * taxpercent.toDouble() / 100
      //  addtax = tax.toString()
        if (currencyPosition == "left") {
            if (productDetailsList?.tax != "0") {
//                productDetailsBinding.tvaddtax.text =
//                    currency.plus(
//                        String.format(
//                            Locale.US,
//                            "%,.02f",
//                            addtax.toDouble()
//                        )
//                    ).plus(" ").plus(getString(R.string.additional)).plus(getString(R.string.tax))
//                productDetailsBinding.tvaddtax.setTextColor(
//                    ResourcesCompat.getColor(
//                        resources,
//                        R.color.red,
//                        null
//                    )
//                )
            } else {
//                productDetailsBinding.tvaddtax.setTextColor(
//                    ResourcesCompat.getColor(
//                        resources,
//                        R.color.green,
//                        null
//                    )
//                )
//                productDetailsBinding.tvaddtax.text = getString(R.string.inclusive_all_taxes)
            }
        } else {
            if (productDetailsList?.tax != "0") {
//                productDetailsBinding.tvaddtax.text =
//                    (
//                            String.format(
//                                Locale.US,
//                                "%,.02f",
//                                addtax.toDouble()
//                            )
//                            ) + currency.plus("").plus(getString(R.string.additional))
//                        .plus(getString(R.string.tax))
//                productDetailsBinding.tvaddtax.setTextColor(
//                    ResourcesCompat.getColor(
//                        resources,
//                        R.color.red,
//                        null
//                    )
//                )
            } else {
//                productDetailsBinding.tvaddtax.setTextColor(
//                    ResourcesCompat.getColor(
//                        resources,
//                        R.color.green,
//                        null
//                    )
//                )
//                productDetailsBinding.tvaddtax.text = getString(R.string.inclusive_all_taxes)
            }
        }
    }

//    @RequiresApi(Build.VERSION_CODES.M)
//    @SuppressLint("NotifyDataSetChanged")
//    private fun setupProductVariationAdapter(uniqueAttributeNames: ArrayList<DynamicVariations>) {
//
//        // Initialize the adapter with unique attribute names
//        variationAdaper = VariationAdapter(productDetailsBinding,
//            this@ActProductDetails,
//            uniqueAttributeNames
//        ) { i: Int, attributeName: String ->
//
//            // Update selected attribute
//            selectedAttribute = variationList[i].attributeName
//
//            // Update variation selection state
//            variationList.forEach { it.attributeName = it.data?.attributeId.toString() == selectedAttribute }
//
//            // Update UI
//            setupProductbhkAdapter(variationList)        }
//
//        // Setup RecyclerView with the adapter
//        productDetailsBinding.rvproductSize.apply {
//            layoutManager = LinearLayoutManager(this@ActProductDetails, LinearLayoutManager.HORIZONTAL, false)
//            itemAnimator = DefaultItemAnimator()
//            adapter = variationAdaper
//        }
//
//    }
//
//    // Handle variation item click
//    private fun handleVariationClick(position: Int, attributeName: String) {
//        // Logic to handle the click event
//        // For example, you might want to update a selected variation list or perform some action
//        Log.d("VariationClick", "Clicked on: $attributeName at position $position")
//
//        // Update UI or perform actions based on the clicked item
//        // For example, you might want to filter or update other views
//    }





//    @RequiresApi(Build.VERSION_CODES.M)
//    @SuppressLint("NotifyDataSetChanged")
//    private fun setupProductbhkAdapter(variationList: ArrayList<DynamicVariationData>, selectedAttribute: String) {
//
//        // Filter data based on the selected attribute
//        val filteredList = variationList.filter { it.attributeId.toString() == selectedAttribute }
//
//        // Initialize the adapter with the filtered data
//        bhkAdaptor = BhkAdapter(
//            productDetailsBinding,
//            this@ActProductDetails,
//            filteredList
//        ) { i: Int, s: String ->
//            if (s == "ItemClick") {
//                // Ensure filteredList is not empty
//                if (filteredList.isNotEmpty()) {
//                    // Update the selection state
//                    filteredList.forEach { item -> item.isSelect = false }
//
//                    val selectedItem = filteredList[i]
//                    selectedItem.isSelect = true
//
//                    // Update button and price visibility
//                    if (selectedItem.quantity == "0") {
//                        productDetailsBinding.btnaddtocart.background = getDrawable(R.drawable.round_gray_bg_9)
//                        productDetailsBinding.btnaddtocart.isClickable = false
//                    } else {
//                        productDetailsBinding.btnaddtocart.background = getDrawable(R.drawable.round_blue_bg_9)
//                        productDetailsBinding.btnaddtocart.isClickable = true
//
//                        if (selectedItem.price?.toDouble() == selectedItem.discountedVariationPrice?.toDouble()) {
//                            productDetailsBinding.tvOldPrice.visibility = View.GONE
//                        } else {
//                            productDetailsBinding.tvOldPrice.visibility = View.VISIBLE
//                        }
//                    }
//
//                    // Update selected item details
//                    selectedBhkPrice = selectedItem.price?.toDouble() ?: 0.0
//                    selectedDisBhkPrice = selectedItem.discountedVariationPrice?.toDouble() ?: 0.0
//                    selectedBhkName = selectedItem.variation.toString()
//                    updateTotalPrice()
//
//                    // Notify the adapter about the change
//                    bhkAdaptor?.updateSelectedPosition(i)
//                }
//            }
//        }
//
//        // Set up the RecyclerView with the adapter
//        productDetailsBinding.rvbhkSize.apply {
//            layoutManager = LinearLayoutManager(
//                this@ActProductDetails,
//                LinearLayoutManager.HORIZONTAL,
//                false
//            )
//            itemAnimator = DefaultItemAnimator()
//            adapter = bhkAdaptor
//        }
//    }






    override fun onResume() {
            super.onResume()
        selectedPackagePrice = 0.0
        selectedDisPackagePrice = 0.0
        selectedBhkPrice = 0.0
        selectedDisBhkPrice = 0.0
      //  mediaPlayer?.reset()

        if(isCheckNetwork(this@ActProductDetails)) {
            callApiProductDetail(intent.getStringExtra("product_id")!!)
            Common.getToast(this@ActProductDetails,intent.getStringExtra("product_id").toString())
        } else {
            alertDialogNoInternet(
                this@ActProductDetails,
                resources.getString(R.string.no_internet)
            )
        }
    }


    companion object{
        var selectedPackagePrice: Double = 0.0
         var selectedDisPackagePrice: Double = 0.0
        private var selectedBhkPrice: Double = 0.0
        private var selectedDisBhkPrice: Double = 0.0
        var selectedVariationId : Int = 0
        var selectedAttributeId : Int = 0
    }

    private fun updateTotalPrice() {
        val totalPrice = selectedPackagePrice + selectedBhkPrice
        val totalDisPrice = selectedDisPackagePrice+selectedDisBhkPrice
        productDetailsBinding.tvOldPrice.text = "\u20b9"+totalPrice
        productDetailsBinding.tvCurrentPrice.text="\u20b9"+totalDisPrice
    }




    private fun processVariationsData(variations: List<DynamicVariations>) {
        // Prepare data for attribute names and variations
        val attributeNames = variations.map { it.attributeName }.distinct()
        variationsMap = variations.groupBy { it.attributeName }.mapValues { it.value.map { it.data } } as Map<String, List<DynamicVariationData>>

        // Set up attribute adapter
        attributeAdapter = AttributeAdapter(this, attributeNames) { selectedAttribute ->
            this.selectedPackageName = selectedAttribute

            val filteredVariations = variationsMap[selectedAttribute] ?: emptyList()
            updateVariationRecyclerView(filteredVariations)
        }
        recyclerViewAttributes.adapter = attributeAdapter
        attributeAdapter.selectFirstItem()

        // Set initial data
        val initialAttribute = attributeNames.firstOrNull()
        if (initialAttribute != null) {
            this.selectedVariationName = initialAttribute
            updateVariationRecyclerView(variationsMap[initialAttribute] ?: emptyList())
        }
    }


    private fun updateVariationRecyclerView(variations: List<DynamicVariationData>) {
        val tempstring = variations[0].variation?.reversed()?.trim()
        if (tempstring!=null) {
            productDetailsBinding.tvBhkSize.text = "Select "+tempstring.substringBefore(" ").reversed()
        }
        variationAdapter = VariationAdaptor(this, variations,productDetailsBinding){selectedVariation->
            this.selectedVariationName = selectedVariation
                productDetailsBinding.selectedService.visibility=View.VISIBLE
                productDetailsBinding.selectedService.text=selectedPackageName+" | "+selectedVariationName //+")"

        }
        recyclerViewVariations.adapter = variationAdapter
        variationAdapter.selectFirstItem()
    }


    private fun changeVisibilityisForm(isform:Boolean){
        if (isform){
            productDetailsBinding.productsizeCard.visibility=View.GONE
            productDetailsBinding.bhkCard.visibility=View.GONE
            productDetailsBinding.clBootombtn.visibility=View.GONE
            productDetailsBinding.cardBookinspection.visibility=View.VISIBLE

            productDetailsBinding.viewXpress.visibility=View.GONE
            productDetailsBinding.viewSerive.visibility=View.GONE
            productDetailsBinding.tvSpecification.visibility=View.GONE
           // productDetailsBinding.clBootombtn.visibility=View.GONE

            // Observe sqft
            viewModel.sqft.observe(this, Observer { sqft ->
                productDetailsBinding.edtSqft.setText(sqft.toString())
                productDetailsBinding.edtTotalprice.setText((sqft*currentPrice.toDouble()).toString())
            })

            // Listen to width input changes
            productDetailsBinding.edtWidth.doOnTextChanged { text, _, _, _ ->
                val width = text.toString().toFloatOrNull() ?: 0f
                viewModel.setWidth(width)
            }

            // Listen to length input changes
            productDetailsBinding.edtHeight.doOnTextChanged { text, _, _, _ ->
                val length = text.toString().toFloatOrNull() ?: 0f
                viewModel.setLength(length)
            }


        }else{
            productDetailsBinding.productsizeCard.visibility=View.VISIBLE
            productDetailsBinding.bhkCard.visibility=View.VISIBLE
            productDetailsBinding.clBootombtn.visibility=View.VISIBLE
            productDetailsBinding.cardBookinspection.visibility=View.GONE
        }
    }


    override fun onPause() {
        super.onPause()
        youTubePlayer?.pause()
      //  mediaPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        youTubePlayer = null
        mediaPlayer?.release()
        mediaPlayer = null
    }


    val dateAndtimeDataSet =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 502) {
                productDetailsBinding.edtDate.setText(result.data?.getStringExtra("date")!!.substring(3))
                productDetailsBinding.edtTime.setText(result.data?.getStringExtra("time").toString())
            }
        }


    private fun callApiScheduleInspection(id: Int?) {
        val hashMap = HashMap<String,String>()
        hashMap["product_id"] = id.toString()
        hashMap["fullName"] =  productDetailsBinding.edtName.text.toString().trim()
        hashMap["address"] =   productDetailsBinding.edtAddress.text.toString().trim()
        hashMap["latitude"] =  latitude
        hashMap["longitude"] = longitude
        hashMap["mobile"] =    productDetailsBinding.edtMobile.text.toString().trim()
        hashMap["email"] =     productDetailsBinding.edtEmail.text.toString().trim()
        hashMap["date"] =      productDetailsBinding.edtDate.text.toString().trim()
        hashMap["time"] =      productDetailsBinding.edtTime.text.toString().trim()
        hashMap["width"] =     productDetailsBinding.edtWidth.text.toString().trim()
        hashMap["length"] =    productDetailsBinding.edtHeight.text.toString().trim()
        hashMap["sqft"] =     productDetailsBinding.edtSqft.text.toString().trim()
        hashMap["total_amount"] = productDetailsBinding.edtTotalprice.text.toString().trim()

        Common.showLoadingProgress(this@ActProductDetails)
        val call = ApiClient.getClient.scheduleInspection(hashMap)
        call.enqueue(object : Callback<InspectionResponse>{
            override fun onResponse(call: Call<InspectionResponse>, response: Response<InspectionResponse>
            ) {
                if (response.isSuccessful){
                    val restResponse = response.body()
                    if (restResponse!=null){
                        if (restResponse.status==1){
                            dismissLoadingProgress()
                            Common.showSuccessFullMsg(this@ActProductDetails,restResponse.message)
                            clearAllFields()
                        }else{
                            Common.dismissLoadingProgress()
                            Common.showErrorFullMsg(this@ActProductDetails,restResponse.message)
                        }
                    }else{
                        dismissLoadingProgress()
                        Common.showErrorFullMsg(this@ActProductDetails,response.message())
                    }
                }else{
                    dismissLoadingProgress()
                    when (response.code()) {
                        404 -> {
                            Common.alertErrorOrValidationDialog(this@ActProductDetails, "Resource not found.")
                        }
                        500 -> {
                            Common.alertErrorOrValidationDialog(this@ActProductDetails, "Server error. Please try again later.")
                        }
                        else -> {
                            Common.alertErrorOrValidationDialog(this@ActProductDetails, "Unexpected error: ${response.code()}")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<InspectionResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                val errorMessage = when (t) {
                    is java.net.UnknownHostException -> "No internet connection. Please check your network."
                    is java.net.SocketTimeoutException -> "Request timed out. Please try again."
                    is java.io.IOException -> "Network error occurred. Please try again."
                    else -> "Something went wrong. Please try again."
                }

                // Display the error message
                Common.alertErrorOrValidationDialog(this@ActProductDetails, errorMessage)

                // Optionally, log the error for debugging purposes
                Log.e("API_ERROR", "onFailure: ${t.localizedMessage}", t)
            }

        })
    }

    private fun validateFields(): Boolean {
        val fields = listOf(
            productDetailsBinding.edtName to "Full Name",
            productDetailsBinding.edtAddress to "Address",
            productDetailsBinding.edtMobile to "Mobile",
            productDetailsBinding.edtEmail to "Email",
            productDetailsBinding.edtDate to "Date",
            productDetailsBinding.edtTime to "Time"
        )

        for ((editText, fieldName) in fields) {
            if (editText.text.toString().trim().isEmpty()) {
                Common.showErrorFullMsg(this@ActProductDetails,"$fieldName is required")
                return false
            }
        }
        return true
    }

    fun clearAllFields() {
        productDetailsBinding.edtName.text.clear()
        productDetailsBinding.edtAddress.text=""
        productDetailsBinding.edtMobile.text.clear()
        productDetailsBinding.edtEmail.text.clear()
        productDetailsBinding.edtDate.text=""
        productDetailsBinding.edtTime.text=""
        productDetailsBinding.edtWidth.text.clear()
        productDetailsBinding.edtHeight.text.clear()
        productDetailsBinding.edtSqft.text.clear()
        productDetailsBinding.edtTotalprice.text.clear()
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


    private fun scrollToDetailsCard() {
        productDetailsBinding.scroll.post {
            productDetailsBinding.scroll.smoothScrollTo(0, productDetailsBinding.detailsCard.bottom,2000)
        }
    }


    private fun displayDynamicStarsforRelatedProducts(rating1: LinearLayout, rating: Float) {
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
            val params = LinearLayout.LayoutParams(33, 33) // Width x Height
            params.setMargins(1, 0, 1, 0)
            starView.layoutParams = params

            // Add the starView to the LinearLayout
            rating1.addView(starView)
        }
    }


    private val scrollListener = ViewTreeObserver.OnScrollChangedListener {
        val visibleRect = Rect()
        val isVisible = productDetailsBinding.imgCard.getGlobalVisibleRect(visibleRect)

        if (isVisible) {
            productDetailsBinding.tvviewall.visibility = View.GONE
            productDetailsBinding.rlToolBar.background = null
            productDetailsBinding.view.background = null
        } else {
            productDetailsBinding.tvviewall.visibility = View.VISIBLE
            productDetailsBinding.rlToolBar.setBackgroundColor(getColor(R.color.white))
            productDetailsBinding.view.setBackgroundColor(ContextCompat.getColor(this, R.color.gray_line_color))
        }


    }



    private fun vibratePhone() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        // Check if the device supports vibration
        if (vibrator.hasVibrator()) {
            // For devices running Android API 26 (Android 8.0) or above
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Using VibrationEffect for API level 26 and above
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                // For devices below API level 26, use the old vibrate method
                vibrator.vibrate(1000) // Vibrates for 3 seconds
            }
        }
    }



}




