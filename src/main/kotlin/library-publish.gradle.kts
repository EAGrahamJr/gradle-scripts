repositories {
    mavenLocal()
}

plugins {
    `java-library`
    `maven-publish`
    id("net.linguica.maven-settings")
}

java {
    withSourcesJar()
}

/**
 * Publish! Destination depends on the environment variable.
 */
tasks.create("libraryDistribution") {
    val publishToRepo = System.getenv("PUBLISH_LIBRARY") == "true"

    publishing {
        publications {
            create<MavenPublication>("library") {
                from(components["java"])
                artifactId = project.name
            }
        }
        if (publishToRepo) {
            val repoName = project.findProperty("library.publish.repoName")?.toString() ?: throw GradleException("Property 'library.publish.repoName' was not set")
            val repoUrl = project.findProperty("library.publish.repoUrl")?.toString() ?: throw GradleException("Property 'library.publish.repoUrl' was not set")
            val useMavenSettings: Boolean = project.findProperty("library.publish.useM2")?.toString().toBoolean()

            repositories {
                maven {
                    name = repoName
                    url = uri(repoUrl)
                    if (!useMavenSettings) {
                        credentials {
                            username = System.getenv("MAVEN_ACTOR") ?: throw GradleException("Environment variable 'MAVEN_ACTOR' was not set")
                            password = System.getenv("MAVEN_TOKEN") ?: throw GradleException("Environment variable 'MAVEN_TOKEN' was not set")
                        }
                    }
                }
                if (useMavenSettings) {
                    mavenSettings {
                        val DEFAULT = System.getenv("HOME") + "/.m2"
                        userSettingsFileName = (System.getenv("M2_HOME") ?: DEFAULT) + "/settings.xml"
                    }
                }
            }
        }
    }
    if (publishToRepo)
        dependsOn("publish")
    else
        dependsOn("publishToMavenLocal")
}
