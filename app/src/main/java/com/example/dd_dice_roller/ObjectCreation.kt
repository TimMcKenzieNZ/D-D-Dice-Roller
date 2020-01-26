package com.example.dd_dice_roller

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class ObjectCreation : AppCompatActivity() {



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.settings_button -> {
            startActivity(Intent(this, Preferences::class.java))
            true
        }
        R.id.party_button -> {
            startActivity(Intent(this, Party::class.java))
            goNative(this)
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
        setContentView(R.layout.activity_object_creation)
    }
}
