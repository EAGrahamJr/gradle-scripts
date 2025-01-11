import com.google.protobuf.gradle.id

repositories {
    mavenLocal()
    mavenCentral()
}

plugins {
    `java-library`
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
