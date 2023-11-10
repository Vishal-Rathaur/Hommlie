package com.ikksa.user.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.ikksa.user.R
import com.ikksa.user.base.BaseActivity
import com.ikksa.user.databinding.ActTutorialBinding
import com.ikksa.user.utils.SharePreference

class ActTutorial : BaseActivity() {
    private lateinit var tutorialBinding: ActTutorialBinding
    var imagelist: ArrayList<Drawable>? = null


    override fun setLayout(): View =tutorialBinding.root

    override fun initView() {
        tutorialBinding= ActTutorialBinding.inflate(layoutInflater)
        imagelist = ArrayList()
        imagelist!!.add(ResourcesCompat.getDrawable(resources, R.drawable.s1, null)!!)
        imagelist!!.add(ResourcesCompat.getDrawable(resources, R.drawable.s2, null)!!)
        imagelist!!.add(ResourcesCompat.getDrawable(resources, R.drawable.s3, null)!!)
        imagelist!!.add(ResourcesCompat.getDrawable(resources, R.drawable.s4, null)!!)
        tutorialBinding.viewPager.adapter = StartScreenAdapter(this@ActTutorial, imagelist!!)
        tutorialBinding.tabLayout.setupWithViewPager(tutorialBinding.viewPager, true)
        tutorialBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                if (i == imagelist!!.size - 1) {
                    tutorialBinding.tvBtnSkip.text = resources.getString(R.string.start_)
                } else {
                    tutorialBinding.tvBtnSkip.text = resources.getString(R.string.skip)
                }
            }
            override fun onPageScrollStateChanged(i: Int) {}
        })

        tutorialBinding.tvBtnSkip.setOnClickListener {
            SharePreference.setBooleanPref(this@ActTutorial, SharePreference.isTutorial, true)
            openActivity(ActMain::class.java)
            finish()
        }
    }
    class StartScreenAdapter(var mContext: Context, var mImagelist: ArrayList<Drawable>) : PagerAdapter() {

        @SuppressLint("SetTextI18n")

        override fun instantiateItem(collection: ViewGroup, position: Int): Any {
            val inflater = LayoutInflater.from(mContext)
            val layout = inflater.inflate(R.layout.row_tutorial, collection, false) as ViewGroup
            val iv: ImageView = layout.findViewById(R.id.ivScreen)
            iv.setImageDrawable(mImagelist[position])
            collection.addView(layout)
            return layout
        }

        override fun destroyItem(
            collection: ViewGroup,
            position: Int,
            view: Any
        ) {
            collection.removeView(view as View)
        }

        override fun getCount(): Int {
            return mImagelist.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }
    }

}