package com.example.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import kotlin.time.Duration.Companion.seconds

/*
⚙️ SETUP FOR RATE LIMITING

1️⃣ Add dependency in libs.versions.toml:

ktor-server-rate-limit = { module = "io.ktor:ktor-server-rate-limit", version.ref = "ktor" }

2️⃣ Add in build.gradle.kts:

implementation(libs.ktor.server.rate.limit)

3️⃣ Enable in Application.module():

fun Application.module() {

    configureResources()

    // ⚠️ IMPORTANT:
    // Must be BEFORE routing
    configureRateLimit()

    configureRouting()
    configureSerialization()
    configureStatusPages()
    configureRequestValidation()
}

📌 WHY BEFORE ROUTING?

→ So rate limiting applies to all routes
*/
// ==========================
// 🎓 LECTURE 6: RATE LIMITING
// ==========================
// 📌 What is Rate Limiting?
// → Restricts number of requests a client can make in given time
// → Prevents:
//    - API abuse
//    - Server overload
// ==========================

fun Application.configureRateLimit(){

    install(RateLimit){

        // ==========================
        // 🔥 1. GLOBAL RATE LIMIT (COMMENTED)
        // ==========================

        /*
        global {
            rateLimiter(limit = 5 , refillPeriod = 60.seconds)
        }
        */

        /*
        🧠 WHY WE COMMENTED THIS?

        - Global rate limit applies to ALL routes
        - Total allowed requests = 5 per minute (not per route)

        Example:
        If 2 routes exist → total calls still = 5 (not 10)

        ❗ Problem:
        - It OVERRIDES route-specific limits

        👉 So we disable global when using custom route limits
        */


        // ==========================
        // 🔹 2. STATIC RATE LIMIT (PUBLIC)
        // ==========================
        register(RateLimitName("public")) {

            // 10 requests per 60 seconds
            rateLimiter(limit = 10, refillPeriod = 60.seconds)
        }

        /*
        🧠 WHAT IS HAPPENING:

        - Named rate limiter ("public")
        - Can be applied to specific routes

        → Independent from other routes
        */


        // ==========================
        // 🔥 3. DYNAMIC RATE LIMIT (PROTECTED)
        // ==========================
        register(RateLimitName("protected")) {

            rateLimiter(limit = 10, refillPeriod = 60.seconds)

            // ==========================
            // 🔹 REQUEST KEY
            // ==========================
            requestKey { call ->

                // Extract query param "type"
                call.request.queryParameters["type"] ?: ""
            }

            /*
            🧠 WHAT IS requestKey?

            - Defines identity for rate limiting
            - Here → based on query parameter "type"

            Example:
            ?type=admin
            ?type=user
            */


            // ==========================
            // 🔹 REQUEST WEIGHT
            // ==========================
            requestWeight { call, key ->

                when(key){
                    "admin" -> 2
                    else -> 1
                }
            }

            /*
            🧠 WHAT IS requestWeight?

            - Defines cost of each request

            Example:
            limit = 10

            admin → weight = 2 → only 5 requests allowed
            user  → weight = 1 → 10 requests allowed

            🔄 FLOW:
            Each request reduces limit based on weight
            */
        }
    }
}
/*
🧠 PRO UNDERSTANDING (VERY IMPORTANT FLOW)

Client → sends request
→ RateLimit plugin intercepts request
→ Checks:
    - limit
    - refill period
    - request weight

→ If limit NOT exceeded:
    → request allowed → route executes

→ If limit exceeded:
    → request blocked
    → 429 Too Many Requests returned

📌 Headers:
- X-RateLimit-Remaining → requests left
*/