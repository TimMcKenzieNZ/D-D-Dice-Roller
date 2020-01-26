package com.example.dd_dice_roller

import android.Manifest
import android.app.Activity
import android.os.AsyncTask
import android.app.AlertDialog
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.JsonReader
import android.util.JsonWriter
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableArrayList
import androidx.room.Room
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.URI
import java.net.URL
import java.util.*

//import androidx.preference.PreferenceFragmentCompat


const val KEY = ""

fun getCharacterImageId(race: String, sex: String): Int {
    when(race) {
        "DragonBorn" -> {
            if (sex == "Male") return R.drawable.dragonbornmale else return R.drawable.dragonbornfemale
        }
        "Dwarf" -> {
            if (sex == "Male") return R.drawable.dwarfmale else return R.drawable.dwarffemale
        }
        "Elf" -> {
            if (sex == "Male") return R.drawable.elfmale else return R.drawable.elffemale
        }
        "Gnome" -> {
            if (sex == "Male") return R.drawable.gnonemale else return R.drawable.gnomefemale
        }
        "HalfElf" -> {
            if (sex == "Male") return R.drawable.halfelfmale else return R.drawable.halfelffemale
        }
        "Halfling" -> {
            if (sex == "Male") return R.drawable.halflingmale else return R.drawable.halflingfemale
        }
        "HalfOrc" -> {
            if (sex == "Male") return R.drawable.halforkmale else return R.drawable.halforkfemale
        }
        "Human" -> {
            if (sex == "Male") return R.drawable.humanmale else return R.drawable.humanfemale
        }
        else -> {
            if (sex == "Male") return R.drawable.tieflingmale else return R.drawable.tieflingfemale
        }
    }
}



fun goNative(activity: Activity) {
    var prefs = PreferenceManager.getDefaultSharedPreferences(activity)
    val isNative: Boolean = prefs.getBoolean("raceLanguage", false)
    val res = activity.getResources()
    val dm = res.getDisplayMetrics()
    val conf = res.getConfiguration()


    val character = (activity.application as CustomApplication).character
    when (character.race) {
        "DragonBorn" -> {

        }
        "Dwarf" -> {
            if (isNative) {
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
            if (isNative) {
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

        }
        "HalfElf" -> {

        }
        "Halfling" -> {

        }
        "HalfOrc" -> {

        }
        "Human" -> {

        }
        else -> {

        }
    }

}
class MainActivity : PermittedActivity() {


    private lateinit var prefs: SharedPreferences

    //var database: CharacterDatabase? = null

    lateinit var database: CharacterDatabase
//        set(value) {
//            field = value
//            value?.let {
//                LoadDatabaseTask(this).execute()
//            }
//        }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        goNative(this)


        //val parameters = mapOf("language" to "en", "apiKey" to com.example.dd_dice_roller.KEY)
        //val url = parameterizeUrl("http://soundimage.org/wp-content/uploads/2014/02/Gentle-Closure.mp3", parameters)
        //
        // DownloadFileTask(this).execute(url)


        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100, {
            val intent = Intent(this, Introduction::class.java)
            //intent.putExtra("character", character)
            startActivity(intent)
        }, {
            Log.d("FOO", "Failed")
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.settings_button -> {
            startActivity(Intent(this, Preferences::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun onStop() {
        super.onStop()
    }



}

var language: String = "Common"



fun parameterizeUrl(url: String, parameters: Map<String, String>): URL {
    val builder = Uri.parse(url).buildUpon()
    // mapping our parameters on the given url
    // forEach is in API v24 onwards, takes in a lambda

    for ((key, value) in parameters) {
        builder.appendQueryParameter(key, value)
    }
//    parameters.forEach { key, value -> builder.appendQueryParameter(key, value) }
    val uri = builder.build()
    return URL(uri.toString())
}
