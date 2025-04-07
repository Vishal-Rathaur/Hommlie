package com.hommlie.user.activity


import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.hommlie.user.R
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActivityActBookingSuccessBinding
import okhttp3.internal.http2.Http2Reader


class ActBookingSuccess : BaseActivity() {

    private lateinit var bookingSuccessBinding: ActivityActBookingSuccessBinding

    private lateinit var lottieAnimationView: LottieAnimationView

    override fun setLayout(): View {
        bookingSuccessBinding = ActivityActBookingSuccessBinding.inflate(layoutInflater)
        return bookingSuccessBinding.root
    }

    override fun initView() {
        // Ensure that the binding is properly initialized before using it
        bookingSuccessBinding = ActivityActBookingSuccessBinding.inflate(layoutInflater)

        lottieAnimationView = bookingSuccessBinding.lottieAnimationView

        Handler(Looper.getMainLooper()).postDelayed({
            lottieAnimationView.visibility=View.VISIBLE
            lottieAnimationView.playAnimation()
        },2000)
    }
}
