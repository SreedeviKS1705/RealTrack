package com.qaptive.location.realtrack

import android.Manifest
import android.R
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.qaptive.location.realtrack.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val PERMISSIONS_REQUEST = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

        }


        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )


        if (permission == PERMISSION_GRANTED) {
            startTrackerService()
        } else {

            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST
            )
        }

        binding.viewListButton.setOnClickListener {
            loadLocationList()
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSIONS_REQUEST && grantResults.size == 1
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


            startTrackerService();
        } else {


            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    private fun startTrackerService() {
        startService(Intent(this, TrackingService::class.java))

        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show()

    }

    private fun loadLocationList() {
        val reference = FirebaseDatabase.getInstance().getReference("location/")
        reference.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val locationData = snapshot.getValue(LocationModel::class.java)
                Log.d("TAG_TRACK_LIST", "onChildAdded: "+locationData?.latitude.toString()+"  :  "+locationData?.longitude.toString())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val locationData = snapshot.getValue(LocationModel::class.java)
                Log.d("TAG_TRACK_LIST", "onChildChanged: "+locationData?.latitude.toString()+"  :  "+locationData?.longitude.toString())
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


}