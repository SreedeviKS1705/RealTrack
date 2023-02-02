package com.qaptive.location.realtrack

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.google.maps.android.PolyUtil
import kotlinx.android.synthetic.main.fragment_track.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.math.ln


class TrackFragment : Fragment(),OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    lateinit var databaseReference: DatabaseReference
    var previousLatLng: LatLng? = null
    var currentLatLng: LatLng? = null
    private var polyline1: Polyline? = null

    private val polylinePoints: ArrayList<LatLng> = ArrayList()
    private var mCurrLocationMarker: Marker? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.view_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setDirectionApi()

        fetchUpdatedLocation()
    }

    private fun setDirectionApi() {
        val fromFKIP = currentLatLng?.latitude.toString() + "," + currentLatLng?.longitude.toString()
        val toMonas = "10.0159" + "," + "76.3419"
       /* val fromFKIP = "-6.3037978" + "," + "106.8693713"
        val toMonas = "-6.1890511" + "," + "106.8251573"*/

        val apiServices = RetrofitClient.apiServices(activity?.applicationContext!!)
        apiServices.getDirection(fromFKIP, fromFKIP, "AIzaSyDQ2vwYGkMvETlWpgFhr6o24OZq4SPh7MI")
            .enqueue(object : Callback<DirectionResponses> {
                override fun onResponse(call: Call<DirectionResponses>, response: Response<DirectionResponses>) {
                    if (response.body()?.status.equals("OK")) {
                        setPolylines(response)
                        Log.d("bisa dong oke", response.message())
                    }
                }

                override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                    Log.e("anjir error", t.localizedMessage)
                }
            })
    }

    private fun setPolylines(response: Response<DirectionResponses>) {
        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points
        val polylineOptions = PolylineOptions()
        polylineOptions.color(Color.RED)
        polylineOptions.addAll(PolyUtil.decode(shape))
        polylineOptions.geodesic(true)
        polylineOptions.width(8f)
        polylineOptions.addAll(polylinePoints)

        mMap.addPolyline(polylineOptions)
    }

    private fun fetchUpdatedLocation() {

        databaseReference = FirebaseDatabase.getInstance().getReference("location")
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                updateMap(snapshot)

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

        /*databaseReference = FirebaseDatabase.getInstance().reference
        val lastQuery: com.google.firebase.database.Query = databaseReference.child("location").orderByKey().limitToLast(1)
        lastQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                updateMap(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })*/

    }

    private fun updateMap(dataSnapshot: DataSnapshot) {
        var latitude = 0.0
        var longitude = 0.0
        val data = dataSnapshot.childrenCount

        val locationModel = dataSnapshot.getValue((LocationModel::class.java))

       /* for (d in 0 until data) {
            latitude = dataSnapshot.child("latitude").getValue(Double::class.java)!!.toDouble()
            longitude = dataSnapshot.child("longitude").getValue(Double::class.java)!!.toDouble()
        }*/
        latitude= locationModel?.latitude!!
        longitude=locationModel.longitude!!

        currentLatLng = LatLng(latitude, longitude)

        if (previousLatLng == null || previousLatLng !== currentLatLng) {
            // add marker line
            previousLatLng = currentLatLng
            polylinePoints.add(currentLatLng!!)
           // polyline1!!.points = polylinePoints

            if (mCurrLocationMarker != null) {
                mCurrLocationMarker!!.position = currentLatLng!!
            } else {
                mCurrLocationMarker = mMap.addMarker(
                    MarkerOptions()
                        .position(currentLatLng!!)
                )
            }
            val polylineOptions = PolylineOptions()
            polylineOptions.color(Color.RED)
            polylineOptions.geodesic(true)
            polylineOptions.width(8f)
            polylineOptions.addAll(polylinePoints)
            mMap.addPolyline(polylineOptions)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng!!, 15f))
        }
    }


    private interface ApiServices {
        @GET("maps/api/directions/json")
        fun getDirection(@Query("origin") origin: String,
                         @Query("destination") destination: String,
                         @Query("key") apiKey: String): Call<DirectionResponses>
    }

    private object RetrofitClient {
        fun apiServices(context: Context): ApiServices {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(context.resources.getString(R.string.base_url))
                .build()

            return retrofit.create<ApiServices>(ApiServices::class.java)
        }
    }


}