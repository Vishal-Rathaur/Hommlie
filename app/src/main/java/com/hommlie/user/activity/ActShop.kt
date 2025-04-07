package com.hommlie.user.activity

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.color.MaterialColors.isColorLight
import com.hommlie.user.R
import com.hommlie.user.databinding.ActivityActShopBinding

class ActShop : AppCompatActivity() {

    private lateinit var fragHomeBinding:ActivityActShopBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        fragHomeBinding = ActivityActShopBinding.inflate(layoutInflater)
        setContentView(fragHomeBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }

      //  fragHomeBinding.scroll.viewTreeObserver.addOnScrollChangedListener(scrollListener)
    }


    private val scrollListener = ViewTreeObserver.OnScrollChangedListener {
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
            val endColor = ContextCompat.getColor(this@ActShop, R.color.white)   // Ending color
            val startColor = ContextCompat.getColor(this@ActShop, R.color.status_color)
            val interpolatedColor = blendColors(startColor!!, endColor, fraction)

            // Apply the interpolated color
            fragHomeBinding.appBar.setBackgroundColor(interpolatedColor)
            window.statusBarColor = interpolatedColor

//                // Fade the header image color
//                fragHomeBinding.headerImage.setColorFilter(interpolatedColor, android.graphics.PorterDuff.Mode.SRC_ATOP)
//
//                // Adjust header image alpha for fading effect
//                fragHomeBinding.headerImage.alpha = 1f - fraction

            // Adjust system UI visibility dynamically
            if (isColorLight(interpolatedColor)) {
               window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
               window.decorView.systemUiVisibility = 0
            }

            Log.d("ScrollFade", "Scroll fraction: $fraction, Interpolated color: $interpolatedColor")
        }else{
            fragHomeBinding.appBar.setBackgroundColor(ContextCompat.getColor(this@ActShop, R.color.white))
            window.statusBarColor = ContextCompat.getColor(this@ActShop, R.color.white)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

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
}