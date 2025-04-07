package com.hommlie.user.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.hommlie.user.R
import com.hommlie.user.databinding.ActivityActshowMapPicAddressBinding
import com.hommlie.user.utils.Common
import java.io.IOException
import java.util.Locale

class ActshowMapPicAddress : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding:ActivityActshowMapPicAddressBinding

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var geocoder: Geocoder
    private var marker: Marker? = null
    private var permissionDialog: AlertDialog? = null

    private val locationPermissionRequestCode = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var addressTextView: TextView
    private lateinit var centerPin: ImageView

    var selectedArea=""
    var selectedAddress=""
    var selectedlat=""
    var selectedlng=""
    var selectedpin=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActshowMapPicAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

        mapView = binding.mapView
        addressTextView=binding.locationAddress
        centerPin = binding.centerPin
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Initialize the Places API
        Places.initialize(applicationContext, getString(R.string.location_key))
        val placesClient = Places.createClient(this)

        // Set up the AutocompleteSupportFragment
        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autoCompletefragment) as AutocompleteSupportFragment
        autocompleteFragment.setHint("Search your location")
        val editText = (autocompleteFragment.view as? ViewGroup)?.findViewById<EditText>(R.id.places_autocomplete_search_input)
        editText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)


        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS))

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {

            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
                if (latLng != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                    updateMarkerAndAddress(latLng)
                }

            }

            override fun onError(status: Status) {
                Toast.makeText(this@ActshowMapPicAddress, "An error occurred: $status", Toast.LENGTH_SHORT).show()
            }
        })

        binding.ivBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.no_animation,R.anim.slide_out)
        }

        binding.confirmButton.setOnClickListener{
            val resultIntent = Intent()
            resultIntent.putExtra("selectedArea", selectedArea.trim())
            resultIntent.putExtra("selectedAddress",selectedAddress.trim())
            resultIntent.putExtra("pincode",selectedpin)
            resultIntent.putExtra("latitude", selectedlat)
            resultIntent.putExtra("longitude", selectedlng)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }


    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Listener to update the address when the camera stops moving
        googleMap.setOnCameraIdleListener {
            val center = googleMap.cameraPosition.target
            updateMarkerAndAddress(center)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionRequestCode)
        }
    }


    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
                    updateMarkerAndAddress(currentLatLng)
                }
            }
        }
    }

//    private fun updateMarkerAndAddress(latLng: LatLng) {
//        // Get the address of the current location
//        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
//        if (addresses != null && addresses.isNotEmpty()) {
//            val address = addresses[0]
//            val addressText = "${address.getAddressLine(0)}"//\nLat: ${latLng.latitude}, Lon: ${latLng.longitude}"
//            addressTextView.text = addressText
//            selectedAddress=addressText
//            selectedlatlng=""+latLng.latitude+","+latLng.longitude
//            val areaName = address.subLocality ?: address.locality ?: "Area not found"
//            binding.locationName.text=areaName
//            selectedArea=areaName
//        } else {
//            addressTextView.text = "Address not found\nLat: ${latLng.latitude}, Lon: ${latLng.longitude}"
//        }
//    }


    private fun updateMarkerAndAddress(latLng: LatLng) {
        val geocoder = Geocoder(this, Locale.getDefault())

        // Check the Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use asynchronous geocoding for Android 13 and above
            val geocodeListener = object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    handleGeocodeResult(addresses, latLng)
                }
                override fun onError(errorMessage: String?) {
                    addressTextView.text = "Unable to fetch address: $errorMessage"
                }
            }

            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1, geocodeListener)
        } else {
            // Use synchronous geocoding for Android 12 and below
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                handleGeocodeResult(addresses, latLng)
            } catch (e: IOException) {
                e.printStackTrace()
                addressTextView.text = "Unable to fetch address due to network error"
            }
        }
    }

    private fun handleGeocodeResult(addresses: List<Address>?, latLng: LatLng) {
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]

            // Extract components
            val streetAddress = address.thoroughfare ?: ""
            val subLocality = address.subLocality ?: ""
            val locality = address.locality ?: ""
            val state = address.adminArea ?: ""
            val postalCode = address.postalCode ?: ""
            val country = address.countryName ?: ""

            // Format the address
            val addressText = listOf(streetAddress, subLocality, locality, state, postalCode, country)
                .filter { it.isNotEmpty() }
                .joinToString(", ")

            // Update UI
            addressTextView.text = addressText
            selectedAddress = listOf(streetAddress, subLocality, locality, state, country)
                .filter { it.isNotEmpty() }
                .joinToString(", ")
            selectedlat = latLng.latitude.toString()
            selectedlng = latLng.longitude.toString()
            selectedpin = postalCode

            // Set area name
            val areaName = subLocality.ifEmpty { locality.ifEmpty { "Area not found" } }
            binding.locationName.text = areaName
            selectedArea = areaName
        } else {
            addressTextView.text = "Address not found\nLat: ${latLng.latitude}, Lon: ${latLng.longitude}"
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapView.getMapAsync(this)
            }
        }
    }



    override fun onResume() {
        super.onResume()
        mapView.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            showPermissionDeniedDialog()
        }else{
            permissionDialog?.dismiss()
          //  getCurrentLocation()
        }
//        if (Common.hasLocationPermission(this@ActshowMapPicAddress)){
//            getCurrentLocation()
//        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    private fun showPermissionDeniedDialog() {

        if (permissionDialog != null && permissionDialog?.isShowing == true) {
            return
        }

        permissionDialog = AlertDialog.Builder(this)
            .setTitle("\uD83D\uDCCD Permission Required")
            .setMessage("This app requires location permission to function properly. Please enable it in the app settings.")
            .setPositiveButton("Go to Settings") { dialog, _ ->
                // Send user to app's settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
                dialog.dismiss()
            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
            .create()

        permissionDialog?.show()
        permissionDialog?.setCancelable(false)
    }


}