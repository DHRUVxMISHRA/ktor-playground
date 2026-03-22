package com.example.plugins.lecture

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.*
import kotlinx.serialization.Serializable
import java.io.File

// ==========================
// 🎓 LECTURE 2: REQUEST HANDLING
// ==========================
// Covers:
// 1. Text Data
// 2. Byte Channel
// 3. File Upload (3 methods)
// 4. JSON Handling
// ==========================

fun Route.requestHandling() {

    // ==========================
    // 🔹 1. TEXT DATA
    // ==========================
    post("greet") {

        // - receiveText() reads raw text from request body
        // - Used when client sends plain text

        val name = call.receiveText()

        call.respondText("Hello $name!")

        /*
        Example:
        POST /greet
        Body (raw text): Ktor

        Response:
        Hello Ktor!
        */
    }


    // ==========================
    // 🔹 2. BYTE CHANNEL DATA
    // ==========================
    post("channel") {

        // - receiveChannel() gives ByteReadChannel
        // - Reads data in chunks (efficient for large data)

        val channel = call.receiveChannel()

        // - readRemaining() → reads full content
        // - readText() → converts to String
        val text = channel.readRemaining().readText()

        call.respondText(text)

        /*
        Example:
        POST /channel
        Body: "Ktor is backend framework"

        Response:
        Ktor is backend framework

        ✅ Advantage:
        - Does NOT load entire data into memory at once
        - Better for large payloads
        */
    }


    // ==========================
    // 🔹 3. FILE UPLOAD
    // ==========================
    /**
     * This route demonstrates different ways to upload files:
     *
     * 1. ByteArray  → Simple but memory heavy ❌
     * 2. Stream     → Better, uses InputStream ⚠️
     * 3. Channel    → Best (non-blocking, scalable) ✅
     */
    post("upload") {

        // 📁 Create file location
        // - uploads/ folder will be created if not exists
        // - sample2.jpg is static name (will overwrite every time)

        val file = File("uploads/sample2.jpg").apply {
            parentFile?.mkdirs()
        }

        /*
        🟡 METHOD 1: BYTE ARRAY
        - Loads entire file into memory
        - Not suitable for large files

        val byteArray = call.receive<ByteArray>()
        file.writeBytes(byteArray)
        */

        /*
        🟠 METHOD 2: STREAM
        - Uses InputStream
        - More efficient than ByteArray but still blocking

        val stream = call.receiveStream()
        FileOutputStream(file).use { outputStream ->
            stream.copyTo(outputStream, bufferSize = 16 * 1024)
        }
        */

        // 🟢 METHOD 3: CHANNEL (BEST)

        // - Non-blocking (coroutines)
        // - Efficient for large files

        val channel = call.receiveChannel()

        // - copyAndClose → copies data & closes channel safely
        // - writeChannel() → writes data into file

        channel.copyAndClose(file.writeChannel())

        call.respondText("File upload successful!")
    }

    /*
    🔄 FILE UPLOAD FLOW:
    Client → sends file
    → Server receives (channel/stream/bytearray)
    → Writes to disk
    → Sends response

    ⚠️ LIMITATIONS:
    - Static file name → overwrites existing file
    - No validation (file type, size)
    - No error handling

    🔥 INTERVIEW POINT:
    - Channel-based upload is preferred in Ktor
    */


    // ==========================
    // 🔹 4. JSON OBJECT HANDLING
    // ==========================
    /**
     * This route handles JSON data sent from client
     * and converts it into Kotlin object using serialization
     */
    post("product") {

        // - receiveNullable<Product>()
        // - Converts JSON → Product object
        // - Returns null if invalid/missing data

        val product = call.receiveNullable<Product>()
            ?: return@post call.respond(HttpStatusCode.BadRequest)

        // - respond() automatically converts object → JSON
        call.respond(product)
    }

    /*
    🔄 JSON FLOW:
    Client sends JSON
    → Ktor converts JSON → Product object
    → Server processes data
    → Response sent as JSON

    ⚠️ IMPORTANT:
    - Requires ContentNegotiation plugin
    - JSON keys must match data class properties

    🔥 INTERVIEW:
    - receive<T>() → unsafe (can crash)
    - receiveNullable<T>() → safe
    */
}


// ==========================
// 🔹 PRODUCT DATA CLASS
// ==========================
/**
 * Represents product data exchanged between client and server
 *
 * Example JSON:
 * {
 *   "name": "Orange",
 *   "category": "Fruits",
 *   "price": 100
 * }
 *
 * Explanation:
 * - @Serializable → required for JSON conversion
 * - Property names MUST match JSON keys
 *
 * Used in:
 * - Receiving request body (JSON → Object)
 * - Sending response (Object → JSON)
 */
@Serializable
data class Product(
    val name: String,
    val category: String,
    val price: Int
)