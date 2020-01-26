package com.example.dd_dice_roller


import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import kotlin.system.exitProcess
import android.util.DisplayMetrics
import android.util.JsonWriter
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import kotlinx.android.synthetic.main.activity_navbar.*
import java.io.OutputStreamWriter
import java.util.*
import android.view.ViewGroup




class ActionList : AppCompatActivity() {


    private lateinit var listView: ListView




    // Model
//    class ItemPage(val name: String, val description: String, val imageUrl: Int) {
//        override fun toString() = name
//    }
    class ItemPage(val name: String, val description: String, val imageUrl: Int) {


        override fun toString() = name
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actionbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.settings_button -> {
            startActivity(Intent(this, Preferences::class.java))
            goNative(this)
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

    override fun onResume() {
        super.onResume()
        goNative(this)
        val character = (application as CustomApplication).character
        hpText.setText(character.currentHp.toString())
        val vg = findViewById<ViewGroup>(R.id.actionList)
        calculateHp(character.currentHp, character.hp)
        goNative(this)
     //   vg.invalidate()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        goNative(this)
//        val vg = findViewById<ViewGroup>(R.id.actionList)
//        val hpText: TextView = findViewById(R.id.hpText)
//        val character = (application as CustomApplication).character
//        Log.d("FOO", "${character.currentHp} <--------------------------")
//        hpText.setText(character.currentHp.toString())
//        vg.invalidate()
//
//        super.onActivityResult(requestCode, resultCode, data)
//
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        goNative(this)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_action_list)
        val character = (application as CustomApplication).character
        calculateHp(character.currentHp, character.hp)


        var ItemPages = arrayOf<ItemPage>(
            ItemPage(this.getString(R.string.attack), this.getString(R.string.might), R.drawable.sword),
            ItemPage(this.getString(R.string.use_ability), this.getString(R.string.heart), R.drawable.ab),
            ItemPage(this.getString(R.string.cast_spell), this.getString(R.string.cunning),R.drawable.cast)
        )






        var name: TextView = findViewById(R.id.nameText)
        var classText: TextView = findViewById(R.id.classText)
        var race: TextView = findViewById(R.id.raceText)
        var hpText: TextView = findViewById(R.id.hpText)
        var acText: TextView = findViewById(R.id.ac)
        var levelText: TextView = findViewById(R.id.level)
        var speedText: TextView = findViewById(R.id.speed)



        name.setText(character.name)
        classText.setText(character.charClass)
        race.setText(character.race)
        acText.setText(this.getString(R.string.ac) + ": " + character.ac.toString())
        levelText.setText(this.getString(R.string.level) + ": " + character.level.toString())
        speedText.setText(this.getString(R.string.speed) + ": " + character.speed.toString())
        hpText.setText(character.currentHp.toString())







        var increment: Button = findViewById(R.id.incrementHp)
        increment.setOnClickListener {
            if (character.currentHp < character.hp) {
                character.currentHp ++
                calculateHp(character.currentHp, character.hp)
                hpText.setText(character.currentHp.toString())
            }
        }

        var decrement: Button = findViewById(R.id.decrementHp)
        decrement.setOnClickListener {
            if (character.currentHp > 0) {
                character.currentHp --
                calculateHp(character.currentHp, character.hp)
                hpText.setText(character.currentHp.toString())
            }
        }

        var sexButton: ImageButton = findViewById(R.id.sexButton)

        sexButton.setOnClickListener {
            if (character.sex == "Male") {
                character.sex = "Female"
                setPic()
                sexButton.setImageResource(R.drawable.female)



            } else {
                character.sex = "Male"
                setPic()
                sexButton.setImageResource(R.drawable.male)
            }
        }

        setPic()




        listView = findViewById(R.id.ItemPageList)
        var listAdapter = CustomListAdapter(this, ItemPages)
        listView.setAdapter(listAdapter)

        listView.setOnItemClickListener() {_, _, actionId: Int, _ ->
            val intent = Intent(this, dieRoller_page::class.java)
            when (actionId) {
                0 -> {
                    intent.putExtra("type", "Attack")
                    val sword: MediaPlayer = MediaPlayer.create(this, R.raw.sword)
                    sword.start()
                }
                1 ->  {
                    intent.putExtra("type", "Ability")
                    val ability: MediaPlayer = MediaPlayer.create(this, R.raw.ability)
                    ability.start()
                }
                else -> {
                    intent.putExtra("type", "Spell")
                    val spell: MediaPlayer = MediaPlayer.create(this, R.raw.spell)
                    spell.start()
                }
            }
            //intent.putExtra("character", character)
            startActivity(intent)

        }




        val hpImage: ImageButton = findViewById(R.id.healthBar)
        hpImage.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val form = layoutInflater.inflate(R.layout.hp_text, null, false)
            builder.setView(form)
            val character = (application as CustomApplication).character


            val editBox: EditText = form.findViewById(R.id.entryBox)
            builder.setMessage(this.getString(R.string.health_set))
            editBox.setText(character.hp.toString())



            builder.setPositiveButton(this.getString(R.string.done)) { _, _ ->
                try {
                    var newHp = editBox.text.toString().replace("\n", "").toInt()
                    if (newHp in 1 .. 999) {
                        character.hp = newHp
                        character.currentHp = character.hp
                        calculateHp(character.currentHp, character.hp)
                        hpText.setText(character.hp.toString())
                    } else {
                        Toast.makeText(this, this.getString(R.string.badHp), Toast.LENGTH_LONG).show()
                    }


                }catch (e: NumberFormatException) {

                    Toast.makeText(this, this.getString(R.string.badHp), Toast.LENGTH_LONG).show()
                }
            }

            builder.show()
        }

        setClassName()
        setPic()

    }
    fun setClassName() {
        val character = (application as CustomApplication).character
        var classText: TextView = findViewById(R.id.classText)
        when(character.charClass) {
            "Barbarian" -> classText.text = this.getString(R.string.Barbarian)
            "Bard" -> classText.text = this.getString(R.string.Bard)
            "Cleric" -> classText.text = this.getString(R.string.Cleric)
            "Druid" -> classText.text = this.getString(R.string.Druid)
            "Fighter" -> classText.text = this.getString(R.string.Fighter)
            "Monk" -> classText.text = this.getString(R.string.Monk)
            "Paladin" -> classText.text = this.getString(R.string.Paladin)
            "Ranger" -> classText.text = this.getString(R.string.Ranger)
            "Rogue" -> classText.text = this.getString(R.string.Rogue)
            "Sorcerer" -> classText.text = this.getString(R.string.Sorcerer)
            "Warlock" -> classText.text = this.getString(R.string.Warlock)
            "Wizard" -> classText.text = this.getString(R.string.Wizard)
        }
    }

    fun setPic() {
        val character = (application as CustomApplication).character
        var image: ImageButton = findViewById(R.id.heroButton)
        var raceText: TextView = findViewById(R.id.raceText)
        var layout: ListView = findViewById(R.id.ItemPageList)



        when(character.race) {
            "DragonBorn" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.dragonbornmale) else image.setImageResource(
                    R.drawable.dragonbornfemale
                )
                raceText.text = this.getString(R.string.dragonborn)
                layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dragonborn))
            }
            "Dwarf" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.dwarfmale) else image.setImageResource(R.drawable.dwarffemale)
                raceText.text = this.getString(R.string.dwarf)
                layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dwarf))
            }
            "Elf" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.elfmale) else image.setImageResource(R.drawable.elffemale)
                raceText.text = this.getString(R.string.elf)
                layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.elf))
            }
            "Gnome" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.gnonemale) else image.setImageResource(R.drawable.gnomefemale)
                raceText.text = this.getString(R.string.gnome)
                layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gnome))
            }
            "HalfElf" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.halfelfmale) else image.setImageResource(
                    R.drawable.halfelffemale
                )
                raceText.text = this.getString(R.string.halfelf)
                layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.halfelf))
            }
            "Halfling" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.halflingmale) else image.setImageResource(
                    R.drawable.halflingfemale
                )
                raceText.text = this.getString(R.string.halfling)
                layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.halfling))
            }
            "HalfOrc" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.halforkmale) else image.setImageResource(
                    R.drawable.halforkfemale
                )
                raceText.text = this.getString(R.string.halforc)
                layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.halforc))
            }
            "Human" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.humanmale) else image.setImageResource(R.drawable.humanfemale)
                raceText.text = this.getString(R.string.human)
                layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.human))
            }
            else -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.tieflingmale) else image.setImageResource(
                    R.drawable.tieflingfemale
                )
                raceText.text = this.getString(R.string.tiefling)
                layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tiefling))
            }
        }
    }



    fun gotoStatistics(view: View) {

        val intent = Intent(this, statistics::class.java)
        startActivity(intent)

    }




//    fun goNative() {
//        val res = getResources()
//        val dm = res.getDisplayMetrics()
//        val conf = res.getConfiguration()
//
//        val character = (application as CustomApplication).character
//        when(character.race) {
//            "DragonBorn" -> {
//                Toast.makeText(applicationContext, applicationContext.getString(R.string.badLanguage), Toast.LENGTH_LONG).show()
//            }
//            "Dwarf" -> {
//                if (language == "Common") {
//                    conf.setLocale(Locale("th"))
//                    conf.locale = Locale("th")
//                    res.updateConfiguration(conf, dm)
//                    language = "Dwarven"
//                    setClassName()
//                    setPic()
//
//                } else {
//                    conf.setLocale(Locale("th"))
//                    conf.locale = Locale("th")
//                    res.updateConfiguration(conf, dm)
//                    language = "Common"
//                    setClassName()
//                    setPic()
//                }
//            }
//            "Elf" -> {
//                if (language == "Common") {
//                    conf.setLocale(Locale("es")) // API 17+ only.
//                    conf.locale = Locale("es")
//                    res.updateConfiguration(conf, dm)
//                    language = "Elvish"
//                    setClassName()
//                    setPic()
//
//                } else {
//                    conf.setLocale(Locale("en"))
//                    conf.locale = Locale("en")
//                    res.updateConfiguration(conf, dm)
//                    language = "Common"
//                    setClassName()
//                    setPic()
//                }
//
//            }
//            "Gnome" -> {
//                Toast.makeText(applicationContext, applicationContext.getString(R.string.badLanguage), Toast.LENGTH_LONG).show()
//            }
//            "HalfElf" -> {
//                Toast.makeText(applicationContext, applicationContext.getString(R.string.badLanguage), Toast.LENGTH_LONG).show()
//            }
//            "Halfling" -> {
//                Toast.makeText(applicationContext, applicationContext.getString(R.string.badLanguage), Toast.LENGTH_LONG).show()
//            }
//            "HalfOrc" -> {
//                Toast.makeText(applicationContext, applicationContext.getString(R.string.badLanguage), Toast.LENGTH_LONG).show()
//            }
//            "Human" -> {
//                Toast.makeText(applicationContext, applicationContext.getString(R.string.badLanguage), Toast.LENGTH_LONG).show()
//            }
//            else -> {
//                Toast.makeText(applicationContext, applicationContext.getString(R.string.badLanguage), Toast.LENGTH_LONG).show()
//            }
//        }
//
//    }


    override fun onStop() {
        super.onStop()
//        val file = openFileOutput("character.json", Context.MODE_PRIVATE)
//        val jsonWriter = JsonWriter(OutputStreamWriter(file))
//        jsonWriter.setIndent(" ")
//        val character = (application as CustomApplication).character
//        character.write(jsonWriter)
//        jsonWriter.close()
        (application as CustomApplication).database.characterDao().update((application as CustomApplication).character)
    }

    fun calculateHp(current: Int, max: Int) {
        val hpImage: ImageButton = findViewById(R.id.healthBar)
        var hp: Float = current/max.toFloat()
        when  {
            hp <= 0.0 -> hpImage.setImageResource(R.drawable.hp16)
            hp <= 0.0625 -> hpImage.setImageResource(R.drawable.hp15)
            hp <= 0.125 -> hpImage.setImageResource(R.drawable.hp14)
            hp <= 0.1875 -> hpImage.setImageResource(R.drawable.hp13)
            hp <= 0.25 -> hpImage.setImageResource(R.drawable.hp12)
            hp <= 0.3125 -> hpImage.setImageResource(R.drawable.hp11)
            hp <= 0.375 -> hpImage.setImageResource(R.drawable.hp10)
            hp <= 0.4375 -> hpImage.setImageResource(R.drawable.hp09)
            hp <= 0.5 -> hpImage.setImageResource(R.drawable.hp08)
            hp <= 0.5625 -> hpImage.setImageResource(R.drawable.hp07)
            hp <= 0.625 -> hpImage.setImageResource(R.drawable.hp06)
            hp <= 0.6875 -> hpImage.setImageResource(R.drawable.hp05)
            hp <= 0.75 -> hpImage.setImageResource(R.drawable.hp04)
            hp <= 0.8125 -> hpImage.setImageResource(R.drawable.hp03)
            hp <= 0.875 -> hpImage.setImageResource(R.drawable.hp02)
            hp <= 0.9375 -> hpImage.setImageResource(R.drawable.hp01)
            else -> {
                hpImage.setImageResource(R.drawable.full)
            }
        }

    }




}



