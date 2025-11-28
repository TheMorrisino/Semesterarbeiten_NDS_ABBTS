pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("org.jetbrains.compose") version "1.7.0"
        id("org.jetbrains.kotlin.jvm") version "2.0.0"
        id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" // <- wichtig
    }
}
rootProject.name = "planner-desktop-mvp"
