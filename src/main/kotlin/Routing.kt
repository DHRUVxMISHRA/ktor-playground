package com.example


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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
//            we can also hit the endpont http://127.0.0.1:8080/blogsfdfdf/test the response will be same
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

//        type safe routing
//        for doing type safe routing we need to add serialization and resources plugin
    }

}
