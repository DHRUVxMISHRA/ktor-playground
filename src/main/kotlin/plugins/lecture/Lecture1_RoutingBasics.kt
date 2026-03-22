package com.example.plugins.lecture

import com.example.plugins.accountRoutes
import io.ktor.http.*
import io.ktor.resources.Resource
import io.ktor.server.application.*
import io.ktor.server.resources.delete
import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*

// ==========================
// 🎓 LECTURE 1: ROUTING BASICS
// ==========================
// Covers:
// 1. Basic Routing (GET)
// 2. Path Parameters
// 3. Query Parameters
// 4. Dynamic Routing (Regex)
// 5. API Versioning (Regex)
// 6. Type-Safe Routing (Resources)
// 7. Nested Routing (via function)
// ==========================

fun Route.routingBasics() {

    // ==========================
    // 🔹 1. BASIC ROUTE
    // ==========================
    get("/") {
        call.respondText("Hello World!")
    }

    // ⚠️ NOTE:
    // If multiple routes have same path → first defined route executes


    // ==========================
    // 🔹 2. PATH + QUERY PARAMETERS
    // ==========================
    get("blogs/{id}") {

        // 📌 Path Parameter
        val id = call.pathParameters["id"]

        // 📌 Query Parameters
        val q1 = call.queryParameters["q1"]
        val q2 = call.queryParameters["q2"]

        call.respondText("Blog with id: $id, q1: $q1, q2: $q2")

        /*
        Example:
        http://localhost:8080/blogs/dhruv?q1=hello&q2=friends

        Response:
        Blog with id: dhruv, q1: hello, q2: friends
        */
    }


    // ==========================
    // 🔹 3. DYNAMIC ROUTING (REGEX)
    // ==========================
    get(Regex(".+/test")) {

        call.respondText("Dynamic route matched: ends with /test")

        /*
        Matches:
        /blogs/test
        /anything/test
        /abc123/test
        */
    }


    // ==========================
    // 🔹 4. API VERSIONING (REGEX)
    // ==========================
    get(Regex("api/(?<apiVersion>v[1-3])/users")) {

        val version = call.pathParameters["apiVersion"]

        call.respondText("API Version: $version")

        /*
        Matches:
        /api/v1/users
        /api/v2/users
        /api/v3/users
        */
    }


    // ==========================
    // 🔹 5. TYPE-SAFE ROUTING
    // ==========================
    get<Blogs> { blogs ->

        val sort = blogs.sort

        call.respondText("Sort order: $sort")

        /*
        Default:
        /blogs → sort = new

        Override:
        /blogs?sort=all → sort = all
        */
    }


    // ==========================
    // 🔹 6. TYPE-SAFE PATH PARAM
    // ==========================
    delete<Blogs.Blog> { blog ->

        val id = blog.id
        val sort = blog.parent.sort

        call.respondText("Blog id: $id, sort: $sort")

        /*
        Example:
        /blogs/123 → id = 123

        With query:
        /blogs/123?sort=all → sort = all
        */
    }


    // ==========================
    // 🔹 7. NESTED ROUTING (MODULAR)
    // ==========================
    accountRoutes()

    // 👉 Keeps routing clean by separating features
}

/**
 * ==========================
 * 🔹 TYPE-SAFE ROUTING MODEL
 * ==========================
 * Defines routes using @Resource
 *
 * Base Route:
 * /blogs?sort=new
 *
 * Nested Route:
 * /blogs/{id}?sort=new
 */
@Resource("blogs")
class Blogs(val sort: String? = "new") {

    @Resource("{id}")
    data class Blog(
        val parent: Blogs = Blogs(),
        val id: String
    )
}