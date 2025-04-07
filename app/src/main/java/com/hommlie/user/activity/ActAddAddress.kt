package com.hommlie.user.activity

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.hommlie.user.R
import com.hommlie.user.api.ApiClient
import com.hommlie.user.api.SingleResponse
import com.hommlie.user.base.BaseActivity
import com.hommlie.user.databinding.ActAddAddressBinding
import com.hommlie.user.model.UserProfileData
import com.hommlie.user.utils.Common
import com.hommlie.user.utils.Common.alertErrorOrValidationDialog
import com.hommlie.user.utils.Common.dismissLoadingProgress
import com.hommlie.user.utils.Common.getCurrentLanguage
import com.hommlie.user.utils.Common.showErrorFullMsg
import com.hommlie.user.utils.Common.showLoadingProgress
import com.hommlie.user.utils.SharePreference
import com.hommlie.user.utils.UserProfileDataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActAddAddress : BaseActivity() {
    private lateinit var addAddressBinding: ActAddAddressBinding
    private var addressId = 0
    var type = 0
    private var isClick = false
    private var latitude = ""
    private var longitude = ""

    private lateinit var addressLauncher: ActivityResultLauncher<Intent>

 //   private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun setLayout(): View = addAddressBinding.root

    override fun initView() {
        addAddressBinding = ActAddAddressBinding.inflate(layoutInflater)
        addAddressBinding.ivBack.setOnClickListener { finish() }
        addAddressBinding.btnsave.setOnClickListener {
            if (Common.isCheckNetwork(this@ActAddAddress)) {
                validation()
            } else {
                alertErrorOrValidationDialog(
                    this@ActAddAddress,
                    resources.getString(R.string.no_internet)
                )
            }
        }
        type = intent.getIntExtra("Type", 0)
        if (type == 1) {
            isClick = true
            addAddressBinding.tvAddressTitle.text = resources.getString(R.string.edit_address)
            addAddressBinding.btnsave.text = resources.getString(R.string.update_address)
            getdata()
        } else {
            addAddressBinding.tvAddressTitle.text = resources.getString(R.string.new_address)
            addAddressBinding.btnsave.text = resources.getString(R.string.save_address)

            addAddressBinding.edFullname.setText(UserProfileDataManager.getUserProfileData()?.name)
            addAddressBinding.edtEmailAddress.setText(UserProfileDataManager.getUserProfileData()?.email)
            addAddressBinding.edPhone.setText(UserProfileDataManager.getUserProfileData()?.mobile)

        }

    //    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check if Places API is initialized
//        if (!Places.isInitialized()) {
//            Places.initialize(applicationContext,applicationContext.getString(R.string.location_key))
//        }
        addressLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Get the address returned from ActAddress
                val address = result.data?.getStringExtra("selectedAddress")
                latitude  =  result.data?.getStringExtra("latitude").toString()
                longitude  =  result.data?.getStringExtra("longitude").toString()
                addAddressBinding.edLandmark.setText(address) // Set the address to the EditText
                addAddressBinding.edPostCodeZip.setText(result.data?.getStringExtra("pincode"))
            }
        }
        addAddressBinding.clGetloction.setOnClickListener {
            val intent = Intent(this,ActshowMapPicAddress::class.java)
            addressLauncher.launch(intent)
        }
        addAddressBinding.edLandmark.setOnClickListener {
            val intent = Intent(this,ActshowMapPicAddress::class.java)
            addressLauncher.launch(intent)
        }

     /*   val autocompleteSupportFragment = (supportFragmentManager.findFragmentById(R.id.autoComplete)
                as AutocompleteSupportFragment)
        autocompleteSupportFragment.setHint("Search your location")
        val editText = (autocompleteSupportFragment.view as? ViewGroup)?.findViewById<EditText>(R.id.places_autocomplete_search_input)
        editText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)


        autocompleteSupportFragment.setPlaceFields(
            listOf(
                Place.Field.LAT_LNG,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS
            )
        )

// Set up the OnPlaceSelectedListener
        autocompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // Handle the selected place
                val selectedLocation = place.address
                val latLng: LatLng? = place.latLng
                var pincode =""
                val addressComponents = place.addressComponents
                if (addressComponents != null) {
                    for (component in addressComponents.asList()) {
                        for (type in component.types) {
                            if (type == "postal_code") {
                                pincode = component.name
                            }
                        }
                        if (pincode.isEmpty()) {
                            pincode=""
                        }
                    }
                }

                autocompleteSupportFragment.setText(null)

                // Update your TextView with the selected location
                addAddressBinding.edStreerAddress.setText(selectedLocation)
                addAddressBinding.edPostCodeZip.setText(pincode)
                addAddressBinding.edLandmark.setText("Latitude: ${latLng?.latitude}, Longitude: ${latLng?.longitude}")
                //   binding.selectedLocation.setOnClickListener {
                // Get the selected location
                //  val selectedLocation = binding.selectedLocation.text.toString()

                // Set the result and finish the activity
//                val resultIntent = Intent()
//                resultIntent.putExtra("selectedLocation", selectedLocation)
//                resultIntent.putExtra("latLng", latLng)
//                resultIntent.putExtra("pincode",pincode)
//                setResult(Activity.RESULT_OK, resultIntent)
//                finish()
                //     }

            }

            override fun onError(status: Status) {
                Log.e(TAG, "An error occurred: $status")
            }
        })  */



    }

    //TODO SET ADDRESS TO EDIT DATA
    private fun getdata() {
        addressId = intent.getIntExtra("address_id", 0)
        addAddressBinding.edFullname.setText(intent.getStringExtra("FirstName"))
        addAddressBinding.edLastName.setText(intent.getStringExtra("LastName"))
        addAddressBinding.edStreerAddress.setText(intent.getStringExtra("StreetAddress"))
        addAddressBinding.edLandmark.setText(intent.getStringExtra("Landmark"))
        addAddressBinding.edPostCodeZip.setText(intent.getStringExtra("Pincode"))
        addAddressBinding.edPhone.setText(intent.getStringExtra("Mobile"))
        addAddressBinding.edtEmailAddress.setText(intent.getStringExtra("Email"))
    }

    //TODO ADDRESS VALIDATION
    private fun validation() = when {
        addAddressBinding.edFullname.text.toString() == "" -> {
            showErrorFullMsg(
                this@ActAddAddress,
                resources.getString(R.string.validation_all)
            )
        }
     /*   addAddressBinding.edLastName.text.toString() == "" -> {
            showErrorFullMsg(
                this@ActAddAddress,
                resources.getString(R.string.validation_all)
            )
        } */
        addAddressBinding.edStreerAddress.text.toString() == "" -> {
            showErrorFullMsg(
                this@ActAddAddress,
                resources.getString(R.string.validation_all)
            )
        }
        addAddressBinding.edLandmark.text.toString() == "" -> {
            showErrorFullMsg(
                this@ActAddAddress,
                resources.getString(R.string.validation_all)
            )
        }
        addAddressBinding.edPostCodeZip.text.toString() == "" -> {
            showErrorFullMsg(
                this@ActAddAddress,
                resources.getString(R.string.validation_all)
            )
        }
        addAddressBinding.edPhone.text.toString() == "" -> {
            showErrorFullMsg(
                this@ActAddAddress,
                resources.getString(R.string.validation_all)
            )
        }
        addAddressBinding.edtEmailAddress.text.toString() == "" -> {
            showErrorFullMsg(
                this@ActAddAddress,
                resources.getString(R.string.validation_all)
            )
        }
        else -> {
            val addadrress = HashMap<String, String>()
            addadrress["user_id"] =
                SharePreference.getStringPref(this@ActAddAddress, SharePreference.userId)
                    ?: ""
            addadrress["name"] = addAddressBinding.edFullname.text.toString()
            //addadrress["last_name"] = addAddressBinding.edLastName.text.toString()
            addadrress["address"] = " "+addAddressBinding.edStreerAddress.text.toString()
            addadrress["landmark"] = addAddressBinding.edLandmark.text.toString()
            addadrress["latitude"] =  latitude
            addadrress["longitude"] =  longitude
            addadrress["pincode"] = addAddressBinding.edPostCodeZip.text.toString()
            addadrress["mobile"] = addAddressBinding.edPhone.text.toString()
            addadrress["email"] = addAddressBinding.edtEmailAddress.text.toString()
            if (type == 1) {
                addadrress["address_id"] = addressId.toString()
                Log.e("request", addadrress.toString())
                callApiUpdateAddress(addadrress)
            } else {
                callApiAddAddress(addadrress)
            }
        }
    }

    //TODO API ADD ADDRESS CALL
    private fun callApiAddAddress(addadrress: HashMap<String, String>) {
        showLoadingProgress(this@ActAddAddress)
        val call = ApiClient.getClient.addAddress(addadrress)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                dismissLoadingProgress()
                if (response.code() == 200) {
                    if (response.body()?.status == 1) {
                        Common.isAddOrUpdated = true
                        finish()
                    } else {
                        showErrorFullMsg(
                            this@ActAddAddress,
                            response.body()?.message.toString()
                        )
                    }
                } else {
                    alertErrorOrValidationDialog(
                        this@ActAddAddress,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActAddAddress,
                    resources.getString(R.string.error_msg)
                )

            }
        })
    }

    //TODO API UPDATE ADDRESS CALL
    private fun callApiUpdateAddress(addressRequest: HashMap<String, String>) {
        showLoadingProgress(this@ActAddAddress)
        val call = ApiClient.getClient.updateAddress(addressRequest)
        call.enqueue(object : Callback<SingleResponse> {
            override fun onResponse(
                call: Call<SingleResponse>,
                response: Response<SingleResponse>
            ) {
                if (response.code() == 200) {
                    dismissLoadingProgress()
                    Common.isAddOrUpdated = true

                    if (response.body()?.status == 1) {
                        setResult(RESULT_OK)
                        finish()

                    } else {
                        showErrorFullMsg(
                            this@ActAddAddress,
                            response.body()?.message.toString()
                        )
                    }
                } else {
                    dismissLoadingProgress()
                    alertErrorOrValidationDialog(
                        this@ActAddAddress,
                        resources.getString(R.string.error_msg)
                    )
                }
            }

            override fun onFailure(call: Call<SingleResponse>, t: Throwable) {
                dismissLoadingProgress()
                alertErrorOrValidationDialog(
                    this@ActAddAddress,
                    resources.getString(R.string.error_msg)
                )
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getCurrentLanguage(this@ActAddAddress, false)
    }



 /*   private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val address = getAddress(latitude, longitude)

//                    val resultIntent = Intent()
//                    resultIntent.putExtra("selectedLocation", address)
//                    resultIntent.putExtra("latLng", ""+latitude+","+longitude)
                  //  resultIntent.putExtra("pincode",pincode)
//                    setResult(Activity.RESULT_OK, resultIntent)
//                    finish()

                    // Update UI with location details
//                    binding.selectedLocation.text = "Latitude: $latitude, Longitude: $longitude"
//                    binding.selectedLocation.text = "Address: $address"


                } else {
                    // Location is null, handle the case accordingly
                    Toast.makeText(
                        this,
                        "Unable to retrieve current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }  */

    private fun getAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        var addressText = ""
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                addressText = address.getAddressLine(0)
                //pincode=address.postalCode
               // addAddressBinding.edStreerAddress.setText(addressText)
                addAddressBinding.edPostCodeZip.setText(address.postalCode)
                addAddressBinding.edLandmark.setText("Latitude: $latitude, Longitude: $longitude")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return addressText
    }


    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }


}