package com.ikksa.user.activity

import android.view.View
import com.ikksa.user.base.BaseActivity
import com.ikksa.user.databinding.ActPaymentSuccessFullBinding

class ActPaymentSuccessFull : BaseActivity() {
    private lateinit var actPaymentSuccessFullBinding: ActPaymentSuccessFullBinding

    override fun setLayout(): View =actPaymentSuccessFullBinding.root

    override fun initView() {
        actPaymentSuccessFullBinding= ActPaymentSuccessFullBinding.inflate(layoutInflater)
        actPaymentSuccessFullBinding.btncontinueshopping.setOnClickListener { openActivity(ActMain::class.java
        ) }
    }

}