package com.example.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import kotlinx.serialization.Serializable

fun Application.configureSession() {

    /**
     * =========================================================================
     * KTOR SESSIONS PLUGIN
     * =========================================================================
     *
     * Sessions plugin is used to:
     * → create sessions
     * → store sessions
     * → manage cookies
     * → persist user authentication state
     */

    install(Sessions) {

        /**
         * =========================================================================
         * COOKIE-BASED SESSION
         * =========================================================================
         *
         * cookie<UserSession>("user_session")
         *
         * UserSession
         * → session data type
         *
         * "user_session"
         * → cookie key name
         */

        cookie<UserSession>("user_session") {

            /**
             * Cookie Name:
             * user_session
             */

            /**
             * =========================================================================
             * COOKIE PATH
             * =========================================================================
             */

            cookie.path = "/"

            /**
             * "/"
             * means:
             * → cookie accessible on all routes
             *
             * Examples:
             * "/api"
             * → only API routes can access cookie
             *
             * "/admin"
             * → only admin routes can access cookie
             */


            /**
             * =========================================================================
             * COOKIE EXPIRATION
             * =========================================================================
             */

            cookie.maxAgeInSeconds = 300

            /**
             * 300 seconds = 5 minutes
             *
             * After 5 minutes:
             * → cookie expires automatically
             * → user becomes logged out
             */

        }
    }
}


/**
 * =========================================================================
 * SESSION DATA CLASS
 * =========================================================================
 *
 * Stores data associated with current user session.
 */

@Serializable
data class UserSession(
    val username: String
)