package com.example.plugins.lecture

import io.ktor.server.auth.authenticate
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.basicAuthentication(){

// ==========================
// 🔐 BASIC AUTHENTICATION (ROUTE LEVEL)
// ==========================

    //  now if i want user to access this route only when authenticated
//    authenticate("basic-auth") {
////        here basic-auth is the name of authentication we given in BasicAuthentication file since there can be multiple type of
////        authentication
//
//        /*
//        🧠 FLOW OF AUTHENTICATION:
//
//        Client Request → Route (authenticate block)
//        → Ktor checks Authentication plugin
//        → validate{} block runs
//        → If valid → allow access
//        → If invalid → return 401 Unauthorized
//        */
//
//        get("basicAuth") {
//            call.respondText("Hello")
//        }
////        now every request in this route builder or in these authenticate will be only accessible if user provides the auth credentials
////        and they are authenticated
//
//        /*
//        🧪 OUTPUT BEHAVIOUR:
//
//        ❌ WITHOUT AUTH:
//        GET http://127.0.0.1:8080/basicAuth
//        → 401 Unauthorized
//
//        ✅ WITH AUTH (Postman):
//        Authorization → Basic Auth
//        Username: admin
//        Password: password
//
//        → 200 OK
//        → Body: Hello
//        */
//
////        now if i hit  http://127.0.0.1:8080/basicAuth this url then i get the error code 401 Unauthorized
////        since we wont be able to access this route without providing the auth credentials
////        but when i hit  http://127.0.0.1:8080/basicAuth this url with Authrization of basic auth with username as admin and
////        Password as password i get the Hello message in body with code 200 OK
//
//    }
}