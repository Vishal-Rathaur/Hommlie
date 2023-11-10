package com.ikksa.user.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.ikksa.user.R
import com.ikksa.user.base.BaseActivity
import com.ikksa.user.databinding.ActMainBinding
import com.ikksa.user.fragment.*
import com.ikksa.user.utils.Common
import com.ikksa.user.utils.SharePreference
import com.ikksa.user.utils.SharePreference.Companion.getBooleanPref
import java.util.*


class ActMain : BaseActivity() {

    private lateinit var mainBinding: ActMainBinding
    override fun setLayout(): View = mainBinding.root
    private var temp = 1

    override fun initView() {
        mainBinding = ActMainBinding.inflate(layoutInflater)
        temp = if (intent.getStringExtra("pos") != null) {
            setFragment(intent.getStringExtra("pos")!!.toInt())
            intent.getStringExtra("pos")!!.toInt()
        } else {
            setFragment(1)
            1
        }

    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.menu_home -> {
                if (temp != 1) {
                    setFragment(1)
                    temp = 1
                }
            }
            R.id.menu_fav -> {
                if (getBooleanPref(this, SharePreference.isLogin)) {
                    if (temp != 2) {
                        setFragment(2)
                        temp = 2
                    }
                } else {
                    openActivity(ActLogin::class.java)
                    finish()
                    finishAffinity()
                }
            }
            R.id.menu_cart -> {
                if (getBooleanPref(this, SharePreference.isLogin)) {
                    if (temp != 3) {
                        setFragment(3)
                        temp = 3
                    }
                } else {
                    openActivity(ActLogin::class.java)
                    finish()
                    finishAffinity()
                }

            }
            R.id.menu_doce -> {
                if (getBooleanPref(this@ActMain, SharePreference.isLogin)) {
                    if (temp != 4) {
                        setFragment(4)
                        temp = 4
                    }
                } else {
                    openActivity(ActLogin::class.java)
                    finish()
                    finishAffinity()
                }
            }

            R.id.menu_profile -> {
                if (temp != 5) {
                    setFragment(5)
                    temp = 5
                }
            }
        }
    }

    @SuppressLint("NewApi")
    fun setFragment(pos: Int) {
        mainBinding.ivHome.imageTintList =
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray, null))
        mainBinding.ivFav.imageTintList =
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray, null))
        mainBinding.ivCart.imageTintList =
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray, null))
        mainBinding.ivDoce.imageTintList =
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray, null))
        mainBinding.ivProfile.imageTintList =
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.gray, null))
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
                homeActivate()
                replaceFragment(HomeFragment())
            }
            2 -> {
                mainBinding.ivFav.imageTintList =
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorPrimaryDark,
                            null
                        )
                    )
               wishlistActivate()
                replaceFragment(FavoriteFragment())
            }
            3 -> {
                mainBinding.ivCart.imageTintList =
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorPrimaryDark,
                            null
                        )
                    )
                cartActivate()
                replaceFragment(MyCartFragment())
            }
            4 -> {
                mainBinding.ivDoce.imageTintList =
                    ColorStateList.valueOf(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.colorPrimaryDark,
                            null
                        )
                    )
                orderActivate()
                replaceFragment(OrderHistoryFragment())
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
                replaceFragment(MyAccountFragment())
            }
        }
    }

   fun homeActivate(){
        mainBinding.tvHome.setTextColor(Color.parseColor("#FF7C7C"))
        mainBinding.tvWishlist.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvCart.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvOrder.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvProfile.setTextColor(Color.parseColor("#000000"))
    }

    fun wishlistActivate(){
        mainBinding.tvWishlist.setTextColor(Color.parseColor("#FF7C7C"))
        mainBinding.tvHome.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvCart.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvOrder.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvProfile.setTextColor(Color.parseColor("#000000"))
    }

    fun cartActivate(){
        mainBinding.tvCart.setTextColor(Color.parseColor("#FF7C7C"))
        mainBinding.tvHome.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvWishlist.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvCart.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvOrder.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvProfile.setTextColor(Color.parseColor("#000000"))
    }

    fun orderActivate(){
        mainBinding.tvOrder.setTextColor(Color.parseColor("#FF7C7C"))
        mainBinding.tvHome.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvWishlist.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvCart.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvProfile.setTextColor(Color.parseColor("#000000"))
    }

    fun profileActivate(){
        mainBinding.tvProfile.setTextColor(Color.parseColor("#FF7C7C"))
        mainBinding.tvHome.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvWishlist.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvCart.setTextColor(Color.parseColor("#000000"))
        mainBinding.tvOrder.setTextColor(Color.parseColor("#000000"))
    }



    @SuppressLint("WrongConstant")
    fun replaceFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.FramFragment, fragment)
            commit()
        }

    override fun onBackPressed() {
        if (temp != 1) {
            temp = 1
            setFragment(1)
        } else {
            mExitDialog()
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
}