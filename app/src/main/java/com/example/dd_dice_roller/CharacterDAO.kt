package com.example.dd_dice_roller

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CharacterDAO {
    @Insert
    fun insert(character: Character): Long

    @Update
    fun update(character: Character)

    @Query("Select * from heroes where id = 1")
    fun get(): Character

    @Query("Select * from heroes")
    fun getAll(): List<Character>
}

// w: warning: The following options were not recognized by any processor: '[room.schemaLocation, kapt.kotlin.generated]'