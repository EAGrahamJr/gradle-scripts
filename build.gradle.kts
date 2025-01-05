plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.7.21"
    `maven-publish`
    id("net.thauvin.erik.gradle.semver") version "1.0.4"
}

repositories {
    mavenLocal()
    gradlePluginPortal()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    // this is so they work for downstream
    implementation("com.google.protobuf:protobuf-gradle-plugin:0.9.4")
}

group = "crackers.buildstuff"

gradlePlugin {
    // Define the plugin
    plugins {
        create("libraryPublish") {
            id = "crackers.buildstuff.crackers-gradle-plugins"
            implementationClass = "LibraryPublishPlugin"
            version = project.provider {
                project.file("version.properties")
                    .readLines()
                    .findLast { it.startsWith("version.semver") }!!
                    .split("=")[1]
            }.get()
        }
    }
}

/**
 * Publish the plugin JAR (no sources).
 */
tasks.create("pluginPublish") {
    val isDefaultBranch = System.getenv("PUBLISH_LIB") == "true"

    publishing {
        if (isDefaultBranch) {
            repositories {
                maven {
                    name = "some name"
                    url = uri("set publish URL")
                }
            }
        }
    }
    if (isDefaultBranch)
        dependsOn("incrementPatch", "build", "publish")
    else
        dependsOn("build", "publishToMavenLocal")
}

defaultTasks("clean", "pluginPublish")
