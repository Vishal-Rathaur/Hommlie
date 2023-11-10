package com.ikksa.user.activity

import android.view.View
import com.bumptech.glide.Glide
import com.ikksa.user.base.BaseActivity
import com.ikksa.user.databinding.ActStoreInfoBinding

class ActStoreInfo : BaseActivity() {

    private lateinit var actStoreInfoBinding: ActStoreInfoBinding
    override fun setLayout(): View = actStoreInfoBinding.root

    override fun initView() {
        actStoreInfoBinding = ActStoreInfoBinding.inflate(layoutInflater)
        actStoreInfoBinding.ivClose.setOnClickListener {
            finish()
            setResult(RESULT_OK)}
        //TODO SET VENDORS STORE INFO
        actStoreInfoBinding.tvstorephone.text=intent.getStringExtra("mobile")?:""
        actStoreInfoBinding.tvstoremail.text=intent.getStringExtra("email")?:""
        actStoreInfoBinding.tvstoreaddress.text=intent.getStringExtra("storeaddress")?:"Address"
        actStoreInfoBinding.tvRatePro.text=intent.getStringExtra("rate")?:"0.0"
        actStoreInfoBinding.tvstorename.text=intent.getStringExtra("vendorsName")?:""
        Glide.with(this@ActStoreInfo)
            .load(intent.getStringExtra("image"))
            .into(actStoreInfoBinding.ivvendors)
    }
}