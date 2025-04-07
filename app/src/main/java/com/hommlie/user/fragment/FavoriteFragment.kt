package com.hommlie.user.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.hommlie.user.R
import com.hommlie.user.activity.ActDuplicateAccount
import com.hommlie.user.activity.ActSearch
import com.hommlie.user.activity.ActSignUp
import com.hommlie.user.api.ApiClient
import com.hommlie.user.base.BaseFragment
import com.hommlie.user.databinding.FragFavoriteBinding
import com.hommlie.user.model.CategoriesResponse
import com.hommlie.user.utils.Common.alertErrorOrValidationDialog
import com.hommlie.user.utils.Common.dismissLoadingProgress
import com.hommlie.user.utils.Common.getCurrentLanguage
import com.hommlie.user.utils.Common.showLoadingProgress
import com.hommlie.user.utils.SharePreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FavoriteFragment : BaseFragment<FragFavoriteBinding>() {
    private lateinit var fragFavBinding: FragFavoriteBinding


    companion object{
        var areaName = MutableLiveData<String>()
        var areadAddress = MutableLiveData<String>()
    }




    var currency: String = ""
    var currencyPosition: String = ""
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )
    private var gridLayoutManager: GridLayoutManager? = null



    var startColor: Int?=null

    private val scrollListener = ViewTreeObserver.OnScrollChangedListener {
        if (!isAdded) return@OnScrollChangedListener
        // Add your scroll logic here
        val visibleRect = Rect()
        val isVisible = fragFavBinding.headerImage.getGlobalVisibleRect(visibleRect)

        if (isVisible) {
            val headerHeight = fragFavBinding.headerImage.height // Get the height of the header image
            val scrollY = fragFavBinding.scroll.scrollY
            //  val fraction = (scrollY / headerHeight.toFloat()).coerceIn(0f, 1f)

            // Start fading after half the header height
            val adjustedScrollY = (scrollY - (headerHeight / 2)).coerceAtLeast(0)
            val fraction = (adjustedScrollY / (headerHeight / 2).toFloat()).coerceIn(0f, 1f)

            // Interpolate between the two colors
            //startColor = ContextCompat.getColor(requireContext(), R.color.status_color) // Starting color
            val endColor = ContextCompat.getColor(requireContext(), R.color.white)   // Ending color
            val interpolatedColor = blendColors(startColor!!, endColor, fraction)

            // Apply the interpolated color
            fragFavBinding.appBar.setBackgroundColor(interpolatedColor)
            requireActivity().window.statusBarColor = interpolatedColor

//                // Fade the header image color
//                fragFavBinding.headerImage.setColorFilter(interpolatedColor, android.graphics.PorterDuff.Mode.SRC_ATOP)
//
//                // Adjust header image alpha for fading effect
//                fragFavBinding.headerImage.alpha = 1f - fraction

            // Adjust system UI visibility dynamically
            if (isColorLight(interpolatedColor)) {
                requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                requireActivity().window.decorView.systemUiVisibility = 0
            }

            Log.d("ScrollFade", "Scroll fraction: $fraction, Interpolated color: $interpolatedColor")
        }else{
            fragFavBinding.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

    }





    override fun initView(view: View) {
        fragFavBinding = FragFavoriteBinding.bind(view)

       // requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)

        gridLayoutManager = GridLayoutManager(requireActivity(), 2, GridLayoutManager.VERTICAL, false)

        currency = SharePreference.getStringPref(requireActivity(), SharePreference.Currency)!!
        currencyPosition = SharePreference.getStringPref(requireActivity(), SharePreference.CurrencyPosition)!!


        callCategories()

        areaName.observe(this, androidx.lifecycle.Observer { area ->
            fragFavBinding.tvArea.text = area.toString()
        })
        areadAddress.observe(this, androidx.lifecycle.Observer { address ->
            fragFavBinding.tvAddress.text = address.toString()
        })


        fragFavBinding.ivprofile.setOnClickListener {
            if (SharePreference.getBooleanPref(requireContext(),SharePreference.isLogin)) {
                openActivity(ActDuplicateAccount::class.java)
            }else{
                openActivity(ActSignUp::class.java)
                requireActivity().finish()
            }
        }
        fragFavBinding.tvSeachText.setOnClickListener { openActivity(ActSearch::class.java) }

    }

    override fun getBinding(): FragFavoriteBinding {
        fragFavBinding = FragFavoriteBinding.inflate(layoutInflater)
        return fragFavBinding
    }



    private fun callCategories() {
        showLoadingProgress(requireActivity())
        val call = ApiClient.getClient.getcategory()
        call.enqueue(object : Callback<CategoriesResponse> {
            override fun onResponse(
                call: Call<CategoriesResponse>,
                response: Response<CategoriesResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        dismissLoadingProgress()
                    //    categoriesList = restResponce.data
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            categoriesList?.removeIf{it.id==38}
//                        }
                        if (isAdded) {
                            if (restResponce.shopHeader.status?:1 == 1) {
                                fragFavBinding.scroll.viewTreeObserver.addOnScrollChangedListener(scrollListener)
                                val headercolor = Color.parseColor(restResponce.shopHeader.textColor ?: ContextCompat.getColor(requireContext(), R.color.home_header).toString())
                                fragFavBinding.tvBranding.setTextColor(headercolor)
                                fragFavBinding.tvArea.setTextColor(headercolor)

                                //  fragFavBinding.tvAddress.setTextColor(Color.parseColor(restResponce.shopHeader.subTextColor ?: ContextCompat.getColor(requireContext(), R.color.black_subHeading_color).toString()))

                                val subHeadingcolor = Color.parseColor(restResponce.shopHeader.subTextColor ?: ContextCompat.getColor(requireContext(), R.color.black_subHeading_color).toString())
                                fragFavBinding.tvAddress.setTextColor(subHeadingcolor)
                                val drawableEnd = fragFavBinding.tvAddress.compoundDrawables[2] // End (right) drawable
                                drawableEnd?.setTint(subHeadingcolor)

                                val backgoundColor = Color.parseColor(restResponce.shopHeader.bgColor ?: ContextCompat.getColor(requireContext(), R.color.status_color).toString())
                                fragFavBinding.appBar.setBackgroundColor(backgoundColor)
                                fragFavBinding.ivprofile.setImageTintList(ColorStateList.valueOf(backgoundColor))
                                startColor = backgoundColor
                                requireActivity().window.statusBarColor=backgoundColor

//                                Glide.with(requireActivity()).load(restResponce.shopHeader.image)
//                                    .placeholder(R.drawable.ic_placeholder)
//                                    .into(fragFavBinding.headerImage)


                            }else{
                                fragFavBinding.cardHeaderimage.visibility=View.GONE
                                fragFavBinding.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                                requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
                                requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            }
                            //    loadCategoriesDeals(categoriesList!!)

//                            for (data in categoriesList!!){
//                                showtextinSearchbox.add(data.categoryName.toString())
//                            }
//                            startSlidingTextAnimation(showtextinSearchbox,fragFavBinding.tvSeachText)
                        }

                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        //  fragFavBinding.rvCategories.visibility = View.GONE
                        //  fragFavBinding.tvCategories.visibility = View.GONE
                        //  fragFavBinding.tvViewAllCate.visibility = View.GONE
                        alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<CategoriesResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }




    private fun blendColors(startColor: Int, endColor: Int, fraction: Float): Int {
        val startAlpha = Color.alpha(startColor)
        val startRed = Color.red(startColor)
        val startGreen = Color.green(startColor)
        val startBlue = Color.blue(startColor)

        val endAlpha = Color.alpha(endColor)
        val endRed = Color.red(endColor)
        val endGreen = Color.green(endColor)
        val endBlue = Color.blue(endColor)

        val blendedAlpha = (startAlpha + (endAlpha - startAlpha) * fraction).toInt()
        val blendedRed = (startRed + (endRed - startRed) * fraction).toInt()
        val blendedGreen = (startGreen + (endGreen - startGreen) * fraction).toInt()
        val blendedBlue = (startBlue + (endBlue - startBlue) * fraction).toInt()

        return Color.argb(blendedAlpha, blendedRed, blendedGreen, blendedBlue)
    }

    private fun isColorLight(color: Int): Boolean {
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        // Calculate the luminance using the formula for relative luminance
        val luminance = (0.299 * red + 0.587 * green + 0.114 * blue) / 255
        return luminance > 0.5
    }




    override fun onDestroyView() {
        super.onDestroyView()
        CoroutineScope(Dispatchers.Main).cancel()
    }

    override fun onResume() {
        super.onResume()
       // callApiWishList()
        getCurrentLanguage(requireActivity(), false)
    }
}