package dev.siroshun.gradle.plugins.bundler

import org.gradle.api.Action
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.expand
import org.gradle.language.jvm.tasks.ProcessResources

interface BundlerExtension {

    val processResourcesAction: Property<Action<ProcessResources>>
    val jarNameInRootBuildDirectory: Property<String>

    fun copyToRootBuildDirectory(filename: String) {
        jarNameInRootBuildDirectory.set(filename)
    }

    fun processResources(action: Action<ProcessResources>) {
        if (processResourcesAction.isPresent) {
            val current = processResourcesAction.get()
            processResourcesAction.set {
                current.execute(this)
                action.execute(this)
            }
        } else {
            processResourcesAction.set(action)
        }
    }

    fun replacePluginVersion(filename: String, version: Any) {
        processResources {
            filesMatching(listOf(filename)) {
                expand("projectVersion" to version)
            }
        }
    }

    fun replacePluginVersion(filename: String, version: Any, minecraftVersion: Any) {
        processResources {
            filesMatching(listOf(filename)) {
                expand(
                    "projectVersion" to version,
                    "minecraftVersion" to minecraftVersion,
                )
            }
        }
    }

    fun replacePluginVersionForBukkit(version: Any) {
        replacePluginVersion("plugin.yml", version)
    }

    fun replacePluginVersionForBukkit(version: Any, minecraftVersion: Any) {
        replacePluginVersion("plugin.yml", version, minecraftVersion)
    }

    fun replacePluginVersionForPaper(version: Any) {
        replacePluginVersion("paper-plugin.yml", version)
    }

    fun replacePluginVersionForPaper(version: Any, minecraftVersion: Any) {
        replacePluginVersion("paper-plugin.yml", version, minecraftVersion)
    }

    fun replacePluginVersionForVelocity(version: Any) {
        replacePluginVersion("velocity-plugin.json", version)
    }
}
