plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.compose") version "1.7.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" // <- wichtig
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.3")
}

compose.desktop {
    application {
        mainClass = "com.planner.app.MainKt"
    }
}

kotlin {
    // Du hast JDK 21 – nutzen:
    jvmToolchain(21)
}
