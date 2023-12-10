val ktorVersion: String by rootProject
val koinVersion: String by rootProject
val swaggerAnnotationsVersion: String by rootProject
val kotestKtorVersion: String by rootProject

plugins {
    id("kotlin-library-conventions")
    kotlin("plugin.serialization")
    id("org.openapi.generator")
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

    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("io.kotest.extensions:kotest-assertions-ktor:$kotestKtorVersion")
    testImplementation("io.ktor:ktor-server-netty:$ktorVersion")
}

openApiGenerate {
    inputSpec.set("$projectDir/src/main/resources/openapi/addrbook.yaml")
    outputDir.set("$projectDir")
    generatorName.set("kotlin")
    globalProperties.put("models", "")
    generateApiDocumentation.set(false)
    generateModelDocumentation.set(false)
    additionalProperties.put("serializationLibrary", "kotlinx_serialization")
    packageName.set("adapters.primaryweb.gen")
}
