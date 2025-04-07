package com.hommlie.user.utils

import com.hommlie.user.model.UserContactInfo
import com.hommlie.user.model.UserProfile

object UserProfileDataManager {

    private var userProfileData: UserProfile? = null

    private var userContactInfo: UserContactInfo? = null

    fun setUserProfileData(data: UserProfile) {
        userProfileData = data
    }

    fun getUserProfileData(): UserProfile? {
        return userProfileData
    }

    fun setUserContactInfo(data : UserContactInfo){
        userContactInfo = data
    }

    fun getUserContactInfo() : UserContactInfo? {
        return userContactInfo
    }

}