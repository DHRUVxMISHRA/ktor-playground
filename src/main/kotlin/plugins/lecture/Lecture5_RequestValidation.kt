package com.example.plugins.lecture

import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

// ==========================
// 🎓 LECTURE 5: REQUEST VALIDATION
// ==========================
// Covers:
// 1. Validation for String
// 2. Validation for JSON
// 3. Route scoped validation
// 4. Validation flow (VERY IMPORTANT)
// ==========================

fun Route.requestValidation(){

    // ==========================
    // 🔹 1. SIMPLE STRING REQUEST (NO VALIDATION)
    // ==========================
    post("message"){

        val message = call.receive<String>()

        call.respondText(message)
    }

    /*
    🔄 FLOW:
    Client → sends raw text
    → receive<String>()
    → directly returned

    ❗ No validation applied here
    */


    // ==========================
    // 🔹 2. JSON REQUEST (GLOBAL VALIDATION APPLIES)
    // ==========================
    post("product5"){

        val product = call.receive<ProductDetail>()

        call.respond(product)
    }

    /*
    🔄 FLOW:
    Client → sends JSON
    → converted to ProductDetail
    → global validation runs
    → if valid → response
    → if invalid → 400 error

    📌 EXAMPLES (POSTMAN):

    ❌ Invalid name:
    {
      "errors": ["Invalid product name"]
    }

    ❌ Invalid category:
    {
      "errors": ["Invalid category name"]
    }

    ❌ Invalid price:
    {
      "errors": ["Invalid price "]
    }

    ✅ Valid:
    {
      "name": "mango",
      "price": 100,
      "category": "fruit"
    }
    */


    // ==========================
    // 🔥 3. ROUTE SCOPED VALIDATION (message1)
    // ==========================
    route("message1"){

        install(RequestValidation){

            validate<String>{ body ->

                if(body.isBlank())
                    ValidationResult.Invalid("Message cannot be empty")

                else if(!body.startsWith("Hello"))
                    ValidationResult.Invalid("Invalid Message")

                else ValidationResult.Valid
            }
        }

        post{
            val message = call.receive<String>()
            call.respondText(message)
        }
    }

    /*
    📌 RULES:
    - Must not be empty
    - Must start with "Hello"

    ❌ Example:
    "" → Message cannot be empty
    "Hi bro" → Invalid Message

    ✅ Example:
    "Hello bro"
    */


    // ==========================
    // 🔥 4. ROUTE SCOPED VALIDATION (message2)
    // ==========================
    route("message2"){

        install(RequestValidation){

            validate<String>{ body ->

                if(body.isBlank())
                    ValidationResult.Invalid("Message cannot be empty")

                else if(!body.startsWith("Hi"))
                    ValidationResult.Invalid("Invalid Message")

                else ValidationResult.Valid
            }
        }

        post{
            val message = call.receive<String>()
            call.respondText(message)
        }
    }

    /*
    📌 DIFFERENCE FROM message1:
    message1 → "Hello"
    message2 → "Hi"

    ❗ WHY ROUTE SCOPING?
    - Same data type (String)
    - Different validation rules
    → avoids conflict
    */


}

/*
🧠 VERY IMPORTANT FLOW (CORE CONCEPT)

Client → sends request
→ call.receive<T>()
→ RequestValidation intercepts
→ validation runs BEFORE route
→ if VALID → route executes
→ if INVALID → 400 response returned

❗ Route will NOT execute if validation fails
*/


@Serializable
data class ProductDetail(
    val name : String?,
    val price : Int?,
    val category : String?
)

/*
📌 NOTE:
- All fields are nullable
- So validation is REQUIRED
*/