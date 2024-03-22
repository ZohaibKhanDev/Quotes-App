package com.example.quotesapi


import androidx.navigation.NavHostController
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuotesItem(
    @SerialName("author")
    val author: String,
    @SerialName("quote")
    val quote: String,
)