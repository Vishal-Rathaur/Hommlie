package com.hommlie.user.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.PictureInPictureParams
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.material.tabs.TabLayout
import com.hommlie.user.R
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActMainBinding
import com.hommlie.user.fragment.*
import com.hommlie.user.model.CleaningServicesItem
import com.hommlie.user.model.FeaturedProductsItem
import com.hommlie.user.model.HotProductsItem
import com.hommlie.user.model.NewProductsItem
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.OnNearByServicesClickListener
import com.hommlie.user.utils.SharePreference
import com.hommlie.user.utils.SharePreference.Companion.getBooleanPref
import java.util.*
import android.content.res.Configuration
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class ActMain : AppCompatActivity(), OnNearByServicesClickListener {

    private var lastBackPressedTime: Long = 0
    private val doubleBackToExitInterval: Long = 2000

    private var activeFragment: Fragment? = null

    private lateinit var llMenu: LinearLayout
    private lateinit var tabLayout: TabLayout



    private lateinit var mainBinding: ActMainBinding
    //override fun setLayout(): View = mainBinding.root
    private var temp = 1

//    override fun initView() {
//        mainBinding = ActMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
       // setContentView(R.layout.act_main)

      //  WindowCompat.setDecorFitsSystemWindows(window, true)



        llMenu = mainBinding.llManu
        tabLayout = mainBinding.tabLayout

       // enterPictureInPictureMode()

        if (savedInstanceState == null) {
            val initialFragment = HomeFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.FramFragment, initialFragment, "OrderFragment")
                .commit()
            activeFragment = initialFragment
        } else {
            // Restore the active fragment
            val activeTag = savedInstanceState.getString("activeFragmentTag")
            activeFragment = supportFragmentManager.findFragmentByTag(activeTag)
        }

       // cardItemCount.value=0
        cardItemCount.observe(this, androidx.lifecycle.Observer { count ->
            mainBinding.tvCount.text = count.toString()
        })

        tabLayout.addTab(tabLayout.newTab().setText("  Home   "))
        tabLayout.addTab(tabLayout.newTab().setText("Favorites"))
        tabLayout.addTab(tabLayout.newTab().setText("  Cart   "))
        tabLayout.addTab(tabLayout.newTab().setText(" Orders  "))
       // tabLayout.addTab(tabLayout.newTab().setText("Profile"))

        if (intent.getStringExtra("pos")!= null) {
            val pos = intent.getStringExtra("pos").toString()
            setFragment(pos.toInt())
        } else {
            setFragment(1)
        }

        tabLayout.isEnabled = false
        tabLayout.isClickable = false
        for (i in 0 until tabLayout.tabCount) {
            val tab = (tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            tab.isEnabled = false
            tab.isClickable = false
        }

//        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab?) {
////                when (tab?.position) {
//                    tab?.select()
////                    0 -> setFragment(1)
////                    1 -> setFragment(2)
////                    2 -> setFragment(3)
////                    3 -> setFragment(4)
////                    4 -> setFragment(5)
////                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                // Optional: Handle unselected tab state
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//                // Optional: Handle reselected tab state
//            }
//        })




    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.menu_home -> {
                if (temp != 1) {
                    setFragment(1)
                   // temp = 1
                }
            }
            R.id.menu_fav -> {
                if (getBooleanPref(this, SharePreference.isLogin)) {
                    if (temp != 2) {
                        setFragment(2)
                        temp = 2
                      //  Common.openActivity(this@ActMain, ActShop::class.java)
                    }
                } else {
                    Common.openActivity(this@ActMain,ActSignUp::class.java)
                    finish()
                    finishAffinity()
                }
            }
            R.id.menu_cart -> {
                if (getBooleanPref(this, SharePreference.isLogin)) {
                    if (temp != 3) {
                        setFragment(3)
                      //  temp = 3
                    }
                } else {
                    Common.openActivity(this@ActMain,ActSignUp::class.java)
                    finish()
                    finishAffinity()
                }

            }
            R.id.menu_doce -> {
                if (getBooleanPref(this@ActMain, SharePreference.isLogin)) {
                    if (temp != 4) {
                        setFragment(4)
                      //  temp = 4
                    }
                } else {
                    Common.openActivity(this@ActMain,ActSignUp::class.java)
                    finish()
                    finishAffinity()
                }
            }

            R.id.menu_profile -> {
                if (getBooleanPref(this@ActMain, SharePreference.isLogin)) {
                    if (temp != 5) {
                        // setFragment(5)
                        //temp = 5
                        Common.openActivity(this@ActMain, ActNotification::class.java)
                    }
                }else {
                    Common.openActivity(this@ActMain,ActSignUp::class.java)
                    finish()
                    finishAffinity()
                }
            }
        }
    }

    @SuppressLint("NewApi")
    fun setFragment(pos: Int) {
        temp =pos
        mainBinding.ivHome.imageTintList =
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.medium_gray, null))
        mainBinding.ivFav.imageTintList =
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.medium_gray, null))
        mainBinding.ivCart.imageTintList =
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.medium_gray, null))
        mainBinding.ivDoce.imageTintList =
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.medium_gray, null))
        mainBinding.ivProfile.imageTintList =
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.medium_gray, null))
        when (pos) {
            1 -> {
                mainBinding.ivHome.imageTintList =
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorPrimaryDark,
                            null
                        )
                    )
                tabLayout.getTabAt(0)?.select()
                homeActivate()
                addFragment(HomeFragment())
            }
            2 -> {
                tabLayout.getTabAt(3)?.select()
                mainBinding.ivFav.imageTintList =
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorPrimaryDark,
                            null
                        )
                    )
               wishlistActivate()
                addFragment(FavoriteFragment())
            }
            3 -> {
                tabLayout.getTabAt(1)!!.select()
                mainBinding.ivCart.imageTintList =
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorPrimaryDark,
                            null
                        )
                    )
                cartActivate()
                addFragment(MyCartFragment())
            }
            4 -> {
                tabLayout.getTabAt(2)!!.select()
                mainBinding.ivDoce.imageTintList =
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorPrimaryDark,
                            null
                        )
                    )
                orderActivate()
                addFragment(OrderHistoryFragment())
            }
            5 -> {
                mainBinding.ivProfile.imageTintList =
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorPrimaryDark,
                            null
                        )
                    )
                profileActivate()
                replaceFragment(MyAccountFragment(),"MyAccountFragment")
            }
        }
    }

   fun homeActivate(){
        mainBinding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        mainBinding.tvWishlist.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        mainBinding.tvCart.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        mainBinding.tvOrder.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        mainBinding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
    }

    fun wishlistActivate(){
        mainBinding.tvWishlist.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        mainBinding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        mainBinding.tvCart.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        mainBinding.tvOrder.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        mainBinding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
    }

    fun cartActivate(){
        mainBinding.tvCart.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        mainBinding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        mainBinding.tvWishlist.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        mainBinding.tvOrder.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        mainBinding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
    }

    fun orderActivate(){
        mainBinding.tvOrder.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        mainBinding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        mainBinding.tvWishlist.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        mainBinding.tvCart.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
        mainBinding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.medium_gray))
    }

    fun profileActivate(){
        mainBinding.tvProfile.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        mainBinding.tvHome.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvWishlist.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvCart.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvOrder.setTextColor(Color.parseColor("#000000"))
    }



    @SuppressLint("WrongConstant")
    fun addFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.FramFragment, fragment)
            commit()
        }



    // Updated function to replace/switch fragments without refreshing
    fun replaceFragment(fragment: Fragment, tag: String) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        // Hide the currently active fragment
        activeFragment?.let {
            transaction.hide(it)
        }

        // Check if the fragment already exists
        val existingFragment = fragmentManager.findFragmentByTag(tag)
        if (existingFragment != null) {
            // Show the existing fragment
            transaction.show(existingFragment)
            activeFragment = existingFragment
        } else {
            // Add the new fragment
            transaction.add(R.id.FramFragment, fragment, tag)
            activeFragment = fragment
        }

        // Add the transaction to the back stack
        transaction.addToBackStack(tag)
        transaction.commit()
    }




    override fun onBackPressed() {
        if (temp != 1) {
            temp = 1
            setFragment(1)
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressedTime < doubleBackToExitInterval) {
                // User pressed back twice within the interval, show exit dialog
                mExitDialog()
            } else {
                // Reset the back press time and show a toast (optional)
                lastBackPressedTime = currentTime
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActMain, false)
    }

    private fun mExitDialog() {
        var dialog: Dialog? = null
        try {
            dialog?.dismiss()
            dialog = Dialog(this@ActMain, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(this@ActMain)
            val mView = mInflater.inflate(R.layout.dlg_confomation, null, false)
            val tvYes: TextView = mView.findViewById(R.id.tvYes)
            val tvNo: TextView = mView.findViewById(R.id.tvNo)
            val finalDialog: Dialog = dialog
            tvYes.setOnClickListener {
                finalDialog.dismiss()
                ActivityCompat.finishAfterTransition(this@ActMain)
                ActivityCompat.finishAffinity(this@ActMain);
                finish()
            }
            tvNo.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun activateCart() {
        handleactivateCart()
    }

    override fun activateOrder() {
        handleactivateOrder()
    }

    override fun activateShop() {
        if (temp != 2) {
            setFragment(2)
        }
    }

    private fun handleactivateCart() {
        if (temp != 3) {
            setFragment(3)
        }
    }

    private fun handleactivateOrder(){
        if (temp != 4) {
            setFragment(4)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        activeFragment?.let {
            outState.putString("activeFragmentTag", it.tag)
        }
    }

    companion object{
        var isDataLoaded=false
        var cleaningProductList: ArrayList<CleaningServicesItem>?=null
        var pestProductList: ArrayList<CleaningServicesItem>?=null
        var safetynettingProductList:ArrayList<CleaningServicesItem>?=null
        var mosquitomessProductList:ArrayList<CleaningServicesItem>?=null
        var mostbookedProductList:ArrayList<HotProductsItem>?=null
        var cardItemCount = MutableLiveData<Int>()
    }

    fun hideMenu() {
        if (llMenu.visibility == View.VISIBLE) {
            llMenu.animate()
                .translationY(llMenu.height.toFloat())
                .setDuration(300)
                .withEndAction { llMenu.visibility = View.GONE }
                .start()
        }
    }

    fun showMenu() {
        if (llMenu.visibility == View.GONE) {
            llMenu.visibility = View.VISIBLE
            llMenu.animate()
                .translationY(0f)
                .setDuration(300)
                .start()
        }
    }



}

