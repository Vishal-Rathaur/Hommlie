package com.hommlie.user.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.ViewPager
import com.hommlie.user.R
import com.hommlie.user.adapter.IntroAdapter
import com.hommlie.user.utils.SharePreference

class Act_IntroScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_act_intro_screen)

        val btn_getstart:CardView=findViewById(R.id.btn_getStart)
        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val introAdapter = IntroAdapter(supportFragmentManager)
        viewPager.adapter = introAdapter

        btn_getstart.setOnClickListener {
            val currentItem = viewPager.currentItem

            if (currentItem < introAdapter.count - 1) {
                // If not on the last fragment, go to the next one
                viewPager.setCurrentItem(currentItem + 1, true)
            } else {
                // If on the last fragment, navigate to the main activity
                SharePreference.setBooleanPref(this@Act_IntroScreen,SharePreference.isIntroseen,true)
                navigateToMainActivity()
            }
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {}

        })


    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, ActMain::class.java)
        startActivity(intent)
        finish()
    }
}