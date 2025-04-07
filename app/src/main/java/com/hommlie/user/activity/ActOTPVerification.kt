package com.hommlie.user.activity


import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActOtpverificationBinding
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.SharePreference
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.auth.api.phone.SmsRetrieverClient
import com.google.android.gms.auth.api.phone.SmsRetrieverStatusCodes
import com.google.android.gms.common.api.ApiException
import com.hommlie.user.api.RestResponse
import com.hommlie.user.model.RegistrationModel
import com.hommlie.user.model.newRegistrationModel
import com.hommlie.user.service.SmsBroadcastReceiver
import com.hommlie.user.utils.Common.alertErrorOrValidationDialog
import com.hommlie.user.utils.Common.showErrorFullMsg
import com.razorpay.AppSignatureHelper
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Arrays

class ActOTPVerification : BaseActivity() {
    private lateinit var otpverificationBinding: ActOtpverificationBinding
    var strEmail: String = ""
    var token = ""
    var loginUserType = ""
    var orderId = ""
    var name = ""

    companion object {
        val autoOTP: MutableLiveData<String> by lazy { MutableLiveData() }
    }



    private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver



    override fun setLayout(): View = otpverificationBinding.root

    override fun initView() {
        otpverificationBinding = ActOtpverificationBinding.inflate(layoutInflater)
        strEmail = intent.getStringExtra("email")!!
        orderId =  intent.getStringExtra("orderId")!!
        name    =  intent.getStringExtra("name")?:""
      //  strToken = FirebaseInstanceId.getInstance().token.toString()
        token = intent.getStringExtra("token").toString()

        val appHash = getAppHash(this)
        Log.d("OTP", "🔑 Your App Hash: $appHash")
        val appSignature = AppSignatureHelper(this)
        Log.d("OTP", "Your App Hash: ${appSignature.appSignatures}")

        if (isValidAppHash(this)) {
            Log.d("OTP", "App hash is valid!")
        } else {
            Log.d("OTP", "App hash is not valid!")
        }

        // Inside your Activity or Fragment
        val fingerprint = "63DF266D21A684A2A12ABF9145EB96290F981E30A471E74382ACDF7A9DE84889"

// Call the function to generate the app hash
        val appHashsh = generateAppHashFromFingerprint(fingerprint)

// Log the generated app hash to verify
        Log.d("appHashsh", "Your App Hash: [$appHashsh]")


        autoOTP.value = ""
        autoOTP.observe(this@ActOTPVerification) { otpdata ->
            Log.d("OTP Observer", "Observed OTP: $otpdata")

            if (!otpdata.isNullOrEmpty() && otpdata.length == 4) {
                otpverificationBinding.otpDigit1.setText(otpdata[0].toString())
                otpverificationBinding.otpDigit2.setText(otpdata[1].toString())
                otpverificationBinding.otpDigit3.setText(otpdata[2].toString())
                otpverificationBinding.otpDigit4.setText(otpdata[3].toString())
                callApiOTP()
            } else {
                Log.e("OTP Error", "OTP is invalid or empty: $otpdata")
            }
        }


        otpverificationBinding.ivBack.setOnClickListener { finish() }

        setEditTextListeners()

        otpverificationBinding.tvMobile.text="+91 "+strEmail

        if (name == "null" || name.isBlank()) {
            otpverificationBinding.name.visibility = View.VISIBLE
            otpverificationBinding.edtName.visibility = View.VISIBLE
        } else {
            otpverificationBinding.edtName.setText(name)
            otpverificationBinding.name.visibility = View.GONE
            otpverificationBinding.edtName.visibility = View.GONE
        }

        otpverificationBinding.tvResendotp.setOnClickListener {
            callApiRegistration()
        }

        otpverificationBinding.btnSubmitotp.setOnClickListener {
            val otp=otpverificationBinding.otpDigit1.text.toString()+otpverificationBinding.otpDigit2.text.toString()+
            otpverificationBinding.otpDigit3.text.toString()+otpverificationBinding.otpDigit4.text.toString()
            Common.closeKeyBoard(this@ActOTPVerification)
            if (otp.length != 4) {
             //   Common.getToast(this@ActOTPVerification,otp)
                Common.alertErrorOrValidationDialog(
                    this@ActOTPVerification, resources.getString(
                        R.string.validation_otp
                    )
                )
            } else if ( otpverificationBinding.edtName.text.toString().trim().isBlank()){
                Common.alertErrorOrValidationDialog(
                    this@ActOTPVerification, "Please provide your name")
            }else {
                if (loginUserType == "1") {
//                    val map = HashMap<String, String>()
//                    map["mobile"] = "+91"+strEmail
//                    map["otp"] = otp
//                    map["app_token"] = token
//                    map["orderId"]= orderId
//                    map["referral_code"] = otpverificationBinding.tvRefferal.text.toString().trim()
//                    if (name == "null" || name.isBlank()) {
//                       map["name"] = otpverificationBinding.edtName.text.toString().trim()
//                    }
                    if (Common.isCheckNetwork(this@ActOTPVerification)) {
                        callApiOTP()
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@ActOTPVerification,
                            resources.getString(R.string.no_internet)
                        )
                    }
                } else {
//                    val map = HashMap<String, String>()
//                    map["mobile"] = "+91"+strEmail
//                    map["otp"] = otp
//                    map["app_token"] = token
//                    map["orderId"]= orderId
//                    map["referral_code"] = otpverificationBinding.tvRefferal.text.toString().trim()
//                    if (name == "null" || name.isBlank()) {
//                        map["name"] = otpverificationBinding.edtName.text.toString().trim()
//                    }
                    if (Common.isCheckNetwork(this@ActOTPVerification)) {
                        callApiOTP()
                    } else {
                        Common.alertErrorOrValidationDialog(
                            this@ActOTPVerification,
                            resources.getString(R.string.no_internet)
                        )
                    }
                }
            }



        }


        otpverificationBinding.otpDigit4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s?.length==1){
                    Common.closeKeyBoard(this@ActOTPVerification)
                }
            }
        })

        timer()
//        otpverificationBinding.tvResendOtp.setOnClickListener {
//            val map = HashMap<String, String>()
//            map["email"] = "+91"+strEmail
//            map["otp"] = otpverificationBinding.edOTP.text.toString()
//            map["token"] = strToken
//
//            if (Common.isCheckNetwork(this@ActOTPVerification)) {
//                callApiResendOTP(map)
//            } else {
//                Common.alertErrorOrValidationDialog(
//                    this@ActOTPVerification,
//                    resources.getString(R.string.no_internet)
//                )
//            }
//        }



    }

    //TODO CALL OTP API
    private fun callApiOTP() {

        val map = HashMap<String, String>()
        map["mobile"] = "+91"+strEmail
        map["otp"] = otpverificationBinding.otpDigit1.text.toString().trim()+otpverificationBinding.otpDigit2.text.toString().trim()+
                otpverificationBinding.otpDigit3.text.toString().trim()+otpverificationBinding.otpDigit4.text.toString().trim()
        map["app_token"] = token
        map["orderId"]= orderId
        map["referral_code"] = otpverificationBinding.tvRefferal.text.toString().trim()
        if (name == "null" || name.isBlank()) {
            map["name"] = otpverificationBinding.edtName.text.toString().trim()
        }

        Common.showLoadingProgress(this@ActOTPVerification)
        val call: Call<JsonObject> = ApiClient.getClient.setEmailVerify(map)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.code() == 200) {
                    val mainObject = JSONObject(response.body().toString())
                    val statusType = mainObject.getInt("status")
                    val statusMessage = mainObject.getString("message")
                    if (statusType == 0) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(this@ActOTPVerification, statusMessage)
                    } else if (statusType == 1) {
                        Common.dismissLoadingProgress()
                        val userid=mainObject.getString("token")

                        SharePreference.setStringPref(this@ActOTPVerification, SharePreference.userId, Common.decodeJWT(userid,this@ActOTPVerification))
                        SharePreference.setStringPref(this@ActOTPVerification, SharePreference.token,userid )

                        SharePreference.setBooleanPref(
                            this@ActOTPVerification,
                            SharePreference.isLogin,
                            true
                        )
                        openActivity(ActMain::class.java)
                        finish()
                    }
                } else {
                    if (response.code() == 500) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActOTPVerification,
                            resources.getString(R.string.error_msg)
                        )
                    } else {
                        val mainErrorObject = JSONObject(response.errorBody()!!.string())
                        val strMessage = mainErrorObject.getString("message")
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(this@ActOTPVerification, strMessage)
                    }
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActOTPVerification,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    private fun callApiResendOTP(map: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActOTPVerification)
        val call: Call<SingleResponse> = ApiClient.getClient.setResendEmailVerification(map)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    val registrationResponse: SingleResponse = response.body()!!
                    if (registrationResponse.status == 1) {
                        otpverificationBinding.otpDigit1.text.clear()
                        otpverificationBinding.otpDigit2.text.clear()
                        otpverificationBinding.otpDigit3.text.clear()
                        otpverificationBinding.otpDigit4.text.clear()

                        Common.dismissLoadingProgress()
                        Common.showSuccessFullMsg(
                            this@ActOTPVerification,
                            registrationResponse.message!!
                        )
                    } else if (registrationResponse.status == 0) {
                        Common.dismissLoadingProgress()
                        Common.showErrorFullMsg(
                            this@ActOTPVerification,
                            registrationResponse.message!!
                        )
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    Common.dismissLoadingProgress()
                    Common.showErrorFullMsg(
                        this@ActOTPVerification,
                        error.getString("message")
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.showErrorFullMsg(
                    this@ActOTPVerification,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onBackPressed() {
        Common.closeKeyBoard(this@ActOTPVerification)
        openActivity(ActSignUp::class.java)
        finish()
        finishAffinity()
    }

    private fun timer() {
        object : CountDownTimer(45000, 1000) {
            override fun onTick(millis: Long) {
                otpverificationBinding.tvDidnt.visibility = View.GONE
                otpverificationBinding.tvResendotp.visibility = View.GONE
                otpverificationBinding.tvTimer.visibility = View.VISIBLE
                val timer = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(
                            millis
                        )
                    ), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millis)
                    )
                )
                otpverificationBinding.tvTimer.text = timer
                if (timer == "00:00") {
                    otpverificationBinding.tvTimer.visibility = View.GONE
                    otpverificationBinding.tvDidnt.visibility = View.VISIBLE
                    otpverificationBinding.tvResendotp.visibility = View.VISIBLE
                }
            }

            override fun onFinish() {
                otpverificationBinding.tvTimer.visibility = View.GONE
                otpverificationBinding.tvDidnt.visibility = View.VISIBLE
                otpverificationBinding.tvResendotp.visibility = View.VISIBLE
            }
        }.start()

    }





    private fun setEditTextListeners() {
        otpverificationBinding.otpDigit1.addTextChangedListener(GenericTextWatcher(otpverificationBinding.otpDigit1, otpverificationBinding.otpDigit2, null))
        otpverificationBinding.otpDigit2.addTextChangedListener(GenericTextWatcher(otpverificationBinding.otpDigit2, otpverificationBinding.otpDigit3, otpverificationBinding.otpDigit1))
        otpverificationBinding.otpDigit3.addTextChangedListener(GenericTextWatcher(otpverificationBinding.otpDigit3, otpverificationBinding.otpDigit4, otpverificationBinding.otpDigit2))
        otpverificationBinding.otpDigit4.addTextChangedListener(GenericTextWatcher(otpverificationBinding.otpDigit4, otpverificationBinding.otpDigit5, otpverificationBinding.otpDigit3))


        setBackspaceListener(otpverificationBinding.otpDigit1, null)
        setBackspaceListener(otpverificationBinding.otpDigit2, otpverificationBinding.otpDigit1)
        setBackspaceListener(otpverificationBinding.otpDigit3, otpverificationBinding.otpDigit2)
        setBackspaceListener(otpverificationBinding.otpDigit4, otpverificationBinding.otpDigit3)

    }

    private fun setBackspaceListener(editText: EditText, previousView: View?) {
        editText.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && editText.text.isEmpty()) {
                // If delete key is pressed and the current digit is empty, move focus to the previous EditText
                previousView?.requestFocus()
                true
            } else {
                false
            }
        }
    }
    private class GenericTextWatcher(
        private val currentView: View,
        private val nextView: View?,
        private val previousView: View?
    ) : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

        }
        override fun afterTextChanged(editable: Editable) {
            if (editable.isEmpty() && previousView != null) {
                previousView.requestFocus()
            } else if (editable.length == 1 && nextView != null) {
                nextView.requestFocus()
            }
        }
    }




    fun startSmsRetriever() {
        val client = SmsRetriever.getClient(this)
        Toast.makeText(this@ActOTPVerification,"Trying to read otp",Toast.LENGTH_SHORT).show()
        // 🔄 Stop any existing listener before starting a new one
        client.startSmsUserConsent(null)

        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Log.d("OTP", "✅ SMS Retriever API started successfully")
        }.addOnFailureListener {
            Log.e("OTP", "❌ Failed to start SMS Retriever API", it)
        }
    }






    private fun extractOtpFromMessage(message: String): String {
        val pattern = Regex("\\b\\d{4,6}\\b")  // Matches 4-6 digit OTP
        return pattern.find(message)?.value.orEmpty()
    }







    private fun callApiRegistration() {
        val hasmap = HashMap<String,String>()
        hasmap["mobile"] ="+91"+strEmail
        Common.showLoadingProgress(this@ActOTPVerification)
        val call = ApiClient.getClient.setRegistration(hasmap)


        call.enqueue(object :Callback<newRegistrationModel>{
            override fun onResponse(call: Call<newRegistrationModel>, response: Response<newRegistrationModel>) {
                if (response.isSuccessful) {
                    val restResponse =response.body()
                    if (restResponse!=null){
                        if (restResponse.status==1){
                            Common.dismissLoadingProgress()
                            Common.dismissLoadingProgress()
                            timer()
                        }else{
                            Common.dismissLoadingProgress()
                            Common.showSuccessFullMsg(this@ActOTPVerification, restResponse.message?.type.toString())
                        }
                    }else{
                        Common.dismissLoadingProgress()
                        Common.showSuccessFullMsg(this@ActOTPVerification,response.message())
                    }
                }else{
                    Common.dismissLoadingProgress()
                    Common.showSuccessFullMsg(this@ActOTPVerification, "Please try again after some time.")
                }
            }

            override fun onFailure(call: Call<newRegistrationModel>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.showSuccessFullMsg(this@ActOTPVerification, "Please try again after some time.")
            }

        })
    }


    override fun onResume() {
        super.onResume()
        startSmsRetriever()  // Start SMS Retriever first

        smsBroadcastReceiver = SmsBroadcastReceiver()
       // val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
       // registerReceiver(smsBroadcastReceiver, intentFilter)

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(smsBroadcastReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(smsBroadcastReceiver, intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(smsBroadcastReceiver)
    }



//    fun getAppHash(context: Context): String {
//        try {
//            val packageName = context.packageName
//            val packageManager = context.packageManager
//            val signatures = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
//                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo.apkContentsSigners
//            } else {
//                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
//            }
//
//            val signature = signatures[0].toByteArray()
//            val messageDigest = MessageDigest.getInstance("SHA-256").digest(signature)
//            val hash = messageDigest.copyOfRange(0, 9)
//            return Base64.encodeToString(hash, Base64.NO_PADDING or Base64.NO_WRAP).substring(0, 11)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return ""
//    }

    fun getAppHash(context: Context): String {
        try {
            val packageName = context.packageName
            val packageManager = context.packageManager

            val signatures = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                // For Android Pie (API 28) and above, use the signingInfo
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo.apkContentsSigners
            } else {
                // For lower versions, use the old GET_SIGNATURES
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures
            }

            // Get the first signature from the signatures array
            val signature = signatures[0].toByteArray()

            // Create a MessageDigest instance for SHA-256
            val messageDigest = MessageDigest.getInstance("SHA-256").digest(signature)

            // Convert the SHA-256 hash to Base64
            val hash = Base64.encodeToString(messageDigest, Base64.NO_PADDING or Base64.NO_WRAP)

            // Log or return the hash (this is the hash for comparison)
            Log.d("OTP", "Generated App Hash: $hash")

            return hash
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    // Function to check if the generated hash matches the one you got from the Play Store Console
    fun isValidAppHash(context: Context): Boolean {
        // This is the SHA-256 fingerprint you retrieved from the Play Store Console
        val playStoreFingerprint = "63:DF:26:6D:21:A6:84:A2:A1:2A:BF:91:45:EB:96:29:0F:98:1E:30:A4:71:E7:43:82:AC:DF:7A:9D:E8:48:89"

        // Get the generated app hash
        val generatedHash = getAppHash(context)

        // Compare the generated hash with the Play Store fingerprint
        // You might need to remove any special characters like ':' from the Play Store fingerprint
        val formattedPlayStoreFingerprint = playStoreFingerprint.replace(":", "")

        // Compare the generated hash with the formatted Play Store fingerprint
        return generatedHash.equals(formattedPlayStoreFingerprint, ignoreCase = true)
    }


    // Helper function to convert a hex string to a byte array
    fun hexStringToByteArray(s: String): ByteArray {
        Log.d("OTP", "Converting hex string to byte array: $s")

        // Check if the string length is even
        if (s.length % 2 != 0) {
            Log.e("OTP", "Invalid fingerprint length, should be even")
            return ByteArray(0)  // Return an empty array if the length is odd
        }

        val len = s.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((s[i].digitToInt(16) shl 4) + s[i + 1].digitToInt(16)).toByte()
        }
        Log.d("OTP", "Byte array generated: ${data.joinToString(",") { it.toString() }}")
        return data
    }

    // Function to generate app hash from the fingerprint
    fun generateAppHashFromFingerprint(fingerprint: String): String {
        try {
            // Convert the provided fingerprint (hex string) to a byte array
            val byteArray = hexStringToByteArray(fingerprint)

            // Check if byteArray is empty
            if (byteArray.isEmpty()) {
                Log.e("OTP", "Byte array is empty. Check your fingerprint format.")
                return ""
            }

            // Create a MessageDigest instance for SHA-256
            val messageDigest = java.security.MessageDigest.getInstance("SHA-256")
            val hash = messageDigest.digest(byteArray)

            // Log the full SHA-256 hash
            Log.d("OTP", "SHA-256 Hash: ${hash.joinToString(",") { it.toString() }}")

            // Get the first 9 bytes (this seems to match the length in your example)
            val hashFirst9Bytes = hash.copyOfRange(0, 9)

            // Log the first 9 bytes of the hash
            Log.d("OTP", "First 9 bytes of SHA-256: ${hashFirst9Bytes.joinToString(",") { it.toString() }}")

            // Convert the hash (first 9 bytes) to Base64 without padding or line breaks
            val base64Hash = Base64.encodeToString(hashFirst9Bytes, Base64.NO_PADDING or Base64.NO_WRAP)

            Log.d("OTP", "Base64 Encoded Hash: $base64Hash")

            return base64Hash
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("OTP", "Error generating app hash: ${e.message}")
        }
        return ""
    }


}



    
