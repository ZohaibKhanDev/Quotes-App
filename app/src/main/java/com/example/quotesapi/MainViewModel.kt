package com.example.quotesapi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotesapi.room.Fav
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository):ViewModel() {

    private val _allQuotes=MutableStateFlow<ResultState<List<QuotesItem>>>(ResultState.Loading)
    val allQuotes:StateFlow<ResultState<List<QuotesItem>>> =_allQuotes.asStateFlow()

    private val _allFav=MutableStateFlow<ResultState<List<Fav>>>(ResultState.Loading)
    val allFav:StateFlow<ResultState<List<Fav>>> =_allFav.asStateFlow()

    private val _delete= MutableStateFlow<ResultState<Unit>>(ResultState.Loading)
    val delete:StateFlow<ResultState<Unit>> =_delete.asStateFlow()

    private val _Insert= MutableStateFlow<ResultState<Unit>>(ResultState.Loading)
    val insert:StateFlow<ResultState<Unit>> =_Insert.asStateFlow()

    fun getAllFav(){
        _allFav.value=ResultState.Loading
        try {
            val response=repository.getAllFav()
            _allFav.value=ResultState.Success(response)
        }catch (e:Exception){
            _allFav.value=ResultState.Error(e)
        }
    }

    fun Delete(fav: Fav){
        _delete.value=ResultState.Loading
        try {
            val response=repository.Delete(fav)
            _delete.value= ResultState.Success(response)
        }catch (e:Exception){
            _delete.value=ResultState.Error(e)
        }
    }

    fun Insert(fav: Fav){
        _Insert.value=ResultState.Loading
        try {
            val response=repository.Insert(fav)
            _Insert.value= ResultState.Success(response)
        }catch (e:Exception){
            _Insert.value=ResultState.Error(e)
        }
    }

    fun getAllQuotes(){
        viewModelScope.launch {
            _allQuotes.value=ResultState.Loading
            try {
                val response=repository.getAllQuotes()
                _allQuotes.value=ResultState.Success(response)
            }catch (e:Exception){
                _allQuotes.value=ResultState.Error(e)
            }
        }
    }

}