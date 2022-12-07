val ktorVersion: String by rootProject
val koinVersion: String by rootProject
val jacksonVersion: String by rootProject
val swaggerAnnotationsVersion: String by rootProject

plugins {
    id("kotlin-library-conventions")
    kotlin("plugin.serialization") version "1.7.20"
}

dependencies {
    api(project(":app:core"))
    api(project(":app:common"))

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-call-id-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.insert-koin:koin-ktor:$koinVersion")
    implementation("io.swagger.core.v3:swagger-annotations:$swaggerAnnotationsVersion")
}
