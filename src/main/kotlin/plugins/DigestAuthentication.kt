package com.example.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.digest
import java.security.MessageDigest

// ==========================
// 🔐 REALM DEFINITION
// ==========================

const val Realm = "Access protected routes"

/*
🧠 WHAT IS REALM?

- A realm is a "security domain"
- It tells the client:
  👉 Which protected resource they are accessing

Example:
"Access protected routes"

Client sees:
→ "You are trying to access this protected area"
*/


// ==========================
// 🔐 USER TABLE (HASHED)
// ==========================

val userTable : Map<String, ByteArray> = mapOf(
    "admin" to getMD5Digest("admin:${Realm}:password"),
    "user"  to getMD5Digest("user:${Realm}:123")
)

/*
🧠 PASSWORD STORAGE FORMAT (VERY IMPORTANT):

Digest Authentication uses:

    username : realm : password

Example:
    admin:Access protected routes:password

Then this full string is hashed using MD5

👉 This hash is stored instead of plain password

----------------------------------------

🧠 WHY THIS FORMAT?

- Realm is included to bind credentials to a specific domain
- Even if same password is used elsewhere,
  hash will be different due to realm
*/


// ==========================
// 🔐 MD5 DIGEST FUNCTION
// ==========================

fun getMD5Digest(value : String) : ByteArray{
    return MessageDigest
        .getInstance("MD5")
        .digest(value.toByteArray())
}

/*
🧠 MD5 HASHING:

- Converts input into fixed-size hash
- Used in Digest Auth protocol

⚠️ NOTE:
MD5 is not considered very secure today,
but still used in Digest Authentication standard
*/


// ==========================
// 🔐 CONFIGURE DIGEST AUTH
// ==========================

fun Application.configureDigestAuthentication(){

    install(Authentication){

        digest("digest-auth") {

            realm = Realm

//            a realm is a security domain used in authentication to group and protect resources, so
//            it simply tells clients which protected area they are trying to access

            /*
            🧠 FLOW INSIDE DIGEST AUTH:

            1. Client sends request
            2. Server sends challenge (realm + nonce)
            3. Client responds with hashed credentials
            4. Server verifies hash using digestProvider
            */

            digestProvider { username, realm ->

                userTable[username]

//                it will return the byte array for the username within this table if its admin it will return the byte array of "admin:${Realm}:password"

                /*
                🧠 WHAT THIS DOES:

                - Fetches stored hashed value for user
                - Used to verify client hash

                If username not found:
                → returns null → authentication fails
                */
            }

            validate {credentials ->

                /*
                🧠 VALIDATION STEP:

                - If digest matches → credentials are valid
                - credentials.userName will be populated
                */

                if(credentials.userName.isNotBlank()){

//                    if username is does not exist in table then digest provider will result in null value and that
//                    will cause this userName value to be blank

                    UserIdPrincipal(credentials.userName)

                    /*
                    🧠 SUCCESS:

                    - User authenticated
                    - Principal created
                    → access granted
                    */

                }else{

                    /*
                    🧠 FAILURE:

                    - Invalid credentials
                    → return null
                    → 401 Unauthorized
                    */

                    null
                }
            }
        }
    }
}

/*
🧠 COMPLETE DIGEST AUTH FLOW:

1. Client → Request (no auth)
2. Server → 401 + WWW-Authenticate (realm + nonce)
3. Client → hash(username + password + realm + nonce)
4. Client → sends hashed request
5. Server → verifies using stored hash
6. If match → 200 OK
   Else → 401 Unauthorized

----------------------------------------

🔐 KEY DIFFERENCE:

Basic Auth:
→ Sends password (encoded)

Digest Auth:
→ Sends hash (secure)

----------------------------------------

⚠️ LIMITATIONS:

- Uses MD5 (weak today)
- Complex compared to JWT
- Not commonly used in modern APIs

----------------------------------------

🚀 REAL WORLD:

Use:
→ JWT Authentication
→ OAuth 2.0

Digest Auth is mainly for:
→ legacy systems
→ learning authentication fundamentals
*/