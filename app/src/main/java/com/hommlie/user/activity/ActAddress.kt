package com.hommlie.user.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.databinding.ActAddressBinding
import com.hommlie.user.databinding.RemoveItemDialogBinding
import com.hommlie.user.databinding.RowAddressBinding
import com.hommlie.user.model.*
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.alertErrorOrValidationDialog
import com.hommlie.user.utils.Common.dismissLoadingProgress
import com.hommlie.user.utils.Common.isCheckNetwork
import com.hommlie.user.utils.Common.showErrorFullMsg
import com.hommlie.user.utils.Common.showLoadingProgress
import com.hommlie.user.utils.Common.showSuccessFullMsg
import com.hommlie.user.utils.SharePreference
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActAddress : BaseActivity() {
    private lateinit var addressBinding: ActAddressBinding
    private var addressDataList: ArrayList<AddressData>? = null
    private var addressDataAdapter: BaseAdaptor<AddressData, RowAddressBinding>? =
        null
    override fun setLayout(): View = addressBinding.root

    override fun initView() {
        addressBinding = ActAddressBinding.inflate(layoutInflater)

        addressBinding.ivAdd.setOnClickListener { openActivity(ActAddAddress::class.java) }
        addressBinding.ivBack.setOnClickListener {
            finish()
        }

        addressBinding.tvNoDataFound.setOnClickListener {
            val intent = Intent(this@ActAddress,ActAddAddress::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in,R.anim.no_animation)
        }

    }

    //TODO API ADDRESS GET API
    private fun callApiAddress() {
        showLoadingProgress(this@ActAddress)
        val hasmap = java.util.HashMap<String, String>()
        hasmap["user_id"] = SharePreference.getStringPref(this@ActAddress, SharePreference.userId).toString()

        val call = ApiClient.getClient.getAddress(hasmap)
        call.enqueue(object : Callback<GetAddressResponse> {
            override fun onResponse(
                call: Call<GetAddressResponse>,
                response: Response<GetAddressResponse>
            ) {
                if (response.isSuccessful) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        dismissLoadingProgress()
                        addressBinding.rvaddress.visibility = View.VISIBLE
                        addressBinding.tvNoDataFound.visibility = View.GONE
                        addressDataList = restResponce.data

                        setAddressData(addressDataList!!)

                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        addressBinding.rvaddress.visibility = View.GONE
                        addressBinding.tvNoDataFound.visibility = View.VISIBLE
                        alertErrorOrValidationDialog(
                            this@ActAddress,
                            restResponce.message.toString()
                        )
                    }
                }else{
                    Common.dismissLoadingProgress()
                    when (response.code()) {
                        404 -> {
                            Common.alertErrorOrValidationDialog(this@ActAddress, "Resource not found.")
                        }
                        500 -> {
                            Common.alertErrorOrValidationDialog(this@ActAddress, "Server error. Please try again later.")
                        }
                        else -> {
                            Common.alertErrorOrValidationDialog(this@ActAddress, "Unexpected error: ${response.code()}")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<GetAddressResponse>, t: Throwable) {
                dismissLoadingProgress()
                val errorMessage = when (t) {
                    is java.net.UnknownHostException -> "No internet connection. Please check your network."
                    is java.net.SocketTimeoutException -> "Request timed out. Please try again."
                    is java.io.IOException -> "Network error occurred. Please try again."
                    else -> "Something went wrong. Please try again."
                }

                // Display the error message
                Common.alertErrorOrValidationDialog(this@ActAddress, errorMessage)

                // Optionally, log the error for debugging purposes
                Log.e("API_ERROR", "onFailure: ${t.localizedMessage}", t)
            }
        })
    }

    //TODO SET ADDRESS DATA, EDIT AND DETELE ADDRESS
    private fun setAddressData(addressDataList: ArrayList<AddressData>) {
        lateinit var binding: RowAddressBinding
        addressDataAdapter =
            object : BaseAdaptor<AddressData, RowAddressBinding>(
                this@ActAddress,
                addressDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: AddressData,
                    position: Int
                ) {

                    binding.tvUserName.text =
                        addressDataList[position].firstName
                    binding.tvareaaddress.text =
                        addressDataList[position].streetAddress + ", " + addressDataList[position].landmark + " - " + addressDataList[position].pincode
                    binding.tvUserphone.text = addressDataList[position].mobile
                    binding.tvusermailid.text = addressDataList[position].email
                    binding.tvDelete.setOnClickListener {
                        if (isCheckNetwork(this@ActAddress)) {
                            removeAddressDialog(addressDataList[position].id.toString(),
                                position)
                        } else {
                            alertErrorOrValidationDialog(
                                this@ActAddress,
                                resources.getString(R.string.no_internet)
                            )
                        }
                    }
                    binding.tvEdit.setOnClickListener {
                        val intent = Intent(this@ActAddress, ActAddAddress::class.java)
                        intent.putExtra("FirstName", addressDataList[position].firstName)
                     //   intent.putExtra("LastName", addressDataList[position].lastName)
                        intent.putExtra("StreetAddress", addressDataList[position].streetAddress)
                        intent.putExtra("Landmark", addressDataList[position].landmark)
                        intent.putExtra("Pincode", addressDataList[position].pincode)
                        intent.putExtra("Mobile", addressDataList[position].mobile)
                        intent.putExtra("Email", addressDataList[position].email)
                        intent.putExtra("address_id", addressDataList[position].id)
                        intent.putExtra("Type", 1)
                        startActivity(intent)
                    }
                    val isComeFromSelectAddress = intent.getBooleanExtra("isComeFromSelectAddress", false)
                    binding.swipe.surfaceView.setOnClickListener {
                        if (isComeFromSelectAddress) {
                            val intent = Intent(this@ActAddress, ActCheckout::class.java)
                            intent.putExtra("FirstName", addressDataList[position].firstName)
                         //   intent.putExtra("LastName", addressDataList[position].lastName)
                            intent.putExtra(
                                "StreetAddress",
                                addressDataList[position].streetAddress
                            )
                            intent.putExtra("Landmark", addressDataList[position].landmark)
                            intent.putExtra("Pincode", addressDataList[position].pincode)
                            intent.putExtra("Mobile", addressDataList[position].mobile)
                            intent.putExtra("Email", addressDataList[position].email)
                            intent.putExtra("latitude",addressDataList[position].latitude)
                            intent.putExtra("longitude",addressDataList[position].longitude)
                            intent.putExtra("address_id", addressDataList[position].id)
                            intent.putExtra("Type", 1)
                            setResult(500, intent)
                            finish()
                        }
                    }
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_address
                }

                override fun getBinding(parent: ViewGroup): RowAddressBinding {
                    binding = RowAddressBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }

        addressBinding.rvaddress.apply {
            if (addressDataList.size > 0) {
                addressBinding.rvaddress.visibility = View.VISIBLE
                addressBinding.tvNoDataFound.visibility = View.GONE
                layoutManager =
                    LinearLayoutManager(this@ActAddress, LinearLayoutManager.VERTICAL, false)
                itemAnimator = DefaultItemAnimator()
                adapter = addressDataAdapter
            } else {
                addressBinding.rvaddress.visibility = View.GONE
                addressBinding.tvNoDataFound.visibility = View.VISIBLE
            }
        }
    }

    //TODO ADDRESS DETELE DIALOG
    @SuppressLint("InflateParams")
    fun dlgDeleteConformationDialog(act: Activity, msg: String?, strCartId: String, pos: Int) {
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
            val mView = mInflater.inflate(R.layout.dlg_confomation, null, false)
            val textDesc: TextView = mView.findViewById(R.id.tvDesc)
            textDesc.text = msg
            val tvOk: TextView = mView.findViewById(R.id.tvYes)
            val finalDialog: Dialog = dialog
            tvOk.setOnClickListener {
                if (isCheckNetwork(this@ActAddress)) {
                    finalDialog.dismiss()
                    val hashMap = HashMap<String, String>()

                    hashMap["user_id"] = SharePreference.getStringPref(
                        this@ActAddress,
                        SharePreference.userId
                    ).toString()
                    hashMap["address_id"] = strCartId
                    callDeleteApi(hashMap, pos)
                } else {
                    alertErrorOrValidationDialog(
                        this@ActAddress,
                        resources.getString(R.string.no_internet)
                    )
                }
            }
            val tvCancle: TextView = mView.findViewById(R.id.tvNo)
            tvCancle.setOnClickListener {
                finalDialog.dismiss()
            }
            dialog.setContentView(mView)
            dialog.show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    //TODO API ADDRESS DELETE CALL
    private fun callDeleteApi(deleteRequest: HashMap<String, String>, pos: Int) {
        showLoadingProgress(this@ActAddress)
        val call = ApiClient.getClient.deleteAddress(deleteRequest)
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.status == 1) {

                        showSuccessFullMsg(
                            this@ActAddress,
                            response.body()?.message.toString()
                        )
                        addressDataList?.removeAt(pos)

                        addressDataAdapter?.notifyDataSetChanged()

                        if (addressDataList?.size ?: 0 > 0) {
                            addressBinding.tvNoDataFound.visibility = View.GONE
                            addressBinding.rvaddress.visibility = View.VISIBLE
                        } else {
                            addressBinding.tvNoDataFound.visibility = View.VISIBLE
                            addressBinding.rvaddress.visibility = View.GONE
                        }
                    } else {
                        showErrorFullMsg(
                            this@ActAddress,
                            response.body()?.message.toString()
                        )
                    }
                } else {
                    when (response.code()) {
                        404 -> {
                            Common.alertErrorOrValidationDialog(this@ActAddress, "Resource not found.")
                        }
                        500 -> {
                            Common.alertErrorOrValidationDialog(this@ActAddress, "Server error. Please try again later.")
                        }
                        else -> {
                            Common.alertErrorOrValidationDialog(this@ActAddress, "Unexpected error: ${response.code()}")
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                val errorMessage = when (t) {
                    is java.net.UnknownHostException -> "No internet connection. Please check your network."
                    is java.net.SocketTimeoutException -> "Request timed out. Please try again."
                    is java.io.IOException -> "Network error occurred. Please try again."
                    else -> "Something went wrong. Please try again."
                }

                // Display the error message
                Common.alertErrorOrValidationDialog(this@ActAddress, errorMessage)

                // Optionally, log the error for debugging purposes
                Log.e("API_ERROR", "onFailure: ${t.localizedMessage}", t)
            }
        })
    }

    fun removeAddressDialog(addressId: String, pos: Int) {
        val removeDialogBinding = RemoveItemDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this@ActAddress)
        dialog.setContentView(removeDialogBinding.root)
        removeDialogBinding.tvRemoveTitle.text = resources.getString(R.string.remove_address)
        removeDialogBinding.tvAlertMessage.text = resources.getString(R.string.remove_address_desc)
        removeDialogBinding.btnProceed.setOnClickListener {
            if (isCheckNetwork(this@ActAddress)) {
                dialog.dismiss()
                val hashMap = HashMap<String, String>()
                hashMap["user_id"] = SharePreference.getStringPref(
                    this@ActAddress,
                    SharePreference.userId
                ).toString()
                hashMap["address_id"] = addressId
                callDeleteApi(hashMap, pos)

            } else {
                alertErrorOrValidationDialog(
                    this@ActAddress,
                    resources.getString(R.string.no_internet)
                )
            }
        }

        removeDialogBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        if (SharePreference.getBooleanPref(this@ActAddress, SharePreference.isLogin)) {
            if (isCheckNetwork(this@ActAddress)) {
                callApiAddress()
            } else {
                alertErrorOrValidationDialog(
                    this@ActAddress,
                    resources.getString(R.string.no_internet)
                )
            }
        } else {
            openActivity(ActSignUp::class.java)
            this@ActAddress.finish()
            this@ActAddress.finishAffinity()
        }
        Common.getCurrentLanguage(this@ActAddress, false)
    }
}