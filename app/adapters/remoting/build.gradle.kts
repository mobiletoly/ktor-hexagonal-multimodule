val typesafeConfigVersion: String by rootProject
val ktorVersion: String by rootProject

plugins {
    id("kotlin-library-conventions")
    kotlin("plugin.serialization")
}

dependencies {
    api(project(":app:core"))
    api(project(":app:common"))

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
    implementation("com.typesafe:config:$typesafeConfigVersion")
}
