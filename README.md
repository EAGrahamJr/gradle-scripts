# gradle-scripts

This project contains my "personal" Kotlin/Gradle build scripts, built as [re-usable plugins](https://docs.gradle.org/current/userguide/custom_plugins.html#sec:precompiled_plugins). Since it took me a bit of dorking to figure out how to _use_ them properly, keeping notes seems a good option.

See the [list of plugins](Plugins.md) currently available.

# Usage

**THIS** was the hard part that's not covered much of anywhere.

## `settings.gradle.kts`

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

## `build.gradle.kts`

A "non-`buildSrc`" project will need to explicitly enable it in scripts:

```kotlin
buildscript {
    dependencies {
        classpath("my.gradle:my-gradle-plugins:1.0.0")
    }
}
```