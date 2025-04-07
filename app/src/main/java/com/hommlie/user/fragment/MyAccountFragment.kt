package com.hommlie.user.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.hommlie.user.BuildConfig
import com.hommlie.user.R
import com.hommlie.user.activity.*
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseFragment
import com.hommlie.user.databinding.FragAccountBinding
import com.hommlie.user.model.ContactInfo
import com.hommlie.user.model.GetProfileResponse
import com.hommlie.user.model.UserData
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.getCurrentLanguage
import com.hommlie.user.utils.OnNearByServicesClickListener
import com.hommlie.user.utils.SharePreference
import com.hommlie.user.utils.UserProfileDataManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyAccountFragment : BaseFragment<FragAccountBinding>() {
    private lateinit var accountBinding: FragAccountBinding
    var dataResponse: UserData? = null
    var contactData = ContactInfo()

    lateinit var  clickListener: OnNearByServicesClickListener

    @SuppressLint("SetTextI18n")
    override fun initView(view: View) {
        accountBinding = FragAccountBinding.bind(view)

        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.white)


        if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
            if (Common.isCheckNetwork(requireActivity())) {
              //  val hasmap = HashMap<String, String>()
              //  hasmap["user_id"] = SharePreference.getStringPref(requireActivity(), SharePreference.userId)!!
             //   callApiProfile(hasmap)
            } else {
                Common.alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.no_internet)
                )
            }
        }
        accountBinding.clEditPaymentDetails.setOnClickListener {
            if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
                onActivityResult.launch(Intent(requireActivity(), ActEditProfile::class.java))
            } else {
                openActivity(ActSignUp::class.java)
                requireActivity().finish()
                requireActivity().finishAffinity()
            }
        }

        accountBinding.clAddress.setOnClickListener{
            val intent =Intent(requireActivity(),ActAddress::class.java)
            startActivity(intent)
        }

        if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
            accountBinding.linearSocialMedia.visibility = View.GONE
        } else {
            accountBinding.linearSocialMedia.visibility = View.GONE
        }
        accountBinding.btnLogout.setOnClickListener { alertLogOutDialog() }
        accountBinding.clSetting.setOnClickListener {
            if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
                openActivity(ActSettings::class.java)
            } else {
                openActivity(ActSignUp::class.java)
                requireActivity().finish()
                requireActivity().finishAffinity()
            }
        }

        accountBinding.clRefer.setOnClickListener {
            val intent =Intent(requireActivity(),ActFetchingLocation::class.java)
            startActivity(intent)
        }

//        accountBinding.llMyorder.setOnClickListener {
//            clickListener.activateOrder()
//        }

        accountBinding.clCareer.setOnClickListener {
            val url = "https://techmonkshub.info/hommlie/careers"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
            }
            startActivity(intent)
        }

        if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
            setProfileData()
        } else if (!SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
            accountBinding.tvUsername.text = "Guest"
            accountBinding.tvEmail.text = "guest@gmail.com"
            accountBinding.ivProfile.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.profile, null))

            accountBinding.btnLogout.visibility = View.GONE
            accountBinding.clDeleteUser.visibility=View.GONE
        } else {
            openActivity(ActSignUp::class.java)
            requireActivity().finish()
            requireActivity().finishAffinity()
        }
        accountBinding.clPrivacypolicy.setOnClickListener {
            startActivity(
                Intent(activity, ActProvacyPolicy::class.java).putExtra(
                    "Type",
                    "Privacy Policy"
                )
            )
        }

        accountBinding.clTemsandcondition.setOnClickListener {
            startActivity(
                Intent(activity, ActProvacyPolicy::class.java).putExtra(
                    "Type",
                    "Terms & Condition"
                )
            )
        }
        accountBinding.clRefundandcancelation.setOnClickListener {
            startActivity(
                Intent(activity, ActProvacyPolicy::class.java).putExtra(
                    "Type",
                    "Refund Policy"
                )
            )
        }

        accountBinding.clAboutus.setOnClickListener {
            startActivity(
                Intent(activity, ActProvacyPolicy::class.java).putExtra(
                    "Type",
                    "About Hommlie"
                )
            )
        }

        accountBinding.clWallet.setOnClickListener {
            val intent = Intent(activity,ActWallet::class.java)
            startActivity(intent)
        }

        accountBinding.clHelp.setOnClickListener {
            if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
                val intent = Intent(requireActivity(), ActHelpContactUs::class.java)
                val bundle = Bundle()
                bundle.putSerializable("contact", contactData)
                intent.putExtras(bundle)
                startActivity(intent)
            } else {
                openActivity(ActSignUp::class.java)
                requireActivity().finish()
                requireActivity().finishAffinity()
            }
        }

        accountBinding.ivInstagram.setOnClickListener {
            val webpage: Uri = Uri.parse(contactData.instagram)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }

        accountBinding.ivLinkedIn.setOnClickListener {
            val webpage: Uri = Uri.parse(contactData.linkedin)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }
        accountBinding.ivFaceBook.setOnClickListener {
            val webpage: Uri = Uri.parse(contactData.facebook)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }

        accountBinding.ivTwitter.setOnClickListener {
            val webpage: Uri = Uri.parse(contactData.twitter)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }

        accountBinding.clDeleteUser.setOnClickListener {

            val hashmap = HashMap<String, String>()
            hashmap["user_id"] =
                SharePreference.getStringPref(requireActivity(), SharePreference.userId) ?: ""

            alertLogOutDialog(hashmap)

        }


        val versionCode: Int = BuildConfig.VERSION_CODE
        val versionName: String = BuildConfig.VERSION_NAME
        accountBinding.tvOwner.text = "App version " + versionName + " (" + versionCode + ")"
    }

    override fun getBinding(): FragAccountBinding {
        accountBinding = FragAccountBinding.inflate(layoutInflater)
        return accountBinding
    }

    //TODO USER LOGOUT DIALOG AND LOGOUT
    private fun alertLogOutDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.log_out)
        builder.setMessage(R.string.logout_text)
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            Common.setLogout(requireActivity())
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    private fun alertLogOutDialog(hasmap: HashMap<String, String>) {
        val builder = AlertDialog.Builder(requireActivity())
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
        Common.showLoadingProgress(requireActivity())
        val call = ApiClient.getClient.deleteAccount(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    Common.dismissLoadingProgress()
                    if (response.body()?.status == 1) {
                        Common.showErrorFullMsg(requireActivity(),response.body()?.message.toString())
                        openActivity(ActSignUp::class.java)
                        //finish()
                        // finishAffinity()
                    } else {
                        Common.showErrorFullMsg(requireActivity(),response.body()?.message.toString())
                    }
                } else {
                    Common.dismissLoadingProgress()
                    Common.alertErrorOrValidationDialog(
                        requireActivity(),
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO API PROFILE CALL
    private fun callApiProfile(hasmap: HashMap<String, String>) {
        Common.showLoadingProgress(requireActivity())
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
                            requireActivity(),
                            SharePreference.userName,
                            dataResponse!!.name!!
                        )
                        SharePreference.setStringPref(
                            requireActivity(),
                            SharePreference.userProfile,
                            dataResponse!!.profilePic!!
                        )
                        if (dataResponse!!.email!=""){
                        SharePreference.setStringPref(
                            requireActivity(),
                            SharePreference.userEmail,
                            dataResponse!!.email!!
                        )}
                        setProfileData()
                    } else if (restResponce.data!!.equals("0")) {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponce.message
                        )
                    }
                } else {
                    val error = JSONObject(response.errorBody()!!.string())
                    if (error.getString("status").equals("2")) {
                        Common.dismissLoadingProgress()
                        Common.setLogout(requireActivity())
                    } else {
                        Common.dismissLoadingProgress()
                        Common.alertErrorOrValidationDialog(
                            requireActivity(),
                            error.getString("message")
                        )
                    }
                }
            }

            override fun onFailure(call: Call<GetProfileResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO SET PROFILE DATA
    private fun setProfileData(
    ) {
        if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
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
                accountBinding.tvUsername.text=UserProfileDataManager.getUserProfileData()?.name ?:""
            }
            accountBinding.tvEmail.text=UserProfileDataManager.getUserProfileData()?.mobile ?:""

            Glide.with(requireActivity()).load(UserProfileDataManager.getUserProfileData()?.profilePic).placeholder(R.drawable.profile).into(accountBinding.ivProfile)

        } else {
            accountBinding.ivProfile.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.hommlie_home, null))
            accountBinding.tvUsername.text="Guest"
            accountBinding.tvEmail.text="guest@gmail.com"
        }
    }

    private var onActivityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            if (Common.isCheckNetwork(requireActivity())) {
//                val hasmap = HashMap<String, String>()
//                hasmap["user_id"] = SharePreference.getStringPref(requireActivity(), SharePreference.userId)!!
//                 callApiProfile(hasmap)
            } else {
                Common.alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.no_internet)
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(requireActivity(), false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNearByServicesClickListener) {
            clickListener = context
        } else {
            throw RuntimeException("$context must implement OnNearByServicesClickListener")
        }
    }

}