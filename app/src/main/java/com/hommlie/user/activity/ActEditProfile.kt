package com.hommlie.user.activity

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActEditProfileBinding
import com.hommlie.user.model.GetProfileResponse
import com.hommlie.user.model.UserData
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.alertErrorOrValidationDialog
import com.hommlie.user.utils.Common.dismissLoadingProgress
import com.hommlie.user.utils.Common.isCheckNetwork
import com.hommlie.user.utils.Common.showErrorFullMsg
import com.hommlie.user.utils.Common.showLoadingProgress
import com.hommlie.user.utils.SharePreference
import com.github.dhaval2404.imagepicker.ImagePicker
import com.hommlie.user.model.UserProfileData
import com.hommlie.user.utils.UserProfileDataManager
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class ActEditProfile : BaseActivity() {

    private lateinit var editProfileBinding: ActEditProfileBinding
    private var imageFile: File? = null
    override fun setLayout(): View = editProfileBinding.root

    override fun initView() {
        editProfileBinding = ActEditProfileBinding.inflate(layoutInflater)
        if (isCheckNetwork(this@ActEditProfile)) {
            callApiProfile()
        } else {
            alertErrorOrValidationDialog(
                this@ActEditProfile,
                resources.getString(R.string.no_internet)
            )
        }
        initClickListeners()
    }

    private fun initClickListeners() {
        editProfileBinding.ivGellary.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)
                .saveDir(
                    File(
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "ServiceProvider"
                    )
                )
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
        editProfileBinding.ivBack.setOnClickListener {
            finish()
            setResult(RESULT_OK)
        }
        editProfileBinding.btnsave.setOnClickListener {
            if (editProfileBinding.edtName.text.toString() == "") {
                showErrorFullMsg(
                    this@ActEditProfile,
                    resources.getString(R.string.validation_all)
                )
            } else {
                if (isCheckNetwork(this@ActEditProfile)) {
                    callApiEditProfile()
                } else {
                    alertErrorOrValidationDialog(
                        this@ActEditProfile,
                        resources.getString(R.string.no_internet)
                    )
                }
            }

        }
    }

    //TODO API EDIT PROFILE CALL
    private fun callApiEditProfile() {
        showLoadingProgress(this@ActEditProfile)
        val hashMap = HashMap<String,String>()
       // hashMap["id"] = SharePreference.getStringPref(this@ActEditProfile,SharePreference.userId).toString()
        hashMap["user_id"] =SharePreference.getStringPref(this@ActEditProfile,SharePreference.userId).toString()
        hashMap["name"] = editProfileBinding.edtName.text.toString().trim()
        hashMap["email"] = editProfileBinding.edtEmail.text.toString().trim()

        val userId = RequestBody.create("text/plain".toMediaTypeOrNull(), SharePreference.getStringPref(this@ActEditProfile, SharePreference.userId).toString())
        val name = RequestBody.create("text/plain".toMediaTypeOrNull(), editProfileBinding.edtName.text.toString().trim())
        val email = RequestBody.create("text/plain".toMediaTypeOrNull(), editProfileBinding.edtEmail.text.toString().trim())

        // Prepare the image file if it exists
        var imagePart: MultipartBody.Part? = null
        if (imageFile != null) {
            val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile!!)
            imagePart = MultipartBody.Part.createFormData("profile_pic", imageFile!!.name, requestFile)
        }
        val call=ApiClient.getClient.setProfile(userId, name, email, imagePart)
        call.enqueue(object : Callback<UserProfileData> {
            override fun onResponse(call: Call<UserProfileData>,response: Response<UserProfileData>) {
                if (response.isSuccessful){
                    val restResponse = response.body()
                    if (restResponse!=null){
                        if (restResponse.status==1){
                            dismissLoadingProgress()
                            callApiProfile()
                        }else{
                            dismissLoadingProgress()
                            alertErrorOrValidationDialog(this@ActEditProfile,restResponse.message)
                        }
                    }else{
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(this@ActEditProfile, resources.getString(R.string.error_msg))
                    }
                }else{
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(this@ActEditProfile, resources.getString(R.string.error_msg)+"2")
                }
            }

            override fun onFailure(call: Call<UserProfileData>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActEditProfile,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    Log.e("FilePath", fileUri.path.toString())
                    fileUri.path.let { imageFile = File(it) }
                    Log.e("imageFileLength", imageFile!!.length().toString())
                    Glide.with(this@ActEditProfile).load(fileUri.path)
                        .into(editProfileBinding.ivProfile)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(
                        this@ActEditProfile,
                        ImagePicker.getError(data),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    //TODO API PROFILE CALL
    private fun callApiProfile() {
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            SharePreference.getStringPref(this@ActEditProfile, SharePreference.userId)!!
        showLoadingProgress(this@ActEditProfile)
        val call = ApiClient.getClient.getProfile(hasmap)
        call.enqueue(object : Callback<GetProfileResponse> {
            override fun onResponse(
                call: Call<GetProfileResponse>,
                response: Response<GetProfileResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce: GetProfileResponse = response.body()!!
                    if (restResponce.status == 1) {
                        dismissLoadingProgress()
                        val dataResponse: UserData = restResponce.data!!
                         setProfileData(dataResponse)
                    } else if (restResponce.data!!.equals("0")) {
                        dismissLoadingProgress()
                        alertErrorOrValidationDialog(
                            this@ActEditProfile,
                            restResponce.message
                        )
                    }
                }
            }

            override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActEditProfile,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SET PROFILE DATA
    private fun setProfileData(dataResponse: UserData) {
        editProfileBinding.edtEmail.setText(dataResponse.email)
        editProfileBinding.edtName.setText(dataResponse.name)
        editProfileBinding.edMobileNumber.setText(dataResponse.mobile)
        editProfileBinding.edtBankName.setText(dataResponse.bankName)
        editProfileBinding.edtAccountNo.setText(dataResponse.accountnumber)
        editProfileBinding.edtIfscCode.setText(dataResponse.ifscCode)
        editProfileBinding.edtUpi.setText(dataResponse.upi)
        Glide.with(this@ActEditProfile).load(dataResponse.profilePic).placeholder(
            ResourcesCompat.getDrawable(resources, R.drawable.profile, null)
        ).into(editProfileBinding.ivProfile)
    }

    override fun onResume() {
        super.onResume()
        Common.getCurrentLanguage(this@ActEditProfile, false)
    }

    //TODO PRODFILE UPDATE SUCCESS DIALOG
    fun successfulDialog(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {
            dialog?.dismiss()
            dialog = Dialog(act, R.style.AppCompatAlertDialogStyleBig)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
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
                setResult(RESULT_OK)
                finish()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}