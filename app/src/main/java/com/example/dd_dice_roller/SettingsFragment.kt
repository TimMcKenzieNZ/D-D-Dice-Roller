package com.example.dd_dice_roller

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import java.util.*
import android.provider.SyncStateContract.Helpers.update
import android.R.id.edit
import android.provider.SyncStateContract.Helpers.update
import android.R.id.edit
import android.provider.SyncStateContract.Helpers.update
import android.R.id.edit
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log


// class to store the settings fragment for the app preferences
class SettingsFragment : PreferenceFragmentCompat() {

    lateinit var languageCheck: Preference


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        languageCheck = findPreference("raceLanguage") // would be good to pull this out to strings
        languageCheck.setOnPreferenceChangeListener(object: Preference.OnPreferenceChangeListener {

            override fun onPreferenceChange(pref: Preference, value: Any): Boolean {
                goNative()
                var prefs = PreferenceManager.getDefaultSharedPreferences(activity)
                var editor = prefs.edit()
                editor.putBoolean("raceLanguage", value.toString().toBoolean())
                editor.commit()
                return true
            }

        })


    }




    val applicationContext = activity?.applicationContext
    fun goNative() {
        val res = getResources()
        val dm = res.getDisplayMetrics()
        val conf = res.getConfiguration()

        val character = (activity?.application as CustomApplication).character
        when (character.race) {
            "DragonBorn" -> {
                Toast.makeText(
                    applicationContext,
                    applicationContext?.getString(R.string.badLanguage),
                    Toast.LENGTH_LONG
                ).show()
            }
            "Dwarf" -> {
                if (language == "Common") {
                    conf.setLocale(Locale("th"))
                    conf.locale = Locale("th")
                    res.updateConfiguration(conf, dm)
                    language = "Dwarven"

                } else {
                    conf.setLocale(Locale("th"))
                    conf.locale = Locale("th")
                    res.updateConfiguration(conf, dm)
                    language = "Common"
                }
            }
            "Elf" -> {
                if (language == "Common") {
                    conf.setLocale(Locale("es")) // API 17+ only.
                    conf.locale = Locale("es")
                    res.updateConfiguration(conf, dm)
                    language = "Elvish"

                } else {
                    conf.setLocale(Locale("en"))
                    conf.locale = Locale("en")
                    res.updateConfiguration(conf, dm)
                    language = "Common"
                }

            }
            "Gnome" -> {
                Toast.makeText(
                    applicationContext,
                    applicationContext?.getString(R.string.badLanguage),
                    Toast.LENGTH_LONG
                ).show()
            }
            "HalfElf" -> {
                Toast.makeText(
                    applicationContext,
                    applicationContext?.getString(R.string.badLanguage),
                    Toast.LENGTH_LONG
                ).show()
            }
            "Halfling" -> {
                Toast.makeText(
                    applicationContext,
                    applicationContext?.getString(R.string.badLanguage),
                    Toast.LENGTH_LONG
                ).show()
            }
            "HalfOrc" -> {
                Toast.makeText(
                    applicationContext,
                    applicationContext?.getString(R.string.badLanguage),
                    Toast.LENGTH_LONG
                ).show()
            }
            "Human" -> {
                Toast.makeText(
                    applicationContext,
                    applicationContext?.getString(R.string.badLanguage),
                    Toast.LENGTH_LONG
                ).show()
            }
            else -> {
                Toast.makeText(
                    applicationContext,
                    applicationContext?.getString(R.string.badLanguage),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}