package com.hommlie.user.model

import com.google.gson.annotations.SerializedName

data class WalletResponse(

	@SerializedName("status")
	val status: Int,

	@SerializedName("transactions")
	val transactions: List<Transaction>,

	@SerializedName("wallet")
	val wallet: Wallet
)

data class Transaction(
	@SerializedName("id")
	val id: Int,

	@SerializedName("wallet_id")
	val walletId: Int,

	@SerializedName("transaction_type")
	val transactionType: String,

	@SerializedName("amount")
	val amount: String,

	@SerializedName("payment_id")
	val paymentId: String,

	@SerializedName("description")
	val description: String,

	@SerializedName("created_at")
	val createdAt: String,

	@SerializedName("updated_at")
	val updatedAt: String
)

data class Wallet(
	@SerializedName("id")
	val id: Int,

	@SerializedName("user_id")
	val userId: Int,

	@SerializedName("balance")
	val balance: Double,

	@SerializedName("created_at")
	val createdAt: String,

	@SerializedName("updated_at")
	val updatedAt: String
)