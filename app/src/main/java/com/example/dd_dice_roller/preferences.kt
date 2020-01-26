package com.example.dd_dice_roller

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceScreen
import kotlinx.android.synthetic.main.activity_navbar.*
import java.util.*

class Preferences : AppCompatActivity() {


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.settings_button -> {
            goNative(this)
            this.finish()

            true
        }
        R.id.party_button -> {
            startActivity(Intent(this, Party::class.java))
            true
        }
        R.id.website_button -> {
            val webIntent = Uri.parse("http://dnd.wizards.com").let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(webIntent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goNative(this)

        setContentView(R.layout.activity_preferences)

    }

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle? ) {
//        var view = super.onCreateView(inflater, container, savedInstanceState)
//        setView(view)
//        return view
//    }

//    override fun onCreateView(parent: View?, name: String?, context: Context?, attrs: AttributeSet?): View {
//        var view = super.onCreateView(parent, name, context, attrs)
//        setView(view)
//        return view
//
//    }

    override fun onCreateView(name: String?, context: Context?, attrs: AttributeSet?): View? {
        return super.onCreateView(name, context, attrs)
//        var view = super.onCreateView(name, context, attrs)
//        setViewBackground(view)
//        return view
    }
    override fun onResume() {
        super.onResume()
        goNative(this)
        //   vg.invalidate()
    }

    override fun onStop() {
        goNative(this)
        super.onStop()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        goNative(this)
        super.onActivityResult(requestCode, resultCode, data)

    }

    fun setViewBackground(view: View) {
        val character = (application as CustomApplication).character

        when(character.race) {
            "DragonBorn" -> {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dragonborn))
            }
            "Dwarf" -> {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dwarf))
            }
            "Elf" -> {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.elf))
            }
            "Gnome" -> {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gnome))
            }
            "HalfElf" -> {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.halfelf))
            }
            "Halfling" -> {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.halfling))
            }
            "HalfOrc" -> {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.halforc))
            }
            "Human" -> {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.human))
            }
            else -> {
                view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tiefling))
            }
        }
    }
}




