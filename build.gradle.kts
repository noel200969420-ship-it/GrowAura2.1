plugins {
    java
    id("fabric-loom") version "1.0-SNAPSHOT"
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://maven.modrinth.com/")
}

val minecraftVersion = "1.21.9"
val yarnMappings = "1.21.9+build.1"
val loaderVersion = "0.16.11"
val fabricApiVersion = "0.134.0+1.21.9"

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$yarnMappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    modImplementation("net.fabricmc.fabric-api:fabric-command-api-v2:$fabricApiVersion")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}
