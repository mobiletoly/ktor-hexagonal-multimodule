plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")
    implementation("org.jetbrains.kotlin:kotlin-serialization:1.9.21")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:12.0.2")
    implementation("org.jetbrains.kotlinx:kover-gradle-plugin:0.7.5")
    implementation("com.github.johnrengelman:shadow:8.1.1")
    implementation("org.openapitools:openapi-generator-gradle-plugin:7.1.0")
}
