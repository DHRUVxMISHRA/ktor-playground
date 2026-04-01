package com.example

import com.example.plugins.configureAutoHeadResponse
import com.example.plugins.configureBasicAuthentication
import com.example.plugins.configurePartialContent
import com.example.plugins.configureRequestValidation
import com.example.plugins.configureResources
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.plugins.configureStatusPages
import com.example.plugins.configureRateLimit
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureResources()
//    configureRateLimit function should be called before configureRouting function
    configureRateLimit()
//    configureBasicAuthentication function should be called before configureRouting function
    configureBasicAuthentication()
    configureRouting()
    configureSerialization()
    configureStatusPages()
    configureRequestValidation()
    configurePartialContent()
    configureAutoHeadResponse()


}
