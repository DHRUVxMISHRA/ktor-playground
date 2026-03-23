package com.example.plugins.lecture

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

// ==========================
// 🎓 LECTURE 4: STATUS PAGES (ERROR HANDLING)
// ==========================
// Purpose:
// → Handle HTTP errors & exceptions globally
// → Return custom responses instead of default server errors
// ==========================

fun Route.statusPages(){

    post("productDetails"){

        /*
        🔄 WHAT IS HAPPENING:

        This route is intentionally used to trigger different HTTP status codes
        so that we can see how StatusPages plugin handles them.
        */

        // 🔥 CASE 1: Exception → handled by exception<Throwable>
        // throw Exception("Database failed to initialize")

        // 🔥 CASE 2: 401 Unauthorized → handled by status(HttpStatusCode.Unauthorized)
        // call.respond(HttpStatusCode.Unauthorized)

        // 🔥 CASE 3: 400 Bad Request → handled by status(HttpStatusCode.BadRequest)
        // call.respond(HttpStatusCode.BadRequest)

        // 🔥 CASE 4: 404 Not Found → handled by statusFile (HTML response)
        call.respond(HttpStatusCode.NotFound)
    }
}

/*
🧠 UNDERSTANDING:

- This route does NOT handle errors itself
- It just returns status codes / throws exception

👉 Actual handling is done in StatusPages plugin


*/