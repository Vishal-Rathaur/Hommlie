package com.hommlie.user.activity

import android.content.Intent
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.hommlie.user.R
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActPaymentSuccessFullBinding

class ActPaymentSuccessFull : BaseActivity() {
    private lateinit var actPaymentSuccessFullBinding: ActPaymentSuccessFullBinding

    private lateinit var lottieAnimationView: LottieAnimationView
    private var mediaPlayer: MediaPlayer? = null

    override fun setLayout(): View =actPaymentSuccessFullBinding.root

    override fun initView() {
        actPaymentSuccessFullBinding= ActPaymentSuccessFullBinding.inflate(layoutInflater)
        actPaymentSuccessFullBinding.btncontinueshopping.setOnClickListener { openActivity(ActMain::class.java
        ) }

        lottieAnimationView = actPaymentSuccessFullBinding.lottieAnimationView

        mediaPlayer = MediaPlayer.create(this, R.raw.booking_succesfull)

        lottieAnimationView.visibility=View.VISIBLE
        lottieAnimationView.playAnimation()

        actPaymentSuccessFullBinding.tvAccount.text="Order ID : "+intent.getStringExtra("order_number").toString()
        val grandTotal = intent.getStringExtra("grand_total")?.toDoubleOrNull() ?: 0.0
        actPaymentSuccessFullBinding.tvTotalValue.text = "\u20b9%.2f".format(grandTotal)

    //    actPaymentSuccessFullBinding.tvTotalValue.text = "\u20b9" + String.format("%.2f", intent.getStringExtra("grand_total").toString())
        actPaymentSuccessFullBinding.tvDate.text=intent.getStringExtra("desired_date").toString()
        actPaymentSuccessFullBinding.tvTime.text=intent.getStringExtra("desired_time").toString()

//        Handler(Looper.getMainLooper()).postDelayed({
//            finish()
//        }, 3000)

        vibratePhone()
        if (mediaPlayer != null) {
            mediaPlayer?.start()
        } else {
            Log.e("MediaPlayer", "MediaPlayer is null. Cannot start playback.")
        }

        actPaymentSuccessFullBinding.clRefer.setOnClickListener {
            val intent  = Intent(this@ActPaymentSuccessFull,ActReferEarn::class.java)
            startActivity(intent)
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



    override fun onBackPressed() {

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
