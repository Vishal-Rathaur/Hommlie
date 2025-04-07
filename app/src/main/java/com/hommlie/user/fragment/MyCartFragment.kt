package com.hommlie.user.fragment

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hommlie.user.R
import com.hommlie.user.activity.ActCheckout
import com.hommlie.user.activity.ActProductDetails
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.base.BaseFragment
import com.hommlie.user.databinding.FragMyCartBinding
import com.hommlie.user.databinding.RemoveItemDialogBinding
import com.hommlie.user.databinding.RowMycartBinding
import com.hommlie.user.model.CartDataItem
import com.hommlie.user.model.GetCartResponse
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.alertErrorOrValidationDialog
import com.hommlie.user.utils.Common.dismissLoadingProgress
import com.hommlie.user.utils.Common.filterHttps
import com.hommlie.user.utils.Common.getCurrentLanguage
import com.hommlie.user.utils.Common.isCheckNetwork
import com.hommlie.user.utils.Common.showErrorFullMsg
import com.hommlie.user.utils.Common.showLoadingProgress
import com.hommlie.user.utils.SharePreference
import com.hommlie.user.utils.SharePreference.Companion.getStringPref
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hommlie.user.activity.ActMain
import com.hommlie.user.activity.ActSignUp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MyCartFragment : BaseFragment<FragMyCartBinding>() {
    private lateinit var fragMyCartBinding: FragMyCartBinding
    private var cartDataList: ArrayList<CartDataItem>? = null
    var currency: String = ""
    var currencyPosition: String = ""
    var count=0
    private var actMain: ActMain? = null
    private var cartListDataAdapter: BaseAdaptor<CartDataItem, RowMycartBinding>? =
        null
    var colorArray = arrayOf(
        "#FDF7FF",
        "#FDF3F0",
        "#EDF7FD",
        "#FFFAEA",
        "#F1FFF6",
        "#FFF5EC"
    )


    override fun initView(view: View) {
        fragMyCartBinding = FragMyCartBinding.bind(view)

     //   retainInstance = true

        requireActivity().window.decorView.systemUiVisibility = 0
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)

        fragMyCartBinding.rvMycard.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var lastScrollY = 0
            private var isMenuVisible = true

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

               // val activity = activity as? ActMain
                // Check if the user is scrolling down or up
                if (dy > 0) {
                    // User is scrolling down
                    if (isMenuVisible) {
                        actMain?.hideMenu()
                        isMenuVisible = false
                        adjustBottomMargin(fragMyCartBinding.btncheckout, resources.getDimensionPixelSize(R.dimen._5sdp)) // Replace with desired margin
                    }
                } else if (dy < 0) {
                    // User is scrolling up
                    if (!isMenuVisible) {
                        actMain?.showMenu()
                        isMenuVisible = true
                        adjustBottomMargin(fragMyCartBinding.btncheckout, resources.getDimensionPixelSize(R.dimen._45sdp))
                    }
                }

                lastScrollY += dy
            }
        })


        currency = getStringPref(requireActivity(), SharePreference.Currency)!!
        currencyPosition = getStringPref(requireActivity(), SharePreference.CurrencyPosition)!!

//        if (isCheckNetwork(requireActivity())) {
//            if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
//                callApiCartData(false)
//            } else {
//                openActivity(ActSignUp::class.java)
//                requireActivity().finish()
//            }
//        } else {
//            alertErrorOrValidationDialog(
//                requireActivity(),
//                resources.getString(R.string.no_internet)
//            )
//        }
    }

    override fun getBinding(): FragMyCartBinding {
        fragMyCartBinding = FragMyCartBinding.inflate(layoutInflater)
        fragMyCartBinding.btncheckout.setOnClickListener { openActivity(ActCheckout::class.java) }
        return fragMyCartBinding
    }


    fun adjustBottomMargin(view: View, targetMargin: Int, duration: Long = 300L) {
        val layoutParams = view.layoutParams as ConstraintLayout.LayoutParams
        val currentMargin = layoutParams.bottomMargin

        ValueAnimator.ofInt(currentMargin, targetMargin).apply {
            this.duration = duration
            addUpdateListener { animator ->
                val animatedValue = animator.animatedValue as Int
                layoutParams.bottomMargin = animatedValue
                view.layoutParams = layoutParams
            }
            start()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActMain) {
            actMain = context
        }
    }


    //TODO API CART CALL
    private fun callApiCartData(isQty: Boolean) {
        if (!isQty) {
            showLoadingProgress(requireActivity())
        }
        val hasmap = HashMap<String, String>()
        hasmap["user_id"] =
            getStringPref(requireActivity(), SharePreference.userId)!!
        val call = ApiClient.getClient.getCartData(hasmap)
        call.enqueue(object : Callback<GetCartResponse> {
            override fun onResponse(
                call: Call<GetCartResponse>,
                response: Response<GetCartResponse>
            ) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    val restResponce = response.body()!!
                    if (restResponce.status == 1) {
                        dismissLoadingProgress()
                        fragMyCartBinding.rvMycard.visibility = View.VISIBLE
                        fragMyCartBinding.tvNoDataFound.visibility = View.GONE
                        cartDataList = restResponce.data
                        if (restResponce.data?.size ?:1 <=3){
                            actMain?.showMenu()
                        }
                        if (isAdded) {
                            loadCartData(cartDataList!!)
                            ActMain.cardItemCount.value=restResponce.data?.size
                        }
                    } else if (restResponce.status == 0) {
                        dismissLoadingProgress()
                        fragMyCartBinding.rvMycard.visibility = View.GONE
                        fragMyCartBinding.tvNoDataFound.visibility = View.VISIBLE
                        alertErrorOrValidationDialog(
                            requireActivity(),
                            restResponce.message.toString()
                        )
                    }
                }else{
                    Common.dismissLoadingProgress()
                    Common.showErrorFullMsg(requireActivity(),"Please try again after some time")
                }
            }

            override fun onFailure(call: Call<GetCartResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }


    fun removeItemDialog(strCartId: String, pos: Int) {
        val removeDialogBinding = RemoveItemDialogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireActivity())
        dialog.setContentView(removeDialogBinding.root)
        removeDialogBinding.tvRemoveTitle.text = resources.getString(R.string.remove_product)
        removeDialogBinding.tvAlertMessage.text = resources.getString(R.string.remove_product_desc)
        removeDialogBinding.btnProceed.setOnClickListener {
            if (isCheckNetwork(requireActivity())) {
                dialog.dismiss()
                val hashMap = HashMap<String, String>()
                hashMap["user_id"] = getStringPref(requireActivity(), SharePreference.userId).toString()
                hashMap["cart_id"] = strCartId
                callDeleteApi(hashMap, pos)
            } else {
                alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.no_internet)
                )
            }
        }

        removeDialogBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    //TODO SET CART DATA
    private fun loadCartData(cartDataList: ArrayList<CartDataItem>) {
        lateinit var binding: RowMycartBinding
        cartListDataAdapter =
            object : BaseAdaptor<CartDataItem, RowMycartBinding>(
                requireActivity(),
                cartDataList
            ) {
                @SuppressLint("NewApi", "ResourceType", "SetTextI18n")
                override fun onBindData(
                    holder: RecyclerView.ViewHolder?,
                    `val`: CartDataItem,
                    position: Int
                ) {
                    val price = cartDataList[position].qty?.toInt()!! * cartDataList[position].price!!.toDouble()
                    if (currencyPosition == "left") {
                        binding.tvcartitemprice.text = "\u20b9"+price.toInt().toString()
                    } else {
                        binding.tvcartitemprice.text ="\u20b9"+price.toInt().toString()
                    }
                    binding.tvorderitem.text = cartDataList[position].qty.toString()
                    binding.tvcateitemname.text = cartDataList[position].productName

                    if (cartDataList[position].variation_name?.isEmpty() == true) {
                        binding.tvcartitemsize.text = ""
                    } else {
                        if (cartDataList[position].attribute_name == null && cartDataList[position].variation_name == null) {
                            binding.tvcartitemsize.text = "-"
                        } else {
                            binding.tvcartitemsize.text = //cartDataList[position].attribute + " : " +
                         cartDataList[position].attribute_name+" ("+cartDataList[position].variation_name+")"
                        }
                    }
                    Glide.with(requireActivity())
                        .load(filterHttps(cartDataList[position].imageUrl)).placeholder(R.drawable.placeholder_homevideo).into(binding.ivCartitemm)
                    binding.ivCartitemm.setBackgroundColor(Color.parseColor(colorArray[position % 6]))

                    binding.tvDelete.setOnClickListener {
                        if (isCheckNetwork(requireActivity())) {
                            removeItemDialog(cartDataList[position].id.toString(),position)
                        } else {
                            alertErrorOrValidationDialog(
                                requireActivity(),
                                resources.getString(R.string.no_internet)
                            )
                        }
                    }
                    binding.llRemove.setOnClickListener {
                        if (isCheckNetwork(requireActivity())) {
                            removeItemDialog(cartDataList[position].id.toString(),position)
                        } else {
                            alertErrorOrValidationDialog(
                                requireActivity(),
                                resources.getString(R.string.no_internet)
                            )
                        }
                    }
                    binding.ivMinus.setOnClickListener {
                        if (cartDataList[position].qty!!.toInt() > 1) {
                            binding.ivMinus.isClickable = true
                            Common.getLog("Qty>>", cartDataList[position].qty.toString())
                            if (isCheckNetwork(requireActivity())) {
                                callQtyUpdate(cartDataList[position], false)
                            } else {
                                alertErrorOrValidationDialog(
                                    requireActivity(),
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        } else {
                            binding.ivMinus.isClickable = true
                            removeItemDialog(cartDataList[position].id.toString(),position)
                            Common.getLog("Qty1>>", cartDataList[position].qty.toString())
                        }
                    }
                    binding.swipe.surfaceView.setOnClickListener {
                        Log.e("product_id--->", cartDataList[position].productId.toString())
                        val intent = Intent(requireActivity(), ActProductDetails::class.java)
                        intent.putExtra("product_id", cartDataList[position].productId.toString())
                        startActivity(intent)
                    }
                    binding.ivPlus.setOnClickListener {
                        if (cartDataList[position].qty!!.toInt() < 10
                        ) {
                            count++
                            if (isCheckNetwork(requireActivity())) {
                                callQtyUpdate(cartDataList[position], true)
                            } else {
                                alertErrorOrValidationDialog(
                                    requireActivity(),
                                    resources.getString(R.string.no_internet)
                                )
                            }
                        } else {
                            alertErrorOrValidationDialog(
                                requireActivity(),
                                resources.getString(R.string.max_qty)
                            )
                        }
                    }

                    val averageRating = 4.6f
                    displayDynamicStars(binding.ivRate,averageRating)
                }

                override fun setItemLayout(): Int {
                    return R.layout.row_mycart
                }

                override fun getBinding(parent: ViewGroup): RowMycartBinding {
                    binding = RowMycartBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return binding
                }
            }
        if (isAdded) {
            fragMyCartBinding.rvMycard.apply {
                if (cartDataList.size > 0) {
                    fragMyCartBinding.rvMycard.visibility = View.VISIBLE
                    fragMyCartBinding.tvNoDataFound.visibility = View.GONE
                    fragMyCartBinding.btncheckout.visibility = View.VISIBLE

                    layoutManager =
                        LinearLayoutManager(
                            activity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    itemAnimator = DefaultItemAnimator()
                    adapter = cartListDataAdapter
                } else {
                    fragMyCartBinding.rvMycard.visibility = View.GONE
                    fragMyCartBinding.tvNoDataFound.visibility = View.VISIBLE
                    fragMyCartBinding.btncheckout.visibility = View.GONE
                }
            }
        }
    }

    //TODO CART ITEM DELETE CINFOFMATION DIALOG
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
                if (isCheckNetwork(requireActivity())) {
                    finalDialog.dismiss()
                    val hashMap = HashMap<String, String>()
                    hashMap["user_id"] =
                        getStringPref(
                            requireActivity(),
                            SharePreference.userId
                        ).toString()
                    hashMap["cart_id"] = strCartId
                    callDeleteApi(hashMap, pos)
                } else {
                    alertErrorOrValidationDialog(
                        requireActivity(),
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

    //TODO API CART ITEM DELETE CALL
    private fun callDeleteApi(hasmap: HashMap<String, String>, pos: Int) {
        showLoadingProgress(requireActivity())

        val call = ApiClient.getClient.deleteProduct(hasmap)
        call.enqueue(object : Callback<SingleResponse> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.status == 1) {
                        cartDataList?.removeAt(pos)
                        callApiCartData(true)
                        if (cartDataList?.size ?: 0 > 0) {
                            fragMyCartBinding.tvNoDataFound.visibility = View.GONE
                            fragMyCartBinding.rvMycard.visibility = View.VISIBLE
                            fragMyCartBinding.btncheckout.visibility = View.VISIBLE
                        } else {
                            fragMyCartBinding.tvNoDataFound.visibility = View.VISIBLE
                            fragMyCartBinding.rvMycard.visibility = View.GONE
                            fragMyCartBinding.btncheckout.visibility = View.GONE
                        }
                    } else {
                        showErrorFullMsg(
                            requireActivity(),
                            response.body()?.message.toString()
                        )
                    }
                } else {
                    alertErrorOrValidationDialog(
                        requireActivity(),
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    //TODO API QTY UPDATE CALL
    private fun callQtyUpdate(cartModel: CartDataItem, isPlus: Boolean) {
        val qty = if (isPlus) {
            cartModel.qty!!.toInt() + 1
        } else {
            cartModel.qty!!.toInt() - 1
        }
        showLoadingProgress(requireActivity())
        val hashMap = HashMap<String, String>()
        hashMap["cart_id"] = cartModel.id.toString()
        hashMap["qty"] = qty.toString()
        val call = ApiClient.getClient.qtyUpdate(hashMap)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.status == 1) {
                        Common.isAddOrUpdated = true
                        callApiCartData(true)
                    } else {
                        response.body()?.message?.let {
                            showErrorFullMsg(
                                requireActivity(),
                                it
                            )
                        }
                    }
                } else {
                    alertErrorOrValidationDialog(
                        requireActivity(),
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    requireActivity(),
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(requireActivity(), false)
        if (isCheckNetwork(requireActivity())) {
            if (SharePreference.getBooleanPref(requireActivity(), SharePreference.isLogin)) {
                callApiCartData(false)
            } else {
                openActivity(ActSignUp::class.java)
                requireActivity().finish()
            }
        } else {
            alertErrorOrValidationDialog(
                requireActivity(),
                resources.getString(R.string.no_internet)
            )
        }
    }


    private fun displayDynamicStars(rating1: LinearLayout, rating: Float) {
        rating1.removeAllViews()

        for (i in 0 until 5) {
            val starView = ImageView(requireActivity())

            // Combine empty and filled stars into a LayerDrawable
            val emptyDrawable = requireContext().getDrawable(R.drawable.ic_star_empty)
            val filledDrawable = ClipDrawable(requireContext().getDrawable(R.drawable.ic_filled_star), Gravity.LEFT, ClipDrawable.HORIZONTAL)

            // Set the level (fill percentage) for each star
            val starLevel = calculateStarFillLevel(rating - i)
            filledDrawable.level = starLevel

            // Apply LayerDrawable combining empty and filled stars
            val layerDrawable = LayerDrawable(arrayOf(emptyDrawable, filledDrawable))
            starView.setImageDrawable(layerDrawable)

            // Set star size
            val params = LinearLayout.LayoutParams(45, 45) // Width x Height
            params.setMargins(4, 0, 4, 0)
            starView.layoutParams = params

            // Add the starView to the LinearLayout
            rating1.addView(starView)
        }
    }

    /**
     * Calculates the fill level for a star:
     * - 0 -> empty
     * - 10000 -> full
     * - Any intermediate value for fractional fill
     */
    private fun calculateStarFillLevel(value: Float): Int {
        return when {
            value >= 1 -> 10000  // Fully filled star
            value > 0 -> (value * 10000).toInt()  // Partially filled star
            else -> 0  // Empty star
        }
    }


}