package com.example.quotesapi.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Fav(
    @PrimaryKey(autoGenerate = true)
    val id:Int?,
    @ColumnInfo("tittle")
    val titttle:String,
    @ColumnInfo("Description")
    val des:String
)
