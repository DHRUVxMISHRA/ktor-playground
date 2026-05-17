package com.example.plugins.lecture

import com.example.plugins.JWTConfig
import com.example.plugins.generateToken
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.serialization.Serializable

/**
 * ============================================================================
 * JWT AUTHENTICATION
 * ============================================================================
 */

// JWT stands for JSON Web Token.
//
// JWT is a compact and URL-safe token format
// used for securely transferring information
// between client and server as JSON data.
//
// JWT Authentication is commonly used in:
//
// → REST APIs
// → Mobile applications
// → Web applications
// → Microservices
//
// JWT authentication is STATELESS.
//
// Stateless means:
//
// → Server does NOT store login session data
// → Server only validates the token
// → Authentication information exists inside token itself
//
// Because of this:
//
// → JWT authentication is scalable
// → No server-side session storage is required
// → Suitable for distributed systems and microservices


/**
 * ============================================================================
 * STRUCTURE OF JWT TOKEN
 * ============================================================================
 */

// A JWT token consists of 3 parts:
//
// HEADER.PAYLOAD.SIGNATURE


/**
 * ============================================================================
 * 1. HEADER
 * ============================================================================
 */

// The header contains metadata about the token.
//
// Example:
//
// {
//    "alg": "HS256",
//    "typ": "JWT"
// }
//
// alg → algorithm used for signature generation
// typ → token type


/**
 * ============================================================================
 * 2. PAYLOAD
 * ============================================================================
 */

// Payload contains CLAIM DATA.
//
// Claims are pieces of information attached to token.
//
// Example:
//
// {
//    "username": "admin",
//    "exp": 1737392020
// }
//
// Common claim data:
//
// → username
// → userId
// → email
// → token expiry
// → roles
//
// When client sends token back to server,
// server extracts payload information
// and identifies the user.


/**
 * ============================================================================
 * 3. SIGNATURE
 * ============================================================================
 */

// Signature is used to verify:
//
// → token integrity
// → token authenticity
//
// Signature is generated using:
//
// → header
// → payload
// → secret key
//
// If token data is modified,
// signature verification fails.


/**
 * ============================================================================
 * JWT AUTHENTICATION FLOW
 * ============================================================================
 */

// STEP 1:
// Client sends login/signup request
// with username and password.
//
// STEP 2:
// Server validates credentials.
//
// STEP 3:
// If credentials are valid:
//
// → Server generates JWT token
// → Server sends token back to client
//
// STEP 4:
// Client stores token.
//
// Usually inside:
//
// → local storage
// → secure storage
// → memory
//
// STEP 5:
// Client sends token in Authorization header.
//
// Example:
//
// Authorization: Bearer jwt_token_here
//
// STEP 6:
// Server validates token.
//
// If token is valid:
//
// → user gets access
//
// Otherwise:
//
// → 401 Unauthorized response


fun Routing.JWTAuthentication(config: JWTConfig) {

    /**
     * =========================================================================
     * IN-MEMORY DATABASE
     * =========================================================================
     */

    // Temporary in-memory database.

    val usersDB = mutableMapOf<String, String>()

    // Key   → username
    // Value → password

    // Real applications use:
    //
    // → SQL databases
    // → MongoDB
    // → Firebase
    // → PostgreSQL
    // → MySQL
    // etc.


    /**
     * =========================================================================
     * JWT SIGNUP ROUTE
     * =========================================================================
     */

    post("JWTSignup") {

        /**
         * Receives JSON request body
         * and converts it into JWTAuthRequest object.
         *
         * Example:
         *
         * {
         *   "username": "user",
         *   "password": "123"
         * }
         */

        val requestData = call.receive<JWTAuthRequest>()


        /**
         * =========================================================================
         * CHECK IF USER ALREADY EXISTS
         * =========================================================================
         */

        if (usersDB.containsKey(requestData.username)) {

            // Prevent duplicate signup.

            call.respondText("User already exists")

        } else {

            /**
             * =========================================================================
             * STORE USER IN DATABASE
             * =========================================================================
             */

            usersDB[requestData.username] = requestData.password

            /**
             * Example:
             *
             * usersDB["user"] = "123"
             */


            /**
             * =========================================================================
             * GENERATE JWT TOKEN
             * =========================================================================
             */

            // Token contains:
            //
            // → username
            // → issuer
            // → audience
            // → expiry
            //
            // signed using secret key

            val token = generateToken(
                config = config,
                username = requestData.username
            )


            /**
             * =========================================================================
             * SEND TOKEN TO CLIENT
             * =========================================================================
             */

            call.respond(
                mapOf("token" to token)
            )

            // Client will use this token
            // to access protected routes.
        }


        /**
         * =========================================================================
         * SIGNUP FLOW SUMMARY
         * =========================================================================
         */

        // 1. Receive username/password
        //
        // 2. Check if user already exists
        //
        // 3. Store user in database
        //
        // 4. Generate JWT token
        //
        // 5. Send token to client
    }


    /**
     * =========================================================================
     * JWT LOGIN ROUTE
     * =========================================================================
     */

    post("JWTLogin") {

        /**
         * Receive login request body.
         */

        val requestData = call.receive<JWTAuthRequest>()


        /**
         * =========================================================================
         * CHECK IF USER EXISTS
         * =========================================================================
         */

        val storedPassword =
            usersDB[requestData.username]
                ?: return@post call.respondText(
                    "User does not exist"
                )

        // If username is not found:
        //
        // → null is returned
        // → login fails


        /**
         * =========================================================================
         * VALIDATE PASSWORD
         * =========================================================================
         */

        if (storedPassword == requestData.password) {

            /**
             * Password matched successfully.
             */

            val token = generateToken(
                config = config,
                username = requestData.username
            )

            /**
             * Send token back to client.
             */

            call.respond(
                mapOf("token" to token)
            )

            // Client can now use this token
            // to access protected routes.

        } else {

            /**
             * Password mismatch.
             */

            call.respondText("Invalid credentials")
        }
    }


    /**
     * =========================================================================
     * JWT LOGOUT
     * =========================================================================
     */

    // JWT authentication is STATELESS.
    //
    // Server does not store session state.
    //
    // Because of this:
    //
    // → logout route is usually unnecessary
    //
    // Client simply:
    //
    // → removes token from storage
    // → stops sending token
    //
    // Then authentication automatically fails.


    /**
     * =========================================================================
     * PROTECTED ROUTES
     * =========================================================================
     */

    authenticate("jwt-auth") {

        /**
         * Only authenticated users
         * can access routes inside this block.
         */

        get("") {

            /**
             * =========================================================================
             * ACCESS JWT PRINCIPAL
             * =========================================================================
             */

            val principal =
                call.principal<JWTPrincipal>()

            // JWTPrincipal contains token payload data.


            /**
             * =========================================================================
             * EXTRACT USERNAME FROM TOKEN
             * =========================================================================
             */

            val username =
                principal
                    ?.payload
                    ?.getClaim("username")
                    ?.asString()

            // Username was stored inside payload
            // while generating token.


            /**
             * =========================================================================
             * GET TOKEN EXPIRY TIME
             * =========================================================================
             */

            val expiresAt =
                principal
                    ?.expiresAt
                    ?.time
                    ?.minus(System.currentTimeMillis())

            // Remaining token validity time in milliseconds.


            /**
             * =========================================================================
             * RESPONSE
             * =========================================================================
             */

            call.respondText(
                "Hello, $username! The token expires after $expiresAt ms."
            )
        }
    }


    /**
     * =========================================================================
     * COMPLETE FLOW TESTING
     * =========================================================================
     */


    /**
     * STEP 1
     * -------------------------------------------------------------------------
     * Access protected route WITHOUT token.
     *
     * URL:
     * http://127.0.0.1:8080/
     *
     * Output:
     * Token is not valid or has expired
     *
     * Status:
     * 401 Unauthorized
     */


    /**
     * STEP 2
     * -------------------------------------------------------------------------
     * Try login before signup.
     *
     * URL:
     * http://127.0.0.1:8080/JWTLogin
     *
     * Body:
     *
     * {
     *   "username": "user",
     *   "password": "123"
     * }
     *
     * Output:
     * User does not exist
     */


    /**
     * STEP 3
     * -------------------------------------------------------------------------
     * Signup user.
     *
     * URL:
     * http://127.0.0.1:8080/JWTSignup
     *
     * Body:
     *
     * {
     *   "username": "user",
     *   "password": "123"
     * }
     *
     * Output:
     *
     * {
     *   "token": "jwt_token_here"
     * }
     */


    /**
     * STEP 4
     * -------------------------------------------------------------------------
     * Access protected route WITH token.
     *
     * URL:
     * http://127.0.0.1:8080/
     *
     * In Postman:
     *
     * Authorization
     * → Bearer Token
     * → Paste generated token
     *
     * Output:
     *
     * Hello, user! The token expires after 86136224 ms.
     */


    /**
     * STEP 5
     * -------------------------------------------------------------------------
     * Login existing user.
     *
     * URL:
     * http://127.0.0.1:8080/JWTLogin
     *
     * Body:
     *
     * {
     *   "username": "user",
     *   "password": "123"
     * }
     *
     * Output:
     *
     * {
     *   "token": "jwt_token_here"
     * }
     */
}


/**
 * ============================================================================
 * REQUEST BODY DATA CLASS
 * ============================================================================
 */

@Serializable
data class JWTAuthRequest(
    val username: String,
    val password: String
)