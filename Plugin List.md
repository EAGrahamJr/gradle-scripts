<!-- TOC -->
* [Gradle Script Plugins](#gradle-script-plugins)
  * [About Maven Credentials](#about-maven-credentials)
  * [List of Tasks](#list-of-tasks)
    * [Publish Java Library](#publish-java-library)
      * [Usage](#usage)
    * [Generate Java and Kotlin `protobuf` implementations](#generate-java-and-kotlin-protobuf-implementations)
      * [Usage](#usage-1)
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

```kotlin
id("crackers.buildstuff.library-publish")
```

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

Generates `protobuf` libraries.

This plugin:

* sets up IDEA "source directories" so generated source is available to projects
* creates artifacts with the project version
  * message classes are compiled to Java
  * service classes are compiled to both Java and Kotln client/server stubs
  * **stub** dependencies included transitively (_including_ the runtime-libraries)

#### Usage

```kotlin
id("crackers.buildstuff.generate-protobuf")
```

This is simply just the "typical" `protobuf` generate setup as [documented](https://github.com/google/protobuf-gradle-plugin), nothing "fancy" added: just the Java and Kotlin generation parts. This should be completely transparent when used and all other options available should be available.

* **Properties**
  * `protobuf.javaVersion` - sets the version of Java to use; defaults to **17** 
