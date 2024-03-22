package com.example.quotesapi

interface QuotesApi {
    suspend fun getAllQuotes():List<QuotesItem>
}