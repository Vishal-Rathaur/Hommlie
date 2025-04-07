package com.hommlie.user.fragment

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hommlie.user.R
import com.hommlie.user.activity.ActAllCategories
import com.hommlie.user.activity.ActAllSubCategories
import com.hommlie.user.activity.ActDuplicateAccount
import com.hommlie.user.activity.ActHelpContactUs
import com.hommlie.user.activity.ActMain
import com.hommlie.user.activity.ActProductDetails
import com.hommlie.user.activity.ActReferEarn
import com.hommlie.user.activity.ActSearch
import com.hommlie.user.activity.ActSignUp
import com.hommlie.user.activity.ActViewAll
import com.hommlie.user.activity.ActshowMapPicAddress
import com.hommlie.user.adapter.SliderAdapter
import com.hommlie.user.adapter.SliderAdapterThoghtfulVedio
import com.hommlie.user.api.ApiClient
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.base.BaseFragment
import com.hommlie.user.databinding.BottomsheetBookInspectionBinding
import com.hommlie.user.databinding.FragHomeBinding
import com.hommlie.user.databinding.RowBannerBinding
import com.hommlie.user.databinding.RowBannerleftBinding
import com.hommlie.user.databinding.RowBannerproductBinding
import com.hommlie.user.databinding.RowCategoriesBinding
import com.hommlie.user.databinding.RowFeaturedproductBinding
import com.hommlie.user.databinding.RowFeatureproductgridBinding
import com.hommlie.user.databinding.RowHotdealsbannerBinding
import com.hommlie.user.databinding.RowReviewBinding
import com.hommlie.user.model.BannerResponse
import com.hommlie.user.model.BottombannerItem
import com.hommlie.user.model.CategoriesResponse
import com.hommlie.user.model.CleaningServicesItem
import com.hommlie.user.model.DataItem
import com.hommlie.user.model.HomeAddVideo
import com.hommlie.user.model.HomefeedResponse
import com.hommlie.user.model.HotProductsItem
import com.hommlie.user.model.InspectionResponse
import com.hommlie.user.model.LargebannerItem
import com.hommlie.user.model.LeftbannerItem
import com.hommlie.user.model.ShopNowDataItem
import com.hommlie.user.model.SlidersItem
import com.hommlie.user.model.Testimonials
import com.hommlie.user.model.TopbannerItem
import com.hommlie.user.model.UserProfileData
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.alertDialogNoInternet
import com.hommlie.user.utils.Common.alertErrorOrValidationDialog
import com.hommlie.user.utils.Common.dismissLoadingProgress
import com.hommlie.user.utils.Common.filterHttps
import com.hommlie.user.utils.Common.getCurrentLanguage
import com.hommlie.user.utils.Common.isCheckNetwork
import com.hommlie.user.utils.Common.showLoadingProgress
import com.hommlie.user.utils.OnNearByServicesClickListener
import com.hommlie.user.utils.SharePreference
import com.hommlie.user.utils.UserProfileDataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.Calendar
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import kotlin.coroutines.resume
import kotlin.math.abs


class HomeFragment : BaseFragment<FragHomeBinding>() {

    private var lastScrollY = 0
    private var isMenuVisible = true


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationPermissionRequestCode = 1000
   // private lateinit var speechRecognizer: SpeechRecognizer
    private val AUDIO_PERMISSION_REQUEST_CODE = 101
    private val VOICE_SEARCH_REQUEST_CODE = 100
    private lateinit var autoCompleteCategory: AutoCompleteTextView

    private lateinit var addressLauncher: ActivityResultLauncher<Intent>

    private var latitude = ""
    private var longitude = ""

    private lateinit var lottieAnimationView: LottieAnimationView

    lateinit var  clickListener: OnNearByServicesClickListener

    private lateinit var geocoder: Geocoder

    private val handler = Handler(Looper.getMainLooper())

    companion object {
        const val REQUEST_LOCATION = 1002
    }

    private var typingJob: Job? = null
    private var isAnimationRunning = false
    private var currentIndex = 0

    private lateinit var fragHomeBinding: FragHomeBinding
    private var timer: Timer? = null

    val showtextinSearchbox = mutableListOf<String>()

    private var bannerList: ArrayList<TopbannerItem>? = null
    private var slidersList: ArrayList<SlidersItem>? = null
    private var leftbannerList: ArrayList<LeftbannerItem>? = null
    private var bottombannerList: ArrayList<BottombannerItem>? = null
    private var featuredProductsList: ArrayList<CleaningServicesItem>? = null
    private var largebannerList: ArrayList<LargebannerItem>? = null
    private var categoriesList: ArrayList<DataItem>? = null
    private var featuredProductsAdapter: BaseAdaptor<CleaningServicesItem, RowFeatureproductgridBinding>? =
        null
    private var newProductsAdaptor: BaseAdaptor<CleaningServicesItem, RowFeatureproductgridBinding>? = null
    private var shopnowAdaptor: BaseAdaptor<ShopNowDataItem, RowFeaturedproductBinding>? = null
    private var hotdealsAdaptor: BaseAdaptor<HotProductsItem, RowFeaturedproductBinding>? =
        null
    var isAPICalling: Boolean = false


    private var inspectionAddress = MutableLiveData<String>()

    private var viewPager: ViewPager2?=null
    private var tabLayout: TabLayout?=null
    private var viewpagerThoughtfulVedio: ViewPager2?=null
    private var tabLayoutThoughtfulVedio: TabLayout?=null

    private var slider:ArrayList<TopbannerItem>?=null

    var colorArray = arrayOf(
        "#FFFFFF",
        "#FFFFFF",
        "#FFFFFF",
        "#FFFFFF",
        "#FFFFFF",
        "#FFFFFF"
    )
   /* "#FDF7FF",
    "#FDF3F0",
    "#EDF7FD",
    "#FFFAEA",
    "#F1FFF6",
    "#FFF5EC"*/

    var startColor: Int?=null

    private val scrollListener = ViewTreeObserver.OnScrollChangedListener {
        if (!isAdded) return@OnScrollChangedListener
        // Add your scroll logic here
        val visibleRect = Rect()
        val isVisible = fragHomeBinding.headerImage.getGlobalVisibleRect(visibleRect)

        if (isVisible) {
            val headerHeight = fragHomeBinding.headerImage.height // Get the height of the header image
            val scrollY = fragHomeBinding.scroll.scrollY
            //  val fraction = (scrollY / headerHeight.toFloat()).coerceIn(0f, 1f)

            // Start fading after half the header height
            val adjustedScrollY = (scrollY - (headerHeight / 2)).coerceAtLeast(0)
            val fraction = (adjustedScrollY / (headerHeight / 2).toFloat()).coerceIn(0f, 1f)

            // Interpolate between the two colors
            //startColor = ContextCompat.getColor(requireContext(), R.color.status_color) // Starting color
            val endColor = ContextCompat.getColor(requireContext(), R.color.white)   // Ending color
            val interpolatedColor = blendColors(startColor!!, endColor, fraction)

            // Apply the interpolated color
            fragHomeBinding.appBar.setBackgroundColor(interpolatedColor)
            requireActivity().window.statusBarColor = interpolatedColor

//                // Fade the header image color
//                fragHomeBinding.headerImage.setColorFilter(interpolatedColor, android.graphics.PorterDuff.Mode.SRC_ATOP)
//
//                // Adjust header image alpha for fading effect
//                fragHomeBinding.headerImage.alpha = 1f - fraction

            // Adjust system UI visibility dynamically
            if (isColorLight(interpolatedColor)) {
                requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                requireActivity().window.decorView.systemUiVisibility = 0
            }

            Log.d("ScrollFade", "Scroll fraction: $fraction, Interpolated color: $interpolatedColor")
        }else{
            fragHomeBinding.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

    }





    override fun initView(view: View) {
        fragHomeBinding = FragHomeBinding.bind(view)
        if (isAdded){
            if (isCheckNetwork(requireActivity())) {
                if (SharePreference.getBooleanPref(requireContext(),SharePreference.isLogin)) {
                    getUserProfile()
                }
                callCategories()
            } else {
                alertDialogNoInternet(
                    requireActivity(),
                    resources.getString(R.string.no_internet)
                )
            }
        }
        init()
        val snapHelper = PagerSnapHelper()
      //  fragHomeBinding.rvBannerproduct.let { snapHelper.attachToRecyclerView(it) }
        fragHomeBinding.rvBanner.let { snapHelper.attachToRecyclerView(it) }
      //  fragHomeBinding.rvNewBanner.let { snapHelper.attachToRecyclerView(it) }
        fragHomeBinding.rvHotDealsBanner.let { snapHelper.attachToRecyclerView(it) }

//       val typeface = Typeface.createFromAsset(getContext()?.assets, "fonts/plussakartasans_extra_bold.ttf.ttf")
//       fragHomeBinding.tvHome.typeface = typeface

   }

    private fun init() {

        lottieAnimationView = fragHomeBinding.hellowLottie
        viewPager=fragHomeBinding.viewPager
        tabLayout=fragHomeBinding.tabLayout
        viewpagerThoughtfulVedio=fragHomeBinding.viewPagerThoughtful
        tabLayoutThoughtfulVedio=fragHomeBinding.tabLayoutThoughtful

//        lottieAnimationView.setAnimation("hiii.json")
//        lottieAnimationView.visibility=View.VISIBLE
//        lottieAnimationView.playAnimation()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        geocoder = Geocoder(requireActivity(), Locale.getDefault())

      //  requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.status_color)
      //  Glide.with(this).load(R.drawable.diwali).into(fragHomeBinding.headerImage)


         fragHomeBinding.scroll.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                val activity = activity as? ActMain

                // Check scroll direction
                if (scrollY > oldScrollY) {
                    // User is scrolling down
                    if (isMenuVisible) {
                        activity?.hideMenu()
                        isMenuVisible = false
                    }
                } else if (scrollY < oldScrollY) {
                    // User is scrolling up
                    if (!isMenuVisible) {
                        activity?.showMenu()
                        isMenuVisible = true
                    }
                }

                lastScrollY = scrollY
            }
        )


        //  setupVideoView(fragHomeBinding.video1,fragHomeBinding.loadingIndicator1, Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.dummy_video),false)
      //  setupVideoView(fragHomeBinding.video2,fragHomeBinding.loadingIndicator2, Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.dummy_video),false)
      //  setupVideoView(fragHomeBinding.video3,fragHomeBinding.loadingIndicator3, Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.dummy_video),false)
      //  setupVideoView(fragHomeBinding.video4,fragHomeBinding.loadingIndicator4, Uri.parse("android.resource://" + requireContext().packageName + "/" + R.raw.dummy_video),false)


//        fragHomeBinding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBar, verticalOffset ->
//            // Check if fully scrolled up
//            if (Math.abs(verticalOffset) == appBar.totalScrollRange) {
//                // Fully collapsed
//                appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
//                requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
//            } else {
//                // Expanded or scrolling
//                appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.status_color))
//                requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.status_color)
//            }
//        })

//        fragHomeBinding.scroll.viewTreeObserver.addOnScrollChangedListener {
//            val visibleRect = Rect()
//            val isVisible = fragHomeBinding.headerImage.getGlobalVisibleRect(visibleRect)
//
//            if (isVisible) {
//                val heightVisible = visibleRect.bottom - visibleRect.top
//                if (heightVisible > 0) {
//                    Log.d("VisibilityCheck", "ImageView is visible. Visible height: $heightVisible")
//                    fragHomeBinding.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.status_color))
//                    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                    requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.status_color)
//                } else {
//                    Log.d("VisibilityCheck", "ImageView is scrolled off-screen.")
//                    fragHomeBinding.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.status_color))
//                    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                    requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.status_color)
//                }
//            } else {
//                Log.d("VisibilityCheck", "ImageView is completely off-screen.")
//                fragHomeBinding.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
//                requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
//            }
//        }

//        fragHomeBinding.scroll.viewTreeObserver.addOnScrollChangedListener {
//            val visibleRect = Rect()
//            val isVisible = fragHomeBinding.headerImage.getGlobalVisibleRect(visibleRect)
//
//            val targetColor: Int
//            val statusBarColor: Int
//
//            if (isVisible) {
//                val heightVisible = visibleRect.bottom - visibleRect.top
//                if (heightVisible > 0) {
//                    Log.d("VisibilityCheck", "ImageView is visible. Visible height: $heightVisible")
//                    targetColor = ContextCompat.getColor(requireContext(), R.color.status_color)
//                    statusBarColor = ContextCompat.getColor(requireContext(), R.color.status_color)
//                } else {
//                    Log.d("VisibilityCheck", "ImageView is scrolled off-screen.")
//                    targetColor = ContextCompat.getColor(requireContext(), R.color.status_color)
//                    statusBarColor = ContextCompat.getColor(requireContext(), R.color.status_color)
//                }
//            } else {
//                Log.d("VisibilityCheck", "ImageView is completely off-screen.")
//                targetColor = ContextCompat.getColor(requireContext(), R.color.white)
//                statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
//            }
//
//            // Animate the app bar background color
//            val currentColor = (fragHomeBinding.appBar.background as? ColorDrawable)?.color
//                ?: ContextCompat.getColor(requireContext(), R.color.status_color)
//            animateColorChange(fragHomeBinding.appBar, currentColor, targetColor)
//
//            // Animate the status bar color
//            val currentStatusBarColor = requireActivity().window.statusBarColor
//            animateStatusBarColorChange(requireActivity(), currentStatusBarColor, statusBarColor)
//        }

//        fragHomeBinding.scroll.viewTreeObserver.addOnScrollChangedListener {
//            val visibleRect = Rect()
//            val isVisible = fragHomeBinding.headerImage.getGlobalVisibleRect(visibleRect)
//
//            if (isVisible) {
//                val headerHeight = fragHomeBinding.headerImage.height // Get the height of the header image
//                val scrollY = fragHomeBinding.scroll.scrollY
//                val fraction = (scrollY / headerHeight.toFloat()).coerceIn(0f, 1f)
//
//                // Interpolate between the two colors
//                val startColor = ContextCompat.getColor(requireContext(), R.color.status_color) // Starting color
//                val endColor = ContextCompat.getColor(requireContext(), R.color.white)   // Ending color
//                val interpolatedColor = blendColors(startColor, endColor, fraction)
//
//                // Apply the interpolated color
//                fragHomeBinding.appBar.setBackgroundColor(interpolatedColor)
//                requireActivity().window.statusBarColor = interpolatedColor
//
//                Log.d("ScrollFade", "Scroll fraction: $fraction, Interpolated color: $interpolatedColor")
//            }else{
//                requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            }
//        }

//         fragHomeBinding.scroll.viewTreeObserver.addOnScrollChangedListener {
//            val visibleRect = Rect()
//            val isVisible = fragHomeBinding.headerImage.getGlobalVisibleRect(visibleRect)
//
//            if (isVisible) {
//                val headerHeight = fragHomeBinding.headerImage.height // Get the height of the header image
//                val scrollY = fragHomeBinding.scroll.scrollY
//              //  val fraction = (scrollY / headerHeight.toFloat()).coerceIn(0f, 1f)
//
//                // Start fading after half the header height
//                val adjustedScrollY = (scrollY - (headerHeight / 2)).coerceAtLeast(0)
//                val fraction = (adjustedScrollY / (headerHeight / 2).toFloat()).coerceIn(0f, 1f)
//
//                // Interpolate between the two colors
//                val startColor = ContextCompat.getColor(requireContext(), R.color.status_color) // Starting color
//                val endColor = ContextCompat.getColor(requireContext(), R.color.white)   // Ending color
//                val interpolatedColor = blendColors(startColor, endColor, fraction)
//
//                // Apply the interpolated color
//                fragHomeBinding.appBar.setBackgroundColor(interpolatedColor)
//                requireActivity().window.statusBarColor = interpolatedColor
//
////                // Fade the header image color
////                fragHomeBinding.headerImage.setColorFilter(interpolatedColor, android.graphics.PorterDuff.Mode.SRC_ATOP)
////
////                // Adjust header image alpha for fading effect
////                fragHomeBinding.headerImage.alpha = 1f - fraction
//
//                // Adjust system UI visibility dynamically
//                if (isColorLight(interpolatedColor)) {
//                    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                } else {
//                    requireActivity().window.decorView.systemUiVisibility = 0
//                }
//
//                Log.d("ScrollFade", "Scroll fraction: $fraction, Interpolated color: $interpolatedColor")
//            }else{
//                fragHomeBinding.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
//                requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
//            }
//        }


      //  fragHomeBinding.scroll.viewTreeObserver.addOnScrollChangedListener(scrollListener)





//        fragHomeBinding.llMyorder.setOnClickListener {
//            clickListener.activateOrder()
//        }
//
//        fragHomeBinding.tvViewAllShopnow.setOnClickListener{
//            clickListener.activateShop()
//        }

        inspectionAddress.value=""

        fragHomeBinding.ivprofile.setOnClickListener {
            if (SharePreference.getBooleanPref(requireContext(),SharePreference.isLogin)) {
                openActivity(ActDuplicateAccount::class.java)
            }else{
                openActivity(ActSignUp::class.java)
                requireActivity().finish()
            }
        }

        fragHomeBinding.tvViewAllCate.setOnClickListener {
            openActivity(
                ActAllCategories::class.java
            )
        }

        fragHomeBinding.llComplaints.setOnClickListener {
            val intent  = Intent(requireActivity(),ActHelpContactUs::class.java)
            requireActivity().startActivity(intent)
        }
        fragHomeBinding.cardWhatsapphelp.setOnClickListener {
            Common.openWhatsApp(requireContext(),"+919844090440","Hi, I Need Support")
        }
        fragHomeBinding.tvViewAllfp.setOnClickListener {
            val intent = Intent(requireActivity(), ActViewAll::class.java)
            intent.putExtra("title", fragHomeBinding.tvfeaturedproduct.text.toString())
            startActivity(intent)
        }
        fragHomeBinding.tvViewAllvendors.setOnClickListener {
            val intent = Intent(requireActivity(), ActViewAll::class.java)
            intent.putExtra("title", fragHomeBinding.tvvendors.text.toString())
            startActivity(intent)
        }
        fragHomeBinding.tvViewArrivals.setOnClickListener {
            val intent = Intent(requireActivity(), ActViewAll::class.java)
            intent.putExtra("title", fragHomeBinding.tvnewArrivals.text.toString())
            startActivity(intent)
        }
        fragHomeBinding.tvViewAllhotdeals.setOnClickListener {
            val intent = Intent(requireActivity(), ActViewAll::class.java)
            intent.putExtra("title", fragHomeBinding.tvHotDeals.text.toString())
            startActivity(intent)
        }
        fragHomeBinding.tvViewAllquickbookservice.setOnClickListener {
            val intent = Intent(requireActivity(), ActViewAll::class.java)
            intent.putExtra("title", fragHomeBinding.tvHotDeals.text.toString())
            startActivity(intent)
        }
        fragHomeBinding.tvViewAllBrand.setOnClickListener {
            val intent = Intent(requireActivity(), ActViewAll::class.java)
            intent.putExtra("title", fragHomeBinding.tvBrand.text.toString())
            startActivity(intent)
        }


        fragHomeBinding.ivWomenn.setOnClickListener {
//            val url = "https://www.hommlie.com/women-empowerment"
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse(url)
//            startActivity(intent)
           startVoiceSearch()
        }
        fragHomeBinding.ivLinkedin.setOnClickListener {
            val url = UserProfileDataManager.getUserContactInfo()?.linkedin.toString()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        fragHomeBinding.ivInsta.setOnClickListener {
            val url = UserProfileDataManager.getUserContactInfo()?.instagram.toString()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        fragHomeBinding.ivFacebook.setOnClickListener {
            val url = UserProfileDataManager.getUserContactInfo()?.facebook.toString()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        fragHomeBinding.ivTwitter.setOnClickListener {
            val url = UserProfileDataManager.getUserContactInfo()?.twitter.toString()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        fragHomeBinding.ivGetcall.setOnClickListener {
            val url = "https://hommlie.com/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }



//        fragHomeBinding.imageView.setOnClickListener {
//            val intent = Intent(requireActivity(), ActshowMapPicAddress::class.java)
//            startActivityForResult(intent,REQUEST_LOCATION)
//        }
        fragHomeBinding.tvArea.setOnClickListener {
            val intent = Intent(requireActivity(), ActshowMapPicAddress::class.java)
            startActivityForResult(intent,REQUEST_LOCATION)
        }
        fragHomeBinding.tvAddress.setOnClickListener {
            val intent = Intent(requireActivity(), ActshowMapPicAddress::class.java)
            startActivityForResult(intent,REQUEST_LOCATION)
        }

      //  fragHomeBinding.tvViewAllBrand.setOnClickListener { openActivity(ActBrand::class.java) }

       // fragHomeBinding.ivheart.setOnClickListener { openActivity(ActOffers::class.java) }

       // fragHomeBinding.ivnotification.setOnClickListener { openActivity(ActNotification::class.java) }

        fragHomeBinding.tvSeachText.setOnClickListener { openActivity(ActSearch::class.java) }

        fragHomeBinding.ivlanguage.setOnClickListener {
            showLanguageDropdown()
        }

     //  Common.getToast(requireActivity(),SharePreference.getStringPref(requireActivity(),SharePreference.userId).toString())


        addressLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Get the address returned from ActAddress
                val address = result.data?.getStringExtra("selectedAddress")
                latitude  =  result.data?.getStringExtra("latitude").toString()
                longitude  =  result.data?.getStringExtra("longitude").toString()
                inspectionAddress.value = address+", "+result.data?.getStringExtra("pincode")
                //successDialogBinding.edtInsAddress.setText(address+", "+result.data?.getStringExtra("pincode"))
            }
        }

    }

    override fun getBinding(): FragHomeBinding {
        fragHomeBinding = FragHomeBinding.inflate(layoutInflater)
        return fragHomeBinding
    }

    //TODO FirstBanner
    private fun callApiBanner() {
        showLoadingProgress(requireActivity())
        val call = ApiClient.getClient.getbanner()
        call.enqueue(object : Callback<BannerResponse> {
            override fun onResponse(
                call: Call<BannerResponse>,
                response: Response<BannerResponse>
            ) {
                        if (response.code() == 200) {
                            val restResponce = response.body()!!
                            dismissLoadingProgress()
                            if (restResponce.status == 1) {
                                fragHomeBinding.tvNoDataFound.visibility = View.GONE
                                bannerList = restResponce.topbanner
                                bottombannerList = restResponce.bottombanner
                                leftbannerList = restResponce.leftbanner
                                largebannerList = restResponce.largebanner
                                Log.v("pop_banner-->", "" + restResponce.popupbanner.toString())
                                if (isAdded) {
                                   // restResponce.sliders?.let { loadPagerImagesSliders(it) }
                                    //    restResponce.topbanner?.let { loadSmallBanner(it) }

                                    slider = ArrayList<TopbannerItem>()
                                    restResponce.topbanner?.let { topBanners ->
                                        slider?.addAll(topBanners)//.map { it.imageUrl.toString() }) // Collect only image values
                                    }
                                    if (slider != null) {
                                        fragHomeBinding.clSmallbanner.visibility = View.VISIBLE
                                        fragHomeBinding.view10.visibility = View.VISIBLE
                                        val adapter = SliderAdapter(slider!!)
                                        viewPager?.adapter = adapter

                                        // Attach TabLayout with ViewPager2
                                        tabLayout?.let {
                                            viewPager?.let { it1 ->
                                                TabLayoutMediator(it, it1) { tab, position ->
                                                    // Customize tabs if needed
                                                }.attach()
                                            }
                                        }
                                    } else {
                                        fragHomeBinding.clSmallbanner.visibility = View.GONE
                                        fragHomeBinding.view10.visibility = View.GONE
                                    }
                                  //  callCategories()

                                    if (!restResponce.bannerReferEarn.isNullOrEmpty()){
                                        if (restResponce.bannerReferEarn[0].status==0){
                                            fragHomeBinding.cardRefer.visibility=View.GONE
                                        }else {
                                            fragHomeBinding.cardRefer.visibility=View.VISIBLE
                                            SharePreference.setStringPref(requireActivity(), SharePreference.referCardImage, restResponce.bannerReferEarn[0].imageUrl.toString())
                                            Glide.with(requireActivity()).load(restResponce.bannerReferEarn[0].imageUrl.toString()).placeholder(R.drawable.ic_placeholder)
                                                .diskCacheStrategy(DiskCacheStrategy.ALL).into(fragHomeBinding.ivRefer)
                                            fragHomeBinding.cardRefer.setOnClickListener {
                                                val intent = Intent(requireActivity(), ActReferEarn::class.java)
                                                // intent.putExtra("referbannerimage", restResponce.bannerReferEarn[0].imageUrl.toString())
                                                startActivity(intent)
                                            }
                                        }
                                    }else{
                                        fragHomeBinding.cardRefer.visibility=View.GONE
                                    }

                                   /* if (!restResponce.bannerPest.isNullOrEmpty()){
                                        Glide.with(requireActivity()).load(restResponce.bannerPest[0].imageUrl.toString()).into(fragHomeBinding.ivUnderPest)
                                        if (restResponce.bannerPest[0].productId!=null){
                                            Log.e("product_id--->", restResponce.bannerPest[0].productId.toString())
                                            val intent = Intent(requireActivity(), ActProductDetails::class.java)
                                            intent.putExtra("product_id",restResponce.bannerPest[0].productId.toString())
                                            startActivity(intent)
                                        }else if (restResponce.bannerPest[0].catId!=null){
                                            Log.e("cat_id--->", restResponce.bannerPest[0].catId.toString())
                                            val intent = Intent(requireActivity(), ActAllSubCategories::class.java)
                                            val extras = Bundle()
                                            extras.putString("cat_id", restResponce.bannerPest[0].catId.toString() + "")
                                            extras.putString("categoryName", restResponce.bannerPest[0].categoryName.toString() + "")
                                            //extras.putString("categoryGraphic",categoriesList[position].motionGraphics.toString())
                                            Common.getToast(requireActivity(),restResponce.bannerPest[0].catId.toString())
                                            intent.putExtras(extras)
                                            startActivity(intent)
                                            requireActivity().overridePendingTransition(R.anim.slide_in,R.anim.no_animation)
                                        }else if (restResponce.bannerPest[0].link!=null){
                                            val webpage: Uri = Uri.parse(restResponce.bannerPest[0].link)
                                            val intent = Intent(Intent.ACTION_VIEW, webpage)
                                            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                                                startActivity(intent)
                                            }
                                        }
                                    }else{
                                        fragHomeBinding.cardIvUnderpest.visibility=View.GONE
                                    }


                                    if (!restResponce.bannerBird.isNullOrEmpty()){
                                        Glide.with(requireActivity()).load(restResponce.bannerBird[0].imageUrl.toString()).into(fragHomeBinding.ivUnderBirdcontrol)

                                        if (restResponce.bannerBird[0].productId!=null){
                                            Log.e("product_id--->", restResponce.bannerBird[0].productId.toString())
                                            val intent = Intent(requireActivity(), ActProductDetails::class.java)
                                            intent.putExtra("product_id",restResponce.bannerBird[0].productId.toString())
                                            startActivity(intent)
                                        }else if (restResponce.bannerBird[0].catId!=null){
                                            Log.e("cat_id--->", restResponce.bannerBird[0].catId.toString())
                                            val intent = Intent(requireActivity(), ActAllSubCategories::class.java)
                                            val extras = Bundle()
                                            extras.putString("cat_id", restResponce.bannerBird[0].catId.toString() + "")
                                            extras.putString("categoryName", restResponce.bannerBird[0].categoryName.toString() + "")
                                            //extras.putString("categoryGraphic",categoriesList[position].motionGraphics.toString())
                                            Common.getToast(requireActivity(),restResponce.bannerBird[0].catId.toString())
                                            intent.putExtras(extras)
                                            startActivity(intent)
                                            requireActivity().overridePendingTransition(R.anim.slide_in,R.anim.no_animation)
                                        }else if (restResponce.bannerBird[0].link!=null){
                                            val webpage: Uri = Uri.parse(restResponce.bannerBird[0].link)
                                            val intent = Intent(Intent.ACTION_VIEW, webpage)
                                            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                                                startActivity(intent)
                                            }
                                        }
                                    }else{
                                        fragHomeBinding.cardIvUnderBirdcontrol.visibility=View.GONE
                                    }


                                    if (!restResponce.bannerMosqito.isNullOrEmpty()){
                                        Glide.with(requireActivity()).load(restResponce.bannerMosqito[0].imageUrl.toString()).into(fragHomeBinding.ivUnderMosquito)

                                        if (restResponce.bannerMosqito[0].productId!=null){
                                            Log.e("product_id--->", restResponce.bannerMosqito[0].productId.toString())
                                            val intent = Intent(requireActivity(), ActProductDetails::class.java)
                                            intent.putExtra("product_id",restResponce.bannerMosqito[0].productId.toString())
                                            startActivity(intent)
                                        }else if (restResponce.bannerMosqito[0].catId!=null){
                                            Log.e("cat_id--->", restResponce.bannerMosqito[0].catId.toString())
                                            val intent = Intent(requireActivity(), ActAllSubCategories::class.java)
                                            val extras = Bundle()
                                            extras.putString("cat_id", restResponce.bannerMosqito[0].catId.toString() + "")
                                            extras.putString("categoryName", restResponce.bannerMosqito[0].categoryName.toString() + "")
                                            //extras.putString("categoryGraphic",categoriesList[position].motionGraphics.toString())
                                            Common.getToast(requireActivity(),restResponce.bannerMosqito[0].catId.toString())
                                            intent.putExtras(extras)
                                            startActivity(intent)
                                            requireActivity().overridePendingTransition(R.anim.slide_in,R.anim.no_animation)
                                        }else if (restResponce.bannerMosqito[0].link!=null){
                                            val webpage: Uri = Uri.parse(restResponce.bannerMosqito[0].link)
                                            val intent = Intent(Intent.ACTION_VIEW, webpage)
                                            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                                                startActivity(intent)
                                            }
                                        }

                                    }else{
                                        fragHomeBinding.cardIvUnderMosquito.visibility=View.GONE
                                    }


                                    if (!restResponce.bannerQuickService.isNullOrEmpty()){
                                        Glide.with(requireActivity()).load(restResponce.bannerQuickService[0].imageUrl.toString()).into(fragHomeBinding.ivUnderQuickbook)

                                        if (restResponce.bannerQuickService[0].productId!=null){
                                            Log.e("product_id--->", restResponce.bannerQuickService[0].productId.toString())
                                            val intent = Intent(requireActivity(), ActProductDetails::class.java)
                                            intent.putExtra("product_id",restResponce.bannerQuickService[0].productId.toString())
                                            startActivity(intent)
                                        }else if (restResponce.bannerQuickService[0].catId!=null){
                                            Log.e("cat_id--->", restResponce.bannerQuickService[0].catId.toString())
                                            val intent = Intent(requireActivity(), ActAllSubCategories::class.java)
                                            val extras = Bundle()
                                            extras.putString("cat_id", restResponce.bannerQuickService[0].catId.toString() + "")
                                            extras.putString("categoryName", restResponce.bannerQuickService[0].categoryName.toString() + "")
                                            //extras.putString("categoryGraphic",categoriesList[position].motionGraphics.toString())
                                            Common.getToast(requireActivity(),restResponce.bannerQuickService[0].catId.toString())
                                            intent.putExtras(extras)
                                            startActivity(intent)
                                            requireActivity().overridePendingTransition(R.anim.slide_in,R.anim.no_animation)
                                        }else if (restResponce.bannerQuickService[0].link!=null){
                                            val webpage: Uri = Uri.parse(restResponce.bannerQuickService[0].link)
                                            val intent = Intent(Intent.ACTION_VIEW, webpage)
                                            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                                                startActivity(intent)
                                            }
                                        }

                                    }else{
                                        fragHomeBinding.cardIvQuickbook.visibility=View.GONE
                                    }  */

                                    handleBanner(
                                        restResponce.bannerPest?.getOrNull(0),
                                        fragHomeBinding.ivUnderPest,
                                        fragHomeBinding.cardIvUnderpest,
                                        fragHomeBinding.viewIvUnderpest,
                                        restResponce.bannerPest?.getOrNull(0)?.status ?: 0,
                                    )

                                    if (restResponce.bannerBird?.getOrNull(0)?.status==1){
                                        fragHomeBinding.cardIvUnderBirdcontrol.setOnClickListener {
                                            successDialogBottomSheet()
                                        }
                                        Glide.with(requireActivity()).load(restResponce.bannerBird.getOrNull(0)?.imageUrl).placeholder(R.drawable.ic_placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).into(fragHomeBinding.ivUnderBirdcontrol)
                                        fragHomeBinding.cardIvUnderBirdcontrol.visibility=View.VISIBLE
                                        fragHomeBinding.viewUnderBirdcontrol.visibility=View.VISIBLE
                                    }else{
                                        fragHomeBinding.cardIvUnderBirdcontrol.visibility=View.GONE
                                        fragHomeBinding.viewUnderBirdcontrol.visibility=View.GONE
                                    }
//                                    handleBanner(
//                                        restResponce.bannerBird?.getOrNull(0),
//                                        fragHomeBinding.ivUnderBirdcontrol,
//                                        fragHomeBinding.cardIvUnderBirdcontrol,
//                                        fragHomeBinding.viewUnderBirdcontrol,
//                                        restResponce.bannerBird?.getOrNull(0)?.status ?: 0
//                                    )

                                    handleBanner(
                                        restResponce.bannerMosqito?.getOrNull(0),
                                        fragHomeBinding.ivUnderMosquito,
                                        fragHomeBinding.cardIvUnderMosquito,
                                        fragHomeBinding.viewUnderMosquito,
                                        restResponce.bannerMosqito?.getOrNull(0)?.status ?: 0
                                    )

                                    handleBanner(
                                        restResponce.bannerQuickService?.getOrNull(0),
                                        fragHomeBinding.ivUnderQuickbook,
                                        fragHomeBinding.cardIvQuickbook,
                                        fragHomeBinding.viewUnderQuickbookImagecard,
                                        restResponce.bannerQuickService?.getOrNull(0)?.status ?: 0
                                    )




                                }
                            } else if (restResponce.status == 0) {
                                fragHomeBinding.tvNoDataFound.visibility = View.VISIBLE
                                dismissLoadingProgress()
                                alertErrorOrValidationDialog(
                                    requireActivity(),
                                    restResponce.message.toString()
                                )
                            }
                        }

            }
            override fun onFailure(call: Call<BannerResponse>, t: Throwable) {
                if (isAdded) {
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                        requireActivity(),
                        resources.getString(R.string.error_msg)
                    )
                }

            }
        })
    }

    //TODO first banner
    private fun loadPagerImagesSliders(slidersList: ArrayList<SlidersItem>) {
        lateinit var binding: RowBannerBinding
        val bannerAdapter = object :
            BaseAdaptor<SlidersItem, RowBannerBinding>(requireActivity(), slidersList) {
            @SuppressLint("NewApi", "ResourceType")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: SlidersItem,
                position: Int
            ) {
               /* .fitCenter()
                    .override(1200,800)*/
                Glide.with(requireActivity()).load(filterHttps(slidersList[position].imageUrl)).placeholder(R.drawable.ic_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivBanner)

                binding.ivBanner.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                holder?.itemView?.setOnClickListener {
                    val webpage: Uri = Uri.parse(slidersList[position].link)
                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(intent)
                    }
                }
            }
            override fun setItemLayout(): Int {
                return R.layout.row_banner
            }
            override fun getBinding(parent: ViewGroup): RowBannerBinding {
                binding = RowBannerBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return binding
            }
        }
        if (bannerList?.size ?: 0 > 0) {
            fragHomeBinding.rvBanner.visibility = View.GONE
            fragHomeBinding.rvBanner.apply {
                layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                itemAnimator = DefaultItemAnimator()
                adapter = bannerAdapter

                timer = Timer()
                fragHomeBinding.rvBanner.let {
                    timer?.schedule(
                        AutoScrollTaskSliders(-1, it, slidersList),
                        0,
                        5000L
                    )
                }
            }
        } else {
            fragHomeBinding.rvBanner.visibility = View.GONE
        }

    }


    //TODO SecondBanner Images
 /*   private fun loadPagerImages(topbannerList: ArrayList<TopbannerItem>) {
        lateinit var binding: RowBannerproductBinding
        val bannerAdapter = object :
            BaseAdaptor<TopbannerItem, RowBannerproductBinding>(requireActivity(), topbannerList) {
            @SuppressLint("NewApi", "ResourceType")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: TopbannerItem,
                position: Int
            ) {
                val data = topbannerList[position]
                binding.ivBanner.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                Glide.with(requireActivity()).load(filterHttps(data.imageUrl))
                   .error(R.drawable.error)
                   .into(binding.ivBanner)

               /* val requestOptions = com.bumptech.glide.request.RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .dontAnimate()
                    .dontTransform()
                Glide.with(requireActivity()).load("http://ikksa.com/ecomm/storage/app/public/images/products/product-629a314e51cd4.jpg").apply(requestOptions)
                    .into(binding.ivBanner)*/


            }

            override fun setItemLayout(): Int {
                return R.layout.row_bannerproduct
            }

            override fun getBinding(parent: ViewGroup): RowBannerproductBinding {
                binding = RowBannerproductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return binding
            }
        }

        if (isAdded) {
            if (bannerList != null) {
                fragHomeBinding.rvBannerproduct.visibility = View.VISIBLE
                fragHomeBinding.rvBannerproduct.apply {
                    layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                    itemAnimator = DefaultItemAnimator()
                    adapter = bannerAdapter
                    timer = Timer()
                    fragHomeBinding.rvBannerproduct.let {
                        timer?.schedule(
                            AutoScrollTask(-1, it, topbannerList),
                            0,
                            5000L
                        )
                    }
                }
            } else {
                fragHomeBinding.rvBannerproduct.visibility = View.GONE
            }
        }
    }  */

    fun typeWiseNavigation(type: String, catId: String, catName: String, productId: String) {
        if (type == "category") {
            val intent = Intent(requireActivity(), ActAllSubCategories::class.java)
            val extras = Bundle()
            extras.putString("cat_id", catId.toString())
            extras.putString(
                "categoryName",
                catName
            )
            intent.putExtras(extras)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in,R.anim.no_animation)
        } else if (type == "product") {
            val intent = Intent(requireActivity(), ActProductDetails::class.java)
            intent.putExtra("product_id", productId)
            startActivity(intent)
        }
    }

    //TODO categories api call
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
                       // dismissLoadingProgress()
                        categoriesList = restResponce.data
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            categoriesList?.removeIf{it.id==38}
                        }
                        if (isAdded) {
                            if (restResponce.appHeader.status?:1 == 1) {

                                val headercolor = Color.parseColor(
                                    restResponce.appHeader.textColor ?: ContextCompat.getColor(
                                        requireContext(),
                                        R.color.home_header
                                    ).toString()
                                )
                                fragHomeBinding.tvBranding.setTextColor(headercolor)
                                //  fragHomeBinding.ivCard.setCardBackgroundColor(headercolor)
                                fragHomeBinding.tvArea.setTextColor(headercolor)

                                //  fragHomeBinding.tvAddress.setTextColor(Color.parseColor(restResponce.appHeader.subTextColor ?: ContextCompat.getColor(requireContext(), R.color.black_subHeading_color).toString()))

                                val subHeadingcolor = Color.parseColor(
                                    restResponce.appHeader.subTextColor ?: ContextCompat.getColor(
                                        requireContext(),
                                        R.color.black_subHeading_color
                                    ).toString()
                                )
                                fragHomeBinding.tvAddress.setTextColor(subHeadingcolor)
                                val drawableEnd =
                                    fragHomeBinding.tvAddress.compoundDrawables[2] // End (right) drawable
                                drawableEnd?.setTint(subHeadingcolor)

                                val backgoundColor = Color.parseColor(
                                    restResponce.appHeader.bgColor ?: ContextCompat.getColor(
                                        requireContext(),
                                        R.color.status_color
                                    ).toString()
                                )
                                fragHomeBinding.appBar.setBackgroundColor(backgoundColor)
                             //   fragHomeBinding.ivprofile.setImageTintList(ColorStateList.valueOf(backgoundColor))
                                startColor =
                                    backgoundColor//restResponce.appHeader.bgColor?.let { Color.parseColor(it) } ?: ContextCompat.getColor(requireContext(), R.color.status_color)


                                fragHomeBinding.scroll.viewTreeObserver.addOnScrollChangedListener(scrollListener)

                                Glide.with(requireActivity()).load(restResponce.appHeader.image)
                                    .placeholder(R.drawable.ic_placeholder)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(fragHomeBinding.headerImage)
                            }else{
                                fragHomeBinding.cardHeaderimage.visibility=View.GONE
                                fragHomeBinding.appBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                                requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)
                                requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                            }
                            loadCategoriesDeals(categoriesList!!)
                            callApiFeaturedProducts()

                            for (data in categoriesList!!){
                                showtextinSearchbox.add(data.categoryName.toString())
                            }
                            startSlidingTextAnimation(showtextinSearchbox,fragHomeBinding.tvSeachText)
                        }

                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                      //  fragHomeBinding.rvCategories.visibility = View.GONE
                      //  fragHomeBinding.tvCategories.visibility = View.GONE
                      //  fragHomeBinding.tvViewAllCate.visibility = View.GONE
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

    //TODO categories Adapter
    private fun loadCategoriesDeals(categoriesList: ArrayList<DataItem>) {
        lateinit var binding: RowCategoriesBinding
        val categoriesAdaptor =
            object : BaseAdaptor<DataItem, RowCategoriesBinding>(
                requireActivity(),
                categoriesList
            ) {
                @SuppressLint("NewApi", "ResourceType")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: DataItem,
                    position: Int
                ) {


                    binding.tvCategoriesName.text = categoriesList[position].categoryName

                    Glide.with(requireActivity())
                        .load(filterHttps(categoriesList[position].imageUrl))
                        .placeholder(R.drawable.ic_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.ivCategories)

                    binding.clMain.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    binding.ivCategories.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    holder?.itemView?.setOnClickListener {
                        Log.e("cat_id--->", categoriesList[position].id.toString())
                        val intent = Intent(requireActivity(), ActAllSubCategories::class.java)
                        val extras = Bundle()
                        extras.putString("cat_id", categoriesList[position].id.toString() + "")
                        extras.putString("categoryName", categoriesList[position].categoryName.toString() + "")
                        extras.putString("categoryGraphic",categoriesList[position].motionGraphics.toString())
                        Common.getToast(requireActivity(),categoriesList[position].id.toString())
                        intent.putExtras(extras)
                        startActivity(intent)
                        requireActivity().overridePendingTransition(R.anim.slide_in,R.anim.no_animation)
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_categories
                }

                override fun getBinding(parent: ViewGroup): RowCategoriesBinding {
                    binding = RowCategoriesBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        if (isAdded) {
            if (categoriesList.size > 0) {
                fragHomeBinding.rvCategories.visibility = View.VISIBLE
              //  fragHomeBinding.tvCategories.visibility = View.VISIBLE
              //  fragHomeBinding.tvViewAllCate.visibility = View.VISIBLE
                fragHomeBinding.clText.visibility = View.VISIBLE
                fragHomeBinding.rvCategories.apply {
                    val spanCount = 4
                    layoutManager =
                        GridLayoutManager(activity,spanCount, LinearLayoutManager.VERTICAL, false)
                    itemAnimator = DefaultItemAnimator()
                    adapter = categoriesAdaptor
                }
            } else {
                fragHomeBinding.clText.visibility = View.GONE
                fragHomeBinding.rvCategories.visibility = View.GONE
              //  fragHomeBinding.tvCategories.visibility = View.GONE
              //  fragHomeBinding.tvViewAllCate.visibility = View.GONE
            }
        }
    }

    //TODO Api HomeFeed call
    private fun callApiFeaturedProducts() {
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(requireActivity(), SharePreference.userId)!!
        val call = ApiClient.getClient.gethomefeeds(hasmap)
        call.enqueue(object : Callback<HomefeedResponse> {
            override fun onResponse(
                call: Call<HomefeedResponse>,
                response: Response<HomefeedResponse>
            ) {
                if (isAdded && activity != null) {
                    requireActivity().runOnUiThread {
                if (response.code() == 200) {
                    callApiBanner()
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        featuredProductsList = restResponce.cleaningServices
                        if (isAdded) {
                            dismissLoadingProgress()
                            activity?.let {
                                SharePreference.setStringPref(
                                    it,
                                    SharePreference.Currency,
                                    restResponce.currency.toString()
                                )
                            }
                            activity?.let {
                                SharePreference.setStringPref(
                                    it,
                                    SharePreference.CurrencyPosition,
                                    restResponce.currencyPosition.toString()
                                )
                            }

                       //     bannerList?.let { loadPagerImages(it) }
                            loadCleaningSercices(featuredProductsList!!, restResponce.currency, restResponce.currencyPosition)
                            ActMain.cleaningProductList=featuredProductsList

                            restResponce.homeAddVideo?.let { loadHomeAddVideo(it) }

                          //  loadSliderReview(restResponce.testimonialsList!!)

                            loadPestControl(restResponce.pestControl!!,restResponce.currency, restResponce.currencyPosition)
                            ActMain.pestProductList=restResponce.pestControl

                            loadsafetyProNetting(restResponce.safetyProNetting!!, restResponce.currency, restResponce.currencyPosition)
                            ActMain.safetynettingProductList=restResponce.safetyProNetting

                          //  loadShopProducts(restResponce.shopNow!!,restResponce.currency,restResponce.currencyPosition)
                         //   bottombannerList?.let { loadPagerImagesBottomBanner(it) }
                        //    leftbannerList?.let { loadPagerImagesLeftBanner(it) }
                            loadMosquitoMess(restResponce.mosquitomess!!,restResponce.currency,restResponce.currencyPosition)
                            ActMain.mosquitomessProductList=restResponce.mosquitomess
                   //         loadOfferImages(restResponce.offer_below_categories!!)
                            loadHotDeals(restResponce.hotProducts!!, restResponce.currency, restResponce.currencyPosition)
                            loadQuickBookService(restResponce.hotProducts!!, restResponce.currency, restResponce.currencyPosition)
                            ActMain.mostbookedProductList=restResponce.hotProducts
                            largebannerList?.let { loadPagerImagesLargeBanner(it) }

                        }
                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponce.message.toString()
                        )
                    }
                   /* if (restResponce.notifications!! > 0) {
                        fragHomeBinding.rlCountnotification.visibility = View.VISIBLE
                    } else {
                        fragHomeBinding.rlCountnotification.visibility = View.GONE
                    } */
                }
                    }
                }
            }

            override fun onFailure(call: Call<HomefeedResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    requireActivity(),
                    "please try again after some time"
                )
            }
        })
    }

    //TODO Cleaning Products Adapter
    private fun loadCleaningSercices(
        featuredProductsList: ArrayList<CleaningServicesItem>,
        currency: String?,
        currencyPosition: String?
    ) {
        lateinit var binding: RowFeatureproductgridBinding
        featuredProductsAdapter =
            object : BaseAdaptor<CleaningServicesItem, RowFeatureproductgridBinding>(
                requireActivity(),
                featuredProductsList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: CleaningServicesItem,
                    position: Int
                ) {
                  /*  if (featuredProductsList.get(position).isWishlist == 0) {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_new_unlike,
                                null
                            )
                        )
                    } else {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_new_like,
                                null
                            )
                        )
                    }

                    binding.ivwishlist.setOnClickListener {
                        if (SharePreference.getBooleanPref(
                                requireActivity(),
                                SharePreference.isLogin
                            )
                        ) {
                            if (featuredProductsList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    featuredProductsList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    requireActivity(),
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(requireActivity())) {
                                    callApiFavourite(map, position)
                                } else {
                                    alertErrorOrValidationDialog(
                                        requireActivity(),
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }

                            if (featuredProductsList[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    featuredProductsList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    requireActivity(),
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(requireActivity())) {
                                    callApiRemoveFavourite(map, position)
                                } else {
                                    alertErrorOrValidationDialog(
                                        requireActivity(),
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                        } else {
                            openActivity(ActSignUp::class.java)
                            activity?.finish()
                        }
                    }
                    if (featuredProductsList[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            featuredProductsList[position].rattings?.get(0)?.avgRatting?.toString()
                    } */

                  /*  binding.tvProductName.text = featuredProductsList[position].subcategoryName
                    if(featuredProductsList[position].productPrice!!.toDouble().equals(featuredProductsList[position].discountedPrice!!.toDouble())){
                        binding.tvProductDisprice.visibility = View.GONE
                    }
                    if (currencyPosition == "left") {

                        binding.tvProductPrice.text = "\u20b9"+featuredProductsList[position].discountedPrice

                        binding.tvProductDisprice.text ="\u20b9"+featuredProductsList[position].productPrice
                    } else {

                        binding.tvProductPrice.text = "\u20b9"+featuredProductsList[position].discountedPrice
                        binding.tvProductDisprice.text =  "\u20b9"+featuredProductsList[position].productPrice
                    }  */

                    val fp = filterHttps(featuredProductsList[position].imageUrl) //productimage?.imageUrl)
                    Glide.with(requireActivity())
                        .load(fp)
                        .placeholder(R.drawable.ic_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    binding.tvProductName.setText(featuredProductsList[position].subcategoryName)

                    holder?.itemView?.setOnClickListener {
//                        Log.e("product_id--->", featuredProductsList[position].id.toString())
//                        val intent = Intent(requireActivity(), ActProductDetails::class.java)
//                        intent.putExtra("product_id", featuredProductsList[position].id.toString())
//                        startActivity(intent)
                        val intent= Intent(requireActivity(), ActViewAll::class.java )
                        intent.putExtra("subcategory_name",featuredProductsList[position].subcategoryName)
                        intent.putExtra("subcategory_id",featuredProductsList[position].id.toString())
                        startActivity(intent)
                    }
                }
                override fun setItemLayout(): Int {
                    return R.layout.row_featureproductgrid
                }
                override fun getBinding(parent: ViewGroup): RowFeatureproductgridBinding {
                    binding = RowFeatureproductgridBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        if (isAdded) {
            if (featuredProductsList.size > 0) {
                fragHomeBinding.rvfeaturedproduct.visibility = View.VISIBLE
                fragHomeBinding.clText1.visibility = View.VISIBLE
                fragHomeBinding.view.visibility = View.VISIBLE
                fragHomeBinding.rvfeaturedproduct.apply {
                    layoutManager =
                        GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
                    fragHomeBinding.rvfeaturedproduct.isNestedScrollingEnabled = true
                    itemAnimator = DefaultItemAnimator()
                    adapter = featuredProductsAdapter
                }
            } else {
                fragHomeBinding.rvfeaturedproduct.visibility = View.GONE
                fragHomeBinding.clText1.visibility = View.GONE
                fragHomeBinding.view.visibility = View.GONE
            }
        }
    }


    //TODO Mosquito_mess Products Adapter
    private fun loadMosquitoMess(
        featuredProductsList: ArrayList<CleaningServicesItem>,
        currency: String?,
        currencyPosition: String?
    ) {
        lateinit var binding: RowFeatureproductgridBinding
        featuredProductsAdapter =
            object : BaseAdaptor<CleaningServicesItem, RowFeatureproductgridBinding>(
                requireActivity(),
                featuredProductsList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: CleaningServicesItem,
                    position: Int
                ) {
                  /*  if (featuredProductsList.get(position).isWishlist == 0) {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_new_unlike,
                                null
                            )
                        )
                    } else {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_new_like,
                                null
                            )
                        )
                    }

                    binding.ivwishlist.setOnClickListener {
                        if (SharePreference.getBooleanPref(
                                requireActivity(),
                                SharePreference.isLogin
                            )
                        ) {
                            if (featuredProductsList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    featuredProductsList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    requireActivity(),
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(requireActivity())) {
                                    callApiFavourite(map, position)
                                } else {
                                    alertErrorOrValidationDialog(
                                        requireActivity(),
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }

                            if (featuredProductsList[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    featuredProductsList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    requireActivity(),
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(requireActivity())) {
                                    callApiRemoveFavourite(map, position)
                                } else {
                                    alertErrorOrValidationDialog(
                                        requireActivity(),
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                        } else {
                            openActivity(ActSignUp::class.java)
                            activity?.finish()
                        }
                    }
                    if (featuredProductsList[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            featuredProductsList[position].rattings?.get(0)?.avgRatting?.toString()
                    }  */

                    binding.tvProductName.text = featuredProductsList[position].subcategoryName
                /*    if(featuredProductsList[position].productPrice!!.toDouble().equals(featuredProductsList[position].discountedPrice!!.toDouble())){
                        binding.tvProductDisprice.visibility = View.GONE
                    }
                    if (currencyPosition == "left") {

                        binding.tvProductPrice.text ="\u20b9"+featuredProductsList[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+featuredProductsList[position].productPrice
                    } else {

                        binding.tvProductPrice.text ="\u20b9"+featuredProductsList[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+featuredProductsList[position].productPrice
                    } */

                    val fp = filterHttps(featuredProductsList[position].imageUrl?.toString())
                    Glide.with(requireActivity())
                        .load(fp)
                        .placeholder(R.drawable.ic_placeholder)
                        .placeholder(R.drawable.ic_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    holder?.itemView?.setOnClickListener {
//                        Log.e("product_id--->", featuredProductsList[position].id.toString())
//                        val intent = Intent(requireActivity(), ActProductDetails::class.java)
//                        intent.putExtra("product_id", featuredProductsList[position].id.toString())
//                        startActivity(intent)
                        val intent= Intent(requireActivity(), ActViewAll::class.java )
                        intent.putExtra("subcategory_name",featuredProductsList[position].subcategoryName)
                        intent.putExtra("subcategory_id",featuredProductsList[position].id.toString())
                        startActivity(intent)
                    }
                }
                override fun setItemLayout(): Int {
                    return R.layout.row_featureproductgrid
                }
                override fun getBinding(parent: ViewGroup): RowFeatureproductgridBinding {
                    binding = RowFeatureproductgridBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        if (isAdded) {
            if (featuredProductsList.size > 0) {
                fragHomeBinding.rvBrand.visibility = View.VISIBLE
                fragHomeBinding.clText4.visibility = View.VISIBLE
                fragHomeBinding.view4.visibility = View.VISIBLE
                fragHomeBinding.rvBrand.apply {
                    layoutManager =
                        GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
                    fragHomeBinding.rvBrand.isNestedScrollingEnabled = true
                    itemAnimator = DefaultItemAnimator()
                    adapter = featuredProductsAdapter
                }
            } else {
                fragHomeBinding.rvBrand.visibility = View.GONE
                fragHomeBinding.clText4.visibility = View.GONE
                fragHomeBinding.view4.visibility = View.GONE
            }
        }
    }

    //TODO SecondBanner Images
/*    private fun loadPagerImagesBottomBanner(bottombannerList: ArrayList<BottombannerItem>) {
        lateinit var binding: RowBannernewBinding
        val bottombannerAdaptor =
            object : BaseAdaptor<BottombannerItem, RowBannernewBinding>(
                requireActivity(),
                bottombannerList
            ) {
                @SuppressLint("NewApi", "ResourceType")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: BottombannerItem,
                    position: Int
                ) {
                    val data = bottombannerList[position]
                    Glide.with(requireActivity()).load(filterHttps(bottombannerList[position].imageUrl))
                        .into(binding.ivBanner)
                    binding.ivBanner.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    holder?.itemView?.setOnClickListener {
                        typeWiseNavigation(
                            data.type.toString(),
                            data.catId.toString(),
                            data.categoryName.toString(),
                            data.productId.toString()
                        )
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_bannernew
                }

                override fun getBinding(parent: ViewGroup): RowBannernewBinding {
                    binding = RowBannernewBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        if (isAdded) {
            if (bottombannerList.size > 0) {
                fragHomeBinding.rvNewBanner.visibility = View.VISIBLE
                fragHomeBinding.rvNewBanner.apply {
                    layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                    itemAnimator = DefaultItemAnimator()
                    adapter = bottombannerAdaptor
                    timer = Timer()
                    fragHomeBinding.rvNewBanner.let {
                        timer?.schedule(
                            AutoScrollTaskBottomBanner(-1, it, bottombannerList),
                            0,
                            5000L
                        )
                    }
                }
            } else {
                fragHomeBinding.rvNewBanner.visibility = View.GONE

            }
        }
    }  */

    //TODO Vendors Adapter
    private fun loadPestControl(vendorsList: ArrayList<CleaningServicesItem>
                            ,currency: String?,
                            currencyPosition: String?) {
        lateinit var binding: RowFeatureproductgridBinding
        val vendorsAdapter =
            object : BaseAdaptor<CleaningServicesItem, RowFeatureproductgridBinding>(requireActivity(), vendorsList) {
                @SuppressLint("NewApi", "ResourceType")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: CleaningServicesItem,
                    position: Int
                ) {
                   /* if (vendorsList.get(position).isWishlist == 0) {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_new_unlike,
                                null
                            )
                        )
                    } else {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_new_like,
                                null
                            )
                        )
                    }

                    binding.ivwishlist.setOnClickListener {
                        if (SharePreference.getBooleanPref(
                                requireActivity(),
                                SharePreference.isLogin
                            )
                        ) {
                            if (vendorsList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    vendorsList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    requireActivity(),
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(requireActivity())) {
                                    callApiFavourite(map, position)
                                } else {
                                    alertErrorOrValidationDialog(
                                        requireActivity(),
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }

                            if (vendorsList[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    vendorsList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    requireActivity(),
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(requireActivity())) {
                                    callApiRemoveFavourite(map, position)
                                } else {
                                    alertErrorOrValidationDialog(
                                        requireActivity(),
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                        } else {
                            openActivity(ActSignUp::class.java)
                            activity?.finish()
                        }
                    }
                    if (vendorsList[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            vendorsList[position].rattings?.get(0)?.avgRatting?.toString()
                    }  */

                    binding.tvProductName.text = vendorsList[position].subcategoryName
                 /*   if(vendorsList[position].productPrice!!.toDouble().equals(vendorsList[position].discountedPrice!!.toDouble())){
                        binding.tvProductDisprice.visibility = View.GONE
                    }
                    if (currencyPosition == "left") {

                        binding.tvProductPrice.text ="\u20b9"+vendorsList[position].discountedPrice

                        binding.tvProductDisprice.text ="\u20b9"+vendorsList[position].productPrice
                    } else {

                        binding.tvProductPrice.text ="\u20b9"+ vendorsList[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+vendorsList[position].productPrice

                    } */

                    val fp = filterHttps(vendorsList[position].imageUrl)
                    Glide.with(requireActivity())
                        .load(fp)
                        .placeholder(R.drawable.ic_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    holder?.itemView?.setOnClickListener {
//                        Log.e("product_id--->", vendorsList[position].id.toString())
//                        Common.getToast(requireActivity(),vendorsList[position].id.toString())
//                        val intent = Intent(requireActivity(), ActProductDetails::class.java)
//                        intent.putExtra("product_id", vendorsList[position].id.toString())
//                        startActivity(intent)
                        val intent= Intent(requireActivity(), ActViewAll::class.java )
                        intent.putExtra("subcategory_name",vendorsList[position].subcategoryName)
                        intent.putExtra("subcategory_id",vendorsList[position].id.toString())
                        startActivity(intent)
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_vendors
                }

                override fun getBinding(parent: ViewGroup): RowFeatureproductgridBinding {
                    binding = RowFeatureproductgridBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        if (isAdded) {
            if (vendorsList.size > 0) {
                fragHomeBinding.rvvendors.visibility = View.VISIBLE
                fragHomeBinding.view1.visibility = View.VISIBLE
                fragHomeBinding.clText2.visibility = View.VISIBLE

                fragHomeBinding.rvvendors.apply {
                    layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                    itemAnimator = DefaultItemAnimator()
                    adapter = vendorsAdapter
                }
            } else {
                fragHomeBinding.rvvendors.visibility = View.GONE
                fragHomeBinding.view1.visibility = View.GONE
                fragHomeBinding.clText2.visibility = View.GONE
            }
        }
    }

    //TODO New Product Adapter
    private fun loadsafetyProNetting(
        newProductsList: ArrayList<CleaningServicesItem>,
        currency: String?,
        currencyPosition: String?
    ) {
        lateinit var binding: RowFeatureproductgridBinding
        newProductsAdaptor =
            object : BaseAdaptor<CleaningServicesItem, RowFeatureproductgridBinding>(
                requireActivity(),
                newProductsList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: CleaningServicesItem,
                    position: Int
                ) {
                   /* if (newProductsList[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            newProductsList[position].rattings?.get(0)?.avgRatting?.toString()
                    }

                    if (newProductsList[position].isWishlist == 0) {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_new_unlike,
                                null
                            )
                        )
                    } else {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_new_like,
                                null
                            )
                        )
                    }

                    binding.ivwishlist.setOnClickListener {
                        if (SharePreference.getBooleanPref(
                                requireActivity(),
                                SharePreference.isLogin
                            )
                        ) {
                            if (newProductsList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    newProductsList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    requireActivity(),
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(requireActivity())) {
                                    callApiFavouriteProduct(map, position, newProductsList)
                                } else {
                                    alertErrorOrValidationDialog(
                                        requireActivity(),
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }

                            if (newProductsList[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    newProductsList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    requireActivity(),
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(requireActivity())) {
                                    callApiRemoveFavouriteProduct(map, position, newProductsList)
                                } else {
                                    alertErrorOrValidationDialog(
                                        requireActivity(),
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                        } else {
                            openActivity(ActSignUp::class.java)
                            activity?.finish()
                        }
                    }  */

                  /*  if(newProductsList[position].productPrice!!.toDouble().equals(newProductsList[position].discountedPrice!!.toDouble())){
                        binding.tvProductDisprice.visibility = View.GONE
                    }
                    if (currencyPosition == "left") {
                        binding.tvProductPrice.text ="\u20b9"+ newProductsList[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+newProductsList[position].productPrice
                    } else {
                        binding.tvProductPrice.text ="\u20b9"+newProductsList[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+ newProductsList[position].productPrice
                    } */

                    binding.tvProductName.text = newProductsList[position].subcategoryName
                    Glide.with(requireActivity())
                        .load(filterHttps(newProductsList[position].imageUrl))
                        .placeholder(R.drawable.ic_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    holder?.itemView?.setOnClickListener {
//                        Log.e("product_id--->", newProductsList[position].id.toString())
//                        val intent = Intent(requireActivity(), ActProductDetails::class.java)
//                        intent.putExtra("product_id", newProductsList[position].id.toString())
//                        startActivity(intent)
                        val intent= Intent(requireActivity(), ActViewAll::class.java )
                        intent.putExtra("subcategory_name",newProductsList[position].subcategoryName)
                        intent.putExtra("subcategory_id",newProductsList[position].id.toString())
                        startActivity(intent)
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_featuredproduct
                }

                override fun getBinding(parent: ViewGroup): RowFeatureproductgridBinding {
                    binding = RowFeatureproductgridBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        if (isAdded) {
            if (newProductsList.size > 0) {
                fragHomeBinding.clText3.visibility = View.VISIBLE
                fragHomeBinding.view3.visibility = View.VISIBLE
                fragHomeBinding.rvnewArrivals.visibility = View.VISIBLE
                fragHomeBinding.rvnewArrivals.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    itemAnimator = DefaultItemAnimator()
                    adapter = newProductsAdaptor
                }
            } else {
                fragHomeBinding.rvnewArrivals.visibility = View.GONE
                fragHomeBinding.clText3.visibility = View.GONE
                fragHomeBinding.view3.visibility = View.GONE
            }
        }
    }


    //TODO featured Products Adapter
    private fun loadShopProducts(
        featuredProductsList: ArrayList<ShopNowDataItem>,
        currency: String?,
        currencyPosition: String?
    ) {
        lateinit var binding: RowFeaturedproductBinding
        shopnowAdaptor =
            object : BaseAdaptor<ShopNowDataItem, RowFeaturedproductBinding>(
                requireActivity(),
                featuredProductsList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: ShopNowDataItem,
                    position: Int
                ) {
                    /*if (featuredProductsList.get(position).isWishlist == 0) {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_new_unlike,
                                null
                            )
                        )
                    } else {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_new_like,
                                null
                            )
                        )
                    }

                    binding.ivwishlist.setOnClickListener {
                        if (SharePreference.getBooleanPref(
                                requireActivity(),
                                SharePreference.isLogin
                            )
                        ) {
                            if (featuredProductsList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    featuredProductsList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    requireActivity(),
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(requireActivity())) {
                                    callApiFavourite(map, position)
                                } else {
                                    alertErrorOrValidationDialog(
                                        requireActivity(),
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }

                            if (featuredProductsList[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    featuredProductsList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    requireActivity(),
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(requireActivity())) {
                                    callApiRemoveFavourite(map, position)
                                } else {
                                    alertErrorOrValidationDialog(
                                        requireActivity(),
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                        } else {
                            openActivity(ActSignUp::class.java)
                            activity?.finish()
                        }
                    }
                    if (featuredProductsList[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            featuredProductsList[position].rattings?.get(0)?.avgRatting?.toString()
                    }  */

                    binding.tvProductName.text = featuredProductsList[position].productName
                    if(featuredProductsList[position].productPrice!!.toDouble().equals(featuredProductsList[position].discountedPrice!!.toDouble())){
                        binding.tvProductDisprice.visibility = View.GONE
                    }
                    if (currencyPosition == "left") {

                        binding.tvProductPrice.text ="\u20b9"+ featuredProductsList[position].discountedPrice

                        binding.tvProductDisprice.text ="\u20b9"+ featuredProductsList[position].productPrice

                    } else {

                        binding.tvProductPrice.text ="\u20b9"+ featuredProductsList[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+ featuredProductsList[position].productPrice

                    }

                    val fp = filterHttps(featuredProductsList[position].productimage?.imageUrl)
                    Glide.with(requireActivity())
                        .load(fp)
                        .placeholder(R.drawable.ic_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    holder?.itemView?.setOnClickListener {
                        Log.e("product_id--->", featuredProductsList[position].id.toString())
                        val intent = Intent(requireActivity(), ActProductDetails::class.java)
                        intent.putExtra("product_id", featuredProductsList[position].id.toString())
                        startActivity(intent)
                    }
                }
                override fun setItemLayout(): Int {
                    return R.layout.row_featureproductgrid
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
        if (isAdded) {
            if (featuredProductsList.size > 0) {
                fragHomeBinding.rvShopnow.visibility = View.VISIBLE
                fragHomeBinding.clText6.visibility = View.VISIBLE
                fragHomeBinding.rvShopnow.apply {
                    layoutManager =
                        GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
                    fragHomeBinding.rvShopnow.isNestedScrollingEnabled = true
                    itemAnimator = DefaultItemAnimator()
                    adapter = shopnowAdaptor
                }
            } else {
                fragHomeBinding.rvShopnow.visibility = View.GONE
                fragHomeBinding.clText6.visibility = View.GONE
            }
        }
    }


    //TODO ThirdBanner Images
    private fun loadPagerImagesLeftBanner(leftbannerList: ArrayList<LeftbannerItem>) {
        lateinit var binding: RowBannerleftBinding
        val leftbannerAdaptor =
            object : BaseAdaptor<LeftbannerItem, RowBannerleftBinding>(
                requireActivity(),
                leftbannerList
            ) {
                @SuppressLint("NewApi", "ResourceType")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: LeftbannerItem,
                    position: Int
                ) {
                    val data = leftbannerList[position]
                    Glide.with(requireActivity()).load(filterHttps(leftbannerList[position].imageUrl)).placeholder(R.drawable.ic_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.ivBanner)
                    binding.ivBanner.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                    holder?.itemView?.setOnClickListener {
                        typeWiseNavigation(
                            data.type.toString(),
                            data.catId.toString(),
                            data.categoryName.toString(),
                            data.productId.toString()
                        )
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_bannerleft
                }

                override fun getBinding(parent: ViewGroup): RowBannerleftBinding {
                    binding = RowBannerleftBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        if (isAdded) {
            if (leftbannerList.size > 0) {
                fragHomeBinding.rvBrandBanner.visibility = View.VISIBLE

                fragHomeBinding.rvBrandBanner.apply {
                    layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                    itemAnimator = DefaultItemAnimator()
                    adapter = leftbannerAdaptor
                }
            } else {
                fragHomeBinding.rvBrandBanner.visibility = View.GONE
            }
        }
    }




    //TODO Offer Images
 /*   private fun loadOfferImages(offerimagedata: ArrayList<OfferImage>) {
        lateinit var binding: RowOfferImageBinding
        val brandsAdaptor =
            object : BaseAdaptor<OfferImage, RowOfferImageBinding>(
                requireActivity(),
                offerimagedata
            ) {
                @SuppressLint("NewApi", "ResourceType")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: OfferImage,
                    position: Int
                ) {

                    Glide.with(requireActivity())
                        .load(filterHttps(offerimagedata[position].imageUrl)).into(binding.ivOfferimage)
                    binding.clOfferimage.backgroundTintList = ColorStateList.valueOf(Color.parseColor(offerimagedata[position].bg_color))
                    binding.tvOfferImage.text=offerimagedata[position].offertext

                    holder?.itemView?.setOnClickListener {
//                        Log.e("brand_id--->", offerimagedata[position].id.toString())
//                        val intent = Intent(requireActivity(), ActBrandDetails::class.java)
//                        intent.putExtra("brand_id", brandsList[position].id.toString())
//                        intent.putExtra("brand_name", brandsList[position].brandName.toString())
//                        startActivity(intent)
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_offer_image
                }

                override fun getBinding(parent: ViewGroup): RowOfferImageBinding {
                    binding = RowOfferImageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        if (isAdded) {
            if (offerimagedata.size > 0) {
                fragHomeBinding.rvOfferimage.visibility = View.VISIBLE
                fragHomeBinding.rvOfferimage.apply {
                    layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                    itemAnimator = DefaultItemAnimator()
                    adapter = brandsAdaptor
                }
            } else {
                fragHomeBinding.rvOfferimage.visibility = View.GONE
            }
        }
    }   */




    private fun displayDynamicStars(rating1: LinearLayout, rating: Float) {
        rating1.removeAllViews()

        for (i in 0 until 5) {
            val starView = ImageView(requireActivity())

            // Combine empty and filled stars into a LayerDrawable
            val emptyDrawable = requireContext().getDrawable(R.drawable.ic_star_empty)
            val filledDrawable = ClipDrawable(requireContext().getDrawable(R.drawable.ic_filled_star), Gravity.LEFT, ClipDrawable.HORIZONTAL)

            // Set the level (fill percentage) for each star
            val starLevel = calculateStarFillLevel(rating - i)
            filledDrawable.level = starLevel

            // Apply LayerDrawable combining empty and filled stars
            val layerDrawable = LayerDrawable(arrayOf(emptyDrawable, filledDrawable))
            starView.setImageDrawable(layerDrawable)

            // Set star size
            val params = LinearLayout.LayoutParams(37, 37) // Width x Height
            params.setMargins(1, 0, 1, 0)
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

    //TODO MostBooked service Adapter
    private fun loadHotDeals(
        hotdealsList: ArrayList<HotProductsItem>,
        currency: String?,
        currencyPosition: String?
    ) {
        lateinit var binding: RowFeaturedproductBinding
        hotdealsAdaptor =
            object : BaseAdaptor<HotProductsItem, RowFeaturedproductBinding>(
                requireActivity(),
                hotdealsList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: HotProductsItem,
                    position: Int
                ) {
                  /*  if (hotdealsList[position].rattings?.size == 0) {
                        binding.tvRatePro.text =
                            "0.0"
                    } else {
                        binding.tvRatePro.text =
                            hotdealsList[position].rattings?.get(0)?.avgRatting?.toString()
                    }
                    if (hotdealsList[position].isWishlist == 0) {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_new_unlike,
                                null
                            )
                        )
                    } else {
                        binding.ivwishlist.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                resources,
                                R.drawable.ic_new_like,
                                null
                            )
                        )
                    }

                    binding.ivwishlist.setOnClickListener {
                        if (SharePreference.getBooleanPref(
                                requireActivity(),
                                SharePreference.isLogin
                            )
                        ) {
                            if (hotdealsList[position].isWishlist == 0) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    hotdealsList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    requireActivity(),
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(requireActivity())) {
                                    callApiFavouriteHot(map, position, hotdealsList)
                                } else {
                                    alertErrorOrValidationDialog(
                                        requireActivity(),
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }

                            if (hotdealsList[position].isWishlist == 1) {
                                val map = HashMap<String, String>()
                                map["product_id"] =
                                    hotdealsList[position].id.toString()
                                map["user_id"] = SharePreference.getStringPref(
                                    requireActivity(),
                                    SharePreference.userId
                                )!!

                                if (isCheckNetwork(requireActivity())) {
                                    callApiRemoveFavouriteHot(map, position, hotdealsList)
                                } else {
                                    alertErrorOrValidationDialog(
                                        requireActivity(),
                                        resources.getString(R.string.no_internet)
                                    )
                                }
                            }
                        } else {
                            openActivity(ActSignUp::class.java)
                            activity?.finish()
                        }
                    } */
                    binding.tvProductName.text = hotdealsList[position].productName

                    if(hotdealsList[position].productPrice!!.toDouble().equals(hotdealsList[position].discountedPrice!!.toDouble())){
                        binding.tvProductDisprice.visibility = View.GONE
                    }

                    if (currencyPosition == "left") {
                        binding.tvProductPrice.text ="\u20b9"+hotdealsList[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+ hotdealsList[position].productPrice
                    } else {
                        binding.tvProductPrice.text ="\u20b9"+hotdealsList[position].discountedPrice
                        binding.tvProductDisprice.text = "\u20b9"+hotdealsList[position].productPrice
                    }

                  //  binding.tvdiscountpercentage.text=(calculateDiscountPercentage(hotdealsList[position].productPrice.toDouble(),hotdealsList[position].discountedPrice!!.toDouble())).toString()+"% off"

                    val calculatedDiscount = calculateDiscountPercentage(hotdealsList[position].productPrice?.toDouble()
                        ?: 0.0, hotdealsList[position].discountedPrice?.toDouble() ?: 0.0)

                    if (calculatedDiscount>0&&calculatedDiscount!=null){
                        binding.tvdiscountpercentage.text= calculatedDiscount.toString()+"% off"
                    }else{
                        binding.tvdiscountpercentage.visibility=View.GONE
                        binding.ivDownarrow.visibility =View.GONE
                    }

                    //displayDynamicStars(binding.ivRate,4.5f)

                    hotdealsList[position].rating?.let { displayDynamicStars(binding.ivRate, it)
                        binding.tvRatePro.text=it.toString()}


                    Glide.with(requireActivity())
                        .load(filterHttps(hotdealsList[position].productimage?.imageUrl))
                        .placeholder(R.drawable.ic_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    holder?.itemView?.setOnClickListener {
                        Log.e("product_id--->", hotdealsList[position].id.toString())
                        val intent = Intent(requireActivity(), ActProductDetails::class.java)
                        intent.putExtra("product_id", hotdealsList[position].id.toString())
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
        if (isAdded) {
            if (hotdealsList.size > 0) {
                fragHomeBinding.rvHotDeals.visibility = View.VISIBLE
                fragHomeBinding.clText5.visibility = View.VISIBLE
                fragHomeBinding.view5.visibility = View.VISIBLE
                fragHomeBinding.view7.visibility = View.VISIBLE

                fragHomeBinding.rvHotDeals.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
                    itemAnimator = DefaultItemAnimator()
                    adapter = hotdealsAdaptor
                }
            } else {
                fragHomeBinding.rvHotDeals.visibility = View.GONE
                fragHomeBinding.view5.visibility = View.GONE
                fragHomeBinding.clText5.visibility = View.GONE
                fragHomeBinding.view7.visibility = View.GONE
            }
        }
    }


    //TODO Quick Booking Service service Adapter
    private fun loadQuickBookService(
        hotdealsList: ArrayList<HotProductsItem>,
        currency: String?,
        currencyPosition: String?
    ) {
        lateinit var binding: RowFeaturedproductBinding
        hotdealsAdaptor =
            object : BaseAdaptor<HotProductsItem, RowFeaturedproductBinding>(
                requireActivity(),
                hotdealsList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: HotProductsItem,
                    position: Int
                ) {
                    /*  if (hotdealsList[position].rattings?.size == 0) {
                          binding.tvRatePro.text =
                              "0.0"
                      } else {
                          binding.tvRatePro.text =
                              hotdealsList[position].rattings?.get(0)?.avgRatting?.toString()
                      }
                      if (hotdealsList[position].isWishlist == 0) {
                          binding.ivwishlist.setImageDrawable(
                              ResourcesCompat.getDrawable(
                                  resources,
                                  R.drawable.ic_new_unlike,
                                  null
                              )
                          )
                      } else {
                          binding.ivwishlist.setImageDrawable(
                              ResourcesCompat.getDrawable(
                                  resources,
                                  R.drawable.ic_new_like,
                                  null
                              )
                          )
                      }

                      binding.ivwishlist.setOnClickListener {
                          if (SharePreference.getBooleanPref(
                                  requireActivity(),
                                  SharePreference.isLogin
                              )
                          ) {
                              if (hotdealsList[position].isWishlist == 0) {
                                  val map = HashMap<String, String>()
                                  map["product_id"] =
                                      hotdealsList[position].id.toString()
                                  map["user_id"] = SharePreference.getStringPref(
                                      requireActivity(),
                                      SharePreference.userId
                                  )!!

                                  if (isCheckNetwork(requireActivity())) {
                                      callApiFavouriteHot(map, position, hotdealsList)
                                  } else {
                                      alertErrorOrValidationDialog(
                                          requireActivity(),
                                          resources.getString(R.string.no_internet)
                                      )
                                  }
                              }

                              if (hotdealsList[position].isWishlist == 1) {
                                  val map = HashMap<String, String>()
                                  map["product_id"] =
                                      hotdealsList[position].id.toString()
                                  map["user_id"] = SharePreference.getStringPref(
                                      requireActivity(),
                                      SharePreference.userId
                                  )!!

                                  if (isCheckNetwork(requireActivity())) {
                                      callApiRemoveFavouriteHot(map, position, hotdealsList)
                                  } else {
                                      alertErrorOrValidationDialog(
                                          requireActivity(),
                                          resources.getString(R.string.no_internet)
                                      )
                                  }
                              }
                          } else {
                              openActivity(ActSignUp::class.java)
                              activity?.finish()
                          }
                      } */
                    binding.tvProductName.text = hotdealsList[position].productName

                    if(hotdealsList[position].productPrice!!.toDouble().equals(hotdealsList[position].discountedPrice!!.toDouble())){
                        binding.tvProductDisprice.visibility = View.GONE
                    }

                    if (currencyPosition == "left") {
                        binding.tvProductPrice.text ="\u20b9"+hotdealsList[position].discountedPrice
                        binding.tvProductDisprice.text ="\u20b9"+ hotdealsList[position].productPrice
                    } else {
                        binding.tvProductPrice.text ="\u20b9"+hotdealsList[position].discountedPrice
                        binding.tvProductDisprice.text = "\u20b9"+hotdealsList[position].productPrice
                    }

                    binding.tvdiscountpercentage.text=(calculateDiscountPercentage(hotdealsList[position].productPrice!!.toDouble(),hotdealsList[position].discountedPrice!!.toDouble())).toString()+"% off"


                    //displayDynamicStars(binding.ivRate,4.5f)
                    hotdealsList[position].rating?.let { displayDynamicStars(binding.ivRate, it)
                        binding.tvRatePro.text=it.toString()}

                    Glide.with(requireActivity())
                        .load(filterHttps(hotdealsList[position].productimage?.imageUrl)).placeholder(R.drawable.ic_placeholder)
                        .transition(DrawableTransitionOptions.withCrossFade(1000))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.ivProduct)
                    binding.ivProduct.setBackgroundColor(Color.parseColor(colorArray[position % 6]))
                    holder?.itemView?.setOnClickListener {
                        Log.e("product_id--->", hotdealsList[position].id.toString())
                        val intent = Intent(requireActivity(), ActProductDetails::class.java)
                        intent.putExtra("product_id", hotdealsList[position].id.toString())
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
        if (isAdded) {
            if (hotdealsList.size > 0) {
                fragHomeBinding.rvHotDeals.visibility = View.VISIBLE
                fragHomeBinding.clQuickbookservice.visibility = View.VISIBLE
                fragHomeBinding.viewUnderQuickbook.visibility = View.VISIBLE

                fragHomeBinding.rvquickbookservice.apply {
                    layoutManager =
                        LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
                    itemAnimator = DefaultItemAnimator()
                    adapter = hotdealsAdaptor
                }
            } else {
                fragHomeBinding.rvquickbookservice.visibility = View.GONE
                fragHomeBinding.clQuickbookservice.visibility = View.GONE
                fragHomeBinding.viewUnderQuickbook.visibility = View.GONE
            }
        }
    }

    //TODO LastBanner Images
    private fun loadPagerImagesLargeBanner(largebannerList: ArrayList<LargebannerItem>) {
        lateinit var binding: RowHotdealsbannerBinding
        val largebannerAdaptor =
            object : BaseAdaptor<LargebannerItem, RowHotdealsbannerBinding>(
                requireActivity(),
                largebannerList
            ) {
                @SuppressLint("NewApi", "ResourceType")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: LargebannerItem,
                    position: Int
                ) {
                    val data = largebannerList[position]
                    Glide.with(requireActivity()).load(largebannerList[position].imageUrl).placeholder(R.drawable.ic_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.ivBanner)
                    binding.ivBanner.setBackgroundColor(Color.parseColor(colorArray[position % 6]))


                    holder?.itemView?.setOnClickListener {
                        typeWiseNavigation(
                            data.type.toString(),
                            data.catId.toString(),
                            data.categoryName.toString(),
                            data.productId.toString()
                        )
                    }
                }
                override fun setItemLayout(): Int {
                    return R.layout.row_hotdealsbanner
                }
                override fun getBinding(parent: ViewGroup): RowHotdealsbannerBinding {
                    binding = RowHotdealsbannerBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        if (isAdded) {
            if (largebannerList.size > 0) {
                fragHomeBinding.rvHotDealsBanner.visibility = View.VISIBLE

                fragHomeBinding.rvHotDealsBanner.apply {
                    layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                    itemAnimator = DefaultItemAnimator()
                    adapter = largebannerAdaptor
                }
            } else {
                fragHomeBinding.rvHotDealsBanner.visibility = View.GONE
            }
        }
    }

    //TODO product remover favourite
  /*  private fun callApiRemoveFavourite(map: HashMap<String, String>, position: Int) {
        if (isAPICalling) {
            return
        }
        isAPICalling = true
        showLoadingProgress(requireActivity())
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
                        featuredProductsList!![position].isWishlist = 0
                        featuredProductsAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponse.message
                        )
                    }
                }
                isAPICalling = false
            }
            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
                isAPICalling = false
            }
        })
    }

    //TODO product favourite
    private fun callApiFavourite(map: HashMap<String, String>, position: Int) {
        if (isAPICalling) {
            return
        }
        isAPICalling = true
        showLoadingProgress(requireActivity())
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
                        featuredProductsList!![position].isWishlist = 1
                        featuredProductsAdapter!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponse.message
                        )
                    }
                }
                isAPICalling = false
            }
            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
                isAPICalling = false
            }
        })
    }

    //TODO product remover favourite
    private fun callApiRemoveFavouriteProduct(
        map: HashMap<String, String>,
        position: Int,
        newProductsList: ArrayList<NewProductsItem>
    ) {
        if (isAPICalling) {
            return
        }
        isAPICalling = true
        showLoadingProgress(requireActivity())
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
                        newProductsList[position].isWishlist = 0
                        newProductsAdaptor!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponse.message
                        )
                    }
                }
                isAPICalling = false
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
                isAPICalling = false
            }
        })
    }

    //TODO product favourite
    private fun callApiFavouriteProduct(
        map: HashMap<String, String>,
        position: Int,
        newProductsList: ArrayList<NewProductsItem>
    ) {
        if (isAPICalling) {
            return
        }
        isAPICalling = true
        showLoadingProgress(requireActivity())
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
                        newProductsList[position].isWishlist = 1
                        newProductsAdaptor!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponse.message
                        )
                    }
                }
                isAPICalling = false
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
                isAPICalling = false
            }
        })
    }

    //TODO product remover favourite
    private fun callApiRemoveFavouriteHot(
        map: HashMap<String, String>,
        position: Int,
        hotdealsList: ArrayList<HotProductsItem>
    ) {
        if (isAPICalling) {
            return
        }
        isAPICalling = true
        showLoadingProgress(requireActivity())
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
                        hotdealsList[position].isWishlist = 0
                        hotdealsAdaptor!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponse.message
                        )
                    }
                }
                isAPICalling = false
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
                isAPICalling = false
            }
        })
    }

    //TODO product favourite
    private fun callApiFavouriteHot(
        map: HashMap<String, String>,
        position: Int,
        hotdealsList: ArrayList<HotProductsItem>
    ) {
        if (isAPICalling) {
            return
        }
        isAPICalling = true
        showLoadingProgress(requireActivity())
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
                        hotdealsList[position].isWishlist = 1
                        hotdealsAdaptor!!.notifyItemChanged(position)
                    } else if (restResponse.status == 0) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponse.message
                        )
                    }
                }
                isAPICalling = false
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
                isAPICalling = false
            }
        })
    }  */

    //TODO SecondBanner Images
    private class AutoScrollTask(
        private var position: Int,
        private var rvBannerproduct: RecyclerView,
        private var arrayList: ArrayList<TopbannerItem>
    ) : TimerTask() {
        override fun run() {
            if (arrayList.size > position) {

                if (position == arrayList.size - 1) {
                    position = 0
                } else {
                    position++
                }
            }
            if (rvBannerproduct != null && rvBannerproduct.layoutManager != null) {
                rvBannerproduct.smoothScrollToPosition(position)
            }
        }
    }

    //TODO first banner
    private class AutoScrollTaskSliders(
        private var position: Int,
        private var rvBanner: RecyclerView,
        private var arrayList: ArrayList<SlidersItem>
    ) : TimerTask() {
        override fun run() {
            if (arrayList.size > position) {

                if (position == arrayList.size - 1) {
                    position = 0
                } else {
                    position++
                }
            }
            if (rvBanner != null && rvBanner.layoutManager != null) {
                rvBanner.smoothScrollToPosition(position)
            }
           // rvBanner.smoothScrollToPosition(position)
        }
    }


    //TODO review
    private class AutoScrollReview(
        private var position: Int,
        private var rvreview: RecyclerView,
        private var arrayList: ArrayList<Testimonials>
    ) : TimerTask() {
        override fun run() {
            if (arrayList.size > position) {

                if (position == arrayList.size - 1) {
                    position = 0
                } else {
                    position++
                }
            }
            if (rvreview != null && rvreview.layoutManager != null) {
                rvreview.smoothScrollToPosition(position)
            }
            // rvBanner.smoothScrollToPosition(position)
        }
    }


    //TODO New small baaner second
    private class AutoScrollTaskSmallBanner(
        private var position: Int,
        private var rvNewBanner: RecyclerView,
        private var arrayList: ArrayList<TopbannerItem>
    ) : TimerTask() {
        override fun run() {
            if (arrayList.size > position) {

                if (position == arrayList.size - 1) {
                    position = 0
                } else {
                    position++
                }
            }
            if (rvNewBanner != null && rvNewBanner.layoutManager != null) {
                rvNewBanner.smoothScrollToPosition(position)
            }
        }
    }


    //TODO show review
    private fun loadSliderReview(slidersList: ArrayList<Testimonials>) {
        lateinit var binding: RowReviewBinding
        val bannerAdapter = object :
            BaseAdaptor<Testimonials, RowReviewBinding>(requireActivity(), slidersList) {
            @SuppressLint("NewApi", "ResourceType")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: Testimonials,
                position: Int
            ) {
                /* .fitCenter()
                     .override(1200,800)*/
                Glide.with(requireActivity()).load(filterHttps(slidersList[position].image))
                    .placeholder(R.drawable.placeholder_homevideo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivImage)

                binding.tvName.text = slidersList[position].name
                binding.tvText.text = slidersList[position].feedback
                binding.tvLocation.text = slidersList[position].location

                val textView = binding.tvText
                val fullText = slidersList[position].feedback ?: ""


                // Update the text based on the expanded state
                updateTextView(textView, fullText, slidersList[position].isExpanded)

                // Set up click listener to toggle expanded state
                textView.setOnClickListener {
                    slidersList[position].isExpanded = !slidersList[position].isExpanded
                    updateTextView(textView, fullText, slidersList[position].isExpanded)
                }



                //  binding.ivBanner.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

               /* holder?.itemView?.setOnClickListener {
                    val webpage: Uri = Uri.parse(slidersList[position].link)
                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivity(intent)
                    }
                } */
            }
            override fun setItemLayout(): Int {
                return R.layout.row_review
            }
            override fun getBinding(parent: ViewGroup): RowReviewBinding {
                binding = RowReviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return binding
            }
        }
        if (slidersList.size > 0) {
            fragHomeBinding.clWhatpeoplesay.visibility = View.VISIBLE
            fragHomeBinding.rvReview.apply {
                layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                itemAnimator = DefaultItemAnimator()
                adapter = bannerAdapter

                timer = Timer()
                fragHomeBinding.rvReview.let {
                    timer?.schedule(
                        AutoScrollReview(-1, it, slidersList),
                        0,
                        5000L
                    )
                }
            }
        } else {
            fragHomeBinding.clWhatpeoplesay.visibility = View.GONE
        }

    }

    private fun updateTextView(textView: TextView, fullText: String, isExpanded: Boolean) {
        if (isExpanded) {
            // Show full text and "Hide" option
            textView.text = fullText
            textView.maxLines = Integer.MAX_VALUE // Show full text
            val spannableText = SpannableString("$fullText Hide")
            spannableText.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.serviceHeadingcolor)),
                spannableText.length - 4, // Index of "Hide"
                spannableText.length,     // End index of "Hide"
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textView.text = spannableText
        } else {
            // Show only 4 lines and "Read more" option
            textView.maxLines = 4
            if (textView.layout != null && textView.layout.lineCount > 3) {
                val endOfSecondLine = textView.layout.getLineEnd(2)
                if (endOfSecondLine <= fullText.length) {
                    val visibleText = fullText.substring(0, endOfSecondLine).trim()
                    val spannableText = SpannableString("$visibleText... Read more")
                    spannableText.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.serviceHeadingcolor)),
                        spannableText.length - 9, // Index of "Read more"
                        spannableText.length,     // End index of "Read more"
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    textView.text = spannableText
                } else {
                    // Fallback for cases where the string length is shorter than expected
                       textView.text = "$fullText... Read more"
                }
            } else {
                textView.text = "$fullText... Read more"
            }
        }
    }


    //TODO show small banner
    private fun loadSmallBanner(slidersList: ArrayList<TopbannerItem>) {
        lateinit var binding: RowBannerproductBinding
        val bannerAdapter = object :
            BaseAdaptor<TopbannerItem, RowBannerproductBinding>(requireActivity(), slidersList) {
            @SuppressLint("NewApi", "ResourceType")
            override fun onBindData(
                holder: RecyclerView.ViewHolder?,
                `val`: TopbannerItem,
                position: Int
            ) {
                /* .fitCenter()
                     .override(1200,800)*/
                Glide.with(requireActivity()).load(filterHttps(slidersList[position].imageUrl)).placeholder(R.drawable.placeholder_homevideo)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivBanner)
//                binding.tvName.text=slidersList[position].name
//                binding.tvText.text=slidersList[position].feedback
//                binding.tvLocation.text=slidersList[position].location

                //  binding.ivBanner.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                /* holder?.itemView?.setOnClickListener {
                     val webpage: Uri = Uri.parse(slidersList[position].link)
                     val intent = Intent(Intent.ACTION_VIEW, webpage)
                     if (intent.resolveActivity(requireActivity().packageManager) != null) {
                         startActivity(intent)
                     }
                 } */
            }
            override fun setItemLayout(): Int {
                return R.layout.row_bannerproduct
            }
            override fun getBinding(parent: ViewGroup): RowBannerproductBinding {
                binding = RowBannerproductBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return binding
            }
        }
        if (slidersList.size > 0) {
            fragHomeBinding.rvSmallbanner.visibility = View.VISIBLE
            fragHomeBinding.view10.visibility=View.VISIBLE
            fragHomeBinding.rvSmallbanner.apply {
                layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                itemAnimator = DefaultItemAnimator()
                adapter = bannerAdapter

                timer = Timer()
                fragHomeBinding.rvSmallbanner.let {
                    timer?.schedule(
                        AutoScrollTaskSmallBanner(-1, it, slidersList),
                        0,
                        5000L
                    )
                }
            }
        } else {
            fragHomeBinding.rvSmallbanner.visibility = View.GONE
            fragHomeBinding.view10.visibility=View.GONE
        }

    }




    override fun onPause() {
        super.onPause()
        stopSlidingTextAnimation()
        if (timer != null) {
            timer?.cancel()
            timer?.purge()
        }
        handler.removeCallbacksAndMessages(null)

        FavoriteFragment.areaName.value=fragHomeBinding.tvArea.text.toString()
        FavoriteFragment.areadAddress.value=fragHomeBinding.tvAddress.text.toString()
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(requireActivity(), false)
        if (isAdded) {
            startSlidingTextAnimation(showtextinSearchbox,fragHomeBinding.tvSeachText)
            if (Common.hasLocationPermission(requireActivity())){
                getCurrentLocation()

            }else {
                val intent = Intent(requireActivity(),ActshowMapPicAddress::class.java)
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.slide_in,R.anim.no_animation)
            }
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        fragHomeBinding.scroll.viewTreeObserver.removeOnScrollChangedListener(scrollListener)

    }



    private fun getUserProfile(){
        val hashMap=HashMap<String,String>()
        hashMap["user_id"]=SharePreference.getStringPref(requireContext(),SharePreference.userId).toString()

        val call= ApiClient.getClient.getUserProfile(hashMap)

        call.enqueue(object : Callback<UserProfileData>{
            override fun onResponse(call: Call<UserProfileData>, response: Response<UserProfileData>) {
                if (response.isSuccessful){
                    val restResponse = response.body()
                    if (restResponse != null) {
                        if (restResponse.status==1){
                             dismissLoadingProgress()
                            if (isAdded) {
                                SharePreference.setDoublePref(requireContext(), SharePreference.wallet, restResponse.walletBalance)
                                Common.getToast(requireActivity(),SharePreference.getDoublePref(requireContext(),SharePreference.wallet).toString())
                                ActMain.cardItemCount.value = restResponse.cartLength
                            }
                             UserProfileDataManager.setUserProfileData(restResponse.data_user)
                            if (isAdded)
                            Glide.with(requireActivity()).load(UserProfileDataManager.getUserProfileData()?.profilePic).placeholder(R.drawable.ic_profile_new)
                                .diskCacheStrategy(DiskCacheStrategy.ALL).into(fragHomeBinding.ivprofile)

                            if (UserProfileDataManager.getUserProfileData()?.name.isNullOrEmpty()) {
                                fragHomeBinding.tvAccountownername.text = "Hello"
                            }else{
                                fragHomeBinding.tvAccountownername.text = "Hello "+ (UserProfileDataManager.getUserProfileData()?.name?.trim()?.substringBefore(" ")?: "")
                            }
                             UserProfileDataManager.setUserContactInfo(restResponse.data_contactInfo)
                        }else{
                            dismissLoadingProgress()
                            Common.showErrorFullMsg(requireActivity(),restResponse.message)
                        }
                    }
                }else{
                    dismissLoadingProgress()
                    Common.showErrorFullMsg(requireActivity(), response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<UserProfileData>, t: Throwable) {
                dismissLoadingProgress()

            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_LOCATION && resultCode == Activity.RESULT_OK) {
            // Get the selected location from the result Intent
            val selectedArea = data?.getStringExtra("selectedArea")
            val selectedAddress= data?.getStringExtra("selectedAddress")
            // Set the selected location in the edtLocation
            fragHomeBinding.tvArea.setText(selectedArea)
            fragHomeBinding.tvAddress.setText(selectedAddress)
        }

        if (requestCode == VOICE_SEARCH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognizedText = matches?.get(0) ?: "No speech recognized"

            // Display the text in a Toast or TextView
           // Common.getToast(requireActivity(),"Recognized Text: $recognizedText")

            val intent = Intent(requireActivity(),ActSearch::class.java)
            intent.putExtra("voicesearch",recognizedText)
            startActivity(intent)
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    Log.d("HomeFragment", "Current Location: $currentLatLng")

                    // Handle geocoding based on Android version
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Use GeocodeListener for asynchronous geocoding on Android 13+
                        Geocoder(requireContext(), Locale.getDefault()).getFromLocation(
                            currentLatLng.latitude,
                            currentLatLng.longitude,
                            1,
                            object : Geocoder.GeocodeListener {
                                override fun onGeocode(addresses: List<Address>) {
                                    if (addresses.isNotEmpty()) {
                                        val address = addresses[0]
                                        val addressText = "${address.getAddressLine(0)}\nLat: ${currentLatLng.latitude}, Lon: ${currentLatLng.longitude}"
                                        fragHomeBinding.tvAddress.text = addressText

                                        val areaName = address.subLocality ?: address.locality ?: "Area not found"
                                        fragHomeBinding.tvArea.text = areaName


                                    } else {
                                        fragHomeBinding.tvAddress.text = "Address not found"
                                        fragHomeBinding.tvArea.text = "Add address"
                                    }
                                }

                                override fun onError(errorMessage: String?) {
                                    fragHomeBinding.tvAddress.text = "Error retrieving address"
                                    fragHomeBinding.tvArea.text = "Add address"
                                }
                            }
                        )
                    } else {
                        // Use synchronous geocoding for Android versions below 13
                        try {
                            val addresses = Geocoder(requireContext(), Locale.getDefault())
                                .getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1)

                            if (!addresses.isNullOrEmpty()) {
                                val address = addresses[0]
                                val addressText = "${address.getAddressLine(0)}\nLat: ${currentLatLng.latitude}, Lon: ${currentLatLng.longitude}"
                                fragHomeBinding.tvAddress.text = addressText

                                val areaName = address.subLocality ?: address.locality ?: "Area not found"
                                fragHomeBinding.tvArea.text = areaName
                            } else {
                                fragHomeBinding.tvAddress.text = "Address not found"
                                fragHomeBinding.tvArea.text = "Add address"
                            }
                        } catch (e: IOException) {
                            fragHomeBinding.tvAddress.text = "Error retrieving address"
                            fragHomeBinding.tvArea.text = "Add address"
                            e.printStackTrace()
                        }
                    }
                }
            }
        } else {
            fragHomeBinding.tvAddress.text = "Please enable location permission"
            fragHomeBinding.tvArea.text = "Add address"
        }
    }



   /* private fun startTypingEffect(messages: MutableList<String>) {
        if (messages.isEmpty()) return

        var currentIndex = 0
        var index = 0

        val typingRunnable = object : Runnable {
            override fun run() {
                val currentMessage = messages[currentIndex]

                if (index < currentMessage.length) {
                    fragHomeBinding.tvSeachText.hint = "Search for " + currentMessage.substring(0, index + 1)
                    index++
                    handler.postDelayed(this, 100)
                } else {
                    index = 0
                    // Move to the next message
                    currentIndex = (currentIndex + 1) % messages.size
                    handler.postDelayed(this, 1000)
                }
            }
        }
        handler.post(typingRunnable)
    } */

//    private fun startTypingEffectWithCoroutines(messages: List<String>) {
//        if (messages.isEmpty()) return
//
//        // Cancel any existing job to avoid overlapping
//        typingJob?.cancel()
//
//        var currentIndex = 0
//
//        typingJob = CoroutineScope(Dispatchers.Main).launch {
//            while (isActive) { // Keep running until the coroutine is explicitly canceled
//                val currentMessage = messages[currentIndex]
//                for (charIndex in 1..currentMessage.length) {
//                    // Update the hint with the current substring
//                    fragHomeBinding.tvSeachText.hint = "Search for " + currentMessage.substring(0, charIndex)
//
//                    // Wait for a short duration before typing the next character
//                    delay(100L) // Typing speed in milliseconds
//                }
//
//
//                // Wait before showing the next message
//                delay(1500L) // Pause between messages
//
//                // Move to the next message
//                currentIndex = (currentIndex + 1) % messages.size
//            }
//        }
//    }

    fun startSlidingTextAnimation(messages: List<String>, textView: TextView) {
        if (messages.isEmpty() || isAnimationRunning) return // Prevent starting animation if already running

        isAnimationRunning = true // Set to true when the animation starts
        var currentIndex = 0

        typingJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) { // Ensure this runs while the fragment/activity is active
                val currentMessage = messages[currentIndex]

                // Animate the current message going up
                val slideUp = ObjectAnimator.ofFloat(textView, "translationY", 0f, textView.height.toFloat())
                val fadeOut = ObjectAnimator.ofFloat(textView, "alpha", 1f, 0f)

                // Group slide-up and fade-out animations
                AnimatorSet().apply {
                    playTogether(slideUp, fadeOut)
                    duration = 500 // Animation duration
                    interpolator = DecelerateInterpolator()
                    start()
                    awaitEnd() // Wait for this animation to finish
                }

                // Update the message index
                currentIndex = (currentIndex + 1) % messages.size

                // Set the new message and prepare it for sliding in
                textView.text = "Search ''"+messages[currentIndex]+"''"
                textView.translationY = textView.height.toFloat() // Position below the visible area
                textView.alpha = 0f

                // Animate the new message sliding in
                val slideDown = ObjectAnimator.ofFloat(textView, "translationY", textView.height.toFloat(), 0f)
                val fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)

                AnimatorSet().apply {
                    playTogether(slideDown, fadeIn)
                    duration = 500 // Animation duration
                    interpolator = DecelerateInterpolator()
                    start()
                    awaitEnd() // Wait for this animation to finish
                }

                // Pause before the next message
                delay(2000L) // Delay between messages
            }
        }
    }

    // Extension function to wait for an Animator to finish
    suspend fun Animator.awaitEnd() = suspendCancellableCoroutine<Unit> { cont ->
        addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                removeListener(this)
                cont.resume(Unit)
            }

            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        start()
    }


    fun stopSlidingTextAnimation() {
        typingJob?.cancel()
        isAnimationRunning = false// Cancel the coroutine when needed
    }



    private fun startRemovingEffect(currentMessage: String, messages: MutableList<String>) {
        var index = currentMessage.length

        val removingRunnable = object : Runnable {
            override fun run() {
                if (index > 0) {
                    fragHomeBinding.tvSeachText.hint = "Search for " + currentMessage.substring(0, index - 1)
                    index--
                    handler.postDelayed(this, 100)
                } else {
                    // Remove the current message and add it to the end of the list to loop
                    messages.removeAt(0)
                    messages.add(currentMessage)
                   // startTypingEffectWithCoroutines(messages)
                }
            }
        }

        handler.post(removingRunnable)
    }


    private fun showLanguageDropdown() {
        val popupMenu = PopupMenu(requireContext(), fragHomeBinding.ivlanguage)
        popupMenu.menuInflater.inflate(R.menu.language_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_english -> {
                    SharePreference.setStringPref(requireActivity(),
                        SharePreference.SELECTED_LANGUAGE,
                        requireActivity().resources.getString(R.string.language_english)
                    )
                    getCurrentLanguage(requireActivity(), true)
                    true
                }
                R.id.menu_arabic -> {
                    SharePreference.setStringPref(
                        requireActivity(),
                        SharePreference.SELECTED_LANGUAGE,
                        requireActivity().resources.getString(R.string.language_arabic)
                    )
                    getCurrentLanguage(requireActivity(), true)
                    true
                }
                R.id.menu_kannada -> {
                    SharePreference.setStringPref(
                        requireActivity(),
                        SharePreference.SELECTED_LANGUAGE,
                        requireActivity().resources.getString(R.string.language_kannada)
                    )
                    getCurrentLanguage(requireActivity(), true)
                    true
                }
                R.id.menu_hindi -> {
                    SharePreference.setStringPref(
                        requireActivity(),
                        SharePreference.SELECTED_LANGUAGE,
                        requireActivity().resources.getString(R.string.language_hindi)
                    )
                    getCurrentLanguage(requireActivity(), true)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

//    fun setupVideoView(videoView: VideoView, videoUri: Uri, autoStart: Boolean = true) {
//        // Initialize the MediaController
//        val mediaController = MediaController(videoView.context)
//        mediaController.setAnchorView(videoView) // Anchor the media controller to the VideoView
//
//        // Set the media controller for the VideoView
//        videoView.setMediaController(mediaController)
//
//        // Set the video URI
//        videoView.setVideoURI(videoUri)
//
//        // Start the video automatically if specified
//        if (autoStart) {
//            videoView.start()
//        }
//    }

    fun setupVideoView(videoView: VideoView, progressBar: ProgressBar, videoUri: Uri, autoStart: Boolean = true) {
        val mediaController = MediaController(videoView.context)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        videoView.setVideoURI(videoUri)

        videoView.setOnPreparedListener {
            progressBar.visibility = View.GONE
            if (autoStart) {
                videoView.start()
            }
        }

        videoView.setOnInfoListener { _, what, _ ->
            when (what) {
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> progressBar.visibility = View.VISIBLE
                MediaPlayer.MEDIA_INFO_BUFFERING_END -> progressBar.visibility = View.GONE
            }
            false
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNearByServicesClickListener) {
            clickListener = context
        } else {
            throw RuntimeException("$context must implement OnNearByServicesClickListener")
        }
    }



    private fun loadHomeAddVideo(homeAddVideo: ArrayList<HomeAddVideo>) {
        val total_video = homeAddVideo.size

     /*   if (total_video>1) {
            fragHomeBinding.clThoughtful.visibility = View.VISIBLE
        }else{
            fragHomeBinding.clThoughtful.visibility = View.GONE
        }
        val thumbnails = listOf(fragHomeBinding.ivThumbnail1, fragHomeBinding.ivThumbnail2, fragHomeBinding.ivThumbnail3)

        for (i in 0 until minOf(total_video, thumbnails.size)) {
            Glide.with(requireActivity())
                .load(homeAddVideo[i % homeAddVideo.size].thumbnail)
                .placeholder(R.drawable.ic_placeholder_homevideo)
                .into(thumbnails[i])
        }
        setupCardClickListeners(homeAddVideo)  */

        if (homeAddVideo!=null) {
            fragHomeBinding.clThoughtful.visibility = View.VISIBLE
            val adapter = SliderAdapterThoghtfulVedio(homeAddVideo)
            viewpagerThoughtfulVedio?.adapter = adapter

            // Attach TabLayout with ViewPager2
            tabLayoutThoughtfulVedio?.let {
                viewpagerThoughtfulVedio?.let { it1 ->
                    TabLayoutMediator(it, it1) { tab, position ->
                        // Customize tabs if needed
                    }.attach()
                }
            }
        }else{
            fragHomeBinding.clThoughtful.visibility = View.GONE
        }

    }

 /*   fun setupCardClickListeners(homeAddVideo: ArrayList<HomeAddVideo>) {
        // List of clickable views and their corresponding indexes in the homeAddVideo list
        val cards = listOf(fragHomeBinding.vcard1, fragHomeBinding.vcard2, fragHomeBinding.vcard3)

        cards.forEachIndexed { index, card ->
            card.setOnClickListener {
                showPopupFragment(index,homeAddVideo)
            }
        }
    } */

    private fun showPopupFragment(index: Int,homeAddVideo: ArrayList<HomeAddVideo>) {
        // Safeguard against accessing an invalid index
        if (index < homeAddVideo.size) {
            val mediatoshow = homeAddVideo[index].video_url.toString()
            val mediatype = "Video"//homeAddVideo[index].media.toString()

            val popupFragment = CustomPopupFragment.newInstance(mediatype, mediatoshow)
            popupFragment.show(parentFragmentManager, "CustomPopup")
            popupFragment.isCancelable = false
        }
    }

    private fun animateColorChange(view: View, startColor: Int, endColor: Int) {
        val colorAnimator = ValueAnimator.ofArgb(startColor, endColor)
        colorAnimator.addUpdateListener { animator ->
            val animatedColor = animator.animatedValue as Int
            view.setBackgroundColor(animatedColor)
        }
        colorAnimator.duration = 1000 // Animation duration in milliseconds
        colorAnimator.start()
    }

    private fun animateStatusBarColorChange(activity: Activity, startColor: Int, endColor: Int) {
        val colorAnimator = ValueAnimator.ofArgb(startColor, endColor)
        colorAnimator.addUpdateListener { animator ->
            val animatedColor = animator.animatedValue as Int
            activity.window.statusBarColor = animatedColor
        }
        colorAnimator.duration = 1000 // Animation duration in milliseconds
        colorAnimator.start()
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



    private fun startVoiceSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")

        try {
            startActivityForResult(intent, VOICE_SEARCH_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            Common.getToast(requireActivity(),"Voice search not supported on your device")
        }
    }



    private fun checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                AUDIO_PERMISSION_REQUEST_CODE
            )
        }
    }

   /* checkAudioPermission()

    speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireActivity())

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    speechRecognizer.setRecognitionListener(object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Toast.makeText(requireActivity(), "Listening...", Toast.LENGTH_SHORT).show()
        }

        override fun onBeginningOfSpeech() {}

        override fun onRmsChanged(rmsdB: Float) {}

        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            Toast.makeText(requireActivity(), "Processing...", Toast.LENGTH_SHORT).show()
        }

        override fun onError(error: Int) {
            Toast.makeText(requireActivity(), "Error occurred: $error", Toast.LENGTH_SHORT).show()
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val voiceInput = matches?.get(0) ?: "No input detected"
            fragHomeBinding.tvSeachText.text = voiceInput
        }

        override fun onPartialResults(partialResults: Bundle?) {}

        override fun onEvent(eventType: Int, params: Bundle?) {}
    })
     speechRecognizer.startListening(intent)
    */

    private fun handleBanner(
        banner: TopbannerItem?,
        imageView: ImageView,
        cardView: View,
        lineView : View,
        status : Int,
        defaultAction: (() -> Unit)? = null
    ) {
        if (banner != null) {
            // Load image into ImageView
            if (status==0){
                cardView.visibility=View.GONE
                lineView.visibility=View.GONE
            }else {
                cardView.visibility = View.VISIBLE
                lineView.visibility=View.VISIBLE
                Glide.with(requireActivity()).load(banner.imageUrl).placeholder(R.drawable.ic_placeholder).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView)

                // Set onClickListener for the banner
                imageView.setOnClickListener {
                    when {

                        banner.link != null -> {
                            val webpage: Uri = Uri.parse(banner.link)
                            val intent = Intent(Intent.ACTION_VIEW, webpage)
                            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                                startActivity(intent)
                            }
                        }

                        banner.productId != null -> {
                            Log.e("product_id--->", banner.productId.toString())
                            val intent = Intent(requireActivity(), ActProductDetails::class.java)
                            intent.putExtra("product_id", banner.productId.toString())
                            startActivity(intent)
                        }

                        banner.catId != null -> {
                            Log.e("cat_id--->", banner.catId.toString())
                            val intent =
                                Intent(requireActivity(), ActAllSubCategories::class.java).apply {
                                    putExtra("cat_id", banner.catId.toString())
                                    putExtra("categoryName", banner.categoryName)
                                }
                            Common.getToast(requireActivity(), banner.catId.toString())
                            startActivity(intent)
                            requireActivity().overridePendingTransition(
                                R.anim.slide_in,
                                R.anim.no_animation
                            )
                        }
                        else -> defaultAction?.invoke()
                    }
                }
            }
        } else {
            // Hide the card if the banner is null
            cardView.visibility = View.GONE
            lineView.visibility=View.GONE
        }
    }




    fun successDialogBottomSheet() {
        val successDialogBinding = BottomsheetBookInspectionBinding.inflate(layoutInflater)

        val dialog = BottomSheetDialog(requireActivity())
        dialog.setContentView(successDialogBinding.root)
        autoCompleteCategory = successDialogBinding.edtInsservicetype
        categoriesList?.let { setupAutoComplete(it,successDialogBinding) }

        successDialogBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

        successDialogBinding.edtInsdate.setOnClickListener {
            showDatePicker(requireActivity()) { selectedDate ->
                successDialogBinding.edtInsdate.text = selectedDate
                Log.d("DatePicker", "Selected Date: $selectedDate")
            }

        }

        successDialogBinding.edtInsAddress.setOnClickListener {
            val intent = Intent(requireActivity(),ActshowMapPicAddress::class.java)
            addressLauncher.launch(intent)
        }

        inspectionAddress.observe(requireActivity()){data->
            if (data!=""){
                successDialogBinding.edtInsAddress.text = data
            }
        }

        successDialogBinding.edtInstime.setOnClickListener {
            showTimePicker(requireActivity()) { selectedSlot ->
                successDialogBinding.edtInstime.text = selectedSlot  // Display in a TextView
                Log.d("TimePicker", "Selected Slot: $selectedSlot")
            }
        }

        successDialogBinding.btnInsrequest.setOnClickListener {
            callApiScheduleInspection(successDialogBinding,dialog)
        }
        dialog.show()
        dialog.setCancelable(false)

    }


    fun showTimePicker(context: Context, onTimeSlotSelected: (String) -> Unit) {
        val timeSlots = listOf(
            "9 to 11 AM",
            "11 to 1 PM",
            "1 to 3 PM",
            "3 to 5 PM",
            "5 to 7 PM"
        )

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(context, { _, selectedHour, _ ->
            val selectedSlot = when (selectedHour) {
                in 9..10 -> timeSlots[0]  // 9 to 11 AM
                in 11..12 -> timeSlots[1] // 11 to 1 PM
                in 13..14 -> timeSlots[2] // 1 to 3 PM
                in 15..16 -> timeSlots[3] // 3 to 5 PM
                in 17..18 -> timeSlots[4] // 5 to 7 PM
                else -> "Unavailable Slot"
            }
            onTimeSlotSelected(selectedSlot)
        }, hour, minute, false) // false -> 12-hour format, true -> 24-hour format

        timePickerDialog.show()
    }


    private fun callApiScheduleInspection(
        successDialogBinding: BottomsheetBookInspectionBinding,
        dialog: BottomSheetDialog
    ) {
        val hashMap = HashMap<String,String>()
        hashMap["cat_name"] =    successDialogBinding.edtInsservicetype.text.toString().trim()
        hashMap["address"] =    successDialogBinding.edtInsAddress.text.toString().trim()
        hashMap["date"] =     successDialogBinding.edtInsdate.text.toString().trim()
        hashMap["email"] =      successDialogBinding.edtInsemail.text.toString().trim()
        hashMap["fullName"] =      successDialogBinding.edtInsname.text.toString().trim()
        hashMap["latitude"] =     latitude
        hashMap["longitude"] =    longitude
        hashMap["mobile"] =     successDialogBinding.edtInsmobile.text.toString().trim()
        hashMap["time"] =  successDialogBinding.edtInstime.text.toString().trim()

        Common.showLoadingProgress(requireActivity())
        val call = ApiClient.getClient.scheduleInspection(hashMap)
        call.enqueue(object : Callback<InspectionResponse>{
            override fun onResponse(call: Call<InspectionResponse>, response: Response<InspectionResponse>
            ) {
                Common.dismissLoadingProgress()
                if (response.isSuccessful) {
                    val restResponse = response.body()
                    if (restResponse != null) {
                        if (restResponse.status == 1) {
                            Common.showSuccessFullMsg(requireActivity(), restResponse.message)
                            dialog.dismiss()
                        } else {
                            Common.showErrorFullMsg(requireActivity(), restResponse.message)
                        }
                    } else {
                        dismissLoadingProgress()
                        Common.showErrorFullMsg(requireActivity(), response.message())
                    }
                } else {
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

            override fun onFailure(call: Call<InspectionResponse>, t: Throwable) {
                dismissLoadingProgress()
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



    private fun setupAutoComplete(categories: ArrayList<DataItem>, successDialogBinding: BottomsheetBookInspectionBinding) {
        val categoryNames = categories.map { it.categoryName }
        val categoryMap = categories.associateBy({ it.categoryName }, { it.id })

        // Set up the adapter
        val adapter = ArrayAdapter(requireActivity(), R.layout.row_customlist, R.id.text_list_item, categoryNames)
        successDialogBinding.edtInsservicetype.apply {
            threshold = 0
            setAdapter(adapter)

            // Show dropdown on focus or touch
            onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) showDropDown()
            }
            setOnTouchListener { _, _ ->
                showDropDown()
                false
            }

            // Handle item selection
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                // Get the selected category name
                val selectedCategoryName = adapter.getItem(position) ?: return@OnItemClickListener
                // Get the corresponding category ID
                val selectedCategoryId = categoryMap[selectedCategoryName]
                // Close the keyboard and show a toast
           //     Common.closeKeyBoard(requireActivity())
          //      Common.getToast(requireActivity(), "Selected: $selectedCategoryName, ID: $selectedCategoryId")
            }
        }
    }

    fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.DAY_OF_YEAR, 1) // Move to tomorrow

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            // Format date as YYYY-MM-DD
            val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            onDateSelected(formattedDate)
        }, year, month, day)

        // Set the minimum selectable date to tomorrow
        datePickerDialog.datePicker.minDate = calendar.timeInMillis

        datePickerDialog.show()

    }


}





