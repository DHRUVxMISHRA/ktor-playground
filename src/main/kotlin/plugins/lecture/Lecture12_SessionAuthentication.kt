package com.example.plugins.lecture

import com.example.plugins.UserSession
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import kotlinx.serialization.Serializable

fun Route.sessionAuthentication() {

    /**
     * =========================================================================
     * SESSION AUTHENTICATION
     * =========================================================================
     *
     * Session Authentication is a STATEFUL authentication mechanism.
     *
     * In session authentication:
     * → User logs in once
     * → Server creates a session
     * → Session data is stored on the server
     * → Session ID / session data is stored in browser cookies
     * → Browser automatically sends cookie in future requests
     * → Server validates the session and authenticates the user
     *
     * Unlike Bearer/JWT authentication:
     * → Session authentication is stateful
     * → Server keeps track of logged in users
     * → Commonly used in traditional web applications
     *
     * Flow of Session Authentication:
     *
     * Step 1:
     * User signs up or logs in
     *
     * Step 2:
     * Server validates credentials
     *
     * Step 3:
     * Server creates a session
     *
     * Step 4:
     * Session is stored in cookies
     *
     * Step 5:
     * Browser automatically sends cookies in future requests
     *
     * Step 6:
     * Server validates session and grants access
     *
     * Important:
     * → Cookies are automatically handled by browser/Postman
     * → Client does not manually send token like Bearer authentication
     * → Session expires after timeout/logout
     */

    /**
     * =========================================================================
     * IN-MEMORY DATABASE
     * =========================================================================
     */

    // In-memory map to store users temporarily
    val usersDB = mutableMapOf<String, String>()

    // Key   → username
    // Value → password

    // In real-world applications:
    // → SQL database
    // → NoSQL database
    // → Cloud database
    // will be used instead of mutableMap


    /**
     * =========================================================================
     * SESSION SIGNUP ROUTE
     * =========================================================================
     */

    post("sessionSignup") {

        /**
         * receive<AuthRequest>()
         *
         * This receives JSON request body from client
         * and converts it into AuthRequest object.
         *
         * Example Request Body:
         *
         * {
         *   "username": "admin",
         *   "password": "123"
         * }
         */

        val requestData = call.receive<AuthRequest>()

        /**
         * requestData.username
         * → admin
         *
         * requestData.password
         * → 123
         */


        /**
         * =========================================================================
         * CHECK IF USER ALREADY EXISTS
         * =========================================================================
         */

        if (usersDB.containsKey(requestData.username)) {

            // If username already exists in database
            // then duplicate signup is not allowed

            call.respondText("User already exists")

        } else {

            /**
             * =========================================================================
             * STORE USER IN DATABASE
             * =========================================================================
             */

            // Store user in memory database

            usersDB[requestData.username] = requestData.password

            /**
             * Example:
             *
             * usersDB["admin"] = "123"
             */


            /**
             * =========================================================================
             * CREATE SESSION
             * =========================================================================
             *
             * call.sessions.set(...)
             *
             * This creates and stores session data in cookies.
             *
             * UserSession(requestData.username)
             * → stores currently logged-in username
             */

            call.sessions.set(UserSession(requestData.username))

            /**
             * Cookie gets generated automatically.
             *
             * Example Cookie:
             *
             * +--------------+------------------+-----------+------+-------------------+----------+--------+
             * | Name         | Value            | Domain    | Path | Expires           | HttpOnly | Secure |
             * +--------------+------------------+-----------+------+-------------------+----------+--------+
             * | user_session | %7B%22usern...  | 127.0.0.1 | /    | Sat, 16 May 20... | true     | false  |
             * +--------------+------------------+-----------+------+-------------------+----------+--------+
             */

            call.respondText("User signup success")
        }
    }


    /**
     * =========================================================================
     * SESSION LOGIN ROUTE
     * =========================================================================
     */

    post("sessionLogin") {

        /**
         * Receive request body from client
         */

        val requestData = call.receive<AuthRequest>()


        /**
         * =========================================================================
         * CHECK IF USER EXISTS
         * =========================================================================
         */

        val storedPassword =
            usersDB[requestData.username]
                ?: return@post call.respondText("User does not exists")

        /**
         * If username is not found:
         * → null is returned
         * → login fails
         */


        /**
         * =========================================================================
         * VALIDATE PASSWORD
         * =========================================================================
         */

        if (storedPassword == requestData.password) {

            /**
             * Password matched successfully
             *
             * Create session again for logged-in user
             */

            call.sessions.set(UserSession(requestData.username))

            /**
             * Cookie gets generated again
             * and stored in browser/Postman
             */

            call.respondText("Login Success")

        } else {

            /**
             * Password did not match
             */

            call.respondText("Invalid credentials")
        }
    }


    /**
     * =========================================================================
     * SESSION LOGOUT ROUTE
     * =========================================================================
     */

    post("sessionLogout") {

        /**
         * clear<UserSession>()
         *
         * Removes session cookie.
         *
         * User becomes logged out.
         */

        call.sessions.clear<UserSession>()

        call.respondText("Logout success")
    }


    /**
     * =========================================================================
     * PROTECTED ROUTE
     * =========================================================================
     */

    authenticate("session-auth") {

        /**
         * Only authenticated users
         * having valid sessions
         * can access this route.
         */

        get("") {

            /**
             * call.principal<UserSession>()
             *
             * Retrieves current authenticated session.
             */

            val username =
                call.principal<UserSession>()?.username

            /**
             * username contains:
             * → currently logged-in username
             */

            call.respondText("Hello, $username")

            /**
             * Example Output:
             *
             * Hello, admin
             */
        }
    }


    /**
     * =========================================================================
     * COMPLETE FLOW TESTING
     * =========================================================================
     */

    /**
     * STEP 1:
     * -------------------------------------------------------------------------
     * Hit:
     * http://127.0.0.1:8080/
     *
     * Output:
     * Unauthorized. Please login
     *
     * Status Code:
     * 401 Unauthorized
     */

    /**
     * STEP 2:
     * -------------------------------------------------------------------------
     * Hit:
     * http://127.0.0.1:8080/sessionLogin
     *
     * Body:
     *
     * {
     *   "username": "admin",
     *   "password": "123"
     * }
     *
     * Output:
     * User does not exists
     *
     * Because signup was not performed yet.
     */

    /**
     * STEP 3:
     * -------------------------------------------------------------------------
     * Hit:
     * http://127.0.0.1:8080/sessionSignup
     *
     * Body:
     *
     * {
     *   "username": "admin",
     *   "password": "123"
     * }
     *
     * Output:
     * User signup success
     *
     * Cookies:
     *
     * +--------------+------------------+-----------+------+-------------------+----------+--------+
     * | Name         | Value            | Domain    | Path | Expires           | HttpOnly | Secure |
     * +--------------+------------------+-----------+------+-------------------+----------+--------+
     * | user_session | %7B%22usern...  | 127.0.0.1 | /    | Sat, 16 May 20... | true     | false  |
     * +--------------+------------------+-----------+------+-------------------+----------+--------+
     */

    /**
     * STEP 4:
     * -------------------------------------------------------------------------
     * Hit:
     * http://127.0.0.1:8080/
     *
     * Output:
     * Hello, admin
     *
     * Because valid session cookie exists.
     */

    /**
     * STEP 5:
     * -------------------------------------------------------------------------
     * Hit:
     * http://127.0.0.1:8080/sessionLogout
     *
     * Output:
     * Logout success
     *
     * Session cookie gets cleared.
     */

    /**
     * STEP 6:
     * -------------------------------------------------------------------------
     * Hit:
     * http://127.0.0.1:8080/
     *
     * Output:
     * Unauthorized. Please login
     *
     * Status Code:
     * 401 Unauthorized
     */

    /**
     * STEP 7:
     * -------------------------------------------------------------------------
     * Hit:
     * http://127.0.0.1:8080/sessionLogin
     *
     * Body:
     *
     * {
     *   "username": "admin",
     *   "password": "123"
     * }
     *
     * Output:
     * Login Success
     *
     * Cookie gets generated again.
     *
     * +--------------+------------------+-----------+------+-------------------+----------+--------+
     * | Name         | Value            | Domain    | Path | Expires           | HttpOnly | Secure |
     * +--------------+------------------+-----------+------+-------------------+----------+--------+
     * | user_session | %7B%22usern...  | 127.0.0.1 | /    | Sat, 16 May 20... | true     | false  |
     * +--------------+------------------+-----------+------+-------------------+----------+--------+
     */
}


/**
 * =========================================================================
 * REQUEST BODY DATA CLASS
 * =========================================================================
 */

@Serializable
data class AuthRequest(
    val username: String,
    val password: String
)