# List of Plugins

**Properties** are set in the project's `gradle.properties` file(s).

## `generate-protobuf`

Generates and publishes `protobuf` libraries.

This plugin produces

- IDEA "source directories" so generated source is available to projects
- artifacts with the project version
  - a ZIP file containting the `protobuf` definitions
  - a Java JAR file, with the pre-compiled classes
    - message classes are compiled to Java
    - service classes are compiled to both Java and Kotln client/server stubs
    - **stub** dependencies included transitively (_not_ the runtime-libraries)
- publishes to a Maven repository when the _environment_ variable `PUBLISH_PROTO=true`
  - uses the `net.linguica.maven-settings` plugin to pick up repository/security information via the typical `M2_HOME` environment setting
  - TODO need to set up properties for remote Maven repo

### Usage

- **Requirements**
  - `protobuf` files are in `src/main/proto`
  - `group`, `module`, and `version` properties are properly set
- **Targets**
  - `protoDistribution` compiles, packages, and publishes the `protobuf`
  - `buildProto` executes `clean`, then `protoDistribution`
- **Properties**
  - `protobuf.javaVersion` - the JVM version to target (default is _1.8_)
  - `protobuf.publish.repoName` - the name of the "publish" repository to target
  - `protobuf.publish.repoUrl` = the URL of the "publish" repository

## docker-build

Builds the Docker image based on the application `runtimeClasspath` dependencies and a local `Dockerfile`.

This could/should evolve into a "standardized" Dockerfile for JVM-based apps and be included in the plugin. See [DockerFactory](src/main/kotlin/DockerFactory.kt)

This plugin produces

- A Docker image based on the local `Dockerfile` tagged with both `latest` and the project's current version.

### Usage

- **Requirements**
  - project `version` set
  - the below properties are defined
- **Targets**
  - `cleanImage` - runs `docker rmi` to _attempt_ to remove app images (may not work on all systems)
  - `buildImage` - runs `docker build` and `docker tag` to create the images
- **Properties**
  - `docker.serviceName` - the name given to the image: the image will be tagged `serviceName:latest` and `serviceName:${project.version}`

### Example (`Kotlin`)

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

`gradle.properties`:

```properties
docker.serviceName=myservice
docker.mainClassName=path.to.AppKt

```