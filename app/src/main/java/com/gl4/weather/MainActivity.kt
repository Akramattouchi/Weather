package com.gl4.weather

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setContentView(R.layout.activity_main)

        // Set up click listeners for the buttons
        findViewById<View>(R.id.btnGoToWeather).setOnClickListener {
            startActivity(Intent(this, WeatherActivity::class.java))
        }

        findViewById<View>(R.id.btnGoToSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
    fun getCurrentTheme(context: Context): String {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPreferences.getString("pref_key_theme", "gradient_bg") ?: "gradient_bg"
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
}