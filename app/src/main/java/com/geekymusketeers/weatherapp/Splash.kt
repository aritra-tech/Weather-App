package com.geekymusketeers.weatherapp

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.geekymusketeers.weatherapp.databinding.ActivitySplashBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class Splash : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var MyLat: Double = 0.0
    var MyLong: Double = 0.0
    private var CITY: String = ""

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //Hides action bar
        supportActionBar?.hide()
        checkLocationPermission()

        binding.permission.setOnClickListener {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            checkLocationPermission()
        }


    }
    private fun checkLocationPermission() {

        val ftask = fusedLocationProviderClient.lastLocation
        //check location permission
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            binding.permission.visibility = View.VISIBLE
            return
        }

        //get latitude and longitude
        ftask.addOnSuccessListener {
            if (it != null) {
                MyLat = it.latitude
                MyLong = it.longitude
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses: List<Address>? = geocoder.getFromLocation(MyLat, MyLong, 1)
                CITY = addresses!![0].locality
                Log.d("TAG", CITY)
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("city",CITY)
                    startActivity(intent)
                    finish()
                }, 1000)
            }
        }.addOnFailureListener {
            Toast.makeText(this,"Please tap on the screen to give permission",Toast.LENGTH_LONG).show()
        }

    }
}