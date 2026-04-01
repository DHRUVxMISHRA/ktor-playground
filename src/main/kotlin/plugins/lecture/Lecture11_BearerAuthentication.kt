package com.example.plugins.lecture

import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.bearerAuthentication(){

// ==========================
// 🔐 BEARER AUTHENTICATION (ROUTE LEVEL)
// ==========================

    /*
    🧠 WHAT IS BEARER AUTHENTICATION?

    - Bearer Authentication is a token-based authentication mechanism
    - Instead of sending username/password every time,
      client sends a TOKEN in request header

    Format:
    Authorization: Bearer <token>

    👉 Example:
    Authorization: Bearer token1

    ----------------------------------------

    🧠 WHY "Bearer"?

    - Whoever "bears" (holds) the token gets access
    - No need to send credentials again
    */

    authenticate("bearer-auth") {

        /*
        🧠 FLOW:

        Client Request → Authorization: Bearer token
        → Ktor Authentication plugin intercepts
        → authenticate{} block validates token
        → If valid → principal set → route executes
        → If invalid → 401 Unauthorized
        */

        get("bearerAuth") {

            //since we are returning UserIdPrincipal on successful authentication we can refer it here
            val username = call.principal<UserIdPrincipal>()?.name

//          this username is the name in the UserDb map in the Bearer Authentication file, for token1 the username will be user1

            call.respondText("Hello, $username")

//            now if i hit  http://127.0.0.1:8080/bearerAuth this endpoint with Auth type of Bearer token with token as token1
//            i get output in the body as Hello, user1 since token1 is corresponding to user1

            /*
            🧪 OUTPUT:

            ✅ Request:
            GET /bearerAuth
            Authorization: Bearer token1

            → 200 OK
            → Body: Hello, user1

            ❌ Invalid Token:
            → 401 Unauthorized
            */
        }
    }
}