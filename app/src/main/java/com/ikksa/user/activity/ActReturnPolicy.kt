package com.ikksa.user.activity

import android.view.View
import com.ikksa.user.base.BaseActivity
import com.ikksa.user.databinding.ActReturnPolicyBinding

class ActReturnPolicy : BaseActivity() {
    private lateinit var actReturnPolicyBinding: ActReturnPolicyBinding

    override fun setLayout(): View =actReturnPolicyBinding.root

    override fun initView() {
        actReturnPolicyBinding= ActReturnPolicyBinding.inflate(layoutInflater)
        //TODO PRODUCT RETURN POLICIES TEXT SET
        val extras = intent.extras
        val description = extras!!.getString("return_policies")
        actReturnPolicyBinding.tvreturnpoliciesDescription.text=description
        actReturnPolicyBinding.ivBack.setOnClickListener { finish() }
    }
}