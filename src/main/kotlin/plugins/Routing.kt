package com.example.plugins



import com.example.plugins.lecture.basicAuthentication
import com.example.plugins.lecture.digestAuthentication
import com.example.plugins.lecture.handlingFormData
import com.example.plugins.lecture.rateLimiting
import com.example.plugins.lecture.requestValidation
import com.example.plugins.lecture.sendingResponse
import com.example.plugins.lecture.servingContents
import com.example.plugins.lecture.statusPages
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.http.content.file
import io.ktor.server.request.receive
import io.ktor.server.request.receiveChannel
import io.ktor.server.request.receiveNullable
import io.ktor.server.request.receiveStream
import io.ktor.server.request.receiveText
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import io.ktor.utils.io.readRemaining
import io.ktor.utils.io.readText
import io.ktor.utils.io.toByteArray
import kotlinx.serialization.Serializable
import java.io.File
import java.io.FileOutputStream

fun Application.configureRouting() {

    //we can use routing with another way using install function
    install(RoutingRoot) {
//        there is two methods to use methods like get post and more
//        1.method first
//        get {  }

//        2.method second
        route(path = "/", HttpMethod.Get) {
            handle {
                call.respondText("hello dhruv")
            }
        }
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        //on calling the route / we will get output hello dhruv because if we have two routes with same
        // path route that are written above will execute

        //query and path parameters
        get("blogs/{id}") {
            //id is path parameter
            val id = call.pathParameters["id"]

//            call.respondText("Blog with id : $id")
            //http://127.0.0.1:8080/blogs/dhruv if we hit this endpoint
//            the response will be Blog with id : dhruv

//            for query parameters
            val q1 = call.queryParameters["q1"]
//            call.respondText("Blog with id : $id query is $q1")
//   http://127.0.0.1:8080/blogs/dhruv?q1=hello if we hit this endpoint
//   the response will be this Blog with id : dhruv query is hello
//   since query parameter name is q1 we wrote q1=hello in endpoint

            val q2 = call.queryParameters["q2"]
            call.respondText("Blog with id : $id query is $q1 and query 2 is $q2")
//            http://127.0.0.1:8080/blogs/dhruv?q1=hello&q2=friends this will be the end point
//            Blog with id : dhruv query is hello and query 2 is friends this will be the response
//            for multiple query we have to seperate then with ampercent (&)
        }

//        dynamic routing
        get(Regex(".+/test")) {//If you wrote something like .+/test, that looks like a path pattern (regular expression)
//            if any end point end with /test then this get request will response to that call
            call.respondText("testing dynamic routing, text api response")
//            http://127.0.0.1:8080/blogs/test for this end point
//            testing dynamic routing, text api response will be the response
//            we can also hit the endpoint http://127.0.0.1:8080/blogsfdfdf/test the response will be same
//            since it is ending with /test
        }

//        for creating 3 different version of an end point
//        api/v1/users
//        api/v2/users
//        api/v3/users
//        one option is to define three diffrent paths for these route
//        other option is to use dynamic route
        get(Regex("api/(?<apiVersion>v[1-3])/users")) {//Regex(".+api/(?<apiVersion>v[1-3])/users") can also write this to make a path pattern (regular expression)
//        by using this we are specifying that we will reference v1,v2 and v3 using the path parameter
//        apiVersion and the number can go to 1 to 3 ([1-3])
            val version = call.pathParameters["apiVersion"]
            call.respondText("Api version is $version")
        }

//        using the type safe routing
        get<Blogs> { blogs ->
            //this one is for passing the query
            val sort = blogs.sort
            call.respondText(" Sort order: $sort")
//           endpoint :-http://127.0.0.1:8080/blogs
//           response :- Sort order: new

//in the below endpoint we overwrite the default value of sort from new to all
//           endpoint :-http://127.0.0.1:8080/blogs?sort=all
//           response :- Sort order: all
        }

//        now for retrieving the path parameter
        delete<Blogs.Blog> { blog ->
            val id = blog.id
//            now if here i want the sort value here  since we are making request for Blogs.Blog
//            so the method will be different here like:-
            val sort = blog.parent.sort
            call.respondText("Blog id: $id sorting : $sort")
//            endpoint:-http://127.0.0.1:8080/blogs/4343
//            response:-Blog id: 4343 sorting : new

//in the below endpoint we overwrite the default value of sort from new to all
//            endpoint:-http://127.0.0.1:8080/blogs/34343?sort=all
//            response:-Blog id: 34343 sorting : all
        }


//        Nested routing
//          route("accounts"){
////              accounts/users/{id} creating
////              accounts/users/{id} deleting
//              route("users"){
//                  get { }
//                  get("{id}"){ }
//                  post(""){ }
//                  patch("{id}"){}
//              }
//
////              accounts/auth/login
////              accounts/auth/signup
//              route("auth"){
//                  post("login") {  }
//                  post("signup") {  }
//              }
////              we can do it here but for systematic arrangement we created a function below for handling all
////              account related routes
//          }

        accountRoutes()
//        now like this we can make function for dynamic and type safe route as well an invoke that function here
//        typeSafeRoutes()
//        dynamicRoutes()


//        Handling Request Payload Data in Ktor
//        1.Text Data
        post("greet") {
            val name = call.receiveText()
            call.respondText("Hello $name!")
//            if we hit endpoint http://0.0.0.0:8080/greet with post request with body:-raw and type Text with value
//            ktor then the output will be Hello Ktor!
        }

//        2.Data with byte read channel
        post("channel") {
            val channel = call.receiveChannel()

            val text = channel.readRemaining().readText()

            call.respondText(text)
            /**            if we hit endpoint http://0.0.0.0:8080/channel with post request with body:-raw and type Text with value
            Ktor is kotlin backend framework then the output will be Ktor is kotlin backend framework

            it is more efficient since it does not load entire data in memory, and it handles data in chunks, it becomes
            more handy when we work with large data
             */
        }
//        3.File uploading
        /**
        // ==========================
        // 🔹 FILE UPLOADING IN KTOR
        // ==========================
        // This route demonstrates different ways to upload files in Ktor:
        //
        // 1. ByteArray  → Simple but loads entire file in memory ❌
        // 2. Stream     → Better, uses InputStream ⚠️
        // 3. Channel    → Best approach (non-blocking, scalable) ✅
        // ==========================
         */

        post("upload") {

            // ==========================
            // 📁 STEP 1: CREATE FILE
            // ==========================
            // - File will be stored in "uploads/" directory
            // - mkdirs() ensures directory exists
            // - Static name → will overwrite every upload (not ideal)

            val file = File("uploads/sample2.jpg").apply {
                parentFile?.mkdirs()
            }


            /**  // ==========================
            // 🟡 METHOD 1: BYTE ARRAY
            // ==========================
            // - Reads entire file into memory
            // - Not suitable for large files

            /*
            val byteArray = call.receive<ByteArray>()
            file.writeBytes(byteArray)
            */


            // ==========================
            // 🟠 METHOD 2: STREAM
            // ==========================
            // - Uses InputStream
            // - More memory efficient than ByteArray

            /*
            val stream = call.receiveStream()
            FileOutputStream(file).use { outputStream ->
            stream.copyTo(outputStream, bufferSize = 16 * 1024)
            }
            */


            // ==========================
            // 🟢 METHOD 3: CHANNEL (BEST)
            // ==========================
            // - Non-blocking (coroutines-based)
            // - Efficient for large files
            // - Recommended in production
             */

            val channel = call.receiveChannel()

            // copyAndClose → safely copies data & closes channel
            // writeChannel() → writes data into file
            channel.copyAndClose(file.writeChannel())


            // ==========================
            // 📤 RESPONSE
            // ==========================
            call.respondText("File upload successful!")
        }

        /**
        // ==========================
        // 🔄 FLOW SUMMARY
        // ==========================
        // Client uploads file
        // → Server receives data (ByteArray / Stream / Channel)
        // → Writes file to disk
        // → Sends response


        // ==========================
        // ⚠️ IMPORTANT NOTES
        // ==========================
        // - Static file name → overwrites existing file
        // - No validation (file type, size, security)
        // - No error handling
        // - Local storage only (not scalable)

        // ✅ Production Improvements:
        // - Use dynamic file names (UUID / timestamp)
        // - Validate file type (jpg, png, etc.)
        // - Limit file size
        // - Use cloud storage (AWS S3 / Firebase)


        // ==========================
        // 🔥 INTERVIEW POINTS
        // ==========================
        // - call.receive<ByteArray>() → simple but memory heavy
        // - call.receiveStream() → blocking IO, better than ByteArray
        // - call.receiveChannel() → non-blocking, best approach

        // ⭐ Key Insight:
        // Channel-based upload is preferred in Ktor
        // for scalable and efficient file handling
         */

//        4.Json object data handling
        /**
// ==========================
// 🔹 JSON OBJECT HANDLING IN KTOR
// ==========================
// This route handles JSON data sent from the client (e.g., Postman)
// and converts it into a Kotlin object using serialization.
//
// - Client sends JSON in request body
// - Ktor automatically converts JSON → Kotlin data class (Product)
// - Server processes it and sends response back
// ==========================
*/
        post("product") {

            // ==========================
            // 📥 RECEIVE JSON DATA
            // ==========================
            // - call.receiveNullable<Product>()
            // - Converts incoming JSON → Product object
            // - Returns null if data is missing or invalid

            val product = call.receiveNullable<Product>()
                ?: return@post call.respond(HttpStatusCode.BadRequest)


            // ==========================
            // 📤 SEND RESPONSE
            // ==========================
            // - Sends the same object back as JSON
            // - Ktor automatically converts object → JSON

            call.respond(product)
        }

        /**
        // ==========================
        // 🔄 FLOW SUMMARY
        // ==========================
        // Client (Postman) sends JSON
        // → Ktor converts JSON → Product object
        // → Server processes data
        // → Sends JSON response back


        // ==========================
        // ⚠️ IMPORTANT NOTES
        // ==========================
        // - Requires ContentNegotiation plugin (JSON serialization)
        // - JSON keys must match data class properties
        // - Missing/invalid data → returns BadRequest
        // - Uses Kotlin Serialization (@Serializable)


        // ==========================
        // 🔥 INTERVIEW POINTS
        // ==========================
        // - call.receive<T>() → receives request body as object
        // - call.receiveNullable<T>() → safe version (avoids crash)
        // - call.respond() → automatically serializes object to JSON
        // - @Serializable → required for JSON conversion
         */

//       HandlingFormData
        handlingFormData()

//        Status Pages
        statusPages()

//        Request validation
        requestValidation()

//        Rate limiting
        rateLimiting()

//        Sending response
        sendingResponse()

//      Serving Content
        servingContents()

//      Basic Authentication
        basicAuthentication()

//      Digest Authentication
        digestAuthentication()
    }


}
//        type safe routing
//        for doing type safe routing we need to add serialization and resources plugin
@Resource("blogs")
class Blogs(val sort : String? = "new"){

    @Resource("{id}")
    data class Blog(val parent : Blogs = Blogs(), val id : String)
/**    here we are specifying that there is a path blogs with query parameter sort by default its value will
//    be new and we take path parameter id :- blogs/{id}?sort=new

//    the reason to take parent Blogs as parameter of data class Blog is that without it we wont be
//    able to link this id with the earlier path blogs, we can give any name to parent variable ,
//    in short we need a default instance of the  parent class we are   referring in the path
*/
}

//        Nested routing
// we have to create the function with Route. since it has the context for the route and we are declaring routes in this
//function
fun Route.accountRoutes(){
//    here we put all account related route
    route("accounts"){
//              accounts/users/{id} creating
//              accounts/users/{id} deleting
        route("users"){
            get { }
            get("{id}"){ }
            post(""){ }
            patch("{id}"){}
        }

//              accounts/auth/login
//              accounts/auth/signup
        route("auth"){
            post("login") {  }
            post("signup") {  }
        }

    }
}

fun Route.dynamicRoutes(){

//        dynamic routing
    get(Regex(".+/test")) {//If you wrote something like .+/test, that looks like a path pattern (regular expression)
//            if any end point end with /test then this get request will response to that call
        call.respondText("testing dynamic routing, text api response")
//            http://127.0.0.1:8080/blogs/test for this end point
//            testing dynamic routing, text api response will be the response
//            we can also hit the endpoint http://127.0.0.1:8080/blogsfdfdf/test the response will be same
//            since it is ending with /test
    }
    //        for creating 3 different version of an end point
//        api/v1/users
//        api/v2/users
//        api/v3/users
//        one option is to define three diffrent paths for these route
//        other option is to use dynamic route
    get(Regex("api/(?<apiVersion>v[1-3])/users")){//Regex(".+api/(?<apiVersion>v[1-3])/users") can also write this to make a path pattern (regular expression)
//        by using this we are specifying that we will reference v1,v2 and v3 using the path parameter
//        apiVersion and the number can go to 1 to 3 ([1-3])
        val version = call.pathParameters["apiVersion"]
        call.respondText("Api version is $version")
    }
}

fun Route.typeSafeRoutes(){


//        using the type safe routing
    get<Blogs>{ blogs ->
        //this one is for passing the query
        val sort = blogs.sort
        call.respondText(" Sort order: $sort")
//           endpoint :-http://127.0.0.1:8080/blogs
//           response :- Sort order: new

//in the below endpoint we overwrite the default value of sort from new to all
//           endpoint :-http://127.0.0.1:8080/blogs?sort=all
//           response :- Sort order: all
    }

//        now for retrieving the path parameter
    delete<Blogs.Blog> {blog->
        val id = blog.id
//            now if here i want the sort value here  since we are making request for Blogs.Blog
//            so the method will be different here like:-
        val sort = blog.parent.sort
        call.respondText("Blog id: $id sorting : $sort")
//            endpoint:-http://127.0.0.1:8080/blogs/4343
//            response:-Blog id: 4343 sorting : new

//in the below endpoint we overwrite the default value of sort from new to all
//            endpoint:-http://127.0.0.1:8080/blogs/34343?sort=all
//            response:-Blog id: 34343 sorting : all
    }
}

//        4.Json object data handling
/**
 * ==========================
 * 🔹 PRODUCT DATA CLASS
 * ==========================
 * Represents the structure of JSON data exchanged between
 * client and server.
 *
 * Example JSON (from Postman):
 * {
 *   "name": "Orange",
 *   "category": "Fruits",
 *   "price": 100
 * }
 *
 * - @Serializable → enables JSON conversion
 * - Property names must match JSON keys
 *
 * Used in:
 * - Receiving request body (JSON → Object)
 * - Sending response (Object → JSON)
 */
@Serializable
data class Product(
    val name : String,
    val category : String,
    val price : Int
)