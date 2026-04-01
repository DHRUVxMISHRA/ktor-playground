package com.example.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.bearer


// ==========================
// 🔐 SIMULATED DATABASE
// ==========================

//now to simulate the database lets create a simple map that stores token and the corresponding username
val userDb : Map<String, String> = mapOf(
    "token1" to "user1",
    "token2" to "user2",
    "token3" to "user3",
    "token4" to "user4",
)

//in a real world scenario the tokens will be stored in a structured way in a database for now we are simply mapping
//tokens to username , using this we can retreive the username based on token provided

/*
🧠 WHAT IS TOKEN?

- A token is a unique string issued by server after login
- It represents the identity of the user

👉 Example:
token1 → user1

----------------------------------------

🧠 REAL WORLD:

- Tokens are usually:
  → JWT (JSON Web Token)
  → Random secure strings

- Stored in:
  → Database / Redis / Cache
*/


// ==========================
// 🔐 CONFIGURE BEARER AUTH
// ==========================

fun Application.configureBearerAuthentication(){

    install(Authentication){

//        similar to basic and digest authenticatoin we have bearer function
        bearer("bearer-auth"){

            realm = "Access protected routes"

            /*
            🧠 REALM (same concept as before):

            - Defines protected resource domain
            - Helps client understand access scope
            */

//            to validate the token we can use the authentication function
            authenticate { tokenCredential ->

//                within this authentication we can implement custom token validation
//                for ex we can check if the token exist in our database and retrieve the corresponding user
//                when a client logs in or signup we can generate a token and return it as a response the
//                client can then use the token to access the protected route

                /*
                🧠 tokenCredential.token:

                - Extracts token from:
                  Authorization: Bearer <token>
                */

                val user = userDb[tokenCredential.token]

//                by doing this we are retreivin username from the token if it exist in our database

                /*
                🧠 VALIDATION LOGIC:

                token → search in DB → get username
                */

                if(!user.isNullOrBlank()){
                    UserIdPrincipal(user)

                    /*
                    🧠 SUCCESS:

                    - Token is valid
                    - User identified
                    → access granted
                    */

                }else{

                    /*
                    🧠 FAILURE:

                    - Token not found / invalid
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
🧠 COMPLETE FLOW (BEARER AUTH):

1. User logs in
2. Server generates TOKEN
3. Client stores token (Postman / App / Browser)
4. Client sends request with:
   Authorization: Bearer <token>

5. Server:
   → extracts token
   → validates token
   → maps token → user
   → allows access

----------------------------------------

🔐 WHY USE BEARER AUTH?

- No need to send password repeatedly
- Faster and scalable
- Used in APIs

----------------------------------------

⚠️ IMPORTANT SECURITY NOTES:

- Anyone with token can access (no password needed)
→ keep token secure

- Always use HTTPS
→ prevents token interception

----------------------------------------

🚀 REAL WORLD:

Instead of simple map:

→ Use JWT (JSON Web Token)
→ Token contains:
   - userId
   - expiry time
   - signature

----------------------------------------

🆚 COMPARISON:

Basic Auth:
→ sends password every time ❌

Digest Auth:
→ uses hash + nonce ⚠️

Bearer Auth:
→ uses token ✅ (modern approach)

----------------------------------------

🎯 NEXT STEP:

→ JWT Authentication (MOST IMPORTANT)
*/