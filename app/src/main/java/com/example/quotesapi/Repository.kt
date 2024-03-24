package com.example.quotesapi

import com.example.quotesapi.room.DataBase
import com.example.quotesapi.room.Fav

class Repository(private val dataBase: DataBase) {
    suspend fun getAllQuotes():List<QuotesItem>{
        return QuoteApiClient.getAllQuotes()
    }
    fun getAllFav():List<Fav>{
        return dataBase.favDao().getAllFav()
    }
    fun Insert(fav: Fav){
        return dataBase.favDao().insert(fav)
    }
    fun Delete(fav: Fav){
        return dataBase.favDao().Delete(fav)
    }
}