package com.example.riby_distance_calculator.screen


import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.riby_distance_calculator.model.Distance
import com.example.riby_distance_calculator.repository.DistanceRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject


class DistanceViewModel @Inject constructor(private val distanceRepository: DistanceRepository ):ViewModel() {

    private val viewModelJob = SupervisorJob()

    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var coordinateArray = mutableListOf<String>()

    val items: MutableLiveData<Double> = MutableLiveData()



    fun getStartCoordinate(startCoordinate:String){
        coordinateArray.clear()
        coordinateArray.add(startCoordinate)
    }


    fun getStopCoordinate(stopCoordinate:String){
        coordinateArray.add(stopCoordinate)
    }


    private fun save(coordinate: Distance){
        uiScope.launch {
            saveCoordinate(coordinate)
        }
    }


    private suspend fun saveCoordinate(item:Distance)= withContext(Dispatchers.IO) {
        distanceRepository.save(item)

    }

    fun getAll(): LiveData<List<Distance>> {
       return distanceRepository.getAllData()
    }


    //called on the stop
     fun getDistanceFromLatLonInKm():Double{
        Log.e("Coordinate",coordinateArray.toString())
        if (coordinateArray.isEmpty() || coordinateArray.size < 2) {
            return -1.0
        } else {
            val firstCoordinateString = coordinateArray[0].split("/")
            val secondCoordinateString = coordinateArray[1].split("/")
            val firstCoordinateLongitude = firstCoordinateString[0].toDouble()
            val firstCoordinateLatitude = firstCoordinateString[1].toDouble()
            val secondCoordinateLongitude = secondCoordinateString[0].toDouble()
            val secondCoordinateLatitude = secondCoordinateString[1].toDouble()

            //["1.234/2.345","1.234/2.345"]
           // [1.234,2.345]
            // calculate Distance

            val distance = SphericalUtil.computeDistanceBetween(LatLng(firstCoordinateLatitude,firstCoordinateLongitude),
                LatLng(secondCoordinateLatitude,secondCoordinateLongitude))
            val distanceCovered = "%.2f".format(distance) + "Meters"

            items.postValue(distance)
            val coordinate = Distance(
                firstCoordinateString[0],
                firstCoordinateString[1],
                secondCoordinateString[0],
                secondCoordinateString[1],distanceCovered
            )
            Log.e("Coordinate2222",coordinate.toString())
            save(coordinate)
            return distance
        }
    }


    private fun getURL(from : LatLng, to : LatLng) : String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "sensor=false"
        val params = "$origin&$dest&$sensor"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }


    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                lng.toDouble() / 1E5)
            poly.add(p)
        }
        return poly
    }



    private fun getDirection(from : LatLng, to : LatLng,context: Context,mMap: GoogleMap) { //Getting the URL
        val url: String = getURL(from,to)
        //Showing a dialog till we get the route
        val stringRequest = StringRequest(url,
            Response.Listener { response ->
                //Calling the method drawPath to draw the path
                drawPath(response,mMap)
                Log.e("Response.Listener",response)
            },
            Response.ErrorListener {
               // loading.dismiss()
            })
        //Adding the request to request queue
        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(stringRequest)
    }


    fun drawLine(mMap: GoogleMap){
        val firstCoordinateString = coordinateArray[0].split("/")
        val secondCoordinateString = coordinateArray[1].split("/")
        val firstCoordinateLongitude = firstCoordinateString[0].toDouble()
        val firstCoordinateLatitude = firstCoordinateString[1].toDouble()
        val secondCoordinateLongitude = secondCoordinateString[0].toDouble()
        val secondCoordinateLatitude = secondCoordinateString[1].toDouble()
//        getDirection(LatLng(firstCoordinateLatitude,firstCoordinateLongitude),
//            LatLng(secondCoordinateLatitude,secondCoordinateLongitude),context,googleMap)
        val line: Polyline = mMap.addPolyline(
            PolylineOptions()
                .add(LatLng(firstCoordinateLatitude,firstCoordinateLongitude),
                    LatLng(secondCoordinateLatitude,secondCoordinateLongitude))
                .width(20f)
                .color(Color.RED)
                .geodesic(true)
        )
        line.isVisible = true
    }



    private fun drawPath(result:String, mMap:GoogleMap){
        try { //Parsing json
            val json = JSONObject(result)
            val routeArray: JSONArray = json.getJSONArray("routes")
            val routes = routeArray.getJSONObject(0)
            val overviewPolylines = routes.getJSONObject("overview_polyline")
            val encodedString = overviewPolylines.getString("points")
            val list = decodePoly(encodedString)
            Log.e("lastLine",list.toString())
            val line: Polyline = mMap.addPolyline(
                PolylineOptions()
                    .addAll(list)
                    .width(20f)
                    .color(Color.RED)
                    .geodesic(true)
            )
            line.isVisible = true
        } catch (e: JSONException) {
        }
    }



    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()

    }


}