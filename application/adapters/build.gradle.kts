val ktorVersion: String by rootProject
val koinVersion: String by rootProject
val hikaricpVersion: String by rootProject
val postgresDriverVersion: String by project
val exposedsqlVersion: String by project

dependencies {
    implementation(project(":application:ports"))
    implementation(project(":application:shared"))
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("io.ktor:ktor-client-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
    implementation("org.koin:koin-ktor:$koinVersion")
    implementation("org.koin:koin-logger-slf4j:$koinVersion")
    implementation("com.zaxxer:HikariCP:$hikaricpVersion")
    implementation("org.postgresql:postgresql:$postgresDriverVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedsqlVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedsqlVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedsqlVersion")
}
