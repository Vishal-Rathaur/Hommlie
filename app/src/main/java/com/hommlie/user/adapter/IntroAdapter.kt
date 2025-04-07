package com.hommlie.user.adapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hommlie.user.fragment.IntroFragment1
import com.hommlie.user.fragment.IntroFragment2
import com.hommlie.user.fragment.IntroFragment3

class IntroAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)  {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> IntroFragment1()
            1 -> IntroFragment2()
            2 -> IntroFragment3()
            else -> throw IllegalArgumentException("Invalid position")
        }
    }

    override fun getCount(): Int {
        return 3 // Number of intro pages
    }
}