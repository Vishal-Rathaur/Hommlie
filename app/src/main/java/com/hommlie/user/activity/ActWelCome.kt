package com.hommlie.user.activity

import android.view.View
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActWelComeBinding


class ActWelCome : BaseActivity() {
    private lateinit var actWelComeBinding: ActWelComeBinding
    override fun setLayout(): View =actWelComeBinding.root

    override fun initView() {
        actWelComeBinding= ActWelComeBinding.inflate(layoutInflater)
    }
}