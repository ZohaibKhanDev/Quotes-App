package com.example.quotesapi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: Repository):ViewModel() {

    private val _allQuotes=MutableStateFlow<ResultState<List<QuotesItem>>>(ResultState.Loading)
    val allQuotes:StateFlow<ResultState<List<QuotesItem>>> =_allQuotes.asStateFlow()

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