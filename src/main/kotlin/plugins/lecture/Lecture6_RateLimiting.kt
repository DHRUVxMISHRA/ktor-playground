package com.example.plugins.lecture

import io.ktor.server.plugins.ratelimit.RateLimitName
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

// ==========================
// 🎓 LECTURE 6: RATE LIMITING (ROUTES)
// ==========================

fun Route.rateLimiting() {

    // ==========================
    // 🔹 1. BASIC ROUTE (NO LIMIT)
    // ==========================
    post("rate") {

        // call.respondText("Hello")

        val requestLeft = call.response.headers["X-RateLimit-Remaining"]

        call.respondText("$requestLeft request left. ")
    }

    /*
    🧠 WHAT IS HAPPENING:

    - No rate limiter applied
    - But header still available

    📌 X-RateLimit-Remaining:
    → Shows how many requests left
    */


    // ==========================
    // 🔹 2. ANOTHER ROUTE (NO LIMIT)
    // ==========================
    post("rate1") {

        val requestLeft = call.response.headers["X-RateLimit-Remaining"]

        call.respondText("$requestLeft request left. ")
    }

    /*
    ❗ NOTE:

    If GLOBAL rate limit was active:
    → rate + rate1 share same limit

    Example:
    total allowed = 5 (not 10)
    */


    // ==========================
    // 🔥 3. ROUTE-SPECIFIC LIMIT (STATIC)
    // ==========================
    rateLimit(RateLimitName("public")) {

        post("rate2") {

            val requestLeft = call.response.headers["X-RateLimit-Remaining"]

            call.respondText("$requestLeft request left. ")
        }
    }

    /*
    🧠 WHAT IS HAPPENING:

    - Applies "public" rate limiter
    - limit = 10 requests / minute

    ✅ IMPORTANT:
    - Independent of other routes
    - Other routes do NOT affect this limit
    */


    // ==========================
    // 🔥 4. ROUTE-SPECIFIC LIMIT (DYNAMIC)
    // ==========================
    rateLimit(RateLimitName("protected")) {

        post("rate3") {

            val requestLeft = call.response.headers["X-RateLimit-Remaining"]

            call.respondText("$requestLeft request left. ")
        }
    }

    /*
    🧠 WHAT IS HAPPENING:

    - Uses dynamic rate limiter

    Example:
    ?type=admin → weight = 2 → fewer requests
    ?type=user  → weight = 1 → more requests

    📌 RESULT:
    Admin → faster limit exhaustion
    User  → slower limit exhaustion
    */
}