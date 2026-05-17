plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    //serialization plugin
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)

//    adding dependencies of serialization, negotiation and server resources
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.resources)
//    status pages dependency
    implementation(libs.ktor.server.status.pages)
//    validation dependency
    implementation(libs.ktor.server.request.validation)
//    rate limit dependency
    implementation(libs.ktor.server.rate.limit)
//    head response dependency
    implementation(libs.ktor.server.auto.head.response)
//    partial content dependency
    implementation(libs.ktor.server.partial.content)
//    basic and digest authentication dependency
    implementation(libs.ktor.server.auth)
//    session auth dependency
    implementation(libs.ktor.server.sessions)
//    jwt auth dependency
    implementation(libs.ktor.server.auth.jwt)
}
