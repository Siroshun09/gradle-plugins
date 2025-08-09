package dev.siroshun.gradle.plugins.aggregated.javadoc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

const val AGGREGATE_JAVADOC_TASK_NAME = "aggregateJavadoc"
const val AGGREGATED_JAVADOC_EXTENSION_NAME = "aggregatedJavadoc"

abstract class AggregatedJavadocPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val extension = target.extensions.create<AggregatedJavadocExtension>(AGGREGATED_JAVADOC_EXTENSION_NAME)
        extension.applyDefaults(target)

        val aggregateTask = target.tasks.register<AggregateJavadocTask>(AGGREGATE_JAVADOC_TASK_NAME)
        aggregateTask.configure {
            group = JavaBasePlugin.DOCUMENTATION_GROUP
            destinationDir = extension.outputDir.get().asFile

            docTitle.convention(extension.name.map { "$it ${extension.version.get()}" })
            maxWarns.convention(extension.maxWarns)
            javadocOptionsAction.convention(extension.javadocOptionsAction)
        }

        target.afterEvaluate {
            aggregateTask.get().includes.addAll(extension.includes.get())
            aggregateTask.get().excludes.addAll(extension.excludes.get())
            aggregateTask.get().modules.addAll(extension.modules.get())
        }
    }
}
