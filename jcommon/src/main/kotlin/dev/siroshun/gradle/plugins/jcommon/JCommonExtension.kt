package dev.siroshun.gradle.plugins.jcommon

import dev.siroshun.gradle.plugins.jcommon.dependency.CommonDependencies
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Property
import java.nio.charset.Charset

interface JCommonExtension {
    val javaVersion: Property<JavaVersion>
    val charset: Property<Charset>
    val disableTestConfiguration: Property<Boolean>

    val commonRepositoriesAction: Property<Action<RepositoryHandler>>
    val commonDependenciesAction: Property<Action<CommonDependencies>>

    fun commonRepositories(action: Action<RepositoryHandler>) {
        if (commonRepositoriesAction.isPresent) {
            val current = commonRepositoriesAction.get()
            commonRepositoriesAction.set {
                current.execute(this)
                action.execute(this)
            }
        } else {
            commonRepositoriesAction.set(action)
        }
    }

    fun commonDependencies(action: Action<CommonDependencies>) {
        if (commonDependenciesAction.isPresent) {
            val current = commonDependenciesAction.get()
            commonDependenciesAction.set {
                current.execute(this)
                action.execute(this)
            }
        } else {
            commonDependenciesAction.set(action)
        }
    }
}

fun JCommonExtension.applyDefaults() {
    javaVersion.set(JavaVersion.VERSION_17)
    charset.set(Charsets.UTF_8)
    disableTestConfiguration.set(false)
}
