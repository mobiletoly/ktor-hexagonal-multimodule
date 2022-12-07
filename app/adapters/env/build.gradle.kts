val typesafeConfigVersion: String by rootProject

plugins {
    id("kotlin-library-conventions")
}

dependencies {
    api(project(":app:core"))

    implementation("com.typesafe:config:$typesafeConfigVersion")
}
