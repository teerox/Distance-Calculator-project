package com.example.riby_distance_calculator.screen

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.riby_distance_calculator.utils.GetResult
import com.example.riby_distance_calculator.BaseApplication
import com.example.riby_distance_calculator.R
import com.example.riby_distance_calculator.databinding.ActivityMainBinding
import com.example.riby_distance_calculator.utils.Permissions
import com.example.riby_distance_calculator.utils.Permissions.KEY_CAMERA_POSITION
import com.example.riby_distance_calculator.utils.Permissions.KEY_LOCATION
import com.example.riby_distance_calculator.utils.Permissions.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
import com.example.riby_distance_calculator.utils.Permissions.getLocationPermission
import com.example.riby_distance_calculator.utils.Permissions.mCameraPosition
import com.example.riby_distance_calculator.utils.Permissions.mLastKnownLocation
import com.example.riby_distance_calculator.utils.Permissions.mLocationPermissionGranted
import com.example.riby_distance_calculator.utils.Permissions.updateLocationUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var mapView: MapView
    private var mPlacesClient: PlacesClient? = null
    private lateinit var mMap: GoogleMap
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mDefaultLocation = LatLng(-33.8523341, 151.2106085)
    private  var longitude:Double = 0.0
    private var latitude:Double = 0.0

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var distanceViewModel: DistanceViewModel



    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (this.application as BaseApplication).component.inject(this)
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        mPlacesClient = Places.createClient(this)
        mapView = binding.map
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        distanceViewModel = ViewModelProvider(this,viewModelFactory).get(DistanceViewModel::class.java)



        //Start button
        binding.btnStartLocationUpdates.setOnClickListener {
            mMap.clear()
            if(mLocationPermissionGranted) {
                binding.btnStartLocationUpdates.visibility = View.GONE
                binding.btnStopLocationUpdates.visibility = View.VISIBLE
                deviceLocation("Start", object : GetResult {
                    override fun onSuccess(result: String) {
                        distanceViewModel.getStartCoordinate(result)

                    }

                    override fun onFailure(failed: String) {

                    }

                })
            }else{
                getLocationPermission(this,mMap)
                Snackbar.make(it,"Permission Required",Snackbar.LENGTH_LONG).show()
            }
        }


        //Stop button
        binding.btnStopLocationUpdates.setOnClickListener {
            getLocationPermission(this,mMap)
            binding.btnStartLocationUpdates.visibility = View.VISIBLE
            binding.btnStopLocationUpdates.visibility = View.GONE
            deviceLocation("Stop",object : GetResult {
                override fun onSuccess(result: String) {
                    distanceViewModel.getStopCoordinate(result)
                    distanceViewModel.getDistanceFromLatLonInKm()
                    distanceViewModel.drawLine(mMap)
                   // binding.distance.text = distance.toString()
                }
                override fun onFailure(failed: String) {
                    Toast.makeText(this@MainActivity,"Error Fetching Result", Toast.LENGTH_LONG).show()
                }
            })

        }

        //Display Distance
        distanceViewModel.items.observeForever {
            if(it == -1.0){
                Snackbar.make(binding.root,"Error Calculating Distance",Snackbar.LENGTH_LONG).show()
            }else {
                val distance = "%.2f".format(it)
                Log.e("HELOO", it.toString())
                binding.distance.text = distance + "Meters"
            }
        }


        distanceViewModel.getAll().observeForever {
            // to get all from database
        }


    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        // Prompt the user for permission.
        getLocationPermission(this,mMap)
        updateLocationUI(mMap,this)


    }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }

    }



    //DEVICE LOCATION
    private fun deviceLocation(pos:String,getResult: GetResult) {
        try {
            if (mLocationPermissionGranted) {
                val locationResult = mFusedLocationProviderClient!!.lastLocation
                locationResult.addOnCompleteListener(this) { task ->

                    if (task.isSuccessful) { // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.result
                        if (mLastKnownLocation != null) {
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        Permissions.mLastKnownLocation!!.latitude,
                                        Permissions.mLastKnownLocation!!.longitude), Permissions.DEFAULT_ZOOM.toFloat()))

                            // Log.e("Latitude", mLastKnownLocation!!.latitude.toString())
                            // Log.e("Longitude",mLastKnownLocation!!.longitude.toString())
                            mDefaultLocation = LatLng(mLastKnownLocation!!.latitude, Permissions.mLastKnownLocation!!.longitude)
                            val marker = mMap.addMarker(
                                MarkerOptions()
                                    .position(mDefaultLocation).visible(true).title(pos))

                            marker.isVisible = true
                            latitude = mLastKnownLocation!!.latitude
                            longitude = mLastKnownLocation!!.longitude
                            getResult.onSuccess("$longitude/$latitude")

                        }
                    } else {
                        Log.d(Permissions.TAG, "Current location is null. Using defaults.")
                        Log.e(Permissions.TAG, "Exception: %s", task.exception)
                       // getResult.onFailure("Not Found")
                        Toast.makeText(this,"Check Network Connection", Toast.LENGTH_LONG).show()
                        mMap.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, Permissions.DEFAULT_ZOOM.toFloat()))
                        mMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
            getLocationPermission(this,mMap)
        } catch (e: SecurityException) {
            Toast.makeText(this,"Location request isn't Enabled", Toast.LENGTH_LONG).show()
            Log.e("Exception: %s", e.message.toString())
        }
    }


    override fun onStart() {
        super.onStart()
        Log.e("Started","Started")
    }

    override fun onRestart() {
        super.onRestart()
        Log.e("Restart","Started")
    }

    override fun onResume() {
        super.onResume()
        Log.e("Resume","Started")

    }

}
