package dev.siroshun.gradle.plugins.maven.central.portal

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.register

const val MAVEN_CENTRAL_PORTAL_EXTENSION_NAME = "mavenCentralPortal"
const val MAVEN_CENTRAL_PORTAL_DIRECTORY_NAME = "mavenCentralPortal"
const val CLEAN_STAGING_TASK_NAME = "cleanStaging"
const val CREATE_BUNDLED_ZIP_FOR_MAVEN_CENTRAL_PORTAL_TASK_NAME = "createBundledZipForMavenCentralPortal"
const val UPLOAD_TO_MAVEN_CENTRAL_PORTAL_TASK_NAME = "uploadToMavenCentralPortal"

abstract class MavenCentralPortalPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create(MAVEN_CENTRAL_PORTAL_EXTENSION_NAME, MavenCentralPortalExtension::class.java)

        extension.stagingDirectory.set(target.layout.buildDirectory.dir("$MAVEN_CENTRAL_PORTAL_DIRECTORY_NAME/staging-${target.version}"))
        extension.bundledZipFile.set(target.layout.buildDirectory.file("$MAVEN_CENTRAL_PORTAL_DIRECTORY_NAME/${target.name}-${target.version}.zip"))

        val cleanupTask = target.tasks.register<Delete>(CLEAN_STAGING_TASK_NAME) {
            extension.stagingDirectory.get().asFile.deleteRecursively()
        }

        val zipTask = target.tasks.register(CREATE_BUNDLED_ZIP_FOR_MAVEN_CENTRAL_PORTAL_TASK_NAME) {
            doLast {
                if (!extension.stagingDirectory.get().asFile.exists()) {
                    error("staging directory not found")
                }

                if (extension.bundledZipFile.get().asFile.exists()) {
                    extension.bundledZipFile.get().asFile.delete()
                }

                val zipParentDir = extension.bundledZipFile.get().asFile.parentFile
                if (!zipParentDir.exists()) {
                    zipParentDir.mkdirs()
                }

                val stagingPath = extension.stagingDirectory.get().asFile.toPath()
                java.util.zip.ZipOutputStream(extension.bundledZipFile.get().asFile.outputStream()).use { zipOut ->
                    extension.stagingDirectory.get().asFile.walk().forEach { file ->
                        if (!file.isDirectory) {
                            val zipEntry = java.util.zip.ZipEntry(stagingPath.relativize(file.toPath()).toString())
                            zipOut.putNextEntry(zipEntry)
                            file.inputStream().use { it.copyTo(zipOut) }
                            zipOut.closeEntry()
                        }
                    }
                }
            }
        }

        val uploadToPortalTask = target.tasks.register<UploadToMavenCentralPortal>(
            UPLOAD_TO_MAVEN_CENTRAL_PORTAL_TASK_NAME
        ) {
            group = PublishingPlugin.PUBLISH_TASK_GROUP
            this@register.bundledZipFile.set(extension.bundledZipFile)
            doLast {
                if (project.version.toString().endsWith("SNAPSHOT") && !extension.publishSnapshot.orElse(false).get()) {
                    error(
                        "Trying to upload snapshot version to Maven Central Portal.\n"
                            .plus("If you want to upload snapshot, set publishSnapshot = true")
                    )
                }
            }
        }

        val targets = if (target.subprojects.isEmpty()) {
            setOf(target)
        } else {
            target.subprojects
        }

        targets.forEach {
            val publishTask = it.tasks.findByName(PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME)
            if (publishTask == null) {
                it.tasks.whenTaskAdded {
                    if (name == PublishingPlugin.PUBLISH_LIFECYCLE_TASK_NAME) {
                        zipTask.get().dependsOn(this)
                    }
                }
            } else {
                zipTask.get().dependsOn(publishTask)
            }
        }

        target.afterEvaluate {
            zipTask.get().dependsOn(cleanupTask)
            uploadToPortalTask.get().dependsOn(zipTask)
        }
    }
}
