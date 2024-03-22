package com.example.quotesapi

import com.example.quotesapi.Constant.BASE_URL
import com.example.quotesapi.Constant.TIMEOUT
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

object QuoteApiClient {

    @OptIn(ExperimentalSerializationApi::class)
    private val client= HttpClient(Android){
        install(ContentNegotiation){
            json(
                Json{
                    isLenient=true
                    ignoreUnknownKeys=true
                    explicitNulls=false
                    prettyPrint=true
                }
            )
        }

        install(Logging){
            level=LogLevel.ALL
            logger=object : Logger{
                override fun log(message: String) {
                    println(message)
                }

            }
        }

        install(HttpTimeout){
            socketTimeoutMillis=TIMEOUT
            connectTimeoutMillis= TIMEOUT
            requestTimeoutMillis= TIMEOUT
        }

    }


    suspend fun getAllQuotes():List<QuotesItem>{
        return client.get(BASE_URL +"quotes/100").body()
    }



}