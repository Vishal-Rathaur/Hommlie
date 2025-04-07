package com.hommlie.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.base.BaseAdaptor
import com.hommlie.user.databinding.ActivityActTransactionBinding
import com.hommlie.user.databinding.RowTransactionhistoryBinding
import com.hommlie.user.model.Transaction
import com.hommlie.user.model.WalletResponse
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.dismissLoadingProgress
import com.hommlie.user.utils.SharePreference
import com.hommlie.user.utils.SharePreference.Companion.getStringPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActTransaction : BaseActivity() {

    private lateinit var transactionBinding: ActivityActTransactionBinding

    private var allTransaction = ArrayList<Transaction>()
    private var creditTransaction = ArrayList<Transaction>()
    private var debitTransaction = ArrayList<Transaction>()

    private var viewAllDataAdapter: BaseAdaptor<Transaction, RowTransactionhistoryBinding>? = null
    private var manager: LinearLayoutManager? = null

    override fun setLayout(): View = transactionBinding.root
    override fun initView() {
        transactionBinding = ActivityActTransactionBinding.inflate(layoutInflater)

        window.statusBarColor = ContextCompat.getColor(this@ActTransaction, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        transactionBinding.root.post {
            moveIndicatorUnderCard(transactionBinding.cardAll)
            transactionBinding.cardAll.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray_line_color))
        }

        manager = LinearLayoutManager(this@ActTransaction, LinearLayoutManager.VERTICAL, false)

        loadWalletDetails(allTransaction)
        if (Common.isCheckNetwork(this@ActTransaction)) {
            if (SharePreference.getBooleanPref(this@ActTransaction, SharePreference.isLogin)) {
                callApiWallet()
            } else {
                val intent = Intent(this@ActTransaction,ActSignUp::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            Common.alertErrorOrValidationDialog(
                this@ActTransaction,
                resources.getString(R.string.no_internet)
            )
        }

        transactionBinding.ivBack.setOnClickListener {
            finish()
        }

        transactionBinding.cardAll.setOnClickListener {
            moveIndicatorUnderCard(transactionBinding.cardAll)
            transactionBinding.cardAll.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray_line_color))
            transactionBinding.cardCredited.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            transactionBinding.cardDebited.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            loadWalletDetails(allTransaction)
        }
        transactionBinding.cardCredited.setOnClickListener {
            moveIndicatorUnderCard(transactionBinding.cardCredited)
            transactionBinding.cardAll.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            transactionBinding.cardCredited.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray_line_color))
            transactionBinding.cardDebited.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            loadWalletDetails(creditTransaction)
        }
        transactionBinding.cardDebited.setOnClickListener {
            moveIndicatorUnderCard(transactionBinding.cardDebited)
            transactionBinding.cardAll.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            transactionBinding.cardCredited.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
            transactionBinding.cardDebited.setCardBackgroundColor(ContextCompat.getColor(this, R.color.gray_line_color))
            loadWalletDetails(debitTransaction)
        }

    }



    //TODO CALL WALLET API
    private fun callApiWallet() {
        // Show a loading indicator
        Common.showLoadingProgress(this@ActTransaction)

        // Prepare the request parameters
        val hasmap = HashMap<String, String>()
        hasmap["userId"] = getStringPref(this@ActTransaction, SharePreference.userId)!!

        // Make the API call
        ApiClient.getClient.getWallet(hasmap).enqueue(object : Callback<WalletResponse> {

            override fun onResponse(call: Call<WalletResponse>, response: Response<WalletResponse>) {
                dismissLoadingProgress()
                if (response.isSuccessful) {
                    val walletResponse = response.body()
                    if (walletResponse?.status == 1) {
                        // Populate the wallet list and load details
                        allTransaction.clear()
                        creditTransaction.clear()
                        debitTransaction.clear()
                        allTransaction.addAll(walletResponse.transactions)
                        loadWalletDetails(allTransaction)
                        walletResponse.transactions.forEach { transaction ->
                            if (transaction.transactionType == "credit") {
                                creditTransaction.add(transaction)
                            } else if (transaction.transactionType == "debit") {
                                debitTransaction.add(transaction)
                            }
                        }
                    } else {
                        // Handle API response with status 0
                        // Common.showErrorFullMsg(this@ActWallet, walletResponse?.message ?: getString(R.string.error_msg))
                    }
                } else {
                    // Handle non-200 HTTP response codes
                    Common.showErrorFullMsg(
                        this@ActTransaction,
                        response.message() ?: getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<WalletResponse>, t: Throwable) {
                Common.dismissLoadingProgress()
                Common.showErrorFullMsg(
                    this@ActTransaction,
                    t.localizedMessage ?: getString(R.string.error_msg)
                )
            }
        })
    }


    //TODO WALLET DATA SET
    private fun loadWalletDetails(walletList: ArrayList<Transaction>) {
        lateinit var binding: RowTransactionhistoryBinding

        viewAllDataAdapter = object : BaseAdaptor<Transaction, RowTransactionhistoryBinding>(this@ActTransaction, walletList) {
            @SuppressLint("NewApi", "ResourceType", "SetTextI18n", "UseCompatLoadingForDrawables")
            override fun onBindData(holder: RecyclerView.ViewHolder?, `val`: Transaction, position: Int) {
                binding.tvwalletdate.text = Common.formatDateTime(walletList[position].createdAt)
                if (walletList[position].transactionType == "credit") {
                    binding.tvDeliveryprice.text = "\u002B\u20b9${walletList[position].amount}"
                    binding.tvwalletName.text = "Cash Deposited"
                    binding.tvDeliveryprice.setTextColor(ContextCompat.getColor(this@ActTransaction, R.color.quantum_googgreen500))
                } else {
                    binding.tvDeliveryprice.text = "\u002D\u20b9${walletList[position].amount}"
                    binding.tvwalletName.text = "Service Booked #"
                    binding.tvDeliveryprice.setTextColor(ContextCompat.getColor(this@ActTransaction, R.color.home_header))
                }
            }

            override fun setItemLayout(): Int {
                return R.layout.row_notification
            }

            override fun getBinding(parent: ViewGroup): RowTransactionhistoryBinding {
                binding = RowTransactionhistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return binding
            }
        }

        // Notify the adapter to update the list
        transactionBinding.rvRecenttrans.apply {
            layoutManager = manager
            adapter = viewAllDataAdapter
        }
    }


    fun moveIndicatorUnderCard(selectedCard: View) {
        // Get the location of the selected card
        val location = IntArray(2)
        selectedCard.getLocationInWindow(location)

        // Move the indicator to the location of the selected card
        transactionBinding.tabLayout.layoutParams = (transactionBinding.tabLayout.layoutParams as ViewGroup.MarginLayoutParams).apply {
            leftMargin = location[0]
            width = selectedCard.width
        }
        transactionBinding.tabLayout.requestLayout()
    }
}