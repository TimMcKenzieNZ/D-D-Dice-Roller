package com.example.dd_dice_roller

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class HeroViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val nameText: TextView = view.findViewById(R.id.nameText)
    private val description: TextView = view.findViewById(R.id.description)
    private val image: ImageView = view.findViewById(R.id.actionImage)

    var isActive: Boolean = false
        set(value) {
            field = value

        }

    var hero: Hero = Hero("Unknown", "race", "class", "sex", 10, "endpointid")
        set(value) {
            field = value
            nameText.text = hero.name
            description.text = String.format("%s, %s", hero.race, hero.charClass)
            Picasso.get().load(getCharacterImageId(hero.race, hero.sex)).into(image)
        }


}