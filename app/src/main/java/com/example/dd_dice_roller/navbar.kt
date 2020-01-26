package com.example.dd_dice_roller

import android.app.AlertDialog
import android.app.Application
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.EditTextPreference
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.preference.PreferenceFragmentCompat
import java.io.Serializable
//import com.example.dd_dice_roller.prototype.R

class navbar : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val character = (application as CustomApplication).character

        //character = intent.getSerializableExtra("character") as Character // type casting in kotlin
        var name: TextView = findViewById(R.id.nameText)
        var className: TextView = findViewById(R.id.classText)
        var raceName: TextView = findViewById(R.id.raceText)
        name.text = character.name
        className.text = character.charClass
        raceName.text = character.race


        setContentView(R.layout.activity_navbar)


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

    //TODO: Add a chooser to pick the web provider


    fun gotoStatistics(view: View) {

        val intent = Intent(this, statistics::class.java)
        startActivity(intent)

    }


    fun gotoActionList(view: View) {

        val intent = Intent(this, ActionList::class.java)
        startActivity(intent)

    }


    fun calculateHp(current: Int, max: Int) {
        val hpImage: ImageButton = findViewById(R.id.healthBar)
        val hp: Float = current/max.toFloat()
        when  {
            hp >= 16/16 -> hpImage.setImageResource(R.drawable.full)
            hp >= 15/16 -> hpImage.setImageResource(R.drawable.hp01)
            hp >= 14/16 -> hpImage.setImageResource(R.drawable.hp02)
            hp >= 13/16 -> hpImage.setImageResource(R.drawable.hp03)
            hp >= 12/16 -> hpImage.setImageResource(R.drawable.hp04)
            hp >= 11/16 -> hpImage.setImageResource(R.drawable.hp05)
            hp >= 10/16 -> hpImage.setImageResource(R.drawable.hp06)
            hp >= 9/16 -> hpImage.setImageResource(R.drawable.hp07)
            hp >= 8/16 -> hpImage.setImageResource(R.drawable.hp08)
            hp >= 7/16 -> hpImage.setImageResource(R.drawable.hp09)
            hp >= 6/16 -> hpImage.setImageResource(R.drawable.hp10)
            hp >= 5/16 -> hpImage.setImageResource(R.drawable.hp11)
            hp >= 4/16 -> hpImage.setImageResource(R.drawable.hp12)
            hp >= 3/16 -> hpImage.setImageResource(R.drawable.hp13)
            hp >= 2/16 -> hpImage.setImageResource(R.drawable.hp14)
            hp >= 1/16 -> hpImage.setImageResource(R.drawable.hp15)
            else -> hpImage.setImageResource(R.drawable.hp16)
        }

    }

}

