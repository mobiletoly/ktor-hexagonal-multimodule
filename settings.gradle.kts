pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://kotlin.bintray.com/kotlin-eap")
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://dl.bintray.com/kotlin/ktor")
        maven(url = "https://dl.bintray.com/kotlin/exposed")
        maven(url = "https://jitpack.io")
    }
    plugins {
        kotlin("jvm") version "1.3.72"
        id("org.jlleitschuh.gradle.ktlint") version "9.2.1"
        id("com.github.johnrengelman.shadow") version "5.2.0"
    }
}

rootProject.name = "addrbook-hexagonal-ktor"
include("application:configuration")
include("application:adapters")
include("application:core")
include("application:ports")
include("application:shared")
