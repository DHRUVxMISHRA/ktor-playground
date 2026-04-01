package com.example.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserHashedTableAuth
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.util.getDigestFunction

fun Application.configureBasicAuthentication(){

    val hashedUserTable = createHashedUserTable()

// ==========================
// 🔐 INSTALL AUTHENTICATION PLUGIN
// ==========================

    install(Authentication){

//        now for basic authentication we have
        basic("basic-auth") {

            /*
            🧠 WHAT IS BASIC AUTH?

            - Sends username & password in header (Base64 encoded)
            - Format:
                Authorization: Basic base64(username:password)

            ⚠️ Not encrypted → only encoded
            👉 Should be used with HTTPS in real apps
            */

            validate{credentials->

//                val username = credentials.name
//                val password = credentials.password
//
//                if(username == "admin" && password == "password"){
//                    UserIdPrincipal(username)
//                } else{
//                    null
//                }

//  so currently we hard coded the username and password value there is one more way to store username and password in the memory
//  using user hash table auth

                /*
                🧠 BETTER APPROACH:

                Instead of hardcoding credentials:
                → Use hashed user table
                → More secure
                */

                hashedUserTable.authenticate(credentials)

//               this authenticate function will automatically perform the validation like if have the username and password valid then
//               it will return UserIdPrinciple if authentication is successful else it will return null

                /*
                🧠 INTERNAL FLOW:

                credentials → username + password
                → password hashed using digest
                → compared with stored hash
                → match → UserIdPrincipal returned
                → else → null → 401 Unauthorized
                */

//                over here instead of using any in memory data base we can also use SQL or NOSQL database in order to perform
//                the validation, in which we can simply store our username and hashed password

            }

        }
    }
}

fun createHashedUserTable () : UserHashedTableAuth{

// ==========================
// 🔐 PASSWORD HASHING + SALTING
// ==========================

//    a digest function is something that encrypts our password, so instead of storing password in plane text format
//    what digest function will do is that it will encrypt our password so that even if someone accesses our database
//    they won't be able to decrypt our password easily

    val digestFunction = getDigestFunction("SHA-256"){"ktor${it.length}"}

    /*
    🧠 DIGEST FUNCTION EXPLAINED:

    - Algorithm: SHA-256
    - Input: password
    - Output: hashed password

    👉 Example:
    password → "password123"
    → hashed → "a8f5f167f44f4964e6c998dee827110c..."

    */

//    we also need to pass the salts, the salts also make it harder to decrypt our password
//    it is a good idea to have different salt for different users, but now we have same but it will vary on the basis of password length

    /*
    🧠 SALT EXPLAINED:

    - Salt = extra value added before hashing
    - Here: "ktor${it.length}"

    👉 Example:
    password = "password"
    → salt = "ktor8"
    → final = "ktor8password"
    → then hashed

    🔥 WHY SALT?
    → Prevents rainbow table attacks
    → Makes hashing more secure
    */

    return UserHashedTableAuth(
        digester = digestFunction,
        table = mapOf(

            /*
            🧠 USER TABLE:

            username → hashed(password)

            NOTE:
            Passwords are NOT stored as plain text
            */

            "admin" to digestFunction("password"),
            "user"  to digestFunction("123")
        )
    )
}