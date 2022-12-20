plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.7.20"
    `maven-publish`
    id("net.linguica.maven-settings") version "0.5"
    id("net.thauvin.erik.gradle.semver") version "1.0.4"
}

repositories {
    mavenLocal()
    gradlePluginPortal()
}

// target Java 8 for maximum compatibility
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    // this is so they work for downstream
    implementation("com.google.protobuf:protobuf-gradle-plugin:0.9.1")
    implementation("net.linguica.gradle:maven-settings-plugin:0.5")
}

group = "crackers.buildstuff"

/**
 * Publish the plugin JAR (no sources).
 */
tasks.create("pluginPublish") {
    val isDefaultBranch = System.getenv("DEFAULT_BRANCH") == "true"

    publishing {
        if (isDefaultBranch) {
            repositories {
                maven {
                    name = "some name"
                    url = uri("set publish URL")
                }
                mavenSettings {
                    userSettingsFileName = System.getenv("HOME") + "/.m2/settings.xml"
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
