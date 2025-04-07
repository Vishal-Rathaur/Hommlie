package com.hommlie.user.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActSignUpBinding
import com.hommlie.user.model.newRegistrationModel
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.alertErrorOrValidationDialog
import com.hommlie.user.utils.Common.showErrorFullMsg
import com.hommlie.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.MessageDigest

class ActSignUp : BaseActivity() {
    private lateinit var signUpBinding: ActSignUpBinding
  //  var strToken = ""
    var orderId=""
    private var token : String = ""
    override fun setLayout(): View = signUpBinding.root

    override fun initView() {
        signUpBinding = ActSignUpBinding.inflate(layoutInflater)

        FirebaseApp.initializeApp(this@ActSignUp)

        // Force the app to always use light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM_TOKEN", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get the FCM token
            token = task.result
            Log.d("FCM_TOKEN", "Token: $token")
        }


        Log.d("release has code key ",generateHashKey(this))

        signUpBinding.edtMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length==10){
                    Common.closeKeyBoard(this@ActSignUp)
                }
            }
        })



      //  checkAndRequestSmsPermissions()


        signUpBinding.tvSkip.setOnClickListener {
            val intent=Intent(this@ActSignUp,ActMain::class.java)
            startActivity(intent)
            finish()
        }

        signUpBinding.btnGetOtp.setOnClickListener {
            if (Common.isValidMobile(signUpBinding.edtMobile.text.toString())) {
                signup()
            }else{
                alertErrorOrValidationDialog(this@ActSignUp,"Please enter mobile number")
            }
            setResult(RESULT_OK)
        }

    }

    private fun signup() {
        if (intent.getStringExtra("loginType") != null) {
            if (signUpBinding.edtMobile.text.toString().equals("")) {
                showErrorFullMsg(this@ActSignUp, resources.getString(R.string.validation_all))
            } else if (intent.getStringExtra("loginType") == "facebook" || intent.getStringExtra("loginType") == "google") {
                val hasmap = HashMap<String, String>()

                hasmap["mobile"] = "+91" + signUpBinding.edtMobile.text.toString()
                hasmap["app_token"] = token
                hasmap["register_type"] = "mobile"
                hasmap["login_type"] = "mobile"
                if (intent.getStringExtra("loginType") == "google") {
                    hasmap["google_id"] = intent.getStringExtra("profileId")!!
                    hasmap["facebook_id"] = ""
                } else {
                    hasmap["facebook_id"] = intent.getStringExtra("profileId")!!
                    hasmap["google_id"] = ""
                }
                if (Common.isCheckNetwork(this@ActSignUp)) {
                    if (!signUpBinding.edtMobile.text.toString().isNullOrBlank()) {
                        callApiRegistration(hasmap)
                    } else {
                        showErrorFullMsg(
                            this@ActSignUp,"Please enter mobile number"
                        )
                    }
                } else {
                    alertErrorOrValidationDialog(
                        this@ActSignUp,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
        } else {
            Log.d("token", token)

                if (!signUpBinding.edtMobile.text.toString().isNullOrBlank()) {
                    val hasmap = HashMap<String, String>()
                    hasmap["mobile"] = "+91" + signUpBinding.edtMobile.text.toString()
                    hasmap["app_token"] = token
                    hasmap["login_type"] = "mobile"
                    hasmap["register_type"] = "mobile"
                    if (Common.isCheckNetwork(this@ActSignUp)) {
                        callApiRegistration(hasmap)
                    } else {
                        alertErrorOrValidationDialog(
                            this@ActSignUp,
                            resources.getString(R.string.no_internet)
                        )
                    }
            }
        }
    }

    private fun callApiRegistration(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActSignUp)
        val call = ApiClient.getClient.setRegistration(hasmap)

        call.enqueue(object :Callback<newRegistrationModel>{
            override fun onResponse(call: Call<newRegistrationModel>, response: Response<newRegistrationModel>) {
                if (response.isSuccessful) {
                    val restResponse =response.body()
                    if (restResponse!=null){
                        if (restResponse.status==1){
                            Common.dismissLoadingProgress()
                            var otpController = ""
                            otpController = if (SharePreference.getStringPref(this@ActSignUp, SharePreference.UserLoginType) == "1") {
                                signUpBinding.edtMobile.text.toString()
                            } else {
                                signUpBinding.edtMobile.text.toString()
                            }
                            startActivity(
                                Intent(this@ActSignUp, ActOTPVerification::class.java)
                                    .putExtra("email", otpController)
                                    .putExtra("orderId", restResponse.message?.request_id)
                                    .putExtra("name",restResponse.user_name.toString())
                                    .putExtra("token",token)
                            )

                        }else{
                            Common.dismissLoadingProgress()
                            Common.showSuccessFullMsg(this@ActSignUp, restResponse.message?.type.toString())
                        }
                    }else{
                        Common.dismissLoadingProgress()
                        Common.showSuccessFullMsg(this@ActSignUp,response.message())
                    }
                }else{
                    Common.dismissLoadingProgress()
                    Common.showSuccessFullMsg(this@ActSignUp, "Please try again after some time.")
                }
            }

            override fun onFailure(call: Call<newRegistrationModel>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.showSuccessFullMsg(this@ActSignUp, "Please try again after some time.")
            }

        })

    }

    fun generateHashKey(context: Context): String {
        val packageName = context.packageName
        val secretKey = "63:DF:26:6D:21:A6:84:A2:A1:2A:BF:91:45:EB:96:29:0F:98:1E:30:A4:71:E7:43:82:AC:DF:7A:9D:E8:48:89"
        val toHash = "$packageName.$secretKey"

        return try {
            val digest = MessageDigest.getInstance("SHA-256").digest(toHash.toByteArray())
            // Convert the byte array to a hex string and take the first 11 characters
            val hashCode = digest.joinToString("") { String.format("%02x", it) }.substring(0, 11)
            hashCode
        } catch (e: Exception) {
            ""
        }
    }



//    private fun checkAndRequestSmsPermissions() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
//                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
//
//                // Request permission if not granted
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
//                    123
//                )
//            } else {
//                Log.d("OTP", "✅ SMS Permissions already granted!")
//            }
//        }
//    }

    private fun checkAndRequestSmsPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11+ (API 30+)
//                Log.d("OTP", "❌ READ_SMS is restricted on Android 11+. Use SmsRetriever API instead!")
//                showPermissionExplanationDialog()
//            } else {
                // Check permissions for Android 6 to Android 10 (API 23 - 29)
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {

//                    ActivityCompat.requestPermissions(
//                        this,
//                        arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
//                        123
//                    )
                    showPermissionExplanationDialog()
                } else {
                    Log.d("OTP", "✅ SMS Permissions already granted!")
                }
//            }
        }
    }


    private fun showPermissionExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle("SMS Permission Required")
            .setMessage("This app requires SMS permissions to automatically detect OTP and make your login process seamless. Please grant SMS access.")
            .setPositiveButton("Allow") { _, _ ->
                // Request the permission after the user understands why it's needed
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
                    123
                )
            }
            .setNegativeButton("Deny") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Permission denied. You need to enter OTP manually.", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .show()
    }

    override fun onResume() {
        super.onResume()
    }

}