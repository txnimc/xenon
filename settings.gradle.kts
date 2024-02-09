pluginManagement {
    repositories {
        maven("https://maven.neoforged.net")
        maven("https://repo.spongepowered.org/repository/maven-public/")
        mavenCentral()
        gradlePluginPortal()
    }
}

val minecraft_version: String by settings
rootProject.name = "xenon-${minecraft_version}"
