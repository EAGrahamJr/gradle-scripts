# gradle-scripts

This project contains my "personal" Kotlin/Gradle build scripts, built as [re-usable plugins](https://docs.gradle.org/current/userguide/custom_plugins.html#sec:precompiled_plugins). Since it took me a bit of dorking to figure out how to _use_ them properly, keeping notes seems a good option.

:bangbang: There is **_ZERO_** guarantee on anything here! Caveat emptor.

If there's a problem and you feel like contributing, please file an issue or fix it and submit a PR.

See the [list of plugins](./Plugin%20List.md) currently available.

**NOTE** Artifacts are currently _not_ published (on my "todo" list): to use these, this project must be downloaded and
built locally.

![Just Build](https://github.com/EAGrahamJr/gradle-scripts/actions/workflows/build.yaml/badge.svg)

## Usage

**THIS** was the hard part that's not covered much of anywhere.

### `settings.gradle.kts`

The location for custom plugins must be specified for the project via `pluginManagement`:

```kotlin
pluginManagement {
    repositories {
        // if you want ANYTHING to work
        gradlePluginPortal()
        // don't know if this is necessary, but probably doesn't hurt
        mavenCentral()
        // especially when building this project locally
        mavenLocal()
        // add custom repos here
    }
}
```

#### `build.gradle.*` scripts

These examples are given for Kotlin scripts: they _should_ work (modified) for Groovy-based scripts as well. The project
will need to explicitly include the dependency and the plugin:

```kotlin
buildscript {
    dependencies {
        classpath("crackers.buildstuff:crackers-gradle-plugins:1.0.1")
    }
}

plugins {
    id("library-publish") version "1.0.1"
}
```

#### `buildSrc` projects

In the main `build.gradle.kts` script, add this dependency:

```kotlin
dependencies {
    implementation("crackers.buildstuff:crackers-gradle-plugins:1.0.1")
}
```

and declare the plugin as needed:

```kotlin
plugins {
    id("library-publish")
}
```

## Building

Only requires a Java 8 JDK, since Gradle is "self-booting".

## Local Artifactory

Thanks to [JFrog](https://jfrog.com/community/open-source/), it's easy to setup a local repository (excluded from Git).
The [`jfrog-test`](jfrog-test.sh) script will start (kind of setup) the _Docker_ image locally, with a lame attempt to
properly configure the server for remote access.
