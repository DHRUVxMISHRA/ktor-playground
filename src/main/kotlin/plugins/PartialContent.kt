package com.example.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.partialcontent.PartialContent

/*
⚙️ REQUIRED SETUP

1️⃣ libs.versions.toml:

ktor-server-partial-content = { module = "io.ktor:ktor-server-partial-content", version.ref = "ktor" }
ktor-server-auto-head-response = { module = "io.ktor:ktor-server-auto-head-response", version.ref = "ktor" }

2️⃣ build.gradle.kts:

implementation(libs.ktor.server.auto.head.response)
implementation(libs.ktor.server.partial.content)

3️⃣ Plugin files:

AutoHeadResponse:
fun Application.configureAutoHeadResponse(){
    install(AutoHeadResponse)
}

PartialContent:
fun Application.configurePartialContent(){
    install(PartialContent)
}

4️⃣ Application.module():

configurePartialContent()
configureAutoHeadResponse()

📌 ORDER:
after routing is fine (no strict restriction)
*/
fun Application.configurePartialContent(){
    install(PartialContent)
}