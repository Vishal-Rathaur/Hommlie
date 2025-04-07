package com.hommlie.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.hommlie.user.databinding.ActForgotPasswordBinding
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.utils.Common
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class ActForgotPassword : BaseActivity() {
    private lateinit var forgotPasswordBinding: ActForgotPasswordBinding
    override fun setLayout(): View = forgotPasswordBinding.root

    override fun initView() {
        forgotPasswordBinding = ActForgotPasswordBinding.inflate(layoutInflater)
        forgotPasswordBinding.ivBack.setOnClickListener { finish() }

        forgotPasswordBinding.btnForgotPassword.setOnClickListener {
            if (forgotPasswordBinding.edtForgetEmail.text.toString() == "") {
                Common.showErrorFullMsg(
                    this@ActForgotPassword,
                    resources.getString(R.string.validation_all)
                )
            } else if (!Common.isValidMobile(forgotPasswordBinding.edtForgetEmail.text.toString())) {
                Common.showErrorFullMsg(
                    this@ActForgotPassword,
                    resources.getString(R.string.validation_valid_mobile)
                )
            }else if(forgotPasswordBinding.edtForgetEmail.text.toString().startsWith("0")) {
                Common.showErrorFullMsg(
                    this@ActForgotPassword,
                    resources.getString(R.string.validation_valid_mobile)
                )
            }else {
                val hasmap = HashMap<String, String>()
                hasmap["country"] = "966"
                hasmap["mobile"] = forgotPasswordBinding.edtForgetEmail.text.toString()
                if (Common.isCheckNetwork(this@ActForgotPassword)) {
                    callApiForgetpassword(hasmap)
                } else {
                    Common.alertErrorOrValidationDialog(
                        this@ActForgotPassword,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
        }

        forgotPasswordBinding.btnUpdatePassword.setOnClickListener {

            if (forgotPasswordBinding.edtotp.text.toString().length != 6) {
                Common.showErrorFullMsg(
                    this@ActForgotPassword,
                    resources.getString(R.string.enter_valid_otp)
                )
            }else if(forgotPasswordBinding.password.text.toString()=="") {
                Common.showErrorFullMsg(
                    this@ActForgotPassword,
                    resources.getString(R.string.enter_password_valid)
                )
            }else if(forgotPasswordBinding.confirmPassword.text.toString()=="") {
                Common.showErrorFullMsg(
                    this@ActForgotPassword,
                    resources.getString(R.string.enter_confirm_password_valid)
                )
            }else if(forgotPasswordBinding.password.text.toString()!=forgotPasswordBinding.confirmPassword.text.toString()) {
                Common.showErrorFullMsg(
                    this@ActForgotPassword,
                    resources.getString(R.string.enter_valid_confirm_password)
                )
            }else {
                val hasmap = HashMap<String, String>()
                hasmap["country"] = "966"
                hasmap["mobile"] = forgotPasswordBinding.edtForgetEmail.text.toString()
                hasmap["otp"] = forgotPasswordBinding.edtotp.text.toString()
                hasmap["confirmPassword"] = forgotPasswordBinding.confirmPassword.text.toString()
                if (Common.isCheckNetwork(this@ActForgotPassword)) {
                    callApiForgetPasswordReset(hasmap)
                } else {
                    Common.alertErrorOrValidationDialog(
                        this@ActForgotPassword,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
        }
    }

    //TODO API FORGET PASSWORD CALL
    private fun callApiForgetpassword(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActForgotPassword)
        val call = ApiClient.getClient.setforgotPassword(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status==1) {

                        Common.showSuccessFullMsg(
                            this@ActForgotPassword,
                            restResponse.message.toString()
                        )
                        forgotPasswordBinding.edtForgetEmail.inputType = InputType.TYPE_NULL;
                        forgotPasswordBinding.edtotp.visibility = View.VISIBLE
                        forgotPasswordBinding.password.visibility = View.VISIBLE
                        forgotPasswordBinding.confirmPassword.visibility = View.VISIBLE
                        forgotPasswordBinding.btnForgotPassword.visibility = View.GONE
                        forgotPasswordBinding.btnUpdatePassword.visibility = View.VISIBLE
                    } else {
                        successfulDialog(
                            this@ActForgotPassword,
                            restResponse.message
                        )
                    }
                } else {
                    Common.dismissLoadingProgress()
                    Common.showErrorFullMsg(
                        this@ActForgotPassword,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.showErrorFullMsg(
                    this@ActForgotPassword,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiForgetPasswordReset(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActForgotPassword)
        val call = ApiClient.getClient.verifyForgotPassword(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    val restResponse: SingleResponse = response.body()!!
                    if (restResponse.status==1) {
                        successfulDialog(
                            this@ActForgotPassword,
                            restResponse.message
                        )
                    } else {
                        successfulDialog(
                            this@ActForgotPassword,
                            restResponse.message
                        )
                    }
                } else {
                    Common.dismissLoadingProgress()
                    Common.showErrorFullMsg(
                        this@ActForgotPassword,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.showErrorFullMsg(
                    this@ActForgotPassword,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO FORGET PASSWORD SUCCESS DIALOG
    @SuppressLint("InflateParams")
    fun successfulDialog(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {
            dialog?.dismiss()
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            );
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(false)
            val mInflater = LayoutInflater.from(act)
            val mView = mInflater.inflate(R.layout.dlg_validation, null, false)
            val textDesc: TextView = mView.findViewById(R.id.tvMessage)
            textDesc.text = msg
            val tvOk: TextView = mView.findViewById(R.id.tvOk)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                finalDialog.dismiss()
                finish()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActForgotPassword, false)
    }
}