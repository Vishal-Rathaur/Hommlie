package com.hommlie.user


import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.security.ProviderInstaller
import com.google.android.gms.security.ProviderInstaller.ProviderInstallListener
import com.google.firebase.messaging.FirebaseMessaging
import com.hommlie.user.activity.ActMain
import com.hommlie.user.activity.ActSignUp
import com.hommlie.user.api.ApiClient
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActSplashScreenBinding
import com.hommlie.user.model.CheckVersionResponse
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.SharePreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import javax.net.ssl.SSLContext


class ActSplashScreen : AppCompatActivity() {

    private lateinit var splashScreenBinding: ActSplashScreenBinding

    private var handler: Handler? = null
    private var runnable: Runnable? = null

    private val PERMISSION_REQUEST_CODE = 112
    private val REQUEST_CODE_UPDATE = 100
    private var token : String = ""
    val versionCode: Int = BuildConfig.VERSION_CODE
    val versionName: String = BuildConfig.VERSION_NAME

   // override fun setLayout(): View = splashScreenBinding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreenBinding = ActSplashScreenBinding.inflate(layoutInflater)
        setContentView(splashScreenBinding.root)

        //Ask for Permission in android 13
        if (Build.VERSION.SDK_INT > 32) {
            if (!shouldShowRequestPermissionRationale("112")){
                getNotificationPermission()
            }
        }

        // Get FCM Token
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("FCM_TOKEN", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            // Get the FCM token
            token = task.result
            Log.d("FCM_TOKEN", "Token: $token")
        }



        if (Common.isCheckNetwork(this@ActSplashScreen)) {
           // init()
            checkTls()
        } else {
            Common.alertDialogNoInternet(
                this@ActSplashScreen,
                resources.getString(R.string.no_internet)
            )
        }



    }

    fun getNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT > 32) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    PERMISSION_REQUEST_CODE
                )
            }
        } catch (e: Exception) {
        }
    }

    private fun init() {

        // Remove any existing callbacks before posting a new one
        runnable?.let { handler?.removeCallbacks(it) }
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            if (SharePreference.getBooleanPref(this@ActSplashScreen, SharePreference.isLogin)) {
                val intent = Intent(this@ActSplashScreen, ActMain::class.java)
                startActivity(intent)
            } else {
                Log.d("signuppp", "called")
                val intent = Intent(this@ActSplashScreen, ActSignUp::class.java)
                startActivity(intent)
            }
            finish()
            overridePendingTransition(R.anim.slide_in, R.anim.no_animation)
        }
        handler?.postDelayed(runnable!!, 6000)

    }


    protected fun checkTls() {
        if (Build.VERSION.SDK_INT < 23) {
            try {
                ProviderInstaller.installIfNeededAsync(this, object : ProviderInstallListener {
                    override fun onProviderInstalled() {
                        var sslContext: SSLContext? = null
                        try {
                            sslContext = SSLContext.getInstance("TLSv1.2")
                            sslContext.init(null, null, null)
                            val engine = sslContext.createSSLEngine()
                        } catch (e: NoSuchAlgorithmException) {
                            e.printStackTrace()
                        } catch (e: KeyManagementException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onProviderInstallFailed(i: Int, intent: Intent?) {}
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



//    private fun checkVersion(){
//        val call = ApiClient.getClient.checkVersion()
//        call.enqueue(object : Callback<CheckVersionResponse> {
//            override fun onResponse(call: Call<CheckVersionResponse>, response: Response<CheckVersionResponse>) {
//                if (response.code() == 200) {
//                    val restResponse  = response.body()
//                    if (restResponse != null) {
//                        if (restResponse.status == 1) {
//                            if (restResponse.versionCode>versionCode){
//                                showUpdateDialog()
//                            }else{
//                                init()
//                            }
//                        }else{
//                            Common.alertErrorOrValidationDialog(this@ActSplashScreen, restResponse.message)
//                        }
//                    }
//
//                } else {
//                    when (response.code()) {
//                        404 -> {
//                            Common.alertErrorOrValidationDialog(this@ActSplashScreen, "Resource not found.")
//                        }
//                        500 -> {
//                            Common.alertErrorOrValidationDialog(this@ActSplashScreen, "Server error. Please try again later.")
//                        }
//                        else -> {
//                            Common.alertErrorOrValidationDialog(this@ActSplashScreen, "Unexpected error: ${response.code()}")
//                        }
//                    }
//                }
//            }
//            override fun onFailure(call: Call<CheckVersionResponse>, t: Throwable) {
//                val errorMessage = when (t) {
//                    is java.net.UnknownHostException -> "No internet connection. Please check your network."
//                    is java.net.SocketTimeoutException -> "Request timed out. Please try again."
//                    is java.io.IOException -> "Network error occurred. Please try again."
//                    else -> "Something went wrong. Please try again."
//                }
//
//                // Display the error message
//                Common.alertErrorOrValidationDialog(this@ActSplashScreen, errorMessage)
//
//                // Optionally, log the error for debugging purposes
//                Log.e("API_ERROR", "onFailure: ${t.localizedMessage}", t)
//            }
//        })
//    }


    private fun showUpdateDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Available")
        builder.setMessage("A new version of the app is available. Please update to continue.")

        builder.setPositiveButton("Update") { _, _ ->
            // Open Play Store or app download link
            val appPackageName = packageName
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (e: android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }

//        builder.setNegativeButton("Cancel") { dialog, _ ->
//            dialog.dismiss()
//        }

        builder.setCancelable(false)
        builder.show()
    }


    override fun onDestroy() {
        super.onDestroy()
        // Remove any existing callbacks before posting a new one
        runnable?.let { handler?.removeCallbacks(it) }
    }


    override fun onResume() {
        super.onResume()
        if (Common.isCheckNetwork(this@ActSplashScreen)){
         //   checkVersion()
            init()
        }else{
            Common.alertDialogNoInternet(this@ActSplashScreen,getString(R.string.no_internet))
        }
    }


}