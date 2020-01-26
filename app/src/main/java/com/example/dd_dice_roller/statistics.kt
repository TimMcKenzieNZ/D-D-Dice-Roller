package com.example.dd_dice_roller


import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.JsonReader
import android.util.JsonWriter
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.example.dd_dice_roller.R
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
//import sun.invoke.util.VerifyAccess.getPackageName
import android.widget.RemoteViews
import kotlinx.android.synthetic.main.activity_navbar.*
import java.util.*


class statistics : AppCompatActivity() {

    // Our list of character statistics
    private lateinit var listView: ListView


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
        setContentView(R.layout.activity_statistics)

        val character = (application as CustomApplication).character
//        listView = findViewById(R.id.attributeList)
//        val listAdapter = ArrayAdapter<Attribute>(this, android.R.layout.simple_list_item_1, attributes)
//        listView.setAdapter(listAdapter)


        var wisdom: EditText = findViewById(R.id.wisdomBox)
        var strength: EditText = findViewById(R.id.strengthBox)
        var dexterity: EditText = findViewById(R.id.dexterityBox)
        var constitution: EditText = findViewById(R.id.constitution)
        var charisma: EditText = findViewById(R.id.charismaBox)
        var intelligence: EditText = findViewById(R.id.intelligenceBox)



        var image: ImageButton = findViewById(R.id.heroButton)
        var layout: ConstraintLayout = findViewById(R.id.stats)


        var nameText: EditText = findViewById(R.id.nameBox)
        var acEdit: EditText = findViewById(R.id.acBox)
        var speedEdit: EditText = findViewById(R.id.speedBox)
        var classSpinner: Spinner = findViewById(R.id.classSpinner)
        var raceSpinner: Spinner = findViewById(R.id.raceSpinner)
        var levelSpinner: Spinner = findViewById(R.id.levelSpinner)
        var hpText: TextView = findViewById(R.id.hpText)



        image.setOnClickListener {
            gotoActionList()
        }





        nameText.setText(character.name)
        acEdit.setText(character.ac.toString())
        speedEdit.setText(character.speed.toString())
        levelSpinner.setSelection(character.level -1)
        hpText.setText(character.currentHp.toString())

        ArrayAdapter.createFromResource(
            this, R.array.level_array, android.R.layout.simple_spinner_dropdown_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            levelSpinner.adapter = adapter
        }


        ArrayAdapter.createFromResource(
            this, R.array.race_array, android.R.layout.simple_spinner_dropdown_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            raceSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this, R.array.class_array, android.R.layout.simple_spinner_dropdown_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            classSpinner.adapter = adapter
        }

        // MUST GO AFTER SPINNER ADAPTER IS INITIALIZED
        setSpinnerItemsAndPhoto(raceSpinner, classSpinner,character,image)



        raceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, number: Int, id: Long) {
                when(number) {
                    0 -> {

                        character.updateRace("DragonBorn")
                        if (character.sex == "Male") image.setImageResource(R.drawable.dragonbornmale) else image.setImageResource(R.drawable.dragonbornfemale)
                        layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dragonborn))
                        setLanguageToEnglishIfChanged()
                    }
                    1 -> {
                        character.updateRace("Dwarf")
                        if (character.sex == "Male") image.setImageResource(R.drawable.dwarfmale) else image.setImageResource(R.drawable.dwarffemale)
                        layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dwarf))
                        setLanguageToEnglishIfChanged()
                    }
                    2 -> {
                        character.updateRace("Elf")
                        if (character.sex == "Male") image.setImageResource(R.drawable.elfmale) else image.setImageResource(R.drawable.elffemale)
                        layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.elf))
                        setLanguageToEnglishIfChanged()
                    }
                    3 -> {
                        character.updateRace("Gnome")
                        if (character.sex == "Male") image.setImageResource(R.drawable.gnonemale) else image.setImageResource(R.drawable.gnomefemale)
                        layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.gnome))
                        setLanguageToEnglishIfChanged()
                    }
                    4 -> {
                        character.updateRace("HalfElf")
                        if (character.sex == "Male") image.setImageResource(R.drawable.halfelfmale) else image.setImageResource(R.drawable.halfelffemale)
                        layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.halfelf))
                        setLanguageToEnglishIfChanged()
                    }
                    5 -> {
                        character.updateRace("Halfling")
                        if (character.sex == "Male") image.setImageResource(R.drawable.halflingmale) else image.setImageResource(R.drawable.halflingfemale)
                        layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.halfling))
                        setLanguageToEnglishIfChanged()
                    }
                    6 -> {
                        character.updateRace("HalfOrc")
                        if (character.sex == "Male") image.setImageResource(R.drawable.halforkmale) else image.setImageResource(R.drawable.halforkfemale)
                        layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.halforc))
                        setLanguageToEnglishIfChanged()
                    }
                    7 -> {
                        character.updateRace("Human")
                        if (character.sex == "Male") image.setImageResource(R.drawable.humanmale) else image.setImageResource(R.drawable.humanfemale)
                        layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.human))
                        setLanguageToEnglishIfChanged()
                    }
                    8 -> {
                        character.updateRace("Tiefling")
                        if (character.sex == "Male") image.setImageResource(R.drawable.tieflingmale) else image.setImageResource(R.drawable.tieflingfemale)
                        layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.tiefling))
                        setLanguageToEnglishIfChanged()
                    }

                }
                
            }
        }

        when(character.charClass) {
            "Barbarian" -> {
                classSpinner.setSelection(0)
            }
            "Bard" -> classSpinner.setSelection(1)
            "Cleric" -> classSpinner.setSelection(2)
            "Druid" -> classSpinner.setSelection(3)
            "Fighter" -> classSpinner.setSelection(4)
            "Monk" -> classSpinner.setSelection(5)
            "Paladin" -> classSpinner.setSelection(6)
            "Ranger" -> classSpinner.setSelection(7)
            "Rogue" -> classSpinner.setSelection(8)
            "Sorcerer" -> classSpinner.setSelection(9)
            "Warlock" -> classSpinner.setSelection(10)
            "Wizard" -> classSpinner.setSelection(11)
        }

        classSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, number: Int, id: Long) {
                when(number) {
                    0 -> character.charClass = "Barbarian"
                    1 -> character.charClass = "Bard"
                    2 -> character.charClass = "Cleric"
                    3 -> character.charClass = "Druid"
                    4 -> character.charClass = "Fighter"
                    5 -> character.charClass = "Monk"
                    6 -> character.charClass = "Paladin"
                    7 -> character.charClass = "Ranger"
                    8 -> character.charClass = "Rogue"
                    9 -> character.charClass = "Sorcerer"
                    10 -> character.charClass = "Warlock"
                    else -> character.charClass = "Wizard"
                }

            }
        }

        levelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, number: Int, id: Long) {
                character.level = number + 1
            }
        }

        nameText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                character.name = nameText.text.toString()
            }
        })

        acEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (acEdit.text.toString().replace("\n", "") == "") character.ac = 0
                else character.ac = acEdit.text.toString().replace("\n", "").toInt()
            }
        })

        speedEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (speedEdit.text.toString().replace("\n", "") == "") character.speed = 0
                else character.speed = speedEdit.text.toString().replace("\n", "").toInt()
            }
        })






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


        wisdom.setText(character.wisdom.toString())
        strength.setText(character.strength.toString())
        dexterity.setText(character.dexterity.toString())
        charisma.setText(character.charisma.toString())
        constitution.setText(character.constitution.toString())
        intelligence.setText(character.intelligence.toString())

        wisdom.setOnEditorActionListener { _, i, _ ->

            if (i == EditorInfo.IME_ACTION_DONE) {
                try {
                    character.wisdom = wisdom.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "Wisdom updated", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Wisdom must be a valid number!", Toast.LENGTH_LONG).show()
                    //character.write()
                }
                false
            } else {
                try {
                    character.wisdom = wisdom.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "Wisdom updated", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Wisdom must be a valid number!", Toast.LENGTH_LONG).show()

                }
                false
                false
            }

        }

        strength.setOnEditorActionListener { _, i, _ ->

            if (i == EditorInfo.IME_ACTION_DONE) {
                try {
                    character.strength= strength.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "Strength updated", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Strength must be a valid number!", Toast.LENGTH_LONG).show()

                    //character.write()
                }
                false
            } else {
                try {
                    character.strength = strength.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "Strength updated", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Strength must be a valid number!", Toast.LENGTH_LONG).show()

                }
                false
                false
            }

        }

        dexterity.setOnEditorActionListener { _, i, _ ->

            if (i == EditorInfo.IME_ACTION_DONE) {
                try {
                    character.dexterity= dexterity.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "Dexterity updated", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Dexterity must be a valid number!", Toast.LENGTH_LONG).show()

                    //character.write()
                }
                false
            } else {
                try {
                    character.dexterity = dexterity.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "Dexterity updated", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Dexterity must be a valid number!", Toast.LENGTH_LONG).show()
                }
                false
                false
            }

        }

        charisma.setOnEditorActionListener { _, i, _ ->

            if (i == EditorInfo.IME_ACTION_DONE) {
                try {
                    character.charisma= charisma.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "Charisma updated", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Charisma must be a valid number!", Toast.LENGTH_LONG).show()

                    //character.write()
                }
                false
            } else {
                try {
                    character.charisma = charisma.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "Charisma updated", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Charisma must be a valid number!", Toast.LENGTH_LONG).show()
                }
                false
                false
            }

        }

        constitution.setOnEditorActionListener { _, i, _ ->

            if (i == EditorInfo.IME_ACTION_DONE) {
                try {
                    character.constitution= constitution.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "Constitution updated", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Constitution must be a valid number!", Toast.LENGTH_LONG).show()

                    //character.write()
                }
                false
            } else {
                try {
                    character.constitution = constitution.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "Constitution updated", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Constitution must be a valid number!", Toast.LENGTH_LONG).show()
                }
                false
                false
            }

        }

        intelligence.setOnEditorActionListener { _, i, _ ->

            if (i == EditorInfo.IME_ACTION_DONE) {
                try {
                    character.intelligence= intelligence.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "Intelligence updated", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Intelligence must be a valid number!", Toast.LENGTH_LONG).show()

                    //character.write()
                }
                false
            } else {
                try {
                    character.intelligence = intelligence.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "intelligence updated", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Intelligence must be a valid number!", Toast.LENGTH_LONG).show()
                }
                false
                false
            }

        }



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
                        Toast.makeText(this, applicationContext.getString(R.string.badHp), Toast.LENGTH_LONG).show()
                    }


                }catch (e: NumberFormatException) {

                    Toast.makeText(this, this.getString(R.string.badHp), Toast.LENGTH_LONG).show()
                }
            }

            builder.show()
        }




    }


    fun setLanguageToEnglishIfChanged() {
        if (language == "Changed") {
            val res = getResources()
            val dm = res.getDisplayMetrics()
            val conf = res.getConfiguration()
            conf.setLocale(Locale("en"))
            conf.locale = Locale("en")
            res.updateConfiguration(conf, dm)
            language = "Common"
            generatePage()
            setPic()
        }
    }


    fun setPic() {
        val character = (application as CustomApplication).character
        var image: ImageButton = findViewById(R.id.heroButton)



        when(character.race) {
            "DragonBorn" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.dragonbornmale) else image.setImageResource(
                    R.drawable.dragonbornfemale
                )

            }
            "Dwarf" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.dwarfmale) else image.setImageResource(R.drawable.dwarffemale)

            }
            "Elf" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.elfmale) else image.setImageResource(R.drawable.elffemale)

            }
            "Gnome" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.gnonemale) else image.setImageResource(R.drawable.gnomefemale)

            }
            "HalfElf" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.halfelfmale) else image.setImageResource(
                    R.drawable.halfelffemale
                )

            }
            "Halfling" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.halflingmale) else image.setImageResource(
                    R.drawable.halflingfemale
                )

            }
            "HalfOrc" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.halforkmale) else image.setImageResource(
                    R.drawable.halforkfemale
                )

            }
            "Human" -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.humanmale) else image.setImageResource(R.drawable.humanfemale)

            }
            else -> {
                if (character.sex == "Male") image.setImageResource(R.drawable.tieflingmale) else image.setImageResource(
                    R.drawable.tieflingfemale
                )

            }
        }
    }




    fun gotoStatistics(view: View) {

        val intent = Intent(this, ActionList::class.java)
        startActivity(intent)

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

    fun setSpinnerItemsAndPhoto(spinner1: Spinner, spinner2: Spinner, character: Character, image: ImageButton) {
        when(character.race) {
            "DragonBorn" -> {
                spinner1.setSelection(0)
                if (character.sex == "Male") image.setImageResource(R.drawable.dragonbornmale) else image.setImageResource(
                    R.drawable.dragonbornfemale
                )
            }
            "Dwarf" -> {
                spinner1.setSelection(1)
                if (character.sex == "Male") image.setImageResource(R.drawable.dwarfmale) else image.setImageResource(R.drawable.dwarffemale)
            }
            "Elf" -> {
                spinner1.setSelection(2)
                if (character.sex == "Male") image.setImageResource(R.drawable.elfmale) else image.setImageResource(R.drawable.elffemale)
            }
            "Gnome" -> {
                spinner1.setSelection(3)
                if (character.sex == "Male") image.setImageResource(R.drawable.gnonemale) else image.setImageResource(R.drawable.gnomefemale)
            }
            "HalfElf" -> {
                spinner1.setSelection(4)
                if (character.sex == "Male") image.setImageResource(R.drawable.halfelfmale) else image.setImageResource(
                    R.drawable.halfelffemale
                )
            }
            "Halfling" -> {
                spinner1.setSelection(5)
                if (character.sex == "Male") image.setImageResource(R.drawable.halflingmale) else image.setImageResource(
                    R.drawable.halflingfemale
                )
            }
            "HalfOrc" -> {
                spinner1.setSelection(6)
                if (character.sex == "Male") image.setImageResource(R.drawable.halforkmale) else image.setImageResource(
                    R.drawable.halforkfemale
                )
            }
            "Human" -> {
                spinner1.setSelection(7)
                if (character.sex == "Male") image.setImageResource(R.drawable.humanmale) else image.setImageResource(R.drawable.humanfemale)
            }
            else -> {
                spinner1.setSelection(8)
                if (character.sex == "Male") image.setImageResource(R.drawable.tieflingmale) else image.setImageResource(
                    R.drawable.tieflingfemale
                )
            }
        }
        calculateHp(character.currentHp, character.hp)




        when(character.charClass) {
            "Barbarian" -> spinner2.setSelection(0)
            "Bard" -> spinner2.setSelection(1)
            "Cleric" -> spinner2.setSelection(2)
            "Druid" -> spinner2.setSelection(3)
            "Fighter" -> spinner2.setSelection(4)
            "Monk" -> spinner2.setSelection(5)
            "Paladin" -> spinner2.setSelection(6)
            "Ranger" -> spinner2.setSelection(7)
            "Rogue" -> spinner2.setSelection(8)
            "Sorcerer" -> spinner2.setSelection(9)
            "Warlock" -> spinner2.setSelection(10)
            else -> spinner2.setSelection(11)
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


        override fun onStop() {

        super.onStop()

//        val file = openFileOutput("character.json", Context.MODE_PRIVATE)
//////        val jsonWriter = JsonWriter(OutputStreamWriter(file))
//////        jsonWriter.setIndent(" ")
//////        val character = (application as CustomApplication).character
//////        character.write(jsonWriter)
//////        jsonWriter.close()
            (application as CustomApplication).database.characterDao().update((application as CustomApplication).character)
    }

    fun gotoActionList() {

        val intent = Intent(this, ActionList::class.java)
        startActivity(intent)

    }


    fun generatePage() {

        val character = (application as CustomApplication).character
        var image: ImageButton = findViewById(R.id.heroButton)
        var classSpinner: Spinner = findViewById(R.id.classSpinner)
        var raceSpinner: Spinner = findViewById(R.id.raceSpinner)

        ArrayAdapter.createFromResource(
            this, R.array.race_array, android.R.layout.simple_spinner_dropdown_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            raceSpinner.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this, R.array.class_array, android.R.layout.simple_spinner_dropdown_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            classSpinner.adapter = adapter
        }

        // MUST GO AFTER SPINNER ADAPTER IS INITIALIZED
        setSpinnerItemsAndPhoto(raceSpinner, classSpinner,character,image)



        var wisdom: TextView = findViewById(R.id.wisdomText)
        var strength: TextView = findViewById(R.id.strengthText)
        var dexterity: TextView = findViewById(R.id.dexterityText)
        var charisma: TextView = findViewById(R.id.charismaText)
        var constitution: TextView = findViewById(R.id.constitutionText)
        var intelligence: TextView = findViewById(R.id.intelligenceText)

        wisdom.text = this.getString(R.string.wisdom)
        strength.text = this.getString(R.string.strength)
        dexterity.text = this.getString(R.string.dexterity)
        charisma.text = this.getString(R.string.charisma)
        constitution.text = this.getString(R.string.constitution)
        intelligence.text = this.getString(R.string.intelligence)



    }








//    override fun onListItemClick(l: ListView?, v: View?, attributeId: Int, id: Long) {
//        Log.d("FOO", "$attributeId")
//    }
}
