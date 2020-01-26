package com.example.dd_dice_roller

import android.content.Intent
import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.transition.TransitionManager
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_introduction.*

class Introduction : AppCompatActivity() {
    private lateinit var parentConstraint: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)
        goNative(this)
        val character = (application as CustomApplication).character
        var name: TextView = findViewById(R.id.nameText)
        var classText: TextView = findViewById(R.id.classText)
        var race: TextView = findViewById(R.id.raceText)
        var acText: TextView = findViewById(R.id.ac)
        var levelText: TextView = findViewById(R.id.level)
        var speedText: TextView = findViewById(R.id.speed)




        name.setText(character.name)
        classText.setText(character.charClass)
        race.setText(character.race)
        acText.setText(this.getString(R.string.ac) + ": " + character.ac.toString())
        levelText.setText(this.getString(R.string.level) + ": " + character.level.toString())
        speedText.setText(this.getString(R.string.speed) + ": " + character.speed.toString())



        val hpText: TextView = findViewById(R.id.hpText)
        hpText.setText(character.currentHp.toString())

        setPic()
        calculateHp(character.currentHp, character.hp)
        setPic()
        addAnimation()
        Handler().postDelayed({
            val intent = Intent(this, ActionList::class.java)
            startActivity(intent)
        }, 2300)

        setClassName()


    }

    private fun addAnimation() {
        parentConstraint = findViewById(R.id.root)

        val constraint1 = ConstraintSet()
        constraint1.clone(parentConstraint)
        val constraint2 = ConstraintSet()

        constraint2.load(this, R.layout.activity_introduction_alt)
        constraint1.applyTo(root)
        Handler().postDelayed({
            TransitionManager.beginDelayedTransition(root)
            constraint2.applyTo(root)
        }, 2000)



    }



    fun setPic() {
        val character = (application as CustomApplication).character
        var image: ImageView = findViewById(R.id.dragon)
        var raceText: TextView = findViewById(R.id.raceText)
        var layout: ConstraintLayout = findViewById(R.id.background)



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
