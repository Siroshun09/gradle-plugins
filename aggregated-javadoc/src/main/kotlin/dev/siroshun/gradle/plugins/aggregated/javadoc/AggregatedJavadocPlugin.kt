package dev.siroshun.gradle.plugins.aggregated.javadoc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

const val AGGREGATE_JAVADOC_TASK_NAME = "aggregateJavadoc"
const val AGGREGATED_JAVADOC_EXTENSION_NAME = "aggregatedJavadoc"

abstract class AggregatedJavadocPlugin : Plugin<Project> {

    private val aggregatingSettings = AggregatingSettings()

    override fun apply(target: Project) {
        val extension = target.extensions.create<AggregatedJavadocExtension>(AGGREGATED_JAVADOC_EXTENSION_NAME)
        extension.applyDefaults(target)

        target.tasks.register<Javadoc>(AGGREGATE_JAVADOC_TASK_NAME).configure {
            group = JavaBasePlugin.DOCUMENTATION_GROUP
            setDestinationDir(extension.outputDir.get().asFile)
            classpath = target.objects.fileCollection()
            doFirst {
                aggregatingSettings.includes.addAll(extension.includes.get())
                aggregatingSettings.excludes.addAll(extension.excludes.get())

                include(aggregatingSettings.includes)
                exclude(aggregatingSettings.excludes)
                exclude("**/module-info.java")

                val opts = options as StandardJavadocDocletOptions
                opts.docTitle("${extension.name.get()} ${extension.version.get()}")
                    .windowTitle("${extension.name.get()} ${extension.version.get()}")
                    .links(*aggregatingSettings.links.toTypedArray())

                opts.addStringOption("Xmaxwarns", extension.maxWarns.toString())
                opts.addStringOption("-add-modules", extension.modules.get().joinToString(","))

                if (extension.javadocOptionsAction.isPresent) {
                    extension.javadocOptionsAction.get().execute(opts)
                }
            }
        }
    }

    fun getAggregatingSetting(): AggregatingSettings {
        return aggregatingSettings
    }
}
