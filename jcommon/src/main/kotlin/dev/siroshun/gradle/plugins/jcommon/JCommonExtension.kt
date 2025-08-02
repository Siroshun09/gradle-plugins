package dev.siroshun.gradle.plugins.jcommon

import dev.siroshun.gradle.plugins.jcommon.dependency.CommonDependencies
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.kotlin.dsl.maven
import java.nio.charset.Charset

interface JCommonExtension {
    val javaVersion: Property<JavaVersion>
    val charset: Property<Charset>
    val disableTestConfiguration: Property<Boolean>

    val commonRepositoriesAction: Property<Action<RepositoryHandler>>
    val commonDependenciesAction: Property<Action<CommonDependencies>>
    val jarTaskConfigurationAction: Property<Action<Jar>>
    val javadocTaskConfigurationAction: Property<Action<Javadoc>>

    val mockitoProvider: Property<Provider<MinimalExternalModuleDependency>>

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

    fun jarTask(action: Action<Jar>) {
        if (jarTaskConfigurationAction.isPresent) {
            val current = jarTaskConfigurationAction.get()
            jarTaskConfigurationAction.set {
                current.execute(this)
                action.execute(this)
            }
        } else {
            jarTaskConfigurationAction.set(action)
        }
    }

    fun javadocTask(action: Action<Javadoc>) {
        if (javadocTaskConfigurationAction.isPresent) {
            val current = javadocTaskConfigurationAction.get()
            javadocTaskConfigurationAction.set {
                current.execute(this)
                action.execute(this)
            }
        } else {
            javadocTaskConfigurationAction.set(action)
        }
    }

    fun setupMockito(mockito: Provider<MinimalExternalModuleDependency>) {
        mockitoProvider.set(mockito)
        commonDependencies {
            testImplementation(mockito)
        }
    }

    fun setupPaperRepository() {
        commonRepositories {
            maven("https://repo.papermc.io/repository/maven-public/") {
                name = "PaperMC"
                mavenContent {
                    includeGroupAndSubgroups("io.papermc")
                    includeGroupAndSubgroups("com.destroystokyo.paper")
                    includeGroupAndSubgroups("com.velocitypowered")
                    includeGroupAndSubgroups("net.md-5")
                    includeGroupAndSubgroups("com.mojang")
                }
            }
        }
    }
}

fun JCommonExtension.applyDefaults() {
    javaVersion.set(JavaVersion.VERSION_17)
    charset.set(Charsets.UTF_8)
    disableTestConfiguration.set(false)
}

fun mergeExtensionProperties(parent: JCommonExtension, child: JCommonExtension) {
    mergeExtensionProperty(parent.javaVersion, child.javaVersion)
    mergeExtensionProperty(parent.charset, child.charset)
    mergeExtensionProperty(parent.disableTestConfiguration, child.disableTestConfiguration)
    mergeExtensionProperty(parent.mockitoProvider, child.mockitoProvider)

    if (parent.commonRepositoriesAction.isPresent) {
        child.commonRepositories(parent.commonRepositoriesAction.get())
    }

    if (parent.commonDependenciesAction.isPresent) {
        child.commonDependencies(parent.commonDependenciesAction.get())
    }

    if (parent.jarTaskConfigurationAction.isPresent) {
        child.jarTask(parent.jarTaskConfigurationAction.get())
    }

    if (parent.javadocTaskConfigurationAction.isPresent) {
        child.javadocTask(parent.javadocTaskConfigurationAction.get())
    }
}

fun <T : Any> mergeExtensionProperty(ref: Property<T>, target: Property<T>) {
    if (ref.isPresent && !target.isPresent) {
        target.set(ref.get())
    }
}
