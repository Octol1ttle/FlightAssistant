plugins {
    kotlin("jvm") version "2.2.0" apply false
    id("dev.kikugie.stonecutter")
    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false
}

stonecutter active "1.20.1-fabric" /* [SC] DO NOT EDIT */

allprojects {
    repositories {
        fun strictMaven(url: String, vararg groups: String) = exclusiveContent {
            forRepository { maven(url) }
            filter { groups.forEach(::includeGroup) }
        }

        mavenCentral()
        strictMaven("https://maven.architectury.dev/", "dev.architectury")
        strictMaven("https://maven.fabricmc.net/", "net.fabricmc")
        strictMaven("https://maven.isxander.dev/releases", "dev.isxander", "org.quiltmc.parsers")
        strictMaven("https://thedarkcolour.github.io/KotlinForForge/", "thedarkcolour")
        strictMaven("https://maven.terraformersmc.com/releases/", "com.terraformersmc")
        strictMaven("https://maven.enjarai.dev/releases/", "nl.enjarai")
        maven("https://maven.enjarai.dev/mirrors")
        maven("https://maven.neoforged.net/releases")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}