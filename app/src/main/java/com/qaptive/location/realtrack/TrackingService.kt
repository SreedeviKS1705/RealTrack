package com.qaptive.location.realtrack

import android.Manifest
import android.R
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.math.log


class TrackingService : Service(){

    private var locationManager: LocationManager? = null
    private lateinit var mLocationCallback: LocationListener
    private val mBinder = LocalBinder()


    override fun onCreate() {
        super.onCreate()
        locationManager = baseContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        buildNotification();
        loginToFirebase();

        mLocationCallback = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (location != null)
                    Log.d("TAG_Track", "onLocationChanged: "+location.latitude)
                    //onNewLocation(location)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

            override fun onProviderEnabled(provider: String) {

            }

            override fun onProviderDisabled(provider: String) {

            }
        }
    }

    protected var stopReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            unregisterReceiver(this)

            stopSelf()
        }
    }

    private fun requestLocationUpdates() {
        val request = LocationRequest()

        request.setInterval(1000)

        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val client = LocationServices.getFusedLocationProviderClient(this)
        val path = "location/"
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {

            client.requestLocationUpdates(request, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location = locationResult.lastLocation
                //    val previousLocation = getLastSavedLocation()
//                    val distance=location?.distanceTo(previousLocation)
                    val ref = FirebaseDatabase.getInstance().getReference(path).push()
                    if (location != null) {
                        ref.setValue(location)

                    }
                }
            }, null)
        }
    }

    private fun getLastSavedLocation(): Location? {
        var locationData : Location? = null
        val databaseReference = FirebaseDatabase.getInstance().reference
        val lastQuery: Query = databaseReference.child("location/").orderByKey().limitToLast(1)
        lastQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val td = dataSnapshot.value as HashMap<String, Any>?
                    ?: return
                val lat = td["latitude"].toString().toDouble()
                val lag = td["longitude"].toString().toDouble()

                Log.d("TAG_TRACK", "onDataChange: "+lat+" : "+lag)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors.
            }
        })
        return locationData
    }

    private fun buildNotification() {
        val stop = "stop"
        registerReceiver(stopReceiver, IntentFilter(stop))
        val broadcastIntent = PendingIntent.getBroadcast(
            this, 0, Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT
        )

// Create the persistent notification//
        val builder: Notification.Builder = Notification.Builder(this)
            .setContentTitle("Real Track")
            .setContentText("Real Track Notification") //Make this notification ongoing so it can’t be dismissed by the user//
            .setOngoing(true)
            .setContentIntent(broadcastIntent)
            .setSmallIcon(R.drawable.btn_star)
        startForeground(1, builder.build())
    }

    private fun loginToFirebase() {

        val email = "test@test.com"
        val password = "123456"

        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            email, password
        ).addOnCompleteListener { task ->
            //If the user has been authenticated...//
            if (task.isSuccessful) {

                requestLocationUpdates()
            } else {

                Log.d("TAG", "Firebase authentication failed")
            }
        }
    }



    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    inner class LocalBinder : Binder() {
        internal val service: TrackingService
            get() = this@TrackingService
    }


}