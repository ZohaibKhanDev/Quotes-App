package com.example.quotesapi.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavDao {
    @Query("SELECT * FROM fav")
    fun getAllFav():List<Fav>
    @Insert
    fun insert(fav: Fav)
    @Delete
    fun Delete(fav: Fav)
}