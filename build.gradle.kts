val kotlinLanguageVersion: String by project
val koinVersion: String by project
val hikaricpVersion: String by project
val inlineLoggerVersion: String by project
val logbackVersion: String by project
val spekVersion: String by project
val junitJupiterVersion: String by project
val ktlintVersion: String by project

plugins {
    base
    jacoco
    id("org.jetbrains.kotlin.jvm")
    id("org.jlleitschuh.gradle.ktlint")
}

apply {
    from("./jacoco.gradle.kts")
}

allprojects {
    // TODO you must change this group to your own
    group = "com.github.mobiletoly.addrbookhexktor"
    version = "1.0-SNAPSHOT"

    repositories {
        jcenter()
        mavenLocal()
        maven(url = "https://plugins.gradle.org/m2/")
        maven(url = "https://jitpack.io")
    }

    apply(plugin = "kotlin")
    apply(plugin = "jacoco")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    tasks.withType<Test> {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:$inlineLoggerVersion")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
        implementation("io.insert-koin:koin-core:$koinVersion")

        testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
        testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
        testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
//        testImplementation(gradleTestKit())
    }

    ktlint {
        version.set(ktlintVersion)
        outputToConsole.set(true)
        enableExperimentalRules.set(true)
        disabledRules.set(setOf("import-ordering"))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
        freeCompilerArgs = listOfNotNull(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xinline-classes",
            "-Xallow-result-return-type"
        )
    }
}
