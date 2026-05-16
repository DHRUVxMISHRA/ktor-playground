package com.example.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.session
import io.ktor.server.response.respondText

fun Application.configureSessionAuthentication() {

    /**
     * =========================================================================
     * SESSION AUTHENTICATION
     * =========================================================================
     *
     * Session Authentication is a stateful authentication mechanism.
     *
     * Stateful means:
     * → Server remembers logged-in users
     * → Session information is stored on server side
     *
     * Sessions help maintain user login state
     * across multiple HTTP requests.
     *
     * Without sessions:
     * → User would need to login again on every request
     */

    install(Authentication) {

        /**
         * session<UserSession>("session-auth")
         *
         * UserSession
         * → session type
         *
         * "session-auth"
         * → authentication provider name
         */

        session<UserSession>("session-auth") {

            /**
             * =========================================================================
             * VALIDATE SESSION
             * =========================================================================
             */

            validate { session ->

                /**
                 * This validate block runs whenever
                 * authenticated routes are accessed.
                 *
                 * If session is valid:
                 * → return session
                 *
                 * If session is invalid:
                 * → return null
                 */

                session

                /**
                 * Since validation already happens
                 * during signup/login,
                 * we directly return session.
                 */
            }


            /**
             * =========================================================================
             * CHALLENGE BLOCK
             * =========================================================================
             *
             * Runs when:
             * → session is invalid
             * → session expired
             * → session missing
             */

            challenge {

                call.respondText(
                    "Unauthorized. Please login",
                    status = HttpStatusCode.Unauthorized
                )

                /**
                 * Response:
                 * → 401 Unauthorized
                 */
            }
        }
    }
}