package dev.siroshun.gradle.plugins.bundler

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.kotlin.dsl.withType

abstract class BundlerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply(JavaLibraryPlugin::class.java)
        target.plugins.apply(ShadowPlugin::class.java)

        val extension = target.extensions.create("bundler", BundlerExtension::class.java)

        val shadowTask = target.tasks.withType<ShadowJar>()
        if (shadowTask.isEmpty()) {
            target.logger.error("BundlerPlugin: No shadow task found.")
            return
        }

        if (shadowTask.size > 1) {
            target.logger.error("BundlerPlugin: Multiple shadow task found.")
            return
        }

        target.tasks.named(JavaBasePlugin.BUILD_TASK_NAME).configure {
            dependsOn(shadowTask.first())
            doLast {
                createArtifactFilepath(target, extension)?.let { file ->
                    shadowTask.first().archiveFile.get().asFile.copyTo(file.asFile, true)
                }
            }
        }

        target.tasks.named(BasePlugin.CLEAN_TASK_NAME).configure {
            doLast {
                createArtifactFilepath(target, extension)?.asFile?.delete()
            }
        }
    }

    fun createArtifactFilepath(project: Project, extension: BundlerExtension): RegularFile? {
        if (!extension.jarNameInRootBuildDirectory.isPresent) {
            return null
        }

        var jarName = extension.jarNameInRootBuildDirectory.get()
        if (jarName.isEmpty()) {
            return null
        }

        if (!jarName.endsWith(".jar")) {
            jarName += ".jar"
        }

        return project.rootProject.layout.buildDirectory.dir("libs").get().file(jarName)
    }
}
