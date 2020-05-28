package com.example.riby_distance_calculator.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng


object Permissions{

     const val TAG = "Response"
     const val DEFAULT_ZOOM = 15
     const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
     const val KEY_CAMERA_POSITION = "camera_position"
     const val KEY_LOCATION = "location"
     var mCameraPosition: CameraPosition? = null
     var mLocationPermissionGranted = false
     var mLastKnownLocation: Location? = null
    private const val PERMISSIONS_REQUEST = 1


    //get permission
    fun getLocationPermission(activity: Activity,mMap: GoogleMap){
        if (ContextCompat.checkSelfPermission(activity.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
            
            val locationManager = activity.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager?
            val criteria = Criteria()
            val loc =  locationManager?.getLastKnownLocation(locationManager.getBestProvider(criteria, false))
            if (loc != null){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude, loc.longitude), 13.toFloat()));

                val cameraPosition =  CameraPosition.Builder()
                    .target(LatLng(loc.latitude, loc.longitude))      // Sets the center of the map to location user
                    .zoom(17.toFloat())                   // Sets the zoom
                    .bearing(90.toFloat())                // Sets the orientation of the camera to east
                    .tilt(40.toFloat())                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST
            )
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSIONS_REQUEST)
        }
    }


     fun updateLocationUI(mMap:GoogleMap,activity: Activity) {
        if (mMap == null) {
            return
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.isMyLocationEnabled = true
                mMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mMap.isMyLocationEnabled = false
                mMap.uiSettings.isMyLocationButtonEnabled = false
                mLastKnownLocation = null
                getLocationPermission(activity,mMap)
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message.toString())
        }
    }


}


