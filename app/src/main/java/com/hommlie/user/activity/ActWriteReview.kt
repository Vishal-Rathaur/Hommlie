package com.hommlie.user.activity

import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActWriteReviewBinding
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ActWriteReview : BaseActivity() {

    private lateinit var writeReviewBinding: ActWriteReviewBinding
    var ratting = ""
    override fun setLayout(): View = writeReviewBinding.root

    override fun initView() {
        writeReviewBinding = ActWriteReviewBinding.inflate(layoutInflater)

        writeReviewBinding.ivBack.setOnClickListener { finish() }
        writeReviewBinding.tvproductname.text = intent.getStringExtra("proName")!!
        Glide.with(this@ActWriteReview)
            .load(intent.getStringExtra("proImage"))
            .into(writeReviewBinding.ivproduct)
        writeReviewBinding.btnsubmit.setOnClickListener {
            callApiAddRattingAndReview()
        }

        writeReviewBinding.libraryNormalRatingbar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            ratingBar.rating = rating
            ratting = rating.toString()
        }


    }

    private fun callApiAddRattingAndReview() {
        if (writeReviewBinding.edtreview.text.toString() == "") {
            Common.showErrorFullMsg(
                this@ActWriteReview,
                resources.getString(R.string.please_write_comment)
            )
        } else {
            Common.showLoadingProgress(this@ActWriteReview)
            val hasmap = HashMap<String, String>()
            hasmap["user_id"] =
                SharePreference.getStringPref(this@ActWriteReview, SharePreference.userId)!!
            hasmap["ratting"] = ratting
            hasmap["comment"] = writeReviewBinding.edtreview.text.toString()
            hasmap["order_id"] = intent.getStringExtra("order_id")!!
            hasmap["product_id"] = intent.getStringExtra("proID")!!
             Log.d("aaaaa", hasmap.toString())
            val call = ApiClient.getClient.addRatting(hasmap,SharePreference.getStringPref(this@ActWriteReview,SharePreference.token).toString())
            call.enqueue(object : Callback<SingleResponse> {
                override fun onResponse(
                    call: Call<SingleResponse>,
                    response: Response<SingleResponse>
                ) {
                    if (response.code() == 200) {
                        Common.dismissLoadingProgress()
                        Common.isAddOrUpdated = true
                        if (response.body()?.status == 1) {
                            setResult(RESULT_OK)
                            finish()
                        } else {
                            Common.showErrorFullMsg(
                                this@ActWriteReview,
                                response.body()?.message.toString()
                            )
                        }
                    } else {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActWriteReview,
                            resources.getString(R.string.error_msg)
                        )
                    }
                }
                override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                        this@ActWriteReview,
                        resources.getString(R.string.error_msg)
                    )
                }
            })
        }
    }
}