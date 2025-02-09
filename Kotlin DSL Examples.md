# Examples for Kotlin Gradle DSL scripts

## Generating and Publishing KDoc

```kotlin
plugins {
    id("crackers.buildstuff.library-publish") version "1.3.0"
}

tasks {
    /**
     * Generates Github markdown and puts it in the "docs" directory.
     */
    dokkaGfm {
        outputDirectory.set(file("$projectDir/docs"))
    }
    // thees pick up ALL java/kotlin docs and packages them as docs jars
    dokkaJavadoc {
        mustRunAfter("javadoc")
        outputDirectory.set(file("$projectDir/build/docs"))
    }
    javadocJar {
        mustRunAfter("dokkaJavadoc")
        include("$projectDir/build/docs")
    }
    // jar docs
    register<Jar>("dokkaJavadocJar") {
        dependsOn(dokkaJavadoc)
        from(dokkaJavadoc.flatMap { it.outputDirectory })
        archiveClassifier.set("javadoc")
    }
    generateMetadataFileForLibraryPublication {
        mustRunAfter("dokkaJavadocJar")
    }
}

defaultTasks("clean", "build", "dokkaJavadocJar", "libraryDistribution")

```
