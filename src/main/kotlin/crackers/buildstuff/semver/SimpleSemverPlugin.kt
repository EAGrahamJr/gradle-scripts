/*
 * Copyright 2022-2025 by E. A. Graham, Jr.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package crackers.buildstuff.semver

import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import java.time.Instant
import java.util.concurrent.atomic.AtomicBoolean

class SimpleSemverVersion(val major: Int, val minor: Int, val patch: Int) {
    override fun toString(): String {
        return "$major.$minor.$patch"
    }
}

/**
 * A simple plugin to manage a semantic version number in a file.
 * The file is expected to have the following format:
 * ```
 * # Semantic Version Counter
 * # 2022-01-01T12:00:00Z
 * 0.0.0
 * ```
 * The plugin provides tasks to increment the major, minor, and patch versions.
 * The version is saved to the file after each increm   ent.
 */
class SimpleSemverPlugin : Plugin<Project> {
    /**
     * Save the version to the file.
     */
    private fun SimpleSemverVersion.saveToFile(file: File) {
        StringBuilder("# Semantic Version Counter\n")
            .append("# ${Instant.now()}\n")
            .append("$major.$minor.$patch")
            .append("\n")
            .let {
                println("Saving to file: $it")
                file.writeText(it.toString())
            }
    }

    override fun apply(project: Project) {
        val versionFile: File = project.providers.gradleProperty(SEMVER_FILE).let { prop ->
            if (prop.isPresent) File(project.rootDir.absolutePath, prop.get())
            else project.layout.projectDirectory.file("semver.version").asFile
        }
        var semverVersion: SimpleSemverVersion? = null

        // create a provider for the version
        val semver = project.provider {
            if (semverVersion == null) {
                project.logger.info("Loading version from file: $versionFile")
                semverVersion = if (versionFile.exists()) {
                    val parts = versionFile.readText().lines().drop(2).first().split(".")
                    SimpleSemverVersion(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
                } else SimpleSemverVersion(0, 0, 0).also { it.saveToFile(versionFile) }
            }
            semverVersion!!
        }

        project.extensions.add("semver", semver)

        // every task that modifies the version should save it to the file
        with(project.tasks) {
            register("incrementMajor") {
                doLast {
                    if (majorDone.compareAndSet(false, true)) {
                        semverVersion = semver.get()
                            .let { sm -> SimpleSemverVersion(sm.major + 1, 0, 0) }
                            .also { it.saveToFile(versionFile) }
                        project.logger.info("Incrementing major: version $semverVersion")
                    } else {
                        project.logger.info("Major already incremented -- no worries")
                    }
                }
            }
            register("incrementMinor") {
                doLast {
                    if (minorDone.compareAndSet(false, true)) {
                        semverVersion = semver.get()
                            .let { sm -> SimpleSemverVersion(sm.major, sm.minor + 1, 0) }
                            .also { it.saveToFile(versionFile) }
                        project.logger.info("Incrementing minor: version $semverVersion")
                    } else {
                        project.logger.info("Minor already incremented -- no sweat")
                    }
                }
            }
            register("incrementPatch") {
                doLast {
                    if (patchDone.compareAndSet(false, true)) {
                        semverVersion = semver.get()
                            .let { sm -> SimpleSemverVersion(sm.major, sm.minor, sm.patch + 1) }
                            .also { it.saveToFile(versionFile) }
                        project.logger.info("Incrementing patch: version $semverVersion")
                    } else
                        project.logger.info("Patch already incremented -- dinna fash")
                }
            }
            register("showVersion") {
                doLast {
                    project.logger.info("Semantic version: ${semver.get()}")
                    project.logger.info("Project version : ${project.version}")
                }
            }
            register("printSemver") {
                doLast {
                    println(semverVersion)
                }
            }
        }
    }

    companion object {
        const val SEMVER_FILE = "simple.semver.file"
        private val patchDone = AtomicBoolean(false)
        private val minorDone = AtomicBoolean(false)
        private val majorDone = AtomicBoolean(false)
    }
}
