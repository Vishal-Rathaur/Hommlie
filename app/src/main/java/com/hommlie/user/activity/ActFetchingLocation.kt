package com.hommlie.user.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.hommlie.user.ActSplashScreen
import com.hommlie.user.R
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActivityActFetchingLocationBinding
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.alertDialogNoInternet
import com.hommlie.user.utils.Common.isCheckNetwork
import java.io.IOException
import java.util.Locale

class ActFetchingLocation : BaseActivity() {

    private lateinit var binding : ActivityActFetchingLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun setLayout(): View = binding.root

    override fun initView() {
        binding = ActivityActFetchingLocationBinding.inflate(layoutInflater)

//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        // Set a translucent status bar color
        window.decorView.setBackgroundColor(ContextCompat.getColor(this@ActFetchingLocation, R.color.white))
        window.statusBarColor = ContextCompat.getColor(this@ActFetchingLocation, R.color.colorPrimary)

        Glide.with(this)
            .asGif()
            .load(R.drawable.fetchinglocation)
            .placeholder(R.drawable.ic_location)
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache for faster loading
            .priority(Priority.HIGH)
            .into(binding.ivImage)

//        Glide.with(this).load(R.drawable.fetchinglocation).into(binding.ivDeliverImage)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@ActFetchingLocation)
       // geocoder = Geocoder(this@ActFetchingLocation, Locale.getDefault())

        binding.ivImage.setOnClickListener {
            val intent = Intent(this@ActFetchingLocation, ActSplashScreen::class.java)
            startActivity(intent)
            finish()
        }

//        if (isCheckNetwork(this@ActFetchingLocation)) {
//            if (Common.hasLocationPermission(this@ActFetchingLocation)){
//                Handler(Looper.getMainLooper()).postDelayed({
//
//                    binding.llFetchedlocation.visibility=View.VISIBLE
//                    binding.ivImage.visibility=View.GONE
//                    binding.text.visibility=View.GONE
//                    overridePendingTransition(R.anim.slide_in,R.anim.no_animation)
//                },3000)
//                getCurrentLocation()
//            }else {
//                val intent = Intent(this@ActFetchingLocation,ActshowMapPicAddress::class.java)
//                startActivity(intent)
//                this@ActFetchingLocation.overridePendingTransition(R.anim.slide_in,R.anim.no_animation)
//            }
//        } else {
//            alertDialogNoInternet(this@ActFetchingLocation, resources.getString(R.string.no_internet))
//        }
    }

    override fun onResume() {
        super.onResume()
        if (isCheckNetwork(this@ActFetchingLocation)) {
            if (Common.hasLocationPermission(this@ActFetchingLocation)){
                getCurrentLocation()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.llFetchedlocation.visibility=View.VISIBLE
            //        binding.ivImage.visibility=View.GONE
                    binding.text.visibility=View.GONE
                    overridePendingTransition(R.anim.slide_in,R.anim.no_animation)
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@ActFetchingLocation, ActSplashScreen::class.java)
                        startActivity(intent)
                        finish()
                        overridePendingTransition(R.anim.slide_in,R.anim.no_animation)
                    },1000)
                },3000)
            }else {
//                val intent = Intent(this@ActFetchingLocation,ActshowMapPicAddress::class.java)
//                startActivity(intent)
//                this@ActFetchingLocation.overridePendingTransition(R.anim.slide_in,R.anim.no_animation)
                Common.requestLocationPermissions(this@ActFetchingLocation)

            }
        } else {
            alertDialogNoInternet(this@ActFetchingLocation, resources.getString(R.string.no_internet))
        }
    }


    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this@ActFetchingLocation, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    Log.d("HomeFragment", "Current Location: $currentLatLng")

                    // Handle geocoding based on Android version
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Use GeocodeListener for asynchronous geocoding on Android 13+
                        Geocoder(this@ActFetchingLocation, Locale.getDefault()).getFromLocation(
                            currentLatLng.latitude,
                            currentLatLng.longitude,
                            1,
                            object : Geocoder.GeocodeListener {
                                override fun onGeocode(addresses: List<Address>) {
                                    if (addresses.isNotEmpty()) {
                                        val address = addresses[0]
                                        val addressText = "${address.getAddressLine(0)}"  //\nLat: ${currentLatLng.latitude}, Lon: ${currentLatLng.longitude}
                                        binding.tvLocation.text = addressText

                                        val areaName = address.subLocality ?: address.locality ?: "Area not found"
                                        binding.tvArea.text = areaName
                                    } else {
                                        binding.tvLocation.text = "Address not found"
                                        binding.tvArea.text = "Add address"
                                    }
                                }

                                override fun onError(errorMessage: String?) {
                                    binding.tvLocation.text = "Error retrieving address"
                                    binding.tvArea.text = "Add address"
                                }
                            }
                        )
                    } else {
                        // Use synchronous geocoding for Android versions below 13
                        try {
                            val addresses = Geocoder(this@ActFetchingLocation, Locale.getDefault())
                                .getFromLocation(currentLatLng.latitude, currentLatLng.longitude, 1)

                            if (!addresses.isNullOrEmpty()) {
                                val address = addresses[0]
                                val addressText = "${address.getAddressLine(0)}"   //Lat: ${currentLatLng.latitude}, Lon: ${currentLatLng.longitude}
                                binding.tvLocation.text = addressText

                                val areaName = address.subLocality ?: address.locality ?: "Area not found"
                                binding.tvArea.text = areaName
                            } else {
                                binding.tvLocation.text = "Address not found"
                                binding.tvArea.text = "Add address"
                            }
                        } catch (e: IOException) {
                            binding.tvLocation.text = "Error retrieving address"
                            binding.tvArea.text = "Add address"
                            e.printStackTrace()
                        }
                    }
                }
            }
        } else {
            binding.tvLocation.text = "Please enable location permission"
            binding.tvArea.text = "Add address"
        }
    }

}