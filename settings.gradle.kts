pluginManagement {
    repositories {
        mavenLocal()
        maven("https://jitpack.io")

        gradlePluginPortal()
        mavenCentral()

        // Modstitch
        maven("https://maven.isxander.dev/releases")

        // Loom platform
        maven("https://maven.fabricmc.net/")

        // MDG platform
        maven("https://maven.neoforged.net/releases/")

        // Stonecutter
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.10"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        fun mc(version: String, vararg loaders: String) {
            for (it in loaders) version("$version-$it", version)
        }
        mc("1.20.1", "fabric", "forge")
        mc("1.21.1", "fabric", "neoforge")
        mc("1.21.6", "fabric")
        mc("1.21.9", "fabric")
    }
}

rootProject.name = "FlightAssistant"
