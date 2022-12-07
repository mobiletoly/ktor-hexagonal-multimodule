val koinVersion: String by rootProject
val javaxValidationVersion: String by rootProject
val hibernateValidatorVersion: String by rootProject
val logbackVersion: String by rootProject
val inlineLoggerVersion: String by rootProject

plugins {
    id("org.jetbrains.kotlin.jvm")
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

    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
