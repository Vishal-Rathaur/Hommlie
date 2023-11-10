package com.ikksa.user.activity


import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import com.ikksa.user.R
import com.ikksa.user.base.BaseActivity
import com.ikksa.user.databinding.ActSplashScreenBinding
import com.ikksa.user.utils.Common
import com.ikksa.user.utils.SharePreference
import com.google.android.gms.security.ProviderInstaller
import com.google.android.gms.security.ProviderInstaller.ProviderInstallListener
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import javax.net.ssl.SSLContext


class ActSplashScreen : BaseActivity() {

    private lateinit var splashScreenBinding: ActSplashScreenBinding

    override fun setLayout(): View = splashScreenBinding.root

    override fun initView() {
        splashScreenBinding = ActSplashScreenBinding.inflate(layoutInflater)
        if (Common.isCheckNetwork(this@ActSplashScreen)) {
            init()
            checkTls()
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActSplashScreen,
                resources.getString(R.string.no_internet)
            )
        }
    }

    private fun init() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (!SharePreference.getBooleanPref(this@ActSplashScreen, SharePreference.isTutorial)) {
                //openActivity(ActTutorial::class.java)
                openActivity(ActMain::class.java)
                finish()
            } else {
                openActivity(ActMain::class.java)
                finish()
            }
        }, 3000)
    }


    protected fun checkTls() {
        if (Build.VERSION.SDK_INT < 21) {
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


}