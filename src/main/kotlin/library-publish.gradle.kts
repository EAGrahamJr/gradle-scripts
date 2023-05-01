repositories {
    mavenLocal()
}

plugins {
    `java-library`
    `maven-publish`
}

java {
    withSourcesJar()
    withJavadocJar()
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

            repositories {
                maven {
                    name = repoName
                    url = uri(repoUrl)
                    isAllowInsecureProtocol = project.findProperty("library.publish.insecure")?.toString().toBoolean()

                    credentials {
                        username = System.getenv("MAVEN_ACTOR") ?: throw GradleException("Environment variable 'MAVEN_ACTOR' was not set")
                        password = System.getenv("MAVEN_TOKEN") ?: throw GradleException("Environment variable 'MAVEN_TOKEN' was not set")
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
