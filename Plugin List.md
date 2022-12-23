<!-- TOC -->

* [Gradle Script Plugins](#gradle-script-plugins)
  * [About Maven Credentials](#about-maven-credentials)
* [List of Plugins](#list-of-plugins)
  * [library-publish](#library-publish)
    * [Usage](#usage)
  * [generate-protobuf](#generate-protobuf)
    * [Usage](#usage)
  * [docker-build](#docker-build)
    * [Usage](#usage)
    * [Example Build Script](#example-build-script)
    * [Example Docker File](#example-docker-file)

<!-- TOC -->

# Gradle Script Plugins

**Properties** are set in the project's `gradle.properties` file(s).

To use a plugin, specify

```kotlin
id("library-publish") version "1.0.0"
```

## About Maven Credentials

Most "publish" task allow for two options to pick up Maven credentials. These are typically managed through a property setting named `xxxx.publish.useM2`. The _default_ is **false** (e.g. use environment variables).

- If the property is **true**, the plugins will use an included plugin<sup>**1**</sup> to pick up repository/security information
  - the typical `M2_HOME` environment setting
  - the _default_ location of `$HOME/.m2` _settings.xml_ file(s)
- Otherwise, two _environment variables_ will be used:
  - `MAVEN_ACTOR` - username
  - `MAVEN_TOKEN` - the you-know-what

<sup>**1**</sup> [net.linguica.maven-settings](https://github.com/mark-vieira/gradle-maven-settings-plugin)

# List of Plugins

## library-publish

Publishes source and binary JAR files for "library" projects to a Maven repository.

### Usage

- **Tasks**
  - `libraryDistribution`creates and publishes the binary and source JARs
    - if the **environment** variable `PUBLISH_LIBRARY` is _true_, the artifacts are pushed to the designated repository
    - otherwise they are published "locally"
- **Requirements**
  - Java or Kotlin source files
  - `group`, `module`, and `version` properties are properly set
- **Properties**
  - `library.publish.repoName` - the name of the "publish" repository to target
  - `library.publish.repoUrl` - the URL of the "publish" repository
  - `library.publish.useM2` - see [Maven Credentials](#about-maven-credentials)

## generate-protobuf

Generates and publishes `protobuf` libraries.

This plugin:

- sets up IDEA "source directories" so generated source is available to projects
- creates artifacts with the project version
  - a ZIP file containting the `protobuf` definitions
  - a Java JAR file, with the pre-compiled classes
    - message classes are compiled to Java
    - service classes are compiled to both Java and Kotln client/server stubs
    - **stub** dependencies included transitively (_not_ the runtime-libraries)
- publishes to a Maven repository

### Usage

- **Tasks**
  - `protoDistribution` compiles, packages, and publishes the `protobuf`
  - `buildProto` executes `clean`, then `protoDistribution`
- **Requirements**
  - `protobuf` files are in `src/main/proto`
  - `group`, `module`, and `version` properties are properly set
- **Properties**
  - `protobuf.javaVersion` - the JVM version to target (default is _1.8_)
  - `protobuf.publish.repoName` - the name of the "publish" repository to target
  - `protobuf.publish.repoUrl` - the URL of the "publish" repository
  - `protobuf.publish.useM2` - see [Maven Credentials](#about-maven-credentials)

## docker-build

Builds the Docker image based on the application `runtimeClasspath` dependencies and a local `Dockerfile`.
This plugin produces

- A Docker image based on the local `Dockerfile` tagged with both `latest` and the project's current version.

### Usage

- **Tasks**
  - `cleanImage` - runs `docker rmi` to _attempt_ to remove app images (may not work on all systems)
  - `buildImage` - runs `docker build` and `docker tag` to create the images
- **Requirements**
  - project `version` set
  - the `Dockerfile` should copy the application JAR and _all_ dependencies from the `build/docker/libs` directory
- **Properties**
  - `docker.serviceName` - the name given to the image: the image will be tagged `serviceName:latest` and `serviceName:${project.version}`

### Example Build Script

`gradle.properties`:

```properties
docker.serviceName=group_path/my_service
```

Tasks in a build script:

```kotlin
tasks {
  clean {
    dependsOn("cleanImage")
  }

  buildImage.configure {
    mustRunAfter("build")
  }
}
```

### Example Docker File

```dockerfile
FROM eclipse-temurin:17-jre

COPY build/docker/libs/* /app/libs/

ENV TZ="America/Los_Angeles"

ENTRYPOINT java -Xmx64m -Xms64m -cp "/app/libs/*" my.project.MainKt

```
