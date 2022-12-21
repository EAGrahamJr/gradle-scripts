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
                docker rmi -f $(docker images -q '$serviceName:*') 2>&1 >> /dev/null
                exit 0
            """.trimIndent()
        )
        logger.info("$serviceName images cleaned")
    }
    /**
     * Builds the Docker image. Manages the build version based on the semantic conventions
     * and re-tags the built image with the proper version.
     */
    create("buildImage") {
        doLast {
            // copy the app and dependencies to the directory to be picked up by the Docker build
            // TODO should be passed as a "build-arg" to the build command?
            val bldDir = "${buildDir.path}/docker/libs"

            copy {
                into(bldDir)
                from(configurations["runtimeClasspath"], "${buildDir.path}/libs")
            }

            // set via `gradle.properties`
            val serviceName = project.findProperty("docker.serviceName") ?: throw GradleException("Property 'docker.serviceName' was not set")
            val imageName = "$serviceName:latest"
            logger.warn(">>> Setting image version: $serviceName:$version\n")
            execScript(
                """
                    docker build -t $imageName .
                    docker tag $imageName $serviceName:$version
                """.trimIndent()
            )
        }
    }
}
