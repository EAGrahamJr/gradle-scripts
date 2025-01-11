plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.0.0"
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
            id = "crackers.buildstuff.library-publish"
            implementationClass = "LibraryPublishPlugin"
            project.afterEvaluate {
                version = project.version
            }
        }
        create("protobufGen") {
            id = "crackers.buildstuff.generate-protobuf"
            implementationClass = "GenerateProtobufPlugin"
            project.afterEvaluate {
                version = project.version
            }
        }
    }
}

fun isDefaultBranch() = System.getenv("PUBLISH_LIB") == "true"

publishing {
    repositories {
        // "protected" by the environment variable
        if (isDefaultBranch()) {
            maven {
                name = "some_name"
                url = uri("http://localhost")
            }
        }
    }
}

/**
 * Publish the plugin JAR (no sources).
 */
tasks.create("pluginPublish") {
    if (isDefaultBranch())
        dependsOn("incrementPatch", "build", "publish")
    else
        dependsOn("build", "publishToMavenLocal")
}

defaultTasks("clean", "pluginPublish")
