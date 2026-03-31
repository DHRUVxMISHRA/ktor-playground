package com.example.plugins.lecture

import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.server.http.content.file
import io.ktor.server.http.content.files
import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.http.content.staticFiles
import io.ktor.server.http.content.staticResources
import io.ktor.server.http.content.staticZip
import io.ktor.server.response.header
import io.ktor.server.routing.Route
import java.io.File
import java.nio.file.Paths

// ==========================
// 🎓 LECTURE 8: SERVING CONTENTS IN KTOR
// ==========================
// 📌 Covers:
// - Serving Resources
// - Serving Files from Folder
// - Headers, Cache Control, Content Type
// - Excluding Paths
// - Serving ZIP content
// - Serving React SPA
// ==========================

fun Route.servingContents(){

// ==========================
// 🔥 1. STATIC RESOURCES
// ==========================
    staticResources("content","static"){

//  http://127.0.0.1:8080/content/index.html if i hit this url i will get the html code in body
//  in the url  http://127.0.0.1:8080/content this is the endpoint and /index.html this is the file name or content name
//  inside the static folder

//  even if we do not pass the file name like we hit  http://127.0.0.1:8080/content this url we will get same output
//  that is because in the definition of the staticRsources funtion the defalut value is index.html so if we have
//  file name index.html that will be used as a default value for that path

        /**
        OUTPUT

        URL: /content/index.html
        → returns HTML content

        URL: /content
        → automatically loads index.html
         */

//        now if in the url if we want to remove the html extention and still be reffer to that file but by doing this we get
//        page not found
//        so for that what we can do is add extentions
        extensions("html")

        /**
        OUTPUT

        URL: /content/index
        → works same as /content/index.html

        NOTE:
        - extensions("html") removes need to write .html
         */

    }


// ==========================
// 🔥 2. STATIC FILES (SERVER FILE SYSTEM)
// ==========================
    staticFiles("staticFiles", File("uploads")){

//    so if i hit  http://127.0.0.1:8080/staticFiles/sample.jpg this url i get sample.jpg as output
//    same we can do it for video files

        /**
        OUTPUT

        URL: /staticFiles/sample.jpg
        → image streamed

        URL: /staticFiles/video.mp4
        → video streamed
         */


// ==========================
// 🔥 EXCLUDE FILES
// ==========================

//    now if i want to exclude some files
//    now if i want to exclude the file which path contains 40mb.jpg file
        exclude { file ->
            file.path.contains("40mb")

//    now if I hit the endpoint  http://127.0.0.1:8080/staticFiles/40mb.jpg i get error code    403 forbidden
//    so using this exclude we can exclude certain path and files that we want to avoid streaming
        }

        /**
        OUTPUT

        URL: /staticFiles/40mb.jpg
        → 403 Forbidden
         */


// ==========================
// 🔥 CONTENT TYPE OVERRIDE
// ==========================

//    now lets suppose i want to update the content type
//    now if I hit  http://127.0.0.1:8080/staticFiles/index.txt this url i will get the index.txt content and the
//    content type will be text/plain but our content is of html but since the extention is of txt it shows content type text/plain
//    so to handle this we can do
        contentType { file ->
            when (file.name) {
                "index.txt" -> ContentType.Text.Html
                else -> null
            }

//      now if I hit  http://127.0.0.1:8080/staticFiles/index.txt this url i will get the index.txt content and the
//      content type will be text/html
        }

        /**
        OUTPUT

        BEFORE:
        Content-Type → text/plain

        AFTER:
        Content-Type → text/html
         */


// ==========================
// 🔥 CACHE CONTROL
// ==========================
        cacheControl { file ->

//                for caching specific files
//                when(file.name){
//                    "index.txt" -> listOf(CacheControl.MaxAge(10000))
//                    else -> emptyList()
//                }

//                now if we want to add cache to all the files
//                listOf(CacheControl.MaxAge(10000))

//      now if i hit  http://127.0.0.1:8080/staticFiles/index.txt this endpoing then in header there will be one more header
//      as Cache-Control               max-age=10000  and the full header will be
//                Cache-Control       max-age=10000
//                Accept-Ranges       bytes
//                Content-Length      151
//                Content-Type        text/html
//                Connection          keep-alive
//      this will make sure that browser caches the file instead of rephasing
//      now we can set some more stuff for caching like some cache directory as well

            when(file.name){
                "index.txt" -> listOf(Immutable,CacheControl.MaxAge(10000))
                else -> emptyList()
            }

//      now if i hit  http://127.0.0.1:8080/staticFiles/index.txt this endpoing then in header there will be one more header
//      as Cache-Control              immutable,max-age=10000  and the full header will be

            /**
            OUTPUT (HEADERS)

            KEY               VALUE
            ---------------------------------
            Cache-Control     immutable,max-age=10000
            Accept-Ranges     bytes
            Content-Length    151
            Content-Type      text/html
            Connection        keep-alive

            NOTE:
            - immutable → no reload even on hard refresh
             */
        }


// ==========================
// 🔥 MODIFY HEADERS
// ==========================
        modify { file, call ->
            call.response.header("FileName", file.name)

//      now if i hit  http://127.0.0.1:8080/staticFiles/index.txt this endpoing then in header there will be one more header
//      as  FileName            index.txt and the full header will be

            /**
            OUTPUT (HEADERS)

            KEY               VALUE
            ---------------------------------
            FileName          index.txt
            Cache-Control     immutable,max-age=10000
            Accept-Ranges     bytes
            Content-Length    151
            Content-Type      text/html
            Connection        keep-alive
             */
        }

    }


// ==========================
// 🔥 3. STATIC ZIP FILE
// ==========================
    staticZip("zipFiles", "sampleZip",
        zip = Paths.get("zips/ZipFiles.zip"))

    // ⚠️ IMPORTANT: Understanding basePath in staticZip
//
// If we use:
// basePath = "zipFiles"
//
// 👉 Ktor will internally look for files like:
// zipFiles/sample.jpg inside the ZIP
//
// So your ZIP structure MUST be:
// ZipFiles.zip
//   └── zipFiles/
//        └── sample.jpg
//
// ❌ If your ZIP is like:
// ZipFiles.zip
//   └── sample.jpg
//
// Then Ktor will NOT find the file because it searches:
// zipFiles/sample.jpg (which doesn't exist)
//
// 💥 Result → 404 Not Found
//
// ✅ Solution:
// Either fix ZIP structure OR remove basePath

    /**
    OUTPUT

    URL: /zipFiles/sample.jpg → image
    URL: /zipFiles/video.mp4 → video
     */


// ==========================
// 🔥 4. SINGLE PAGE APPLICATION
// ==========================
    singlePageApplication {
        react("react-app-path")

//        now if you had some react app content you can simple add the content in the server file system and you
//        can simple refer to react app path, so you can simply pass the path of your react app and using this
//        you can serve your single page application

    }

    /**
    NOTE:
    - Used for React / SPA apps
    - Automatically handles routing
     */

}


// ==========================
// 🔥 CUSTOM CACHE CONTROL
// ==========================
object Immutable : CacheControl(null){
    override fun toString(): String {
        return "immutable"
    }
}