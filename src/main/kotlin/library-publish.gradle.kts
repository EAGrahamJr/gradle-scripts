repositories {
    mavenLocal()
}

plugins {
    `java-library`
    `maven-publish`
    id("net.linguica.maven-settings")
}

/**
 * Publish! Destination depends on the environment variable.
 */
tasks.create("libraryDistribution") {
    val publishToRepo = System.getenv("PUBLISH_PROTO") == "true"

    publishing {
        publications {
            create<MavenPublication>("library") {
                from(components["java"])
                artifactId = project.name
            }
        }
        if (publishToRepo) {
            repositories {
                maven {
                    name = "some name"
                    url = uri("some release location")
                }
                mavenSettings {
                    userSettingsFileName = System.getenv("HOME") + "/.m2/settings.xml"
                }
            }
        }
    }
    if (publishToRepo)
        dependsOn("publish")
    else
        dependsOn("publishToMavenLocal")
}
