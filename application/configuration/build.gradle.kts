val kotlinVersion: String by rootProject
val ktorVersion: String by rootProject
val koinVersion: String by rootProject
val testcontainersVersion: String by rootProject
val kluentVersion: String by rootProject
val junitVersion: String by rootProject
val spekVersion: String by rootProject
val hikaricpVersion: String by rootProject

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

plugins {
    application
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":application:ports"))
    implementation(project(":application:adapters"))
    implementation(project(":application:core"))
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("org.koin:koin-ktor:$koinVersion")
    implementation("org.koin:koin-logger-slf4j:$koinVersion")
    implementation("com.zaxxer:HikariCP:$hikaricpVersion")

    testImplementation("io.ktor:ktor-jackson:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
}

tasks {
    // --- Run "./gradle shadowJar" to generate fat jar with all dependencies
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveVersion.set("") // to disable adding version at the end of JAR filename
        archiveBaseName.set("addrbook-hexagonal-ktor")
        archiveClassifier.set(null as String?)
    }
}
