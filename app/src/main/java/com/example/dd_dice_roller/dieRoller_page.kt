package com.example.dd_dice_roller

import android.app.AlertDialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.util.JsonWriter
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_die_roller_page.*
import kotlinx.android.synthetic.main.activity_navbar.*
import java.io.OutputStreamWriter
import java.util.*
import kotlin.random.Random
import android.view.ViewGroup


class dieRoller_page : AppCompatActivity() {

    private lateinit var listView: ListView
    private var rolls: Array<String> = arrayOf()
    private var dieNumber = 1
    private var dieType = 4
    private var modifier = 0
    lateinit var  target: String
    var targetIndex: Int = 0
    private lateinit var actionType: String
    private var toSucceed = 0

    lateinit var character: Character



    private val serviceIntent
        get() = Intent(this, SensorService::class.java)

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(Notification.CATEGORY_ALARM, "Die roller shake notification", importance).apply {
            description = "To let you know we are waiting for a shaking"
        }
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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
        goNative(this)
        setContentView(R.layout.activity_die_roller_page)
        character = (application as CustomApplication).character
        var isShaking = (application as CustomApplication).isShaking
        target = this.getString(R.string.none)

        createNotificationChannel()

        startForegroundService(serviceIntent)

        var toSucceedText: TextView = findViewById(R.id.toSuccedText)



        toSucceedText.visibility = View.GONE



        val type = intent.getStringExtra("type")
        var typeText: TextView = findViewById(R.id.typeText)
        when(type) {
            "Attack" -> {
                typeText.setText(this.getString(R.string.attack_type))

            }
            "Ability" -> {
                //modifiersBox.setText(calculateMod(character.strength).toString())
                typeText.setText(this.getString(R.string.ability))
            }
            else -> {
                //modifiersBox.setText(calculateMod(character.intelligence).toString())
                //modifier = calculateMod(character.intelligence)
                typeText.setText(this.getString(R.string.spell))
            }
        }
        actionType = type




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


        val typeDropdown: Spinner = findViewById(R.id.typeSpinner)

        ArrayAdapter.createFromResource(
            this, R.array.die_type_array, android.R.layout.simple_spinner_dropdown_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeDropdown.adapter = adapter
        }

        typeDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, type: Int, id: Long) {
                dieType = when(type + 1) {
                    1 -> 4
                    2 -> 6
                    3 -> 8
                    4 -> 10
                    5 -> 12
                    6 -> 20
                    else -> 0
                }
            }
        }

        val numberDropdown: Spinner = findViewById(R.id.numberSpinner)

        ArrayAdapter.createFromResource(
            this, R.array.die_number_array, android.R.layout.simple_spinner_dropdown_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            numberDropdown.adapter = adapter
        }

        numberDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, number: Int, id: Long) {
                dieNumber = number

            }
        }

        val targetDropdown: Spinner = findViewById(R.id.targetSpinner)


        var heroList: ArrayList<String> = arrayListOf(this.getString(R.string.none)) //
        (application as CustomApplication).party.forEach {hero ->
            heroList.add(hero.name)
        }
        var array: ArrayList<String> = heroList
        var arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, array)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        targetDropdown.adapter = arrayAdapter

        targetDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, newTarget: Int, id: Long) {
                target = heroList[newTarget]
                targetIndex = newTarget
            }

        }



        //populateChar(this.findViewById(R.id.include), intent)






        val modifierText: EditText = findViewById(R.id.modifiersBox)
        modifierText.setOnEditorActionListener { _, i, _ ->

            if (i == EditorInfo.IME_ACTION_DONE) {
                    try {
                        modifier = modifierText.text.toString().replace("\n", "").toInt()
                        Toast.makeText(this, "Modifier updated", Toast.LENGTH_LONG).show()
                    }catch (e: NumberFormatException) {
                        Toast.makeText(this, "Modifier must be a valid number!", Toast.LENGTH_LONG).show()
                    }
                    false
            } else {
                try {
                    modifier = modifierText.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "Modifier updated", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Modifier must be a valid number!", Toast.LENGTH_LONG).show()
                }
                false
            }

        }

        val toSucceedBox: EditText = findViewById(R.id.toSuccedBox)
        toSucceedBox.visibility = View.GONE
        toSucceedBox.setOnEditorActionListener { _, i, _ ->

            if (i == EditorInfo.IME_ACTION_DONE) {
                try {
                    toSucceed = toSucceedText.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "Success value set", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Success value must be a valid number!", Toast.LENGTH_LONG).show()
                }
                false
            } else {
                try {
                    toSucceed = toSucceedText.text.toString().replace("\n", "").toInt()
                    Toast.makeText(this, "Success value set", Toast.LENGTH_LONG).show()
                }catch (e: NumberFormatException) {
                    Toast.makeText(this, "Success value must be a valid number!", Toast.LENGTH_LONG).show()
                }
                false
            }

        }

        val dieImage: ImageView = findViewById(R.id.dieView)

        Glide.with(this).load(R.drawable.roll).into(dieImage!!)


        listView = findViewById(R.id.rollList)
        val listAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rolls)
        listView.setAdapter(listAdapter)



        fun roll() {
            val roll: MediaPlayer = MediaPlayer.create(this, R.raw.roll)
            Glide.with(this).load(R.drawable.roll).into(dieImage!!)
            roll.start()

            var results = mutableListOf<String>(this.getString(R.string.results))
            var total = 0
            for (i in 1..dieNumber + 1) {
                if (dieType != 0) {
                    var result = (Random.nextInt(dieType) + modifier + 1).toString()
                    total += result.toInt()
                    if (result.toInt() == dieType + modifier) {
                        Glide.with(this).load(R.drawable.natural20).into(dieImage!!)
                        val success: MediaPlayer = MediaPlayer.create(this, R.raw.success)
                        success.start()
                    }
                    else if (result.toInt() - modifier == 1) {
                        Glide.with(this).load(R.drawable.natural1).into(dieImage!!)
                        val fail: MediaPlayer = MediaPlayer.create(this, R.raw.fail)
                        fail.start()
                    }

                    results.add(result)
                } else {
                    var result = (Random.nextInt(100) + 1).toString()
                    total += result.toInt()
                    if (result.toInt() == 100){
                        Glide.with(this).load(R.drawable.natural20).into(dieImage!!)
                        val success: MediaPlayer = MediaPlayer.create(this, R.raw.success)
                        success.start()
                    }
                    results.add(result)
                }
            }
//            results.forEach {
//                try {
//
//                } catch (e: NumberFormatException) {
//                    // Probably the results string
//                }
//            }
            rolls = results.toTypedArray()
            val listAdapter = ArrayAdapter<String>(this, R.layout.listview_text, rolls)
            listView.setAdapter(listAdapter)
            if (targetIndex != 0 && targetIndex != 1) {
                if(actionType == "Attack")  {
                    sendAction(total * -1)
                } else  {
                    sendAction(total)
                }
            }
        }
        isShaking.observe { if (it == true) roll() }


        var rollButton: Button = findViewById(R.id.rollButton)
        rollButton.setOnClickListener {
            roll()
        }




        var increment: Button = findViewById(R.id.incrementHp)
        increment.setOnClickListener {
            if (character.currentHp < character.hp) {
                character.currentHp ++
                calculateHp(character.currentHp, character.hp)
                hpText.text = character.currentHp.toString()
            }
        }

        var decrement: Button = findViewById(R.id.decrementHp)
        decrement.setOnClickListener {
            if (character.currentHp > 0) {
                character.currentHp --
                calculateHp(character.currentHp, character.hp)
                hpText.text = character.currentHp.toString()
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
                        Toast.makeText(this, this.getString(R.string.badHp), Toast.LENGTH_LONG).show()
                    }


                }catch (e: NumberFormatException) {

                    Toast.makeText(this, this.getString(R.string.badHp), Toast.LENGTH_LONG).show()
                }
            }

            builder.show()
        }

        setPic()
        setClassName()



        calculateHp(character.currentHp, character.hp)
    }

    fun sendAction(total: Int) {
        val thing: EditText = findViewById(R.id.thingBox)
        val effect = thing.text.toString()
        val heroAction = HeroAction(character.name, effect, effect,total, actionType )
        (application as CustomApplication).sendHeroAction(heroAction)
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



    fun gotoStatistics(view: View) {

        val intent = Intent(this, statistics::class.java)
        startActivity(intent)

    }



    fun setPic() {
        val character = (application as CustomApplication).character
        var image: ImageButton = findViewById(R.id.heroButton)
        var raceText: TextView = findViewById(R.id.raceText)
        var layout: ConstraintLayout = findViewById(R.id.layout)

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

    fun generatePage() {
        val character = (application as CustomApplication).character

        val typeDropdown: Spinner = findViewById(R.id.typeSpinner)



        ArrayAdapter.createFromResource(
            this, R.array.die_type_array, android.R.layout.simple_spinner_dropdown_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeDropdown.adapter = adapter
        }


        val numberDropdown: Spinner = findViewById(R.id.numberSpinner)

        ArrayAdapter.createFromResource(
            this, R.array.die_number_array, android.R.layout.simple_spinner_dropdown_item
        ).also {adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            numberDropdown.adapter = adapter
        }


        var dieNumber: TextView = findViewById(R.id.numberText)
        var dieType: TextView = findViewById(R.id.dieText)
        var dieModifiers: TextView = findViewById(R.id.modifiersText)
        var roll: Button = findViewById(R.id.rollButton)

        dieNumber.text = this.getString(R.string.die_number)
        dieType.text = this.getString(R.string.die_type)
        dieModifiers.text = this.getString(R.string.modifiers)
        roll.text = this.getString(R.string.roll)

        val type = intent.getStringExtra("type")
        var typeText: TextView = findViewById(R.id.typeText)
        when(type) {
            "Attack" -> {
                typeText.setText(this.getString(R.string.attack_type))
            }
            "Ability" -> {
                //modifiersBox.setText(calculateMod(character.strength).toString())
                typeText.setText(this.getString(R.string.ability))
            }
            else -> {
                //modifiersBox.setText(calculateMod(character.intelligence).toString())
                //modifier = calculateMod(character.intelligence)
                typeText.setText(this.getString(R.string.spell))
            }
        }


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
}
