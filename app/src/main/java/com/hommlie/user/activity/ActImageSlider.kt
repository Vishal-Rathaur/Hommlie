package com.hommlie.user.activity

import android.view.View
import com.denzcoskun.imageslider.models.SlideModel
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActImageSliderBinding
import com.hommlie.user.model.ProductimagesItem
import com.hommlie.user.utils.Common.filterHttps

class ActImageSlider : BaseActivity() {
    private lateinit var imageSliderBinding: ActImageSliderBinding
    var imgList: ArrayList<ProductimagesItem>? = null

    override fun setLayout(): View = imageSliderBinding.root

    override fun initView() {
        imageSliderBinding = ActImageSliderBinding.inflate(layoutInflater)
        imgList = intent.getStringArrayListExtra("imageList") as ArrayList<ProductimagesItem>?
        val imageList = ArrayList<SlideModel>()
        for (i in 0 until imgList?.size!!) {
            val slideModel = SlideModel(filterHttps(imgList!![i].imageUrl))
            imageList.add(slideModel)
        }

        imageSliderBinding.imageSlider.setImageList(imageList)
        imageSliderBinding.ivCancle.setOnClickListener {
            finish()
        }
    }

}