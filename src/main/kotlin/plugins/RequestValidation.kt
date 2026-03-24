package com.example.plugins

import com.example.plugins.lecture.ProductDetail
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult

// ==========================
// 🔹 GLOBAL REQUEST VALIDATION
// ==========================
// Applied to ALL routes unless scoped
// ==========================

fun Application.configureRequestValidation(){

    install(RequestValidation){

        // ==========================
        // 🔥 GLOBAL STRING VALIDATION (COMMENTED)
        // ==========================

        /*
        validate<String>{ body ->

            if(body.isBlank())
                ValidationResult.Invalid("Message cannot be empty")

            else if(!body.startsWith("Hello"))
                ValidationResult.Invalid("Invalid Message")

            else ValidationResult.Valid
        }
        */

        /*
        🧠 WHY WE COMMENTED THIS?

        👉 Because we are using ROUTE-SCOPED validation

        ❗ VERY IMPORTANT BEHAVIOR:

        - Ktor does NOT override validation
        - It MERGES them

        👉 That means:

        Global + Route validation BOTH run

        Example:

        Global → startsWith("Hello")
        Route → startsWith("Hi")

        Input: "Hello bro"

        Global → PASS ✅
        Route → FAIL ❌

        👉 FINAL RESULT:
        ❌ Request fails

        📌 RULE:
        ALL validations must pass

        👉 So to avoid conflict:
        we disable (comment) global String validation
        */


        // ==========================
        // 🔥 JSON VALIDATION (ProductDetail)
        // ==========================
        validate<ProductDetail>{ body ->

            if(body.name.isNullOrBlank())
                ValidationResult.Invalid("Invalid product name")

            else if(body.category.isNullOrBlank())
                ValidationResult.Invalid("Invalid category name")

            else if(body.price == null || body.price <= 0)
                ValidationResult.Invalid("Invalid price ")

            else ValidationResult.Valid
        }

        /*
        🔄 FLOW:

        Client → sends JSON
        → converted to ProductDetail
        → validation runs
        → if valid → route executes
        → if invalid → 400 error

        📌 RESPONSE FORMAT:
        {
          "errors": ["Invalid product name"]
        }
        */
    }
}