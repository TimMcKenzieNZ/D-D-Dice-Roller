package com.example.dd_dice_roller


import android.media.MediaPlayer
import android.net.Uri
import com.squareup.picasso.Downloader
import android.os.AsyncTask
import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.room.Room
import java.io.*
import java.lang.ref.WeakReference
import java.net.URL
import java.net.URLConnection
import javax.net.ssl.HttpsURLConnection


class DownloadFileTask(val activity: MainActivity) : AsyncTask<URL, String, String>() {


    override fun doInBackground(vararg urls: URL) : String {
        val connection = urls[0].openConnection() as URLConnection
        connection.connect()
        var input: InputStream = BufferedInputStream(urls[0].openStream(), 1000)
        val file = File.createTempFile("dand_", ".mp3", activity.cacheDir)
        var output = FileOutputStream(file)


        val data = ByteArray(1024)
        var total: Long = 0
        var count = input.read(data)
        while (count !== -1) {

            total += count


            output.write(data, 0, count)
            count = input.read(data)

        }
        output.flush()
        output.close()
        input.close()


        return file.toString()
    }


    override fun onPostExecute(fileString: String) {
        super.onPostExecute(fileString)
        val file: File = File(fileString)

        if (file.exists()) {
            file.setReadable(true)
        }
        val player = MediaPlayer.create(activity, Uri.fromFile(file))

        player?.start()

        player?.setOnCompletionListener {
            player?.stop()
            player?.release()
            file.delete()
        }
    }
}

class LoadDatabaseTask(val activity: MainActivity) : AsyncTask<Unit, Unit, CharacterDatabase?>() {

    private val taskActivity = WeakReference(activity)

    override fun doInBackground(vararg p0: Unit?): CharacterDatabase? {
        var database: CharacterDatabase? = null
        taskActivity.get()?.let {
            database = Room.databaseBuilder(it.applicationContext, CharacterDatabase::class.java, "heroes").fallbackToDestructiveMigration().build()
        }
        return database
    }

    override fun onPostExecute(database: CharacterDatabase?) {
        taskActivity.get()?.let {
            it.database = database!!
            Log.d("BAR", "db Loaded")

            //LoadCharacterTask(it.database, character, party)
        }
    }
}

//class LoadDatabaseTask2(val activity: MainActivity, var databaseLoaded: Boolean) : AsyncTask<Unit, Unit, CharacterDatabase?>() {
//
//    private val taskActivity = WeakReference(activity)
//
//    override fun doInBackground(vararg p0: Unit?): CharacterDatabase? {
//        var database: CharacterDatabase? = null
//        taskActivity.get()?.let {
//            database = Room.databaseBuilder(it.applicationContext, CharacterDatabase::class.java, "heroes").allowMainThreadQueries().build()
//        }
//        return database
//    }
//
//    override fun onPostExecute(database: CharacterDatabase?) {
//        taskActivity.get()?.let {
//            it.database = database
//            databaseLoaded = true
//            Log.d("BAR", "db Loaded")
//
//            //LoadCharacterTask(it.database, character, party)
//        }
//    }
//}

//class LoadDatabaseTask(val activity: MainActivity) : AsyncTask<Unit, Unit, CharacterDatabase?>() {
//
//    private val taskActivity = WeakReference(activity)
//
//    override fun doInBackground(vararg p0: Unit?): CharacterDatabase? {
//        var database: CharacterDatabase? = null
//        taskActivity.get()?.let {
//            database = Room.databaseBuilder(it.applicationContext, CharacterDatabase::class.java, "heroes").fallbackToDestructiveMigration().build()
//        }
//        return database
//    }
//
//    override fun onPostExecute(database: CharacterDatabase?) {
//        taskActivity.get()?.let {
//            it.database = database
//        }
//    }
//}

//class LoadDatabaseTask2(val activity: MainActivity) : AsyncTask<Unit, Unit, CharacterDatabase?>() {
//
//    private val taskActivity = WeakReference(activity)
//
//    override fun doInBackground(vararg p0: Unit?): CharacterDatabase? {
//        var database: CharacterDatabase? = null
//        taskActivity.get()?.let {
//            database = Room.databaseBuilder(it.applicationContext, CharacterDatabase::class.java, "heroes").fallbackToDestructiveMigration().build()
//        }
//        return database
//    }
//
//    override fun onPostExecute(database: CharacterDatabase) {
//        taskActivity.get()?.let {
//            it.database2 = database
//        }
//    }
//}

//class LoadDatabaseTask3(val app: CustomApplication) : AsyncTask<Unit, Unit, CharacterDatabase?>() {
//
//    //private val taskActivity = WeakReference(activity)
//
//    override fun doInBackground(vararg p0: Unit?): CharacterDatabase? {
//        var database: CharacterDatabase?
//
//        database = Room.databaseBuilder(app, CharacterDatabase::class.java, "heroes").fallbackToDestructiveMigration().build()
//
//        return database
//    }
//
//    override fun onPostExecute(database: CharacterDatabase?) {
//
//        app.database = database!!
//        LoadCharacterTask(database, app.character, app.party)
//
//    }
//}


//class LoadCharacterTask(private val database: CharacterDatabase, private var hero: Character, var party: ObservableList<Hero>) : AsyncTask<Unit, Unit, Character>() {
//    override fun doInBackground(vararg p0: Unit?): Character {
//        val characterDao = database.characterDao()
//        return characterDao.get()
//    }
//
//    override fun onPostExecute(character: Character?) {
//        if (character != null) {
//            hero = character
//            val self = Hero(hero.name, hero.race, hero.charClass, hero.sex, hero.currentHp)
//            party.add(self)
//        }
//        else {
//            hero = Character()
//            val self = Hero(hero.name, hero.race, hero.charClass, hero.sex, hero.currentHp)
//            party.add(self)
//            NewCharacterTask(database, hero)
//        }
//
//    }
//}

class LoadCharacterTask(private val database: CharacterDatabase, private var hero: Character) : AsyncTask<Unit, Unit, Character>() {
    override fun doInBackground(vararg p0: Unit?): Character {
        val characterDao = database.characterDao()
        return characterDao.get()
    }

    override fun onPostExecute(character: Character?) {
        if (character != null) {
            hero = character

        }
        else {
            hero = Character()
            NewCharacterTask(database, hero)

        }

    }
}

class NewCharacterTask(private val database: CharacterDatabase,
                  private val character: Character) : AsyncTask<Unit, Unit, Unit>() {
    override fun doInBackground(vararg p0: Unit?) {
        character.id = database.characterDao().insert(character)
    }

}

class UpdateCharacterTask(private val database: CharacterDatabase,
                     private val character: Character) : AsyncTask<Unit, Unit, Unit>() {
    override fun doInBackground(vararg p0: Unit?) {
        database.characterDao().update(character)
    }
}