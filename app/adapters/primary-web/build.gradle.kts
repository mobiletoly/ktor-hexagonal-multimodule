val ktorVersion: String by rootProject
val koinVersion: String by rootProject
val jacksonVersion: String by rootProject
val swaggerAnnotationsVersion: String by rootProject

plugins {
    id("com.github.mobiletoly.addrbookhexktor.kotlin-library-conventions")
    kotlin("plugin.serialization") version "1.7.21"
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-id-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.swagger.core.v3:swagger-annotations:$swaggerAnnotationsVersion")
}
