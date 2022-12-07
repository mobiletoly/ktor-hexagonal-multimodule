val typesafeConfigVersion: String by rootProject
val hikaricpVersion: String by rootProject
val exposedSqlVersion: String by rootProject
val postgresDriverVersion: String by rootProject

plugins {
    id("kotlin-library-conventions")
}

dependencies {
    api(project(":app:core"))
    implementation("org.jetbrains.exposed:exposed-core:$exposedSqlVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedSqlVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedSqlVersion")
    implementation("com.zaxxer:HikariCP:$hikaricpVersion")
    implementation("org.postgresql:postgresql:$postgresDriverVersion")
}
