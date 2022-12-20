repositories {
    mavenLocal()
}

tasks {
    fun execScript(script: String) = exec {
        commandLine("sh", "-c", script)
    }

    create("cleanImage") {
        val serviceName = project.findProperty("docker.serviceName") ?: throw GradleException("Property 'docker.serviceName' was not set")
        execScript(
            """
                docker rmi -f $(docker images -q '$serviceName:*')
                echo "$serviceName Images cleaned"
            """.trimIndent()
        )
    }
    /**
     * Builds the Docker image. Manages the build version based on the semantic conventions
     * and re-tags the built image with the proper version.
     */
    create("buildImage") {
        doLast {
            val bldDir = buildDir.path
            // copy the app and dependencies to the directory to be picked up by the Docker build
            // TODO better location - should be passed as a "build-arg" to the build command?
            copy {
                into("$bldDir/tmp/libs")
                from(configurations["runtimeClasspath"], "$bldDir/libs")
            }

            // set via `gradle.properties`
            val serviceName = project.findProperty("docker.serviceName") ?: throw GradleException("Property 'docker.serviceName' was not set")

            val imageName = "$serviceName:latest"
            println(">>> Setting image version: $serviceName:$version")
            execScript(
                """
                    docker build -t $imageName .
                    docker tag $imageName $serviceName:$version
                """.trimIndent()
            )
        }
    }
}
