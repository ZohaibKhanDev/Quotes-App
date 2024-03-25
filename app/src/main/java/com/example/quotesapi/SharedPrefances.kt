package com.example.quotesapi

import android.content.Context

class SharedPrefances (
    context: Context
){
    private val prefances=context.getSharedPreferences(
        context.packageName,
        Context.MODE_PRIVATE
    )
    private val editor=prefances.edit()

    private val keyName="name"
    private val keyFav="fav"
    private val keyList="List"

    var dark get() = prefances.getBoolean(
        keyName,false

    )
        set(value) {
            editor.putBoolean(keyName,value)
            editor.commit()
        }
    var list get() = prefances.getBoolean(
        keyList,false

    )
        set(value) {
            editor.putBoolean(keyList,value)
            editor.commit()
        }


    var fav get() = prefances.getBoolean(
        keyFav,false

    )
        set(value) {
            editor.putBoolean(keyFav,value)
            editor.commit()
        }




}