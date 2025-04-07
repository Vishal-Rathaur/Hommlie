package com.hommlie.user.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.hommlie.user.R
import com.hommlie.user.databinding.ActivityActReferEarnBinding
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.SharePreference
import java.io.File
import java.io.FileOutputStream

class ActReferEarn : AppCompatActivity() {

    private lateinit var binding:ActivityActReferEarnBinding
    private lateinit var referalCode:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActReferEarnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val referImage = SharePreference.getStringPref(this@ActReferEarn,SharePreference.referCardImage).toString()

        Glide.with(this@ActReferEarn).load(referImage).into(binding.ivReferbanner)

        referalCode = SharePreference.getStringPref(this@ActReferEarn,SharePreference.userRefralCode).toString()+"  "

        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.cardInvite.setOnClickListener {
            finish()
        }
        binding.cardReferNow.setOnClickListener {
            finish()
        }



        binding.tvRefferalCode.text = referalCode

        binding.cardReferNow.setOnClickListener{
            shareReferralCode(referalCode)
        }
        binding.cardInvite.setOnClickListener {
            shareReferralCodeviaSMS(referalCode)
        }
        binding.tvRefferalCode.setOnClickListener {
            copyTextToClipboard(referalCode)
        }

    }


//    private fun shareReferralCode(referralCode: String) {
//        val shareText = "Get an instant ₹100 voucher to use on Hommlie's trusted services! [Application Download link] To redeem the voucher, install the app using the link provided and enter the referral code: $referralCode. Hurry, the reward expires in 4 weeks!"
//        val shareIntent = Intent().apply {
//            action = Intent.ACTION_SEND
//            putExtra(Intent.EXTRA_TEXT, shareText)
//            type = "text/plain"
//        }
//        val chooserIntent = Intent.createChooser(shareIntent, "Share Referral Code")
//        startActivity(chooserIntent)
//    }

    private fun shareReferralCode(referralCode: String) {
        val shareText = "Get an instant ₹100 voucher to use on Hommlie's trusted services! \n\n" +
                "Download here: https://play.google.com/store/apps/details?id=com.hommlie.user \n\n" +
                "To redeem the voucher, install the app and enter the referral code: $referralCode \n\n" +
                "Hurry, the reward expires in 4 weeks!"

        // Create file inside cache directory
        val imageFile = File(cacheDir, "referandearn.jpeg")

        // Get drawable and save as file
        val drawable = resources.getDrawable(R.drawable.referandearn, null) as BitmapDrawable
        val bitmap = drawable.bitmap
        FileOutputStream(imageFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // Save as PNG
        }

        // Get content URI using FileProvider
        val imageUri = FileProvider.getUriForFile(this, "${packageName}.provider", imageFile)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_STREAM, imageUri) // Attach the image
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // ✅ Grant read permission
        }

        // 🔥 Manually grant URI permission to all apps that can handle the share intent
        val resInfoList = packageManager.queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "Share Referral Code")
        startActivity(chooserIntent)
    }






    private fun shareReferralCodeviaSMS(referralCode: String) {
        val smsBody = "Join my app using this referral code: $referralCode"
        val smsIntent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("sms:")
            putExtra("sms_body", smsBody)
        }
        if (smsIntent.resolveActivity(packageManager) != null) {
            startActivity(smsIntent) // Open the SMS app
        } else {
            // If no SMS app is found, show an error message
            // Handle appropriately (optional)
        }
    }

    private fun copyTextToClipboard(text: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("", text)
        clipboardManager.setPrimaryClip(clipData)
      //  Common.getToast(this@ActReferEarn,text)
    }

}