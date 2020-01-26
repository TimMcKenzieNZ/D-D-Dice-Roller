package com.example.dd_dice_roller


import android.util.JsonReader
import android.util.JsonWriter
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "heroes")
class Character(var name: String = "Unknown Hero",
                var race: String = "Unknown Race",
                var charClass: String = "Unknown Class",
                var hp: Int = 1,
                var currentHp: Int = 1,
                var strength: Int = 0,
                var dexterity: Int = 0,
                var constitution: Int = 0,
                var intelligence: Int = 0,
                var wisdom: Int = 0,
                var charisma: Int = 0,
                var sex: String = "Male",
                var ac: Int = 0,
                var level: Int = 1,
                var speed: Int = 10
) {

    @PrimaryKey(autoGenerate = true) var id: Long = 0

    fun updateRace(race: String) {
        if (this.race != race) {
            language = "Changed"
        }
        this.race = race
    }



    fun write(writer: JsonWriter) {
        writer.beginObject()
        writer.name("name").value(name)
        writer.name("race").value(race)
        writer.name("charClass").value(charClass)
        writer.name("hp").value(hp)
        writer.name("currentHp").value(currentHp)
        writer.name("strength").value(strength)
        writer.name("dexterity").value(dexterity)
        writer.name("constitution").value(constitution)
        writer.name("intelligence").value(intelligence)
        writer.name("wisdom").value(wisdom)
        writer.name("charisma").value(charisma)
        writer.name("sex").value(sex)
        writer.name("ac").value(ac)
        writer.name("level").value(level)
        writer.name("speed").value(speed)
        writer.endObject()

    }


    companion object {
        fun read(reader: JsonReader): Character {
            val character = Character()
            reader.beginObject()
            while (reader.hasNext()) {
                val key = reader.nextName()
                when (key) {
                    "name" -> character.name = reader.nextString()
                    "race" -> character.race = reader.nextString()
                    "charClass" -> character.charClass = reader.nextString()
                    "hp" -> character.hp = reader.nextInt()
                    "currentHp" -> character.currentHp = reader.nextInt()
                    "strength" -> character.strength = reader.nextInt()
                    "dexterity" -> character.dexterity = reader.nextInt()
                    "constitution" -> character.constitution = reader.nextInt()
                    "intelligence" -> character.intelligence = reader.nextInt()
                    "wisdom" -> character.wisdom = reader.nextInt()
                    "charisma" -> character.charisma = reader.nextInt()
                    "sex" -> character.sex = reader.nextString()
                    "ac" -> character.ac = reader.nextInt()
                    "level" -> character.level = reader.nextInt()

                    else -> character.speed = reader.nextInt()
                }
            }
            reader.endObject()


            return character
        }
    }

}
