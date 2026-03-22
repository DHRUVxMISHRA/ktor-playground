package com.example.plugins.lecture

import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receiveMultipart
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import java.io.File

// ==========================
// 🎓 LECTURE 3: FORM DATA HANDLING
// ==========================
// 📌 What is Form Data?
// → Data sent by client (browser/Postman) like a form submission
//
// Types:
// 1. application/x-www-form-urlencoded → simple key=value pairs
// 2. multipart/form-data → used for files + mixed data
// ==========================

fun Route.handlingFormData() {

    // ==========================
    // 🔹 1. URL ENCODED FORM DATA
    // ==========================
    /**
     * Used when form contains only text fields
     *
     * Internally:
     * Data is sent like → key=value&key2=value2
     *
     * Example:
     * productId=123&quantity=100
     */
    post("checkout") {

        // 🔹 receiveParameters():
        // → reads full form body
        // → converts into key-value map

        val formData = call.receiveParameters()

        // extracting values from map
        val productId = formData["productId"]
        val quantity = formData["quantity"]

        call.respondText(
            "Order placed successfully ProductId : $productId & Quantity : $quantity"
        )
    }

    /*
    🔄 WHAT IS HAPPENING:
    Client sends:
    productId=123&quantity=100

    → receiveParameters() parses it
    → becomes map:
      { productId=123, quantity=100 }

    → we extract values
    → send response

    ✅ BEST FOR:
    - Simple text data

    ❌ LIMITATION:
    - Cannot handle files (binary data)
    */


    // ==========================
    // 🔹 2. MULTIPART FORM DATA
    // ==========================
    /**
     * Used when form contains:
     * - Files
     * - Mixed data (text + file)
     *
     * Internally:
     * Data is divided into multiple "parts"
     *
     * Example:
     * Part 1 → image1.jpg
     * Part 2 → image2.jpg
     * Part 3 → greet = hello
     */
    post("multipartData") {

        // ⚠️ Limit set to 40MB
        val data = call.receiveMultipart(
            formFieldLimit = 1024 * 1024 * 40
        )

        // stores all fields (text + file names)
        val fields = mutableMapOf<String, MutableList<String>>()

        // 🔁 loop through each part
        data.forEachPart { partData ->

            when (partData) {

                // ==========================
                // 📄 TEXT FIELD
                // ==========================
                is PartData.FormItem -> {

                    // name of field (e.g., "greet")
                    val key = partData.name ?: return@forEachPart

                    // value (e.g., "hello")
                    val value = partData.value

                    // store in map
                    fields.getOrPut(key) { mutableListOf() }
                        .add(value)

                    // free memory
                    partData.dispose()
                }

                // ==========================
                // 📁 FILE FIELD
                // ==========================
                is PartData.FileItem -> {

                    val key = partData.name ?: return@forEachPart
                    val fileName = partData.originalFileName ?: return@forEachPart

                    // store file name in map
                    fields.getOrPut(key) { mutableListOf() }
                        .add(fileName)

                    // create file location
                    val file = File("uploads/$fileName").apply {
                        parentFile?.mkdirs()
                    }

                    // 🔥 IMPORTANT:
                    // provider() → gives file data stream
                    // writeChannel() → opens file for writing
                    // copyAndClose() → copies data into file

                    partData.provider().copyAndClose(file.writeChannel())

                    // free memory
                    partData.dispose()
                }

                else -> {}
            }
        }

        call.respond("Form fields : $fields")
    }

    /*
    🔄 WHAT IS HAPPENING:

    Client sends multipart request:
    → images (files)
    → greet (text)

    → receiveMultipart() gives stream of parts
    → forEachPart loops each part

    IF text:
    → read value → store in map

    IF file:
    → read stream → save file to disk
    → store filename

    → final response contains all fields

    ⚠️ IMPORTANT:
    - Data is NOT loaded fully in memory
    - It is processed part-by-part (streaming)

    ⚠️ REAL ISSUE YOU FACED:
    - formFieldLimit = 40MB
    - File > 40MB → request fails
    → 500 Internal Server Error

    📌 FIX (if needed):
    Increase limit:
    formFieldLimit = 1024 * 1024 * 100   // 100MB

    🔥 INTERVIEW POINT:
    - multipart = best for file uploads
    - urlencoded = best for simple text
    - streaming makes multipart scalable
    */
}