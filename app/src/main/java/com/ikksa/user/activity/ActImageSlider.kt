package com.ikksa.user.activity

import android.view.View
import com.denzcoskun.imageslider.models.SlideModel
import com.ikksa.user.base.BaseActivity
import com.ikksa.user.databinding.ActImageSliderBinding
import com.ikksa.user.model.ProductimagesItem
import com.ikksa.user.utils.Common.filterHttps

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