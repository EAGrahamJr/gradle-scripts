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

## About Maven Credentials

Publish operations to external URLs require credentials: these are supplied through _envronment variables_:

* `MAVEN_ACTOR` - username
* `MAVEN_TOKEN` - the you-know-what

Also, sometimes an _insecure_ registry is involved. See [this link](https://docs.gradle.org/7.5.1/dsl/org.gradle.api.artifacts.repositories.UrlArtifactRepository.html#org.gradle.api.artifacts.repositories.UrlArtifactRepository:allowInsecureProtocol) to understand that particular setting.

## List of Tasks

### Publish Java Library

Publishes source and binary JAR files for "library" projects to a Maven repository. See also [Kotlin Examples](Kotlin%20DSL%20Examples.md).

#### Usage

* **Tasks**
  * `libraryDistribution`creates and publishes the binary and source JARs
    * if the **environment** variable `PUBLISH_LIBRARY` is _true_, the artifacts are pushed to the designated repository
    * otherwise they are published "locally"
    * the project's `-javadoc.jar` is automatically generated from artifacts labelled as "javadoc"
      * if no Javadoc is generated, an "empty" JAR is produced
* **Requirements**
  * Java and/or Kotlin source files
  * `group`, `module`, and `version` properties are properly set
* **Properties**
  * `library.publish.repoName` - the name of the "publish" repository to target
  * `library.publish.repoUrl` - the URL of the "publish" repository
  * `library.publish.insecure` - if "true" (default **false**), allows for use of an _insecure_ registry (see [above](#about-maven-credentials))

### Generate Java and Kotlin `protobuf` implementations

Generates and publishes `protobuf` libraries. It is an extention of the above _ Publish Java Library_.

This plugin:

* sets up IDEA "source directories" so generated source is available to projects
* creates artifacts with the project version
  * a ZIP file containting the `protobuf` definitions
  * a Java JAR file, with the pre-compiled classes
    * message classes are compiled to Java
    * service classes are compiled to both Java and Kotln client/server stubs
    * **stub** dependencies included transitively (_not_ the runtime-libraries)
* publishes all the artifacts to a Maven repository
  * if the **environment** varible `PUBLISH_PROTO` is _true_, the artifacts are pushed to the designated repository
    * otherwise they are published "locally"

#### Usage

* **Tasks**
  * `protoDistribution` compiles, packages, and publishes the `protobuf`
  * `buildProto` executes `clean`, then `protoDistribution`
* **Requirements**
  * `protobuf` files are in `src/main/proto`
  * `group`, `module`, and `version` properties are properly set
* **Properties**
  * `protobuf.javaVersion` - the JVM version to target (default is _17_)
  * `protobuf.publish.repoName` - the name of the "publish" repository to target
  * `protobuf.publish.repoUrl` - the URL of the "publish" repository
  * `protobuf.publish.insecure` - if "true" (default **false**), allows for use of an _insecure_ registry (see [above](#about-maven-credentials))
