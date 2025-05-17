package dev.siroshun.gradle.plugins.aggregated.javadoc.collector

import dev.siroshun.gradle.plugins.aggregated.javadoc.AGGREGATE_JAVADOC_TASK_NAME
import dev.siroshun.gradle.plugins.aggregated.javadoc.AggregatedJavadocPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType

const val COLLECT_JAVADOC_TASK = "collectJavadocTask"

abstract class AggregatedJavadocCollectorPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val javadocTask = target.tasks.withType<Javadoc>()[JavaPlugin.JAVADOC_TASK_NAME]
        val collectTask = target.tasks.register(COLLECT_JAVADOC_TASK).get()
        val javadocClasspath = target.files().builtBy(collectTask)

        target.afterEvaluate {
            val aggregating =
                target.rootProject.plugins.getPlugin(AggregatedJavadocPlugin::class.java).getAggregatingSetting()

            collectTask.doFirst {
                javadocClasspath.from(javadocTask.classpath.files).builtBy(javadocTask.classpath)
                aggregating.includes.addAll(javadocTask.includes)
                aggregating.excludes.addAll(javadocTask.excludes)
                (javadocTask.options as StandardJavadocDocletOptions).links?.let { aggregating.links.addAll(it) }
            }

            target.rootProject.tasks.named<Javadoc>(AGGREGATE_JAVADOC_TASK_NAME) {
                dependsOn(target.tasks.getByName(JavaPlugin.CLASSES_TASK_NAME))
                source(javadocTask.source)
                (classpath as ConfigurableFileCollection).from(javadocClasspath)
            }
        }
    }
}
