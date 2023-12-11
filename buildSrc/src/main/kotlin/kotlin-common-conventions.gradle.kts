val koinVersion: String by rootProject
val jakartaValidationVersion: String by rootProject
val hibernateValidatorVersion: String by rootProject
val logbackVersion: String by rootProject
val inlineLoggerVersion: String by rootProject
val kotestVersion: String by rootProject
val kotestKoinVersion: String by rootProject
val kotestKtorVersion: String by rootProject

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jetbrains.kotlinx.kover")
}

repositories {
    mavenCentral()
}

dependencies {
    constraints {
    }

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:$inlineLoggerVersion")
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    api("jakarta.validation:jakarta.validation-api:$jakartaValidationVersion")
    implementation("org.hibernate:hibernate-validator:$hibernateValidatorVersion")

    testCompileOnly("io.insert-koin:koin-test:$koinVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-assertions-ktor:$kotestKtorVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-koin:$kotestKoinVersion")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    verbose.set(true)
    outputToConsole.set(true)
    filter {
        exclude("**/gen/**")
    }
}

koverMerged {
    // true to disable instrumentation and all Kover tasks in this project
//    enable()
    dependencies {
        kover(project(":app:core"))
        kover(project(":app:infra"))
        kover(project(":app:adapters:env"))
        kover(project(":app:adapters:persist"))
        kover(project(":app:adapters:primary-web"))
        kover(project(":app:adapters:remoting"))
    }
}
