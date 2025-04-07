@file:Suppress("DEPRECATION")

package com.hommlie.user.utils

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.androidadvance.topsnackbar.TSnackbar
import com.auth0.android.jwt.JWT
import com.hommlie.user.BuildConfig
import com.hommlie.user.R
import com.hommlie.user.activity.ActMain
import com.hommlie.user.activity.ActSignUp
import com.hommlie.user.utils.SharePreference.Companion.SELECTED_LANGUAGE
import com.hommlie.user.utils.SharePreference.Companion.getStringPref
import com.hommlie.user.utils.SharePreference.Companion.setBooleanPref
import com.hommlie.user.utils.SharePreference.Companion.setStringPref
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object Common {
    var isProfileEdit: Boolean = false
    var isProfileMainEdit: Boolean = false
    var isAddOrUpdated: Boolean = false

    const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1002

    fun getToast(activity: Activity, strTxtToast: String) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(activity, strTxtToast, Toast.LENGTH_SHORT).show()
        }
    }

    fun isValidAmount(strPattern: String): Boolean {
        return Pattern.compile(
            "^[0-9]+([.][0-9]{2})?\$"
        ).matcher(strPattern).matches();
    }
    fun getLog(strKey: String, strValue: String) {
        Log.e(">>>---  $strKey  ---<<<", strValue)
    }


    fun isValidEmail(strPattern: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(strPattern).matches();
    }

    fun extractYoutubeVideoIdFromUrl(url: String): String? {
        val regex = Regex("(?:https?://)?(?:www\\.)?(?:youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]{11})")
        val matchResult = regex.find(url)
        return matchResult?.groupValues?.get(1)
    }

    fun isValidMobile(phone: String): Boolean {
        // Check if the phone number is exactly 10 digits long and starts with a digit greater than 5
        val pattern = Regex("^[6-9]\\d{9}$")
        return pattern.matches(phone)
    }

    fun isCheckNetwork(context: Context): Boolean {
        val connectivityManager = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    fun openActivity(activity: Activity, destinationClass: Class<*>?) {
        activity.startActivity(Intent(activity, destinationClass))
        activity.overridePendingTransition(R.anim.fad_in, R.anim.fad_out)
    }

    var dialog: Dialog? = null

    fun dismissLoadingProgress() {
        if (dialog != null && dialog!!.isShowing) {
            dialog!!.dismiss()
            dialog = null
        }
    }
    fun formatDateTime(input: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Parse as UTC
            val date = inputFormat.parse(input)
            val outputFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH)
            outputFormat.format(date!!) // Format the date into the desired output
        } catch (e: Exception) {
            e.printStackTrace()
            "Invalid Date"
        }
    }

    fun showLoadingProgress(context: Activity) {
        if (dialog != null) {
            dialog!!.dismiss()
            dialog = null
        }
        dialog = Dialog(context)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setContentView(R.layout.dlg_progress)
        dialog!!.setCancelable(false)
        dialog!!.show()
    }

    fun alertErrorOrValidationDialog(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
            }
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
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun alertDialogNoInternet(act: Activity, msg: String?) {
        var dialog: Dialog? = null
        try {
            if (dialog != null) {
                dialog.dismiss()
                dialog = null
            }
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
                act.recreate()
            }
            dialog.setContentView(mView)
            dialog.show()
            dialog.setCancelable(false)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun showErrorFullMsg(activity: Activity, message: String) {
        val snackbar: TSnackbar = TSnackbar.make(
            activity.findViewById(android.R.id.content),
            message,
            TSnackbar.LENGTH_SHORT
        )
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView: View = snackbar.getView()
        snackbarView.setBackgroundColor(Color.RED)
        val textView = snackbarView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        snackbar.show()
    }

    fun showSuccessFullMsg(activity: Activity, message: String) {
        val snackbar: TSnackbar = TSnackbar.make(
            activity.findViewById(android.R.id.content),
            message,
            TSnackbar.LENGTH_SHORT
        )
        snackbar.setActionTextColor(Color.WHITE)
        val snackbarView: View = snackbar.view
        snackbarView.setBackgroundColor(activity.resources.getColor(R.color.light_green))
        val textView = snackbarView.findViewById<View>(R.id.snackbar_text) as TextView
        textView.setTextColor(Color.WHITE)
        snackbar.show()
    }

    fun setLogout(activity: Activity) {
        val getUserID: String? = SharePreference.getStringPref(activity, SharePreference.userId)
        val isTutorialsActivity: Boolean =
            SharePreference.getBooleanPref(activity, SharePreference.isTutorial)
        val preference = SharePreference(activity)
        preference.mLogout()
        setBooleanPref(activity, SharePreference.isTutorial, isTutorialsActivity)
        if (getUserID != null) {
            setStringPref(activity,SharePreference.userId,"")
            SharePreference.setBooleanPref(activity,SharePreference.isLogin,false)
        }
        val intent = Intent(activity, ActSignUp::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
    }

    fun setImageUpload(strParameter: String, mSelectedFileImg: File): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            strParameter,
            mSelectedFileImg.getName(),
            RequestBody.create("image/*".toMediaType(), mSelectedFileImg)
        )
    }

    fun setRequestBody(bodyData: String): RequestBody {
        return bodyData.toRequestBody("text/plain".toMediaType())
    }


    fun closeKeyBoard(activity: Activity) {
        val view: View? = activity.currentFocus
        if (view != null) {
            try {
                val imm: InputMethodManager =
                    activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


    fun getCurrentLanguage(context: Activity, isChangeLanguage: Boolean) {
        if (getStringPref(context, SELECTED_LANGUAGE) == null || getStringPref(
                context,
                SELECTED_LANGUAGE
            ).equals("", true)) {
            setStringPref(
                context,
                SELECTED_LANGUAGE,
                context.resources.getString(R.string.language_english)
            )
        }
        val locale = if (getStringPref(context, SELECTED_LANGUAGE).equals(context.resources.getString(R.string.language_english), true)) {
            Locale("en-us")
        }else if (getStringPref(context, SELECTED_LANGUAGE).equals(context.resources.getString(R.string.language_hindi), true)){
            Locale("hi")
        } else if (getStringPref(context, SELECTED_LANGUAGE).equals(context.resources.getString(R.string.language_kannada), true)){
            Locale("kn")
        }else {
            Locale("ar")
        }

        //start
        val activityRes = context.resources
        val activityConf = activityRes.configuration
        if (getStringPref(context, SELECTED_LANGUAGE).equals(context.resources.getString(R.string.language_english), true)) {
            activityConf.setLocale(Locale("en-us")) // API 17+ only.
        } else if (getStringPref(context, SELECTED_LANGUAGE).equals(context.resources.getString(R.string.language_hindi), true)){
            activityConf.setLocale(Locale("hi"))
        }else if(getStringPref(context, SELECTED_LANGUAGE).equals(context.resources.getString(R.string.language_kannada), true)){
                activityConf.setLocale(Locale("kn"))
        }else {
            activityConf.setLocale(Locale("ar"))
        }
        activityRes.updateConfiguration(activityConf, activityRes.displayMetrics)
        val applicationRes = context.applicationContext.resources
        val applicationConf = applicationRes.configuration
        applicationConf.setLocale(locale)
        applicationRes.updateConfiguration(applicationConf, applicationRes.displayMetrics)

        if (isChangeLanguage) {
            val intent = Intent(context, ActMain::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
            context.finish()
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    fun getDate(strDate: String): String {
        val curFormater = SimpleDateFormat("dd-MM-yyyy", Locale.US)
        val dateObj = curFormater.parse(strDate)
        val postFormater = SimpleDateFormat("dd MMM yyyy", Locale.US)
        return postFormater.format(dateObj)
    }
    //"2021-10-26 05:05:56"
    fun getDateTime(strDate: String): String {
        val curFormater = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val dateObj = curFormater.parse(strDate)
        val postFormater = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.US)
        return postFormater.format(dateObj)
    }

    fun filterHttps(url: String?): String? {
        var str = url
        val oldValue = "https"
        val newValue = "http"
        val output = str?.replace(oldValue,newValue)
        return output
    }
    fun decodeJWT(jwt: String,context: Context) : String {
        var id=""
        try {
            val decodedJWT = JWT(jwt)

            id = decodedJWT.getClaim("id").asInt().toString()
            setStringPref(context,SharePreference.userRefralCode,decodedJWT.getClaim("referral_code").asString().toString())
            val issuedAt = decodedJWT.issuedAt
            val expiresAt = decodedJWT.expiresAt

//            println("ID: $id")
//            println("Issued At: $issuedAt")
//            println("Expires At: $expiresAt")

        } catch (exception: Exception) {
            exception.printStackTrace()
            println("Failed to decode JWT: ${exception.message}")
        }
        return id
    }

    fun openWhatsApp(context: Context, phoneNum: String, defaultMessage: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val url = "https://api.whatsapp.com/send?phone=$phoneNum&text=${URLEncoder.encode(defaultMessage, "UTF-8")}"
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        } catch (e: Exception) {
            // Handle exceptions, e.g., if WhatsApp is not installed on the device
            Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
        }
    }

    fun openDialPad(context: Context, phoneNum: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNum")
        context.startActivity(intent)
    }


    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermissions(activity: Activity) {
        // Request the necessary permissions
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }


    fun isNotificationPermissionGranted(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPostNotificationPermission(activity: Activity) {
        // Check if the Android version is 13 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Request the necessary permission for Android 13 and above
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // For versions below Android 13, no need to request notification permission
            // Optionally, you can handle showing notifications directly or inform the user
        }
    }


}