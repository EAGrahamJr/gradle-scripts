import com.google.protobuf.gradle.id
import org.gradle.internal.impldep.junit.runner.Version.id

repositories {
    mavenLocal()
    mavenCentral()
}

plugins {
    `java-library`
    `maven-publish`
    idea
    id("com.google.protobuf")
}

// settable Java version - defaults to 17
java {
    JavaVersion.toVersion(project.findProperty("protobuf.javaVersion")?.toString() ?: "17").run {
        sourceCompatibility = this
        targetCompatibility = this
    }
}

dependencies {
    implementation("io.grpc:grpc-protobuf:${grpcVersion}")
    implementation("com.google.protobuf:protobuf-kotlin:${protobufVersion}")
    implementation("io.grpc:grpc-stub:${grpcVersion}")
    implementation("io.grpc:grpc-kotlin-stub:${grpcKotlinVersion}")

    // just in case
    implementation("javax.annotation:javax.annotation-api:1.3.2")
}

val protoFiles = "$projectDir/src/main/proto"
val outputDestination = "$projectDir/build/protoDist"

/**
 * Make the generated classes "visible" to IDEA
 */
idea {
    module {
        sourceDirs + file("./build/generated/source/proto/main/grpckotlin")
        sourceDirs + file("./build/generated/source/proto/main/grpc")
    }
}

/**
 * Compile the protobuf: this happens as part of the normal build cycle, so no custom task is necessary.
 */
protobuf {
    protoc {
        // The artifact spec for the Protobuf Compiler
        artifact = "com.google.protobuf:protoc:${protobufVersion}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${grpcKotlinVersion}:jdk8@jar"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

/**
 * Sets up a ZIP file containing only the protobuf file(s) for publishing.
 */
tasks.register("packageDistributionZip", Zip::class.java) {
    getDestinationDirectory().set(file(outputDestination))
    from(protoFiles)
}

/**
 * Publish! Destination depends on the environment variable.
 */
tasks.create("protoDistribution") {
    val publishToRepo = System.getenv("PUBLISH_PROTO") == "true"

    publishing {
        publications {
            create<MavenPublication>("library") {
                from(components["java"])
                artifact(tasks.findByName("packageDistributionZip"))
                artifactId = project.name
            }
        }
        if (publishToRepo) {
            val repoName = project.findProperty("protobuf.publish.repoName")?.toString() ?: throw GradleException("Property 'protobuf.publish.repoName' was not set")
            val repoUrl = project.findProperty("protobuf.publish.repoUrl")?.toString() ?: throw GradleException("Property 'protobuf.publish.repoUrl' was not set")

            repositories {
                maven {
                    name = repoName
                    url = uri(repoUrl)
                    credentials {
                        username = System.getenv("MAVEN_ACTOR") ?: throw GradleException("Environment variable 'MAVEN_ACTOR' was not set")
                        password = System.getenv("MAVEN_TOKEN") ?: throw GradleException("Environment variable 'MAVEN_TOKEN' was not set")
                    }
                }
            }
        }
    }
    dependsOn("packageDistributionZip")
    if (publishToRepo)
        dependsOn("publish")
    else
        dependsOn("publishToMavenLocal")
}

task("buildProto") {
    dependsOn("clean", "protoDistribution")
}
