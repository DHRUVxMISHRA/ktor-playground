package com.example.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.plugins.statuspages.statusFile
import io.ktor.server.response.respond
import io.ktor.server.response.respondText

// ==========================
// 🔹 STATUS PAGES CONFIGURATION
// ==========================
// 📌 What is StatusPages?
// → A Ktor plugin used for global error handling
// → It intercepts:
//    1. Exceptions (like crash / runtime errors)
//    2. HTTP status codes (400, 401, 404, etc.)
// → And allows us to return custom responses (text / HTML)
// ==========================

fun Application.configureStatusPages(){

    install(StatusPages){
                /*
        🧠 FLOW OF ERROR HANDLING IN KTOR:

        Route → returns status / throws exception
        → StatusPages plugin intercepts it
        → Decides how to handle (custom text OR HTML)
        → Sends final response to client

        📌 Example:
        call.respond(HttpStatusCode.BadRequest)

        → intercepted by StatusPages
        → status(HttpStatusCode.BadRequest) OR statusFile()
        → custom response returned
        */

        // ==========================
        // 🔥 1. GLOBAL EXCEPTION HANDLER
        // ==========================
        exception<Throwable>{ call, cause ->

            /*
            🧠 WHAT IS HAPPENING:

            - Any unhandled exception in app will come here
            - Example:
                throw Exception("Database failed")

            - Instead of crashing server,
              we send custom 500 response
            */

            call.respondText(
                "500 : ${cause.message}",
                status = HttpStatusCode.InternalServerError
            )
        }


        // ==========================
        // 🔥 2. CUSTOM STATUS HANDLERS
        // ==========================
        /*
        🧠 WHAT IS HAPPENING:

        - When we return a status code from route:
            call.respond(HttpStatusCode.BadRequest)

        - This block intercepts it and overrides default response
        */

        // 🔹 401 Unauthorized
        status(HttpStatusCode.Unauthorized){ call, _ ->

            call.respondText(
                "401 : You are not authorized to access the resource",
                status = HttpStatusCode.Unauthorized
            )
        }

        // 🔹 400 Bad Request
        status(HttpStatusCode.BadRequest){ call, _ ->

            call.respondText(
                "400 : Please check your request body",
                status = HttpStatusCode.BadRequest
            )
        }


        // ==========================
        // 🔥 3. HTML ERROR PAGES
        // ==========================
        /*
        🧠 WHAT IS statusFile():

        - Maps HTTP status code → HTML file
        - Instead of plain text → returns HTML page
        */


        /*
        🔥 MULTIPLE STATUS FILE MAPPING:

        # is replaced by status code

        Example:
        filePattern = "errors/error#.html"

        → 400 → errors/error400.html
        → 401 → errors/error401.html
        → 404 → errors/error404.html
        */


        /*
        ⚠️ VERY IMPORTANT (PRIORITY RULE):

        - If same status code is defined multiple times,
          → LAST defined handler will execute

        Example:
        status(HttpStatusCode.BadRequest)   ❌ ignored
        statusFile(HttpStatusCode.BadRequest) ✅ executed

        👉 Order matters in StatusPages
        */


        /*
        ⚠️ VERY IMPORTANT (HTML FILES):

        - HTML files are NOT created automatically
        - You MUST create them manually

        📁 Required structure:
        resources/
         └── errors/
              ├── error400.html
              ├── error401.html
              ├── error404.html

        ❌ If file is missing:
        → error OR fallback response

        👉 Ktor only maps status → file
        */


        statusFile(
            HttpStatusCode.BadRequest,
            HttpStatusCode.Unauthorized,
            HttpStatusCode.NotFound,
            filePattern = "errors/error#.html"
        )

        // ==========================
// 🔥 4. REQUEST VALIDATION EXCEPTION HANDLER
// ==========================
        exception<RequestValidationException> { call, cause ->

            /*
            🧠 WHAT IS HAPPENING:

            - This handles validation errors thrown by RequestValidation plugin

            - Whenever validation fails:
                ValidationResult.Invalid("error message")

            → Ktor throws:
                RequestValidationException

            - Instead of default response,
              we intercept it here
            */


            /*
            🔄 FLOW:

            Client → sends invalid request
            → RequestValidation plugin runs
            → validation fails
            → RequestValidationException thrown
            → StatusPages intercepts
            → THIS block executes
            → custom response sent
            */


            /*
            📌 RESPONSE FORMAT:

            {
              "errors": [
                "Invalid product name",
                "Invalid price"
              ]
            }

            👉 cause.reasons = list of all validation error messages
            */


            /*
            ❗ WHY WE USE THIS?

            - Default Ktor validation response is not user-friendly
            - We want structured JSON response (like real APIs)

            ✅ Best Practice:
            Always handle validation errors globally
            */


            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("errors" to cause.reasons)
            )
        }
    }
}