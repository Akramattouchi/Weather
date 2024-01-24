package com.gl4.weather

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            // Listen for changes in the city preference
            val cityPreference: EditTextPreference? = findPreference("pref_key_city")
            cityPreference?.setOnPreferenceChangeListener { _, newValue ->
                // Update the city value in WeatherActivity when the preference changes
                val city = newValue.toString()
                updateCityInWeatherActivity(city)
                true
            }
            fun getCurrentTheme(): String {
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
                return sharedPreferences.getString("pref_key_theme", "gradient_bg") ?: "gradient_bg"
            }

            // Listen for changes in the theme preference
            val themePreference: ListPreference? = findPreference("pref_key_theme")
            themePreference?.setOnPreferenceChangeListener { _, newValue ->
                val theme = newValue.toString()
                updateAppTheme(theme)
                true
            }
            val temperatureUnitPreference: ListPreference? = findPreference("pref_key_temperature_unit")
            temperatureUnitPreference?.setOnPreferenceChangeListener { _, newValue ->
                // Remove the default value from SharedPreferences
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
                val editor = sharedPreferences.edit()
                editor.remove("pref_key_temperature_unit")
                editor.apply()

                // Save the new temperature unit
                editor.putString("pref_key_temperature_unit", newValue.toString())
                editor.apply()
                true
            }
        }
        private fun updateTemperatureUnitInWeatherActivity(temperatureUnit: String) {
            // Save the selected temperature unit in SharedPreferences
            val sharedPreferences1 = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val editor = sharedPreferences1.edit()
            editor.putString("pref_key_temperature_unit", temperatureUnit)
            editor.apply()
        }

        private fun updateCityInWeatherActivity(city: String) {
            // Save the selected city in SharedPreferences
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val editor = sharedPreferences.edit()
            editor.putString("pref_key_city", city)
            editor.apply()
        }

        private fun updateAppTheme(theme: String) {
            // Save the selected theme in SharedPreferences
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            val editor = sharedPreferences.edit()
            editor.putString("pref_key_theme", theme)
            editor.apply()

            // Update the app theme dynamically
            val themeMode = when (theme) {
                "light" -> AppCompatDelegate.MODE_NIGHT_NO
                "dark" -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }

            AppCompatDelegate.setDefaultNightMode(themeMode)
        }


    }
}