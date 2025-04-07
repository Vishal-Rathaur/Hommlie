package com.hommlie.user.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

class SharePreference {

    companion object {
        lateinit var mContext: Context
        lateinit var sharedPreferences: SharedPreferences
        val PREF_NAME: String = "FoodApp"
        val PRIVATE_MODE: Int = 0
        lateinit var editor: SharedPreferences.Editor
        val isLogin: String = "isLogin"
        val userId: String = "userid"
        val userMobile: String = "usermobile"
        val wallet: String = "wallet"
        val loginType: String = "loginType"
        val userEmail: String = "useremail"
        val userName: String = "userName"
        val isIntroseen: String = "isSeen"
        val userBankName: String ="userBankName"
        val userBankAccountNo: String="userBankAccountNo"
        val userBankIfsc : String="userIfsc"
        val userUpi : String ="userUpi"
        val userProfile: String = "userprofile"
        val isTutorial = "tutorial"
        val token = "token"
        val isCoupon = "coupon"
        val userRefralCode: String = "referral_code"
        var SELECTED_LANGUAGE = "selected_language"
        var UserLoginType = "isEmailLogin"
        var Currency = "currency"
        var CurrencyPosition = "currencyPosition"
        var referCardImage = ""


        fun getIntPref(context: Context, key: String): Int {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            return pref.getInt(key, -1)
        }

        fun setIntPref(context: Context, key: String, value: Int) {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            pref.edit().putInt(key, value).apply()
        }

        fun getDoublePref(context: Context, key: String): Double {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            return pref.getString(key, "0.0")?.toDouble() ?: 0.0
        }

        fun setDoublePref(context: Context, key: String, value: Double) {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            pref.edit().putString(key, value.toString()).apply()
        }

        fun getStringPref(context: Context, key: String): String? {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            return pref.getString(key, "")
        }

        fun setStringPref(context: Context, key: String, value: String) {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            pref.edit().putString(key, value).apply()
        }

        fun getBooleanPref(context: Context, key: String): Boolean {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            return pref.getBoolean(key, false)
        }

        fun setBooleanPref(context: Context, key: String, value: Boolean) {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            pref.edit().putBoolean(key, value).apply()
        }
    }

    @SuppressLint("CommitPrefEdits")
    constructor(mContext1: Context) {
        mContext = mContext1
        sharedPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = sharedPreferences.edit()
    }

    fun mLogout() {
        editor.clear()
        editor.commit()
    }
}