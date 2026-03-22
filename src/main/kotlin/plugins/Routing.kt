package com.example.plugins


import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get

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


//        Nested routing

    }

}
//        type safe routing
//        for doing type safe routing we need to add serialization and resources plugin
@Resource("blogs")
class Blogs(val sort : String? = "new"){

    @Resource("{id}")
    data class Blog(val parent : Blogs = Blogs(), val id : String)
//    here we are specifying that there is a path blogs with query parameter sort by default its value will
//    be new and we take path parameter id :- blogs/{id}?sort=new

//    the reason to take parent Blogs as parameter of data class Blog is that without it we wont be
//    able to link this id with the earlier path blogs, we can give any name to parent variable ,
//    in short we need a default instance of the  parent class we are   referring in the path
}
