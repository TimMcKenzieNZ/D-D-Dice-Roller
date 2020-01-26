package com.example.dd_dice_roller

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
//import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import android.widget.Toast
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.circularreveal.CircularRevealHelper.STRATEGY
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.DiscoveryOptions
import kotlinx.android.synthetic.main.activity_navbar.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.ObjectOutputStream


class Party : PermittedActivity() {

    lateinit var partyList: MutableCollection<ActionList.ItemPage>

    private var hostEndpoint: String? = null

    lateinit var clientConnectionsClient: ConnectionsClient

    lateinit var character: Character

    lateinit var party: ObservableList<Hero>

    lateinit var hostConnectionLifecycleCallback: ConnectionLifecycleCallback

    lateinit var clientConnectionLifecycleCallback: ConnectionLifecycleCallback

    lateinit var hostConnectionsClient: ConnectionsClient

    private lateinit var adapter: HeroAdapter

    private lateinit var heroList: RecyclerView


    private val callback = object :  ObservableList.OnListChangedCallback<ObservableList<Hero>>() {

        override fun onChanged(layers: ObservableList<Hero>) {
            updatePartyList()
        }

        override fun onItemRangeChanged(layers: ObservableList<Hero>, i: Int, i1: Int) {
            updatePartyList()
        }

        override fun onItemRangeInserted(layers: ObservableList<Hero>, start: Int, count: Int) {
            updatePartyList()
        }

        override fun onItemRangeMoved(sender: ObservableList<Hero>, fromPosition: Int, toPosition: Int, itemCount: Int) {
            updatePartyList()
        }

        override fun onItemRangeRemoved(sender: ObservableList<Hero>, positionStart: Int, itemCount: Int) {
            updatePartyList()
        }
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
            this.finish()
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

    private lateinit var teamList: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        goNative(this)
        setContentView(R.layout.activity_party)

        character = (application as CustomApplication).character
        party = (application as CustomApplication).party


        party.addOnListChangedCallback(callback)

        heroList = findViewById(R.id.heroList)

        clientConnectionsClient = (application as CustomApplication).clientConnectionsClient

        hostConnectionsClient = (application as CustomApplication).hostConnectionsClient

        clientConnectionLifecycleCallback = (application as CustomApplication).clientConnectionLifecycleCallback
        hostConnectionLifecycleCallback = (application as CustomApplication).hostConnectionLifecycleCallback

        var hostButton: Button = findViewById(R.id.hostButton)

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        hostButton.setOnClickListener {
//            requestPermissions(permissions, 100, {
//                advertise()
//            }, {
//                Toast.makeText(this, "Location not permitted.", Toast.LENGTH_LONG).show()
//            })
            startAdvertising()
        }

        adapter = HeroAdapter(this, (application as CustomApplication))

        heroList.adapter = adapter
        heroList.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        heroList.addItemDecoration(DividerItemDecoration(this, RecyclerView.VERTICAL))






        var searchButton: Button = findViewById(R.id.searchButton)


        searchButton.setOnClickListener {
//            requestPermissions(permissions, 100, {
//                discover()
//            }, {
//                Toast.makeText(this, "Location not permitted.", Toast.LENGTH_LONG).show()
//            })
            startDiscovery()
        }


        updatePartyList() // MUST go before setPic()!
        setPic()

        var name: TextView = findViewById(R.id.nameText)
        var classText: TextView = findViewById(R.id.classText)
        var race: TextView = findViewById(R.id.raceText)
        var hpText: TextView = findViewById(R.id.hpText)
        var acText: TextView = findViewById(R.id.ac)
        var levelText: TextView = findViewById(R.id.level)
        var speedText: TextView = findViewById(R.id.speed)

        setClassName()



        name.setText(character.name)
        //classText.setText(character.charClass)
        //race.setText(character.race)
        acText.setText(this.getString(R.string.ac) + ": " + character.ac.toString())
        levelText.setText(this.getString(R.string.level) + ": " + character.level.toString())
        speedText.setText(this.getString(R.string.speed) + ": " + character.speed.toString())
        hpText.setText(character.currentHp.toString())

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
        calculateHp(character.currentHp, character.hp)

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

    fun setPic() {
        val character = (application as CustomApplication).character
        var image: ImageButton = findViewById(R.id.heroButton)
        var raceText: TextView = findViewById(R.id.raceText)
        var layout: ConstraintLayout = findViewById(R.id.party)



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



    /** Broadcasts our presence using Nearby Connections so other players can find us.  */
    private fun startAdvertising() {

        val options = AdvertisingOptions.Builder().setStrategy(Strategy.P2P_STAR).build() // we want 1 to N with a clients -> single hub connection
        hostConnectionsClient.startAdvertising(
            String.format("%s's %s", character.name, this.getString(R.string.party)), packageName, hostConnectionLifecycleCallback,
            options)
        .addOnSuccessListener {
            Toast.makeText(this, "Advertising...", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to advertise...", Toast.LENGTH_LONG).show()
        }

    }








    // CLIENT related methods:


    // Creates a discoverListener with a modifiable list  lists of discovered end points
    private fun createDiscoverListener(items: MutableList<Endpoint>, adapter: ArrayAdapter<Endpoint>): EndpointDiscoveryCallback {
        return object : EndpointDiscoveryCallback() {
            override fun onEndpointFound(id: String, info: DiscoveredEndpointInfo) {
                items.add(Endpoint(id, info))
                adapter.notifyDataSetChanged()
            }

            override fun onEndpointLost(p0: String) {
                items.removeIf { it.id == p0 }
                adapter.notifyDataSetChanged()
            }
        }
    }


    /** Starts looking for other players using Nearby Connections.  */
    private fun startDiscovery() {

        // set up the endpoint list to look through
        val items = mutableListOf<Endpoint>()
        val adapter = showEndpointChooser(items)
        val callback = createDiscoverListener(items, adapter)

        // we want 1 to N with a clients -> single hub connection
        val options = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()

        clientConnectionsClient.startDiscovery(packageName, callback, options).addOnSuccessListener {
            Toast.makeText(this, "Looking for hosts...", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        }
    }


    // Receives an endpoint and informs user of the status of the request to join it
    private fun joinHost(endpoint: Endpoint) {
            clientConnectionsClient.requestConnection(character.name, endpoint.id, clientConnectionLifecycleCallback)
            .addOnSuccessListener {
                Toast.makeText(this@Party, "Connected to ${endpoint.name}.", Toast.LENGTH_LONG).show()
                //TODO update hero list, and join button
            }
            .addOnFailureListener {
                Toast.makeText(this@Party, "Rejected by ${endpoint.name}.", Toast.LENGTH_LONG).show()
            }
    }

    // Shows the available hosts to join
    private fun showEndpointChooser(items: List<Endpoint>): ArrayAdapter<Endpoint> {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)

        AlertDialog.Builder(this).run {
            setTitle("Choose host...")
            setAdapter(adapter) { _, i -> joinHost(items[i]) }
            setOnDismissListener {
                Nearby.getConnectionsClient(this@Party).stopDiscovery()
            }
            show()
        }

        return adapter
    }
    fun gotoStatistics(view: View) {

        val intent = Intent(this, statistics::class.java)
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





    /** Disconnects from the party and update the UI.  */
    fun disconnect(view: View) {
        clientConnectionsClient.disconnectFromEndpoint(character.name)
        //TODO handle the user leaving a party they joined
        //TODO update hero list, and join button
    }

    fun updatePartyList() {
//        val party = (application as CustomApplication).party
//        partyList = mutableListOf()
//
//
//
//        party.forEach{hero ->
//            partyList.add(ActionList.ItemPage(hero.name, String.format("%s, %s", hero.race, hero.charClass), getCharacterImageId(hero.race, hero.sex)))
//        }
//
//        teamList = findViewById(R.id.teamList)
//        var listAdapter = CustomListAdapter(this, partyList.toTypedArray())
//        teamList.adapter = listAdapter
//        listAdapter.notifyDataSetChanged()
//
//        val party = (application as CustomApplication).party
//        partyList = mutableListOf()


        adapter.clear()
        party.forEach{hero ->
            adapter.insert(hero)
        }
        adapter.notifyDataSetChanged()

//        teamList = findViewById(R.id.teamList)
//        var listAdapter = CustomListAdapter(this, partyList.toTypedArray())
//        teamList.adapter = listAdapter
//        listAdapter.notifyDataSetChanged()
    }





}
