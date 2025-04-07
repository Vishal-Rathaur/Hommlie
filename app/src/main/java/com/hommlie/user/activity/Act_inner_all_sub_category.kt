package com.hommlie.user.activity

import android.net.Uri
import android.view.View
import android.widget.VideoView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hommlie.user.adapter.SubInnerCateAdapter
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActivityActInnerAllSubCategoryBinding
import com.hommlie.user.model.InnerSubcat
import java.util.ArrayList


@Suppress("DEPRECATION")
class Act_inner_all_sub_category : BaseActivity() {

    private lateinit var binding: ActivityActInnerAllSubCategoryBinding


    var videoView : VideoView? =null
    var recyclerView:RecyclerView?=null

    var innerSubcat:InnerSubcat?=null

    override fun setLayout(): View =binding.root

    override fun initView() {
       binding=ActivityActInnerAllSubCategoryBinding.inflate(layoutInflater)

        recyclerView=binding.rvSubcate

        videoView=binding.videoview
        val videoUri = Uri.parse("https://techmonkshub.info/hommly/salon_demo_video.mp4")
        videoView!!.setVideoURI(videoUri)
        videoView!!.setOnPreparedListener { mp ->
            mp.setVolume(0f, 0f)
            videoView!!.start()
        }
        videoView!!.setOnCompletionListener { vp->videoView!!.start() }
        videoView!!.start()

        binding.Subcategoriename.text=intent.getStringExtra("innersubcategoryName")

        if (intent.hasExtra("innersubcategoryData")) {
            val myList = intent.getParcelableArrayExtra("innersubcategoryData")
            val innersubcategotylist = myList?.mapNotNull { it as? InnerSubcat }
            val adapter = innersubcategotylist?.let { SubInnerCateAdapter(it) }
            binding.rvSubcate.layoutManager =
                GridLayoutManager(this@Act_inner_all_sub_category,3)
            binding.rvSubcate.adapter = adapter

        } else {

        }

       binding.ivBack.setOnClickListener { finish() }

    }

}