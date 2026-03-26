package com.example.plugins.lecture

import io.ktor.http.ContentDisposition
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.http.content.LocalPathContent
import io.ktor.server.response.*
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.serialization.Serializable
import java.io.File
import java.nio.file.Path
import kotlin.io.path.exists

// ==========================
// 🎓 LECTURE 7: SENDING RESPONSE
// ==========================
// 📌 Covers:
// - Text Response
// - JSON Response
// - File Streaming & Download
// - Headers & Cookies
// - Status Codes
// - Redirects
// ==========================

fun Route.sendingResponse(){

// ==========================
// 🔥 1. TEXT RESPONSE
// ==========================
    get("send"){
        call.respondText(
            text = "Hello , world",
            contentType = ContentType.Text.Plain,
            status = HttpStatusCode.OK
        )

        /**
        OUTPUT (POSTMAN)

        BODY:
        Hello , world

        HEADERS:
        KEY               VALUE
        ---------------------------------
        Content-Length    13
        Content-Type      text/plain; charset=UTF-8
        Connection        keep-alive
         */
    }


// ==========================
// 🔥 2. JSON RESPONSE
// ==========================
    get("product1") {

        val response = ProductResponse(
            message = "Successfully fetched products",
            data = List(10){
                Product1("apple",10,"fruits ")
            }
        )

        call.respond(response)

        /**
        OUTPUT

        BODY:
        {
        "message": "Successfully fetched products",
        "data": [ ... 10 items ... ]
        }

        NOTE:
        - Automatically converted to JSON via ContentNegotiation
         */
    }


// ==========================
// 🔥 3. FILE STREAMING
// ==========================
    get("stream"){
        val fileName = call.request.queryParameters["filename"] ?: " "
        val file = File("uploads/$fileName")

        if(!file.exists()){
            return@get call.respond(HttpStatusCode.NotFound )
        }

        call.respondFile(file)

        /**
        OUTPUT

        URL:
        /stream?filename=sample.jpg → image shown
        /stream?filename=video.mp4 → video streamed

        NOTE:
        - File is streamed (not downloaded)
         */
    }


// ==========================
// 🔥 4. FILE DOWNLOAD
// ==========================
    get("download"){
        val fileName = call.request.queryParameters["filename"] ?: " "
        val file = File("uploads/$fileName")

        if(!file.exists()){
            return@get call.respond(HttpStatusCode.NotFound )
        }

        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Attachment.withParameter(
                ContentDisposition.Parameters.FileName,
                fileName
            ).toString()
        )

        call.respondFile(file)

        /**
        OUTPUT

        POSTMAN:
        → File preview

        BROWSER:
        → File gets downloaded automatically

        NOTE:
        - Content-Disposition = Attachment forces download
         */
    }


// ==========================
// 🔥 5. FILE FROM PATH
// ==========================
    get("fileFromPath"){
        val fileName = call.request.queryParameters["filename"] ?: " "
        val filePath = Path.of("uploads/$fileName")

        if(!filePath.exists()) return@get call.respond(HttpStatusCode.NotFound)
        else call.respond(LocalPathContent(filePath))

        /**
        OUTPUT

        /fileFromPath?filename=sample.jpg → image

        NOTE:
        - No File object used
         */
    }


// ==========================
// 🔥 6. STATUS CODES
// ==========================
    get("status"){
        call.response.status(HttpStatusCode.OK)

        /**
        OUTPUT

        STATUS: 200 OK
        BODY: empty
         */
    }

    get("customStatus"){
        call.response.status(HttpStatusCode(413, "Custom Error Status"))

        /**
        OUTPUT

        STATUS: 413 Custom Error Status
        BODY: empty
         */
    }


// ==========================
// 🔥 7. HEADERS
// ==========================
    get("headers"){

        call.response.headers.append(HttpHeaders.ETag,"hfehfe")
        call.response.header(HttpHeaders.ETag,"hfdhfdhd")
        call.response.etag("hdhdhhd")

        call.response.header("Custom-Header","My custom header value")

        call.respond(HttpStatusCode.OK)

        /**
        OUTPUT

        KEY               VALUE
        ---------------------------------
        ETag              hfehfe
        ETag              hfdhfdhd
        ETag              hdhdhhd
        Custom-Header     My custom header value
        Accept-Ranges     bytes
        Content-Length    0
        Content-Type      text/plain
        Connection        keep-alive

        NOTE:
        - append() → multiple values
        - header() → overwrite/add
         */
    }


// ==========================
// 🔥 8. COOKIES
// ==========================
    get("cookies"){
        call.response.cookies.append(
            "new-cookie",
            "new cookie value"
        )

        call.respond(HttpStatusCode.OK)

        /**
        OUTPUT

        STATUS: 200 OK

        HEADERS:
        KEY           VALUE
        ---------------------------------
        Set-Cookie    new-cookie=new+cookie+value

        COOKIES TAB:
        KEY          VALUE
        ---------------------------------
        new-cookie   new%2Bcookie...

        NOTE:
        - Cookie automatically encoded
         */
    }


// ==========================
// 🔥 9. REDIRECT
// ==========================
    get("redirect"){
        call.respondRedirect("moved", permanent = true)

        /**
        OUTPUT

        STATUS: 301 Moved Permanently
        Redirect → /moved
         */
    }

    get("moved"){
        call.respondText("Redirected to moved route")

        /**
        OUTPUT

        BODY:
        Redirected to moved route
         */
    }

}


// ==========================
// 🔥 DATA CLASSES
// ==========================
@Serializable
data class Product1(
    val name : String?,
    val price : Int?,
    val category : String?
)

@Serializable
data class ProductResponse(
    val message : String,
    val data : List<Product1>
)