val typesafeConfigVersion: String by rootProject

plugins {
    id("com.github.mobiletoly.addrbookhexktor.kotlin-library-conventions")
}

dependencies {
    api(project(":app:core"))

    implementation("com.typesafe:config:$typesafeConfigVersion")
}
