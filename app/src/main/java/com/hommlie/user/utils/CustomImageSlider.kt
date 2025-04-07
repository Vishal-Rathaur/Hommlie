package com.hommlie.user.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.hommlie.user.R
import com.hommlie.user.model.MediaType
import com.hommlie.user.model.MySlideModel

class CustomImageSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.custom_image_slider, this)
    }

    fun setSlideList(slideList: List<MySlideModel>) {
        // Logic to display images and videos
        for (slide in slideList) {
            when (slide.type) {
                MediaType.IMAGE -> {
                    val imageView = ImageView(context)
                    addView(imageView)
                    displayImage(imageView, slide.url)
                }
                MediaType.VIDEO -> {
                    val videoView = VideoView(context)
                    addView(videoView)
                    displayVideo(videoView, slide.url)
                }
            }
        }
    }

    private fun displayImage(imageView: ImageView, url: String) {
        Glide.with(context)
            .load(url)
            .into(imageView)
    }

    private fun displayVideo(videoView: VideoView, url: String) {
        videoView.setVideoPath(url)
        videoView.start()
    }
}