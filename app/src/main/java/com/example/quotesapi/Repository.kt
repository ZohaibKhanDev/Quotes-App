package com.example.quotesapi

class Repository {
    suspend fun getAllQuotes():List<QuotesItem>{
        return QuoteApiClient.getAllQuotes()
    }
}