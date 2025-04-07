package com.hommlie.user.activity

import android.view.View
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActSplashBinding


class
ActOption : BaseActivity() {

    private lateinit var splashBinding: ActSplashBinding

    override fun setLayout(): View =splashBinding.root

    override fun initView() {
        splashBinding = ActSplashBinding.inflate(layoutInflater)
        splashBinding.btnLogin.setOnClickListener { openActivity(ActSignUp::class.java) }
        splashBinding.tvSignUp.setOnClickListener { openActivity(ActSignUp::class.java) }
        splashBinding.btnSkip.setOnClickListener { openActivity(ActMain::class.java) }
    }
}