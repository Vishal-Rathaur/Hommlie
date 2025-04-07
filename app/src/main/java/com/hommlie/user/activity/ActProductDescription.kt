package com.hommlie.user.activity

import android.graphics.text.LineBreaker
import android.os.Build
import android.text.Html
import android.text.Layout
import android.view.View
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActProductDescriptionBinding

class ActProductDescription : BaseActivity() {

    private lateinit var productDescriptionBinding: ActProductDescriptionBinding

    var title = ""
    var data  = ""

    override fun setLayout(): View =productDescriptionBinding.root

    override fun initView() {
        productDescriptionBinding= ActProductDescriptionBinding.inflate(layoutInflater)
        //TODO PRODUCT DESCRIPTION TEXT SET
        val extras = intent.extras
        title = extras!!.getString("title").toString()
        data = extras!!.getString("description").toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            productDescriptionBinding.tvProductDescription.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            productDescriptionBinding.tvProductDescription.breakStrategy = LineBreaker.BREAK_STRATEGY_SIMPLE
        }


        //  if (title=="FAQs"){
        if (!title.isNullOrEmpty()) {
            productDescriptionBinding.tvviewall.text = title
        } else {
            productDescriptionBinding.tvviewall.text = "Default Text"
        }

//        }else{
//
//        }

        if (!data.isNullOrEmpty()) {
        //    productDescriptionBinding.tvProductDescription.text = data
            productDescriptionBinding.tvProductDescription.text= Html.fromHtml(data)
        } else {
            productDescriptionBinding.tvProductDescription.text = "Default Text"
        }


        productDescriptionBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }



    }
}