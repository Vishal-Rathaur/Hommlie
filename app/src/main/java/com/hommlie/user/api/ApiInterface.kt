package com.hommlie.user.api

import com.hommlie.user.model.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {

    @POST("login")
    fun getLogin(@Body map: HashMap<String, String>): Call<RestResponse<LoginModel>>

    @POST("wallet/add-money")
    fun confirmRecharge(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("register")
    fun setRegistration(@Body map: HashMap<String, String>): Call<newRegistrationModel>

    @POST("vendorsregister")
    fun setVendorsRegister(@Body map: HashMap<String, String>): Call<SingleResponse>


    @POST("verifyotp")
    fun setEmailVerify(@Body map: HashMap<String, String>): Call<JsonObject>

    @POST("resendemailverification")
    fun setResendEmailVerification(@Body map: HashMap<String, String>): Call<SingleResponse>

    //@POST("forgotPassword")
    @POST("forgotpasswordotp")
    fun setforgotPassword(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("forgotpasswordverifyotp")
    fun verifyForgotPassword(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("changepassword")
    fun setChangePassword(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("homefeeds")
    fun gethomefeeds(@Body map: HashMap<String, String>): Call<HomefeedResponse>

    @GET("banner")
    fun getbanner(): Call<BannerResponse>

    @GET("category")
    fun getcategory(): Call<CategoriesResponse>

    @POST("subcategory")
    fun getSubCategoriesDetail(@Body map: HashMap<String, String>): Call<SubCategoriesResponse>

    @POST("addtowishlist")
    fun setAddToWishList(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("removefromwishlist")
    fun setRemoveFromWishList(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("viewalllisting")
    fun setViewAllListing(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ): Call<ViewAllListResponse>

    @POST("getwishlist")
    fun getWishList(@Body map: HashMap<String, String>): Call<GetWishListResponse>

    @POST("deleteaccount")
    fun deleteAccount(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("getprofile")
    fun getProfile(@Body map: HashMap<String, String>): Call<GetProfileResponse>

    @Multipart
    @POST("editprofile")
    fun setProfile(@Part("user_id") userId: RequestBody, // Send the user_id as part of the request
                   @Part("name") name: RequestBody, // Send name as part of the request
                   @Part("email") email: RequestBody, // Send email as part of the request
                   @Part image: MultipartBody.Part?): Call<UserProfileData>
      /*  @Part("id") userId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("email") bankName:RequestBody,
        @Part("bank_account") accountnumber:RequestBody,
        @Part("bank_ifsc") ifscCode:RequestBody,
        @Part("upi") upi:RequestBody,
        @Part profileimage: MultipartBody.Part?
    ): Call<SingleResponse> */

    @POST("saveaddress")
    fun addAddress(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("getaddress")
    fun getAddress(@Body map: HashMap<String, String>): Call<GetAddressResponse>

    @POST("deleteaddress")
    fun deleteAddress(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("editaddress")
    fun updateAddress(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("productdetails")
    fun getProductDetails(@Body map: HashMap<String, String>): Call<GetProductDetailsResponse>

    @POST("createInspection")
    fun scheduleInspection(@Body map: HashMap<String, String>): Call<InspectionResponse>

    @POST("vendorproducts")
    fun getVendorProducts(@Body map: HashMap<String, String>): Call<GetVendorDetailsResponse>

    @POST("productreview")
    fun getProductReview(@Body map: HashMap<String, String>): Call<ProductReviewResponse>

    @GET("brands")
    fun getBrands(@Query("page") page: String): Call<BrandResponse>

    @POST("brandsproducts")
    fun getBrandDetails(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ): Call<BrandDetailsResponse>

    @GET("vendors")
    fun getVendors(@Query("page") page: String): Call<VendorsResponse>

    @POST("vendorproducts")
    fun getVendorsDetails(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ): Call<VendorsDetailsResponse>

    @POST("notification")
    fun getNotificatios(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ): Call<NotificationsResponse>

    @POST("orderdetails")
    fun getOrderDetails(@Body map: HashMap<String, String>): Call<OrderDetailsResponse>

    @POST("trackorder")
    fun getTrackOrder(@Body map: HashMap<String, String>): Call<TrackOrderResponse>

    @GET("cmspages")
    fun getCmsData():Call<CmsPageResponse>
    @POST("orderhistory")
    fun getOrderHistory(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ): Call<OrderHistoryResponse>

    @POST("resellerlist")
    fun getResellerList(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ) : Call<SellerMarginResponse>

    @POST("cancelorder")
    fun getCancelOrder(@Body map: HashMap<String, String>,@Header("Authorization") token: String): Call<SingleResponse>

    @POST("rescheduleorder")
    fun rescheduleOrder(@Body map: HashMap<String, String>,@Header("Authorization") token: String): Call<SingleResponse>

    @POST("addtocart")
    fun getAddtocart(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("getcart")
    fun getCartData(@Body map: HashMap<String, String>): Call<GetCartResponse>

    @POST("deleteproduct")
    fun deleteProduct(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("qtyupdate")
    fun qtyUpdate(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("checkout")
    fun getCheckOut(@Body map: HashMap<String, String>): Call<GetCheckOutResponse>

    @POST("paymentlist")
    fun getPaymentList(@Body map: HashMap<String, String>): Call<PaymentListResponse>

    @POST("order")
    fun setOrderPayment(@Body map: HashMap<String, String>): Call<SingleResponse>

    @GET("searchproducts")
    fun getSearchProducts(): Call<SearchProductResponse>

//    @GET("searchproducts")
//    fun getSearchProducts(@Body map: HashMap<String, String>): Call<SearchProductResponse>

    @POST("filter")
    fun getFilter(
        @Query("page") page: String,
        @Body map: HashMap<String, String>
    ): Call<GetFilterResponse>

    @POST("products")
    fun getProduct(@Body map: HashMap<String, String>): Call<ProductResponse>

    @GET("coupons")
    fun getCoupon(
        @Query("page") page: String
    ): Call<GetCouponResponse>

    @POST("wallet/transactions")
    fun getWallet(@Body map: HashMap<String, String>): Call<WalletResponse>

    @POST("returnconditions")
    fun getOrderReturnRequest(
        @Body map: HashMap<String, String>
    ): Call<OrderRetuenRequestResponse>

    @POST("returnrequest")
    fun returnRequest(@Body map: HashMap<String, String>): Call<SingleResponse>

    @POST("addratting")
    fun addRatting(@Body map: HashMap<String, String>,@Header("Authorization") token: String): Call<SingleResponse>


    @POST("message")
    fun help(@Body map: HashMap<String, String>): Call<SingleResponse>

  @POST("recharge")
    fun addMoney(@Body map: HashMap<String, String>): Call<SingleResponse>


    @POST("initiatePaymentApp")
    fun getPaymentUrl(@Body map: HashMap<String, String>): Call<PaymentUrlResponse>

    @GET("getCoupons")
    fun getCoupons():Call<Coupns>

    @POST("getprofile")
    fun getUserProfile(@Body map:HashMap<String,String>):Call<UserProfileData>

    @POST("verifyPayment")
    fun verifyPayment(@Body map: HashMap<String, String>):Call<PaymentSingleResponse>

    @POST("initiatePayment")
    fun initiatePayment(@Body map: HashMap<String, String>):Call<InitiatePaymentResponse>


    @GET("checkVersion")
    fun checkVersion():Call<CheckVersionResponse>

}
