package dev.siroshun.gradle.plugins.aggregated.javadoc.collector

import dev.siroshun.gradle.plugins.aggregated.javadoc.AGGREGATE_JAVADOC_TASK_NAME
import dev.siroshun.gradle.plugins.aggregated.javadoc.AggregateJavadocTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import java.io.File

abstract class AggregatedJavadocCollectorPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.afterEvaluate {
            val javadocTask = target.tasks.withType<Javadoc>()[JavaPlugin.JAVADOC_TASK_NAME]

            val javadocTaskSource = javadocTask.source
            val javadocTaskClasspath = javadocTask.classpath
            val javadocTaskIncludes = javadocTask.includes
            val javadocTaskExcludes = javadocTask.excludes
            val options = javadocTask.options as StandardJavadocDocletOptions

            val moduleInfoFIle = project.file("src/main/java/module-info.java")
            val moduleName = detectModuleName(moduleInfoFIle)
            val requiredModules = getRequiredModules(moduleInfoFIle)

            target.rootProject.tasks.named<AggregateJavadocTask>(AGGREGATE_JAVADOC_TASK_NAME).configure {
                source(javadocTaskSource)
                (classpath as ConfigurableFileCollection).from(javadocTaskClasspath)

                includes.addAll(javadocTaskIncludes)
                excludes.addAll(javadocTaskExcludes)
                modules.addAll(requiredModules)
                moduleName?.let { internalModules.add(it) }
                options.links?.let { links.addAll(it) }
            }
        }
    }

    fun detectModuleName(moduleInfoFile: File): String? {
        if (!moduleInfoFile.exists()) {
            return null
        }

        return moduleInfoFile.readLines()
            .firstOrNull { it.startsWith("module ") }
            ?.replace(Regex("""module\s+|[\s{]"""), "")
            ?.trim()
    }

    fun getRequiredModules(moduleInfoFile: File): List<String> {
        if (!moduleInfoFile.exists()) {
            return emptyList()
        }

        return moduleInfoFile.readLines()
            .filter { it.startsWith("    requires ") }
            .map { it.replace(Regex("""\s*requires\s+|[\s;]"""), "").trim() }
    }
}
