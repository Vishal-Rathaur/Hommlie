package com.hommlie.user.activity

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.hommlie.user.BuildConfig
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActivityActDuplicateAccountBinding
import com.hommlie.user.model.ContactInfo
import com.hommlie.user.model.GetProfileResponse
import com.hommlie.user.model.UserData
import com.hommlie.user.model.UserProfileData
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.dismissLoadingProgress
import com.hommlie.user.utils.Common.getCurrentLanguage
import com.hommlie.user.utils.OnNearByServicesClickListener
import com.hommlie.user.utils.SharePreference
import com.hommlie.user.utils.UserProfileDataManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActDuplicateAccount : BaseActivity() {

    private lateinit var accountBinding:ActivityActDuplicateAccountBinding

    var dataResponse: UserData? = null
    var contactData = ContactInfo()


    override fun setLayout(): View =accountBinding.root

    override fun initView() {
        accountBinding = ActivityActDuplicateAccountBinding.inflate(layoutInflater)

        if (SharePreference.getBooleanPref(this@ActDuplicateAccount, SharePreference.isLogin)) {
            if (Common.isCheckNetwork(this@ActDuplicateAccount)) {
                //  val hasmap = HashMap<String, String>()
                //  hasmap["user_id"] = SharePreference.getStringPref(requireActivity(), SharePreference.userId)!!
                //   callApiProfile(hasmap)
            } else {
                Common.alertErrorOrValidationDialog(
                    this@ActDuplicateAccount,
                    resources.getString(R.string.no_internet)
                )
            }
        }

        accountBinding.clEditPaymentDetails.setOnClickListener {
            if (SharePreference.getBooleanPref(this@ActDuplicateAccount, SharePreference.isLogin)) {
                onActivityResult.launch(Intent(this, ActEditProfile::class.java))
            } else {
                openActivity(ActSignUp::class.java)
            finish()
                finishAffinity()
            }
        }

        accountBinding.clVendorDetails.setOnClickListener {
            if (SharePreference.getBooleanPref(this@ActDuplicateAccount, SharePreference.isLogin)) {
                onActivityResult.launch(Intent(this, ActEditProfile::class.java))
            } else {
                openActivity(ActSignUp::class.java)
                finish()
                finishAffinity()
            }
        }

        accountBinding.clAddress.setOnClickListener{
            val intent = Intent(this@ActDuplicateAccount,ActAddress::class.java)
            startActivity(intent)
        }

        if (SharePreference.getBooleanPref(this@ActDuplicateAccount, SharePreference.isLogin)) {
            accountBinding.linearSocialMedia.visibility = View.GONE
        } else {
            accountBinding.linearSocialMedia.visibility = View.GONE
        }
        accountBinding.btnLogout.setOnClickListener { alertLogOutDialog() }
        accountBinding.clSetting.setOnClickListener {
            if (SharePreference.getBooleanPref(this@ActDuplicateAccount, SharePreference.isLogin)) {
                openActivity(ActSettings::class.java)
            } else {
                openActivity(ActSignUp::class.java)
             finish()
               finishAffinity()
            }
        }

        val referImage = SharePreference.getStringPref(this@ActDuplicateAccount,SharePreference.referCardImage).toString()

        Glide.with(this@ActDuplicateAccount).load(referImage).placeholder(R.drawable.ic_placeholder).into(accountBinding.ivRefer)

        accountBinding.cardRefer.setOnClickListener {
            val intent = Intent(this@ActDuplicateAccount,ActReferEarn::class.java)
            startActivity(intent)
        }
        accountBinding.cardWallet.setOnClickListener {
            val intent = Intent(this@ActDuplicateAccount,ActWallet::class.java)
            startActivity(intent)
        }

        accountBinding.llMyorder.setOnClickListener {
            val intent = Intent(this@ActDuplicateAccount,ActMain::class.java)
            intent.putExtra("pos","4")
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.no_animation,R.anim.slide_out)
        }

        accountBinding.ivBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.no_animation,R.anim.slide_out)
        }

        accountBinding.clCareer.setOnClickListener {
            val url = "https://www.hommlie.com/careers"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(intent)
        }

//        if (SharePreference.getBooleanPref(this@ActDuplicateAccount, SharePreference.isLogin)) {
//            setProfileData()
//        } else if (!SharePreference.getBooleanPref(this@ActDuplicateAccount, SharePreference.isLogin)) {
//            accountBinding.tvUsername.text = "Guest"
//            accountBinding.tvEmail.text = "guest@gmail.com"
//            accountBinding.ivProfile.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.profile, null))
//
//            accountBinding.btnLogout.visibility = View.GONE
//            accountBinding.clDeleteUser.visibility=View.GONE
//        } else {
//            openActivity(ActSignUp::class.java)
//            finish()
//            finishAffinity()
//        }
        accountBinding.clPrivacypolicy.setOnClickListener {
            startActivity(
                Intent(this@ActDuplicateAccount, ActProvacyPolicy::class.java).putExtra(
                    "Type",
                    "Privacy Policy"
                )
            )
        }

        accountBinding.clTemsandcondition.setOnClickListener {
            startActivity(
                Intent(this@ActDuplicateAccount, ActProvacyPolicy::class.java).putExtra(
                    "Type",
                    "Terms & Condition"
                )
            )
        }
        accountBinding.clRefundandcancelation.setOnClickListener {
            startActivity(
                Intent(this@ActDuplicateAccount, ActProvacyPolicy::class.java).putExtra(
                    "Type",
                    "Refund Policy"
                )
            )
        }

        accountBinding.clAboutus.setOnClickListener {
            startActivity(
                Intent(this@ActDuplicateAccount, ActProvacyPolicy::class.java).putExtra(
                    "Type",
                    "About Hommlie"
                )
            )
        }

        accountBinding.clWallet.setOnClickListener {
            val intent = Intent(this@ActDuplicateAccount,ActWallet::class.java)
            startActivity(intent)
        }

        accountBinding.clHelp.setOnClickListener {
            if (SharePreference.getBooleanPref(this@ActDuplicateAccount, SharePreference.isLogin)) {
                val intent = Intent(this@ActDuplicateAccount, ActHelpContactUs::class.java)
                val bundle = Bundle()
                bundle.putSerializable("contact", contactData)
                intent.putExtras(bundle)
                startActivity(intent)
            } else {
                openActivity(ActSignUp::class.java)
                finish()
                finishAffinity()
            }
        }

        accountBinding.ivInstagram.setOnClickListener {
            val webpage: Uri = Uri.parse(contactData.instagram)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(this@ActDuplicateAccount.packageManager) != null) {
                startActivity(intent)
            }
        }

        accountBinding.ivLinkedIn.setOnClickListener {
            val webpage: Uri = Uri.parse(contactData.linkedin)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(this@ActDuplicateAccount.packageManager) != null) {
                startActivity(intent)
            }
        }
        accountBinding.ivFaceBook.setOnClickListener {
            val webpage: Uri = Uri.parse(contactData.facebook)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(this@ActDuplicateAccount.packageManager) != null) {
                startActivity(intent)
            }
        }

        accountBinding.ivTwitter.setOnClickListener {
            val webpage: Uri = Uri.parse(contactData.twitter)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(this@ActDuplicateAccount.packageManager) != null) {
                startActivity(intent)
            }
        }

        accountBinding.clDeleteUser.setOnClickListener {

            val hashmap = HashMap<String, String>()
            hashmap["user_id"] =
                SharePreference.getStringPref(this@ActDuplicateAccount, SharePreference.userId) ?: ""

            alertLogOutDialog(hashmap)

        }


        val versionCode: Int = BuildConfig.VERSION_CODE
        val versionName: String = BuildConfig.VERSION_NAME
        accountBinding.tvOwner.text = "App version " + versionName + " (" + versionCode + ")"

    }


    //TODO USER LOGOUT DIALOG AND LOGOUT
    private fun alertLogOutDialog() {
        val builder = AlertDialog.Builder(this@ActDuplicateAccount)
        builder.setTitle(R.string.log_out)
        builder.setMessage(R.string.logout_text)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            Common.setLogout(this@ActDuplicateAccount)
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    private fun alertLogOutDialog(hasmap: HashMap<String, String>) {
        val builder = AlertDialog.Builder(this@ActDuplicateAccount)
        builder.setTitle(R.string.delete_text)
        builder.setMessage(R.string.delete_account)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            deleteAccount(hasmap)
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    private fun deleteAccount(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActDuplicateAccount)
        val call = ApiClient.getClient.deleteAccount(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    if (response.body()?.status == 1) {
                        Common.showErrorFullMsg(this@ActDuplicateAccount,response.body()?.message.toString())
                        openActivity(ActSignUp::class.java)
                        //finish()
                        // finishAffinity()
                    } else {
                        Common.showErrorFullMsg(this@ActDuplicateAccount,response.body()?.message.toString())
                    }
                } else {
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                        this@ActDuplicateAccount,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActDuplicateAccount,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO API PROFILE CALL
    private fun callApiProfile(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(this@ActDuplicateAccount)
        val call = ApiClient.getClient.getProfile(hasmap)
        call.enqueue(object : Callback<GetProfileResponse> {
            override fun onResponse(
                call: Call<GetProfileResponse>,
                response: Response<GetProfileResponse>
            ) {
                if (response.code() == 200) {
                    val restResponce: GetProfileResponse = response.body()!!
                    if (restResponce.status == 1) {
                        Common.dismissLoadingProgress()
                        dataResponse = restResponce.data!!
//                        contactData = restResponce.contactinfo!!
                        SharePreference.setStringPref(
                            this@ActDuplicateAccount,
                            SharePreference.userName,
                            dataResponse!!.name!!
                        )
                        SharePreference.setStringPref(
                            this@ActDuplicateAccount,
                            SharePreference.userProfile,
                            dataResponse!!.profilePic!!
                        )
                        if (dataResponse!!.email!=""){
                            SharePreference.setStringPref(
                                this@ActDuplicateAccount,
                                SharePreference.userEmail,
                                dataResponse!!.email!!
                            )}
                        setProfileData()
                    } else if (restResponce.data!!.equals("0")) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActDuplicateAccount,
                            restResponce.message
                        )
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    if (error.getString("status").equals("2")) {
                        Common.dismissLoadingProgress()
                        Common.setLogout(this@ActDuplicateAccount)
                    } else {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            this@ActDuplicateAccount,
                            error.getString("message")
                        )
                    }
                }
            }

            override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    this@ActDuplicateAccount,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SET PROFILE DATA
    private fun setProfileData(
    ) {
        if (SharePreference.getBooleanPref(this@ActDuplicateAccount, SharePreference.isLogin)) {
//            accountBinding.tvUsername.text =
//                SharePreference.getStringPref(requireActivity(), SharePreference.userName)
//            accountBinding.tvEmail.text =
//                SharePreference.getStringPref(requireActivity(), SharePreference.userEmail)
//            Glide.with(requireActivity()).load(SharePreference.getStringPref(requireActivity(), SharePreference.userProfile)
//            ).placeholder(ResourcesCompat.getDrawable(resources, R.drawable.profile, null))
//                .into(accountBinding.ivProfile)

            if (UserProfileDataManager.getUserProfileData()?.name.toString().isEmpty()){
                accountBinding.tvUsername.text="Complete your profile"
            }else{
                accountBinding.tvUsername.text= UserProfileDataManager.getUserProfileData()?.name ?:""
            }
            accountBinding.tvMobile.text= UserProfileDataManager.getUserProfileData()?.mobile ?:""
            accountBinding.tvEmail.text= UserProfileDataManager.getUserProfileData()?.email ?:""

            Glide.with(this@ActDuplicateAccount).load(UserProfileDataManager.getUserProfileData()?.profilePic).placeholder(R.drawable.profile).into(accountBinding.ivProfile)

        } else {
            accountBinding.ivProfile.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.hommlie_home, null))
            accountBinding.tvUsername.text="Guest"
            accountBinding.tvEmail.text="guest@gmail.com"
            accountBinding.tvMobile.visibility=View.GONE
        }
    }

    private var onActivityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            if (Common.isCheckNetwork(this@ActDuplicateAccount)) {
//                val hasmap = HashMap<String, String>()
//                hasmap["user_id"] = SharePreference.getStringPref(requireActivity(), SharePreference.userId)!!
//                 callApiProfile(hasmap)
            } else {
                Common.alertErrorOrValidationDialog(
                    this@ActDuplicateAccount,
                    resources.getString(R.string.no_internet)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (SharePreference.getBooleanPref(this@ActDuplicateAccount, SharePreference.isLogin)) {
            getUserProfile()
            accountBinding.tvCurrentBalance.setText("\u20b9 "+SharePreference.getDoublePref(this@ActDuplicateAccount,SharePreference.wallet).toString())
        } else if (!SharePreference.getBooleanPref(this@ActDuplicateAccount, SharePreference.isLogin)) {
            accountBinding.tvUsername.text = "Guest"
            accountBinding.tvEmail.text = "guest@gmail.com"
            accountBinding.ivProfile.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.profile, null))

            accountBinding.btnLogout.visibility = View.GONE
            accountBinding.clDeleteUser.visibility=View.GONE
        } else {
            openActivity(ActSignUp::class.java)
            finish()
            finishAffinity()
        }
        getCurrentLanguage(this@ActDuplicateAccount, false)
    }


    private fun getUserProfile(){
        Common.showLoadingProgress(this@ActDuplicateAccount)
        val hashMap=HashMap<String,String>()
        hashMap["user_id"]=SharePreference.getStringPref(this@ActDuplicateAccount,SharePreference.userId).toString()

        val call= ApiClient.getClient.getUserProfile(hashMap)

        call.enqueue(object : Callback<UserProfileData>{
            override fun onResponse(call: Call<UserProfileData>, response: Response<UserProfileData>) {
                if (response.isSuccessful){
                    val restResponse = response.body()
                    if (restResponse != null) {
                        if (restResponse.status==1){
                            dismissLoadingProgress()
                            UserProfileDataManager.setUserProfileData(restResponse.data_user)
                            setProfileData()
                        }else{
                            dismissLoadingProgress()
                           // Common.showErrorFullMsg(requireActivity(),restResponse.message)
                        }
                    }
                }else{
                    dismissLoadingProgress()
                   // Common.showErrorFullMsg(this@ActDuplicateAccount, response.body()?.message.toString())
                }
            }

            override fun onFailure(call: Call<UserProfileData>, t: Throwable) {
                dismissLoadingProgress()

            }

        })
    }

}