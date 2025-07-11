package dev.siroshun.gradle.plugins.jcommon

import dev.siroshun.gradle.plugins.jcommon.dependency.CommonDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.withType
import org.gradle.language.jvm.tasks.ProcessResources
import org.gradle.process.CommandLineArgumentProvider

const val JCOMMON_EXTENSION_NAME = "jcommon"

abstract class JCommonPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<JCommonExtension>(JCOMMON_EXTENSION_NAME)
        extension.applyDefaults()

        val targets = if (target.subprojects.isEmpty()) {
            setOf(target)
        } else {
            target.subprojects
        }

        targets.forEach {
            it.plugins.apply(JavaLibraryPlugin::class.java)
            val mockitoAgent = it.configurations.create("mockitoAgent")

            if (target == it) {
                it.afterEvaluate {
                    runAfterEvaluate(it, extension, mockitoAgent)
                }
            } else {
                val subExtension = it.extensions.create<JCommonExtension>(JCOMMON_EXTENSION_NAME)
                it.afterEvaluate {
                    mergeExtensionProperties(extension, subExtension)
                    runAfterEvaluate(it, subExtension, mockitoAgent)
                }
            }
        }

        target.tasks.findByName(BasePlugin.CLEAN_TASK_NAME) ?: target.tasks.register(BasePlugin.CLEAN_TASK_NAME).configure {
            group = BasePlugin.BUILD_GROUP
            doLast {
                target.layout.buildDirectory.get().asFile.deleteRecursively()
            }
        }
    }

    private fun runAfterEvaluate(
        target: Project,
        extension: JCommonExtension,
        mockitoAgent: Configuration
    ) {
        target.extensions.configure<JavaPluginExtension> {
            sourceCompatibility = extension.javaVersion.get()
            targetCompatibility = extension.javaVersion.get()

            toolchain {
                languageVersion.set(JavaLanguageVersion.of(extension.javaVersion.get().ordinal + 1))
            }
        }

        target.tasks.withType<JavaCompile>().configureEach {
            options.encoding = extension.charset.get().name()
            options.release.set(extension.javaVersion.get().ordinal + 1)
        }

        target.tasks.withType<ProcessResources>().configureEach {
            filteringCharset = extension.charset.get().name()
        }

        if (!extension.disableTestConfiguration.get()) {
            target.tasks.withType<Test>().configureEach {
                useJUnitPlatform()
                testLogging {
                    events(TestLogEvent.FAILED, TestLogEvent.SKIPPED)
                }
            }
        }

        target.repositories.mavenCentral()

        if (extension.commonRepositoriesAction.isPresent) {
            extension.commonRepositoriesAction.get().execute(target.repositories)
        }

        if (extension.commonDependenciesAction.isPresent) {
            extension.commonDependenciesAction.get().execute(CommonDependencies(target.dependencies))
        }

        if (extension.jarTaskConfigurationAction.isPresent) {
            target.tasks.withType<Jar> {
                extension.jarTaskConfigurationAction.get().execute(this)
            }
        }

        if (extension.javadocTaskConfigurationAction.isPresent) {
            target.tasks.withType<Javadoc> {
                extension.javadocTaskConfigurationAction.get().execute(this)
            }
        }

        if (extension.mockitoProvider.isPresent) {
            val mockito = extension.mockitoProvider.get().get().copy()
            mockito.isTransitive = false
            mockitoAgent.dependencies.add(mockito)
            mockitoAgent.resolve()
            target.tasks.withType<Test>().configureEach {
                dependsOn(mockitoAgent)
                jvmArgumentProviders.add(
                    project.objects.newInstance<JavaAgentArgumentProvider>().apply {
                        classpath.from(mockitoAgent.asPath)
                    }
                )
            }
        }
    }

    abstract class JavaAgentArgumentProvider : CommandLineArgumentProvider {

        @get:Classpath
        abstract val classpath: ConfigurableFileCollection

        override fun asArguments() = listOf("-javaagent:${classpath.singleFile.absolutePath}")

    }
}
