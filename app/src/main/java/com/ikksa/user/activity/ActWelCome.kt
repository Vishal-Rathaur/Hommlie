package com.ikksa.user.activity

import android.view.View
import com.ikksa.user.base.BaseActivity
import com.ikksa.user.databinding.ActWelComeBinding


class ActWelCome : BaseActivity() {
    private lateinit var actWelComeBinding: ActWelComeBinding
    override fun setLayout(): View =actWelComeBinding.root

    override fun initView() {
        actWelComeBinding= ActWelComeBinding.inflate(layoutInflater)
    }
}