package com.gl4.weather

import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.preference.PreferenceManager
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class WeatherActivity : AppCompatActivity() {

    var CITY: String = "Tunis,tn"
    var API: String = "8afa65cd9160b44ce01ec3943ac7d50a" // Use API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val selectedCity = sharedPreferences.getString("pref_key_city", "Tunis,tn")

        // Use the selected city in your weather task
        CITY = selectedCity ?: "Tunis,tn"

        if (isConnectedToInternet()) {
            weatherTask().execute()
        } else {
            Toast.makeText(this, "No internet connection. Please check your internet connection.", Toast.LENGTH_SHORT).show()
        }
    }
    fun isConnectedToInternet(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun setAppTheme(theme: String) {
        when (theme) {
            "light" -> {
                setTheme(R.style.AppThemeLight)
                findViewById<View>(R.id.yourRootLayout)?.setBackgroundResource(R.drawable.gradient_bg_light)
            }
            "dark" -> {
                setTheme(R.style.AppThemeDark)
                findViewById<View>(R.id.yourRootLayout)?.setBackgroundResource(R.drawable.gradient_bg_dark)
            }
            else -> {
                setTheme(R.style.AppTheme)
                findViewById<View>(R.id.yourRootLayout)?.setBackgroundResource(R.drawable.gradient_bg)
            }
        }
    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */
            findViewById<ProgressBar>(R.id.loader).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.GONE
            findViewById<TextView>(R.id.errorText).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response: String? = null

            try {
                val sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity)
                val temperatureUnit = sharedPreferences1.getString("pref_key_temperature_unit", "metric")
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=$temperatureUnit&appid=$API").readText(
                    Charsets.UTF_8
                )
            } catch (e: IOException) {
                // Handle network errors here
                runOnUiThread {
                    Toast.makeText(this@WeatherActivity, "Network error. Please check your internet connection.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle other types of errors here
                runOnUiThread {
                    Toast.makeText(this@WeatherActivity, "An error occurred while fetching the weather data.", Toast.LENGTH_SHORT).show()
                }
            }

            return response
        }



        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                if (result != null) {
                    /* Extracting JSON returns from the API */
                    val jsonObj = JSONObject(result)
                    val main = jsonObj.getJSONObject("main")
                    val sys = jsonObj.getJSONObject("sys")
                    val wind = jsonObj.getJSONObject("wind")
                    val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                    val updatedAt: Long = jsonObj.getLong("dt")
                    val updatedAtText =
                        "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                            Date(updatedAt * 1000)
                        )
                    val temp = main.getString("temp") + "°C"
                    val tempMin = "Min Temp: " + main.getString("temp_min") + "°C"
                    val tempMax = "Max Temp: " + main.getString("temp_max") + "°C"
                    val pressure = main.getString("pressure")
                    val humidity = main.getString("humidity")

                    val sunrise: Long = sys.getLong("sunrise")
                    val sunset: Long = sys.getLong("sunset")
                    val windSpeed = wind.getString("speed")
                    val weatherDescription = weather.getString("description")

                    val address = jsonObj.getString("name") + ", " + sys.getString("country")

                    /* Populating extracted data into our views */
                    findViewById<TextView>(R.id.address).text = address
                    findViewById<TextView>(R.id.updated_at).text = updatedAtText
                    findViewById<TextView>(R.id.status).text = weatherDescription.capitalize()
                    findViewById<TextView>(R.id.temp).text = temp
                    findViewById<TextView>(R.id.temp_min).text = tempMin
                    findViewById<TextView>(R.id.temp_max).text = tempMax
                    findViewById<TextView>(R.id.sunrise).text =
                        SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                            Date(sunrise * 1000)
                        )
                    findViewById<TextView>(R.id.sunset).text =
                        SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(
                            Date(sunset * 1000)
                        )
                    findViewById<TextView>(R.id.wind).text = windSpeed
                    findViewById<TextView>(R.id.pressure).text = pressure
                    findViewById<TextView>(R.id.humidity).text = humidity

                    /* Views populated, Hiding the loader, Showing the main design */
                    findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                    findViewById<RelativeLayout>(R.id.mainContainer).visibility = View.VISIBLE
                } else {
                    // Handle the case where the result is null
                    findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                    findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                // Handle exceptions
                findViewById<ProgressBar>(R.id.loader).visibility = View.GONE
                findViewById<TextView>(R.id.errorText).visibility = View.VISIBLE
            }
        }
    }
}