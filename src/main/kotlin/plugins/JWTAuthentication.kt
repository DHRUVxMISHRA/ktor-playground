package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respondText
import java.util.Date

/**
 * ============================================================================
 * CONFIGURE JWT AUTHENTICATION
 * ============================================================================
 */

fun Application.configureJWTAuthentication(
    config: JWTConfig
) {

    /**
     * Install Authentication plugin.
     */

    install(Authentication) {

        /**
         * Configure JWT Authentication provider.
         */

        jwt("jwt-auth") {

            /**
             * =========================================================================
             * REALM
             * =========================================================================
             */

            // Realm represents protected area name.

            realm = config.realm


            /**
             * =========================================================================
             * JWT VERIFIER
             * =========================================================================
             */

            // Verifier validates:
            //
            // → token signature
            // → issuer
            // → audience
            // → token integrity

            val jwtVerifier = JWT
                .require(
                    Algorithm.HMAC256(config.secret)
                )
                .withAudience(config.audience)
                .withIssuer(config.issuer)
                .build()


            /**
             * Register verifier.
             */

            verifier(jwtVerifier)


            /**
             * =========================================================================
             * TOKEN VALIDATION
             * =========================================================================
             */

            validate { jwtCredential ->

                /**
                 * Extract username from payload claims.
                 */

                val username =
                    jwtCredential
                        .payload
                        .getClaim("username")
                        .asString()

                /**
                 * Validation logic.
                 */

                if (!username.isNullOrBlank()) {

                    /**
                     * Authentication successful.
                     */

                    JWTPrincipal(jwtCredential.payload)

                } else {

                    /**
                     * Authentication failed.
                     */

                    null
                }

                // Returning JWTPrincipal:
                // → authentication success
                //
                // Returning null:
                // → authentication failure
                // → 401 Unauthorized
            }


            /**
             * =========================================================================
             * AUTHENTICATION FAILURE HANDLER
             * =========================================================================
             */

            challenge { _, _ ->

                // Executed when:
                //
                // → token is invalid
                // → token expired
                // → token missing
                // → token verification failed

                call.respondText(
                    "Token is not valid or has expired",
                    status = HttpStatusCode.Unauthorized
                )
            }
        }
    }
}


/**
 * ============================================================================
 * JWT CONFIG DATA CLASS
 * ============================================================================
 */

// Stores all JWT configuration values.

data class JWTConfig(

    // Protected area name.
    val realm: String,

    // Secret key used for signing token.
    val secret: String,

    // Token issuer.
    val issuer: String,

    // Intended token audience.
    val audience: String,

    // Token expiry duration in milliseconds.
    val tokenExpiry: Long
)


/**
 * ============================================================================
 * GENERATE JWT TOKEN
 * ============================================================================
 */

fun generateToken(
    config: JWTConfig,
    username: String
): String {

    /**
     * Create JWT token.
     */

    return JWT.create()

        /**
         * Audience claim.
         */

        .withAudience(config.audience)

        /**
         * Issuer claim.
         */

        .withIssuer(config.issuer)

        /**
         * Custom claim data.
         */

        .withClaim("username", username)

        /**
         * Token expiration time.
         */

        .withExpiresAt(
            Date(
                System.currentTimeMillis() +
                        config.tokenExpiry
            )
        )

        /**
         * Sign token using HMAC256 algorithm.
         */

        .sign(
            Algorithm.HMAC256(config.secret)
        )

    // Same configuration must be used
    // while:
    //
    // → generating token
    // → validating token
}