package com.example.dd_dice_roller

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Database



import android.content.Context




@Database(entities = [Character::class], version = 2)
abstract class CharacterDatabase : RoomDatabase() {

    abstract fun characterDao(): CharacterDAO

    companion object {

        private var INSTANCE: CharacterDatabase? = null

        fun getInMemoryDatabase(context: Context): CharacterDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, CharacterDatabase::class.java!!,"heroes" )
                    .allowMainThreadQueries().fallbackToDestructiveMigration().build()
            }
            return INSTANCE as CharacterDatabase
        }
    }
}
