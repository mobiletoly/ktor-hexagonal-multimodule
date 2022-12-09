val koinVersion: String by rootProject
val javaxValidationVersion: String by rootProject
val hibernateValidatorVersion: String by rootProject
val logbackVersion: String by rootProject
val inlineLoggerVersion: String by rootProject
val kotestVersion: String by rootProject
val kotestKoinVersion: String by rootProject

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

    testCompileOnly("io.insert-koin:koin-test:$koinVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-property:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-koin:$kotestKoinVersion")
//    testImplementation("io.kotest:kotest-property:$kotestVersion")

    // Use JUnit Jupiter for testing.
//    testImplementation("org.junit.jupiter:junit-jupiter:5.9.1")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
