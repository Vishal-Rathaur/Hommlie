package com.hommlie.user.activity

import android.os.Build
import android.text.Html
import android.view.View
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActProvacyPolicyBinding
import com.hommlie.user.model.CmsPageResponse
import com.hommlie.user.utils.Common
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActProvacyPolicy : BaseActivity() {
    private lateinit var actProvacyPolicyBinding: ActProvacyPolicyBinding


    override fun setLayout(): View = actProvacyPolicyBinding.root

    override fun initView() {
        actProvacyPolicyBinding = ActProvacyPolicyBinding.inflate(layoutInflater)
        actProvacyPolicyBinding.ivBack.setOnClickListener {
            finish()
        }
        when {
            intent.getStringExtra("Type") == "Privacy Policy" -> {
                actProvacyPolicyBinding.tvTitle.text = intent.getStringExtra("Type")
            }
            intent.getStringExtra("Type") == "About Hommlie" -> {
                actProvacyPolicyBinding.tvTitle.text = intent.getStringExtra("Type")
            }
            intent.getStringExtra("Type") == "Terms & Condition" -> {
                actProvacyPolicyBinding.tvTitle.text = intent.getStringExtra("Type")
            }
            intent.getStringExtra("Type") == "Refund Policy" -> {
                actProvacyPolicyBinding.tvTitle.text = intent.getStringExtra("Type")
            }
        }
        callCmsDataApi()
    }

    private fun callCmsDataApi() {
        Common.showLoadingProgress(this@ActProvacyPolicy)
        val call = ApiClient.getClient.getCmsData()
        call.enqueue(object : Callback<CmsPageResponse> {
            override fun onResponse(
                call: Call<CmsPageResponse>,
                response: Response<CmsPageResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce = response.body()!!
                    Common.dismissLoadingProgress()
                    if (restResponce.status == 1) {
                        when {
                            intent.getStringExtra("Type") == "Privacy Policy" -> {
                                if (restResponce.privacypolicy.isNullOrEmpty()){
                                    actProvacyPolicyBinding.tvCmsData.text=""
                                }else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        actProvacyPolicyBinding.tvCmsData.text = Html.fromHtml(
                                            restResponce.privacypolicy,
                                            Html.FROM_HTML_MODE_COMPACT
                                        )
                                    } else {
                                        actProvacyPolicyBinding.tvCmsData.text =
                                            Html.fromHtml(restResponce.privacypolicy)
                                    }
                                }
                            }
                            intent.getStringExtra("Type") == "About Hommlie" -> {
                                if (restResponce.about.isNullOrEmpty()){
                                    actProvacyPolicyBinding.tvCmsData.text=""
                                }else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        actProvacyPolicyBinding.tvCmsData.text = Html.fromHtml(
                                            restResponce.about,
                                            Html.FROM_HTML_MODE_COMPACT
                                        )
                                    } else {
                                        actProvacyPolicyBinding.tvCmsData.text =
                                            Html.fromHtml(restResponce.about)
                                    }
                                }
                            }
                            intent.getStringExtra("Type") == "Terms & Condition" -> {
                                if (restResponce.termsConditions.termsConditions.isNullOrEmpty()){
                                    actProvacyPolicyBinding.tvCmsData.text=""
                                }else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        actProvacyPolicyBinding.tvCmsData.text = Html.fromHtml(
                                            restResponce.termsConditions.termsConditions,
                                            Html.FROM_HTML_MODE_COMPACT
                                        )
                                    } else {
                                        actProvacyPolicyBinding.tvCmsData.text =
                                            Html.fromHtml(restResponce.termsConditions.termsConditions)
                                    }
                                }
                            }
                            intent.getStringExtra("Type") == "Refund Policy" -> {
                                if (restResponce.refundpolicy.isNullOrEmpty()){
                                    actProvacyPolicyBinding.tvCmsData.text=""
                                }else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        actProvacyPolicyBinding.tvCmsData.text = Html.fromHtml(
                                            restResponce.refundpolicy,
                                            Html.FROM_HTML_MODE_COMPACT
                                        )
                                    } else {
                                        actProvacyPolicyBinding.tvCmsData.text =
                                            Html.fromHtml(restResponce.refundpolicy)
                                    }
                                }
                            }
                        }
                    } else if (restResponce.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActProvacyPolicy,
                            restResponce.message.toString()
                        )
                    }
                }
            }

            override fun onFailure(call: Call<CmsPageResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActProvacyPolicy,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }
}