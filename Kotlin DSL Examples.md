# Examples for Kotlin Gradle DSL scripts

## Generating and Publishing KDoc

```kotlin

tasks {
    /**
     * Generates Github markdown and puts it in the "docs" directory.
     */
    dokkaGfm {
        outputDirectory.set(file("$projectDir/docs"))
    }
    /**
     * Generates Javadoc style documentation via the dependent task and
     * creates the appropriate JAR artifact
     */
    register<Jar>("dokkaJavadocJar") {
        dependsOn(dokkaJavadoc)
        from(dokkaJavadoc.flatMap { it.outputDirectory })
        archiveClassifier.set("javadoc")
    }
    /**
     * Later versions of Gradle require some explicit task dependencies...
     */
    generateMetadataFileForLibraryPublication {
        mustRunAfter("dokkaJavadocJar")
    }
}

defaultTasks("clean", "build", "dokkaJavadocJar", "libraryDistribution")

```
