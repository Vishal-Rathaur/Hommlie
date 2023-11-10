package com.ikksa.user.activity

import android.view.View
import com.ikksa.user.base.BaseActivity
import com.ikksa.user.databinding.ActSplashBinding


class
ActOption : BaseActivity() {

    private lateinit var splashBinding: ActSplashBinding

    override fun setLayout(): View =splashBinding.root

    override fun initView() {
        splashBinding = ActSplashBinding.inflate(layoutInflater)
        splashBinding.btnLogin.setOnClickListener { openActivity(ActLogin::class.java) }
        splashBinding.tvSignUp.setOnClickListener { openActivity(ActSignUp::class.java) }
        splashBinding.btnSkip.setOnClickListener { openActivity(ActMain::class.java) }
    }
}