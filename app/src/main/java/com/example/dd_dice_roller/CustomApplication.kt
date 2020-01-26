package com.example.dd_dice_roller

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.JsonReader
import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.gms.nearby.connection.PayloadTransferUpdate.Status
import java.io.*
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import android.R
import android.app.PendingIntent.getActivity
import android.os.AsyncTask
import android.view.ViewGroup






// Global

val TAG = "D&D_ROLLER"



class CustomApplication: Application() {


    private var hostEndpoint: String? = null

    lateinit var character: Character

    var isShaking = ObservableBoolean(false)

    lateinit var party: ObservableList<Hero>

    lateinit var clientConnectionsClient : ConnectionsClient

    lateinit var hostConnectionsClient : ConnectionsClient

    lateinit var database: CharacterDatabase

    fun updateParty(incoming: MutableList<Hero>) {
        val hero = party[0]

        incoming.forEach { hero ->
            party.add(hero)

        }
        party.distinctBy{Pair(it.name, it.name)}
        //party.notifyObservers()
        notifyUser(hero.name, "Joined your party!")
    }


    fun updateHero(action: HeroAction) {
        character.currentHp += action.damage
        if (character.currentHp < 0) character.currentHp = 0
        if (character.currentHp > character.hp) character.currentHp = character.hp
        //TODO refresh the activity to show changes
//        val vg = findViewById(R.id.mainLayout)
//
//        applicationContext.
//        vg.invalidate()
        Toast.makeText(this@CustomApplication, "${action.name} did ${action.action} to you", Toast.LENGTH_SHORT).show()
        notifyUser(action.name, "${action.name} used their ${action.type}, ${action.action}, on you!")

    }

    fun notifyUser(title: String, text: String) {
        val notification = Notification.Builder(applicationContext, Notification.CATEGORY_REMINDER).run {
            setSmallIcon(R.drawable.sym_def_app_icon)
            setContentTitle(title)
            setContentText(text)
            setAutoCancel(true)
            build()
        }

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, notification)
    }

    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(Notification.CATEGORY_REMINDER, "D&D_Roller Notifications", importance).apply {
            description = "Show hero interactions"
        }
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }


    // Following code is sourced from Google nearby api examples:

    // Callbacks for receiving payloads
    val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {


            try { // try to read input stream as an incoming list of heroes
                val inputStream: InputStream = payload.asStream()!!.asInputStream()
                val objectInputStream = ObjectInputStream(inputStream)
                val wrapper = objectInputStream.readObject() as PayloadWrapper
                if (wrapper.type == 1) {
                    val heroList = wrapper.heroList
                    if(heroList != null) updateParty(heroList)
                } else {
                    val action = wrapper.action
                    if (action != null) updateHero(action)
                }
            }

            // we don't know what they are sending
            catch (e: Exception) {
                Log.e("FOO","got full exception " + e.message)
                }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            if (update.status == Status.SUCCESS) {
                //TODO notify user and perform appropriate action

            }
        }
    }




    // Callbacks for connections to other devices
    val clientConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.i(TAG, "onConnectionInitiated: accepting connection")
            clientConnectionsClient.acceptConnection(endpointId, payloadCallback)

        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {

                when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    hostEndpoint = endpointId
                    Toast.makeText(this@CustomApplication, "Connected to $endpointId.", Toast.LENGTH_SHORT).show()

                    clientConnectionsClient.stopDiscovery()

                    sendHeroData()

                    //connectionsClient.stopAdvertising()

                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Toast.makeText(this@CustomApplication, String.format("%s rejected connection", endpointId), Toast.LENGTH_SHORT).show()
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Toast.makeText(this@CustomApplication, String.format("Error connecting to %s", endpointId), Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onDisconnected(endpointId: String) {

            Toast.makeText(this@CustomApplication, "$endpointId disconnected from the party", Toast.LENGTH_SHORT).show()

        }
    }


    // Callbacks for connections to other devices
    val hostConnectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
            Log.i(TAG, "onConnectionInitiated: accepting connection")
            hostConnectionsClient.acceptConnection(endpointId, payloadCallback)

        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {

            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    hostEndpoint = endpointId
                    Toast.makeText(this@CustomApplication, String.format("%s connected", endpointId), Toast.LENGTH_SHORT).show()

                    sendHeroData()


                    //connectionsClient.stopAdvertising()

                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Toast.makeText(this@CustomApplication, "Rejected", Toast.LENGTH_SHORT).show()
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Toast.makeText(this@CustomApplication, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            Toast.makeText(this@CustomApplication, "$endpointId disconnected from the party", Toast.LENGTH_SHORT).show()
            //TODO notify party and drop hero from their lists
        }
    }

    // Sends a stream of all the heroes in this user's party as a payload
    fun sendHeroData() {

        val outputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(outputStream)
        objectOutputStream.writeObject(PayloadWrapper(1, party, null))
        objectOutputStream.flush()
        objectOutputStream.close()
        val heroInputStream: InputStream = ByteArrayInputStream(outputStream.toByteArray())
        hostEndpoint?.let {
            clientConnectionsClient.sendPayload(it, Payload.fromStream(heroInputStream))
        }
    }

    fun sendHeroAction(action: HeroAction) {
        Log.e("FOO","creating hero action package")
        val outputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(outputStream)
        objectOutputStream.writeObject(PayloadWrapper(2,null, action))
        objectOutputStream.flush()
        objectOutputStream.close()
        val heroInputStream: InputStream = ByteArrayInputStream(outputStream.toByteArray())
        hostEndpoint?.let {
            Log.e("FOO","sending off package")
            clientConnectionsClient.sendPayload(it, Payload.fromStream(heroInputStream))
        }
    }






    override fun onCreate() {
        super.onCreate()
        database = CharacterDatabase.getInMemoryDatabase(applicationContext)

        //database = Room.databaseBuilder(this, CharacterDatabase::class.java, "heroes").fallbackToDestructiveMigration().build()

//        while (loadDbTask.status != AsyncTask.Status.FINISHED) {
//            Log.d("BAR", "loading db")
//
//        }
        val characterDao = database!!.characterDao()


        character = characterDao.get()



        if (character == null) {

            character = Character()
            database.characterDao().insert(character)

        }

        //character = characters[characters.size - 1]

        //var character = (application as CustomApplication).character
//        var loadCharacterTask = LoadCharacterTask(database!!, character)
//        loadCharacterTask.execute()



//        while (loadCharacterTask.status != AsyncTask.Status.FINISHED) {
//            Log.d("BAR", "loading character")
//        }


        party = ObservableArrayList<Hero>()
        var self = Hero(character.name, character.race, character.charClass, character.sex, character.currentHp,"figure out how to get endpointid and put it here")
        party.add(self)








        //character = Character()


//        try {
//            val file = openFileInput("character.json")
//            val reader = JsonReader(InputStreamReader(file))
//            character = Character.read(reader)
//            reader.close()
//        } catch (e: FileNotFoundException) {
//
//            character = Character()
//        }
        //Thread.sleep(12000)


        clientConnectionsClient = Nearby.getConnectionsClient(this)
        hostConnectionsClient = Nearby.getConnectionsClient(this)

        createNotificationChannel()
    }


}


// I would like to thank Jack Steel for the following code...

typealias BooleanObserver = (Boolean) -> Unit

class ObservableBoolean(private var value: Boolean) {

    private val listeners: MutableList<BooleanObserver> = mutableListOf()

    fun observe(observer: BooleanObserver) {
        listeners.add(observer)
    }

    fun set(boolean: Boolean) {
        value = boolean
        listeners.forEach {it.invoke(value)}
    }

    fun get(): Boolean = value
}

