package dev.siroshun.gradle.plugins.bundler

import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources

abstract class BundlerPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        if (!target.plugins.hasPlugin(JavaLibraryPlugin::class.java)) {
            target.plugins.apply(JavaLibraryPlugin::class.java)
        }

        if (!target.plugins.hasPlugin(ShadowPlugin::class.java)) {
            target.plugins.apply(ShadowPlugin::class.java)
        }

        val libsDir = target.rootProject.layout.buildDirectory.dir("libs")
        val extension = target.extensions.create("bundler", BundlerExtension::class.java)

        val shadowTasks = target.tasks.withType<ShadowJar>()
        if (shadowTasks.isEmpty()) {
            target.logger.error("BundlerPlugin: No shadow task found.")
            return
        }

        if (shadowTasks.size > 1) {
            target.logger.error("BundlerPlugin: Multiple shadow task found.")
            return
        }

        val shadowTask = shadowTasks.first()
        val archiveFile = shadowTask.archiveFile

        target.tasks.named(JavaBasePlugin.BUILD_TASK_NAME).configure {
            dependsOn(shadowTask)
            doLast {
                createArtifactFilepath(libsDir, extension)?.let { file ->
                    archiveFile.get().asFile.copyTo(file.asFile, true)
                }
            }
        }

        target.tasks.named(BasePlugin.CLEAN_TASK_NAME).configure {
            doLast {
                createArtifactFilepath(libsDir, extension)?.asFile?.delete()
            }
        }

        target.afterEvaluate {
            if (extension.processResourcesAction.isPresent) {
                target.tasks.withType<ProcessResources>().configureEach {
                    extension.processResourcesAction.get().execute(this)
                }
            }
        }
    }

    fun createArtifactFilepath(libsDir: Provider<Directory>, extension: BundlerExtension): RegularFile? {
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

        return libsDir.get().file(jarName)
    }
}
