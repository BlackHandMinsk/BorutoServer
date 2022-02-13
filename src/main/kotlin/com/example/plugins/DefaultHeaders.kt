package com.example.plugins

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.HttpHeaders.CacheControl

import java.time.Duration


fun Application.configureDefaultHeader(){
    install(DefaultHeaders){
        val oneYearInsSeconds = Duration.ofDays(365).seconds
        header(name = HttpHeaders.CacheControl,value = "public, max-age=$oneYearInsSeconds, immutable")
    }
}