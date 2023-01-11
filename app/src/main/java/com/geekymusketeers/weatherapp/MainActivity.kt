package com.geekymusketeers.weatherapp


import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.geekymusketeers.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var MyLat:Double = 0.0
    var MyLong:Double = 0.0
    private var CITY: String = ""
    val API: String = "1600e7751d62b47e1535d2ee5442c724"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()

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
                weatherask().execute()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class weatherask() : AsyncTask<String, Void, String>()
    {
        override fun onPreExecute() {
            super.onPreExecute()
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            findViewById<ConstraintLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorMessage).visibility = View.GONE

        }

        override fun doInBackground(vararg p0: String?): String? {

            //currentCordinates()
            var response:String? = try{
                URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                    Charsets.UTF_8)

            } catch(e : Exception) {
                null
            }

            return response
        }

        @SuppressLint("DefaultLocale")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val updatedAt:Long = jsonObj.getLong("dt")
                val updatedAtText = "Updated at: "+ SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(Date(updatedAt*1000))
                val temp = main.getString("temp")+"°C"
                val tempMin = "Min Temp: " + main.getString("temp_min")+"°C"
                val tempMax = "Max Temp: " + main.getString("temp_max")+"°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")

                val address = jsonObj.getString("name")+", "+sys.getString("country")

                binding.address.text = address
                binding.updatedAt.text = updatedAtText
                binding.status.text = weatherDescription.capitalize()
                binding.temp.text = temp
                binding.minTemp.text = tempMin
                binding.maxTemp.text = tempMax
                binding.sunrise.text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise * 1000))
                binding.sunset.text =
                    SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset * 1000))
                binding.pressure.text = pressure
                binding.wind.text = windSpeed
                binding.humidity.text = humidity

                binding.progressBar.visibility = View.GONE
                binding.mainContainer.visibility = View.VISIBLE
            }
            catch (e: Exception)
            {
                binding.progressBar.visibility = View.GONE
                binding.errorMessage.visibility = View.VISIBLE
            }
        }
    }
}