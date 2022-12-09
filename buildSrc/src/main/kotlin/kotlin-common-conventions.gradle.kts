val koinVersion: String by rootProject
val javaxValidationVersion: String by rootProject
val hibernateValidatorVersion: String by rootProject
val logbackVersion: String by rootProject
val inlineLoggerVersion: String by rootProject
val kotestVersion: String by rootProject
val kotestKoinVersion: String by rootProject

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jlleitschuh.gradle.ktlint-idea")
}

repositories {
    mavenCentral()
}

dependencies {
    constraints {
        // Define dependency versions as constraints
        implementation("org.apache.commons:commons-text:1.9")
    }

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:$inlineLoggerVersion")
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("io.insert-koin:koin-logger-slf4j:$koinVersion")
    api("javax.validation:validation-api:$javaxValidationVersion")
    implementation("org.hibernate:hibernate-validator:$hibernateValidatorVersion")

    testCompileOnly("io.insert-koin:koin-test:$koinVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-koin:$kotestKoinVersion")
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    // I don't like when linter complains that ${i} must be replaced to $i in string templates.
    // I personally think that template "email${i}@example.com" is easier to read than "email$i@example.com"
    this.disabledRules.add("string-template")

    verbose.set(true)
    outputToConsole.set(true)
    filter {
        exclude("**/gen/**")
    }
}
