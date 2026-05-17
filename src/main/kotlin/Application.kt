package com.example

import com.example.plugins.JWTConfig
import com.example.plugins.configureAutoHeadResponse
import com.example.plugins.configureBasicAuthentication
import com.example.plugins.configureBearerAuthentication
import com.example.plugins.configureDigestAuthentication
import com.example.plugins.configureJWTAuthentication
import com.example.plugins.configurePartialContent
import com.example.plugins.configureRequestValidation
import com.example.plugins.configureResources
import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import com.example.plugins.configureStatusPages
import com.example.plugins.configureRateLimit
import com.example.plugins.configureSession
import com.example.plugins.configureSessionAuthentication
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    /**
     * =========================================================================
     * READ JWT CONFIGURATION FROM application.conf
     * =========================================================================
     */

    // Access:
    //
    // ktor {
    //    jwt {
    //       ...
    //    }
    // }

    val jwt = environment.config.config("ktor.jwt")

    /**
     * Read individual JWT properties.
     */

    val realm = jwt.property("realm").getString()

    val secret = jwt.property("secret").getString()

    val issuer = jwt.property("issuer").getString()

    val audience = jwt.property("audience").getString()

    val tokenExpiry =
        jwt.property("expiry")
            .getString()
            .toLong()

    /**
     * Create JWTConfig object.
     */

    val config = JWTConfig(
        realm = realm,
        issuer = issuer,
        audience = audience,
        tokenExpiry = tokenExpiry,
        secret = secret
    )

    configureResources()
//    configureRateLimit function should be called before configureRouting function
    configureRateLimit()
//    configureBasicAuthentication function should be called before configureRouting function
//    configureBasicAuthentication()
//    configureDigestAuthentication function should be called before configureRouting function
//    configureDigestAuthentication()
//    configureBearerAuthentication function should be called before configureRouting function
//    configureBearerAuthentication()
//    configureSession function should be called before configureRouting function
//    configureSession()
//    configureSessionAuthentication function should be called before configureRouting function
//    configureSessionAuthentication()
    /**
     * =========================================================================
     * INSTALL JWT AUTHENTICATION
     * =========================================================================
     */

    // Authentication must be configured
    // before routing.
//    configureJWTAuthentication function should be called before configureRouting function
    configureJWTAuthentication(config)
    /**
     * Pass JWT config to routing.
     */
    configureRouting(config)
    configureSerialization()
//    configureStatusPages()
    configureRequestValidation()
    configurePartialContent()
    configureAutoHeadResponse()


}
