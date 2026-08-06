plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("dev.isxander.modstitch.base") version "0.8.5"
    id("fabric-loom") version "1.15.5" apply false
    id("me.modmuss50.mod-publish-plugin")
    id("me.fallenbreath.yamlang") version "1.5.0"
}

fun prop(name: String) : String {
    return findProperty(name)?.toString() ?: throw IllegalArgumentException("Missing property: $name")
}

fun ifFindProperty(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)
        ?.let(consumer)
}

// Variables
class ModData {
    val id = prop("mod.id")
    val name = prop("mod.name")
    val version = prop("mod.version")
    val group = prop("mod.group")
}
val mod = ModData()
val minecraft = stonecutter.current.project.substringBeforeLast('-')
val minecraftVersionRange = prop("mod.mc_version_range")

// See https://stonecutter.kikugie.dev/stonecutter/guide/comments#condition-constants
val loader: String = name.split("-")[1]

// Minecraft 26.x (unobfuscated) requires Java 25, 1.20.6+ requires Java 21, older requires Java 17
val javaVersionTarget: Int = when {
    stonecutter.eval(minecraft, ">=26") -> 25
    stonecutter.eval(minecraft, ">=1.20.6") -> 21
    else -> 17
}
stonecutter {
    constants {
        match(loader, "fabric", "neoforge", "forge")
        put("do-a-barrel-roll", hasProperty("deps.dabr"))
    }
}

base { archivesName.set("${mod.id}-$loader") }
group = mod.group
version = "${mod.version}+mc$minecraft"

tasks.withType<Jar> {
    from(rootDir.resolve("LICENSE"))
}

modstitch {
    minecraftVersion = minecraft

    javaVersion = javaVersionTarget

    java {
        withSourcesJar()
    }

    kotlin {
        jvmToolchain(javaVersionTarget)
    }

    // If parchment doesnt exist for a version yet you can safely
    // omit the "deps.parchment" property from your versioned gradle.properties
    parchment {
        ifFindProperty("deps.parchment") { mappingsVersion = it }
    }

    metadata {
        modId = mod.id
        modName = mod.name
        modVersion = mod.version

        fun <K : Any, V : Any> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        val refmapString = ",\"refmap\": \"${mod.id}.refmap.json\""
        replacementProperties.populate {
            // You can put any other replacement properties/metadata here that
            // modstitch doesn't initially support. Some examples below.
            put("mc", minecraftVersionRange)
            put("fml", if (loader == "neoforge") "1" else "45")
            put("mnd", if (loader == "neoforge") "type = \"required\"" else "mandatory = true")
            put("refmap", if (loader == "forge") refmapString else "")
            put("flmin", if (loader == "fabric") ">=${prop("deps.fabric_loader")}" else "")
            put("javamin", ">=$javaVersionTarget")
        }

        overwriteProjectVersionAndGroup = false
    }

    // Fabric Loom (Fabric)
    loom {
        fabricLoaderVersion = prop("deps.fabric_loader")

        // Configure loom like normal in this block.
        configureLoom {
            @Suppress("UnstableApiUsage")
            mixin {
                useLegacyMixinAp = false
            }

            runConfigs.all {
                ideConfigGenerated(environment == "client")
                runDir("../../run")
            }
        }
    }

    // ModDevGradle (NeoForge, Forge, Forgelike)
    moddevgradle {
        ifFindProperty("deps.forge") { forgeVersion = it }
        ifFindProperty("deps.neoforge") { neoForgeVersion = it }

        // Configures client and server runs for MDG, it is not done by default
        defaultRuns(server = false)

        // This block configures the `neoforge` extension that MDG exposes by default,
        // you can configure MDG like normal from here
        configureNeoForge {
            runs.all {
                gameDirectory = layout.projectDirectory.dir("../../run")
            }
        }
    }

    mixin {
        // You do not need to specify mixins in any mods.json/toml file if this is set to
        // true, it will automatically be generated.
        addMixinsToModManifest = true

        configs.register(mod.id) { side = CLIENT }

        // Most of the time you wont ever need loader specific mixins.
        // If you do, simply make the mixin file and add it like so for the respective loader:
        // if (isLoom) configs.register("examplemod-fabric")
        // if (isModDevGradleRegular) configs.register("examplemod-neoforge")
        // if (isModDevGradleLegacy) configs.register("examplemod-forge")
    }
}

// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
    modstitchImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    modstitch.loom {
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fapi")}")
        modstitchModImplementation("net.fabricmc:fabric-language-kotlin:${property("deps.flk")}+${property("deps.flk_kotlin")}")
        modstitchModImplementation("com.terraformersmc:modmenu:${property("deps.modmenu")}")
    }

    modstitch.moddevgradle {
        modstitchModImplementation("thedarkcolour:kotlinforforge${if (modstitch.isModDevGradleRegular) "-neoforge" else ""}:${property("deps.kff")}")
    }

    // Anything else in the dependencies block will be used for all platforms.
    modstitchModImplementation("dev.architectury:architectury-${loader}:${property("deps.arch_api")}")
    modstitchModImplementation("dev.isxander:yet-another-config-lib:${property("deps.yacl")}")

    ifFindProperty("deps.dabr") {
        val legacyPermsApi: Boolean = loader == "fabric" && stonecutter.eval(minecraft, "<1.21.6")
        modstitchModImplementation("nl.enjarai:do-a-barrel-roll:$it") {
            if (legacyPermsApi) exclude("me.lucko")
        }
        if (legacyPermsApi) {
            modstitchModImplementation("com.github.Octol1ttle:fabric-permissions-api:v0.2")
        }
    }
}

yamlang {
    targetSourceSets.set(mutableListOf(sourceSets["main"]))
    inputDir.set("assets/${mod.id}/lang")
}

// Modstitch's source configuration overrides stonecutter's automatic wiring of
// the preprocessed (chiseled) sources into the compile source set, so we wire
// them up explicitly here. The generated (preprocessed) sources fully replace
// the shared raw sources under src/main.
sourceSets["main"].apply {
    // Kotlin srcDirs must also include the generated Java dir so Kotlin/Java
    // joint compilation sees the Java classes (e.g. mixin invokers, interfaces).
    kotlin.setSrcDirs(listOf(
        layout.buildDirectory.dir("generated/stonecutter/main/kotlin"),
        layout.buildDirectory.dir("generated/stonecutter/main/java"),
    ))
    java.setSrcDirs(listOf(layout.buildDirectory.dir("generated/stonecutter/main/java")))
}
tasks.matching { it.name == "compileKotlin" || it.name == "compileJava" || it.name == "sourcesJar" || it.name == "processResources" }.configureEach {
    dependsOn(tasks.named("stonecutterGenerate"))
}

// Publishing
publishMods {
    val modrinthToken = findProperty("modrinthToken")
    val curseforgeToken = findProperty("curseforgeToken")
    dryRun = modrinthToken == null || curseforgeToken == null

    modstitch.onEnable {
        file = modstitch.finalJarTask.flatMap { it.archiveFile }
    }
    additionalFiles.from(tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile })

    displayName = "${mod.name} ${mod.version} for ${loader.replaceFirstChar { it.uppercase() }} ${property("mod.mc_title")}"
    version = "${mod.version}+mc$minecraft-$loader"
    changelog = rootProject.file("CHANGELOG.md").readText()
    type = STABLE
    modLoaders.add(loader)
    if (loader == "fabric") {
        modLoaders.add("quilt")
    }

    val targets = property("mod.mc_targets").toString().split(' ')
    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = modrinthToken.toString()
        targets.forEach(minecraftVersions::add)
        if (loader == "fabric") {
            requires("fabric-language-kotlin")
            optional("modmenu")
        } else {
            requires("kotlin-for-forge")
        }
        requires("architectury-api")
        requires("yacl")
    }

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = curseforgeToken.toString()
        targets.forEach(minecraftVersions::add)
        if (loader == "fabric") {
            requires("fabric-language-kotlin")
            optional("modmenu")
        } else {
            requires("kotlin-for-forge")
        }
        requires("architectury-api")
        requires("yacl")
    }
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(modstitch.finalJarTask.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    dependsOn("build")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }

    rootProject.tasks.register("runActive") {
        group = "project"
        dependsOn(tasks.named("runClient"))
    }
}