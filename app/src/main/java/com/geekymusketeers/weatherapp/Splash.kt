package com.geekymusketeers.weatherapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.geekymusketeers.weatherapp.databinding.ActivitySplashBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.*

class Splash : AppCompatActivity() {

//    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
//    var MyLat: Double = 0.0
//    var MyLong: Double = 0.0
//    private var CITY: String = ""

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 100
    }

    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        initViews()

        if (checkPermissionForReadExtertalStorage()) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 1000)
        } else {
            binding.grantPermissionButton.visibility = View.VISIBLE
            binding.lottieAnimationView.visibility = View.GONE
            binding.textView.visibility = View.GONE
        }
    }

    private fun initViews() {
        binding.grantPermissionButton.setOnClickListener{
            grant(it)
        }
        binding.allowPermission.setOnClickListener {
            grant(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissionForReadExtertalStorage()) {
            binding.grantPermissionButton.visibility = View.GONE
            binding.lottieAnimationView.visibility = View.VISIBLE
            binding.textView.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 1000)
            binding.allowPermission.visibility= View.GONE
        } else {
            binding.grantPermissionButton.text = "Grant location permission"
        }
    }

    private fun grant(view: View) {
        when (view.id) {
            R.id.grantPermissionButton -> if (checkPermissionForReadExtertalStorage()) {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.textView.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 1000)
            } else {
                //Make Request
                binding.grantPermissionButton.text = "Let's go!"
                requestPermission()
            }
            R.id.allow_permission -> {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            if (requestCode == REQUEST_LOCATION_PERMISSION) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    binding.allowPermission!!.visibility = View.GONE
                } else {
                    // permission denied
                    // Check whether checked dont ask again
                    checkUserRequestedDontAskAgain()
                }
            }
        }
    }

    private fun checkPermissionForReadExtertalStorage(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        } else {
            binding.grantPermissionButton.text = "Grant location permission"
            false
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun checkUserRequestedDontAskAgain() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val rationalFlagFine =
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
            val rationalFlagCOARSE =
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (!rationalFlagFine && !rationalFlagCOARSE) {
                binding.grantPermissionButton.text = "Grant location permission"
                binding.allowPermission.visibility = View.VISIBLE
            }
        }
    }
}