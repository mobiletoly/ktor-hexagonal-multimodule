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
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:11.0.0")
    implementation("org.jetbrains.kotlinx:kover:0.6.1")
    implementation("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
}
