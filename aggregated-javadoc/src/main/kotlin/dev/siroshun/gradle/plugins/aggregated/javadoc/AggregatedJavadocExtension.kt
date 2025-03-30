package dev.siroshun.gradle.plugins.aggregated.javadoc

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.external.javadoc.StandardJavadocDocletOptions

interface AggregatedJavadocExtension {
    val name: Property<String>
    val version: Property<String>
    val includes: Property<Iterable<String>>
    val excludes: Property<Iterable<String>>
    val modules: Property<Iterable<String>>
    val maxWarns: Property<Int>
    val javadocOptionsAction: Property<Action<StandardJavadocDocletOptions>>

    val outputDir: DirectoryProperty

    fun javadocOptions(action: Action<StandardJavadocDocletOptions>) {
        if (javadocOptionsAction.isPresent) {
            val current = javadocOptionsAction.get()
            javadocOptionsAction.set {
                current.execute(this)
                action.execute(this)
            }
        } else {
            javadocOptionsAction.set(action)
        }
    }
}

fun AggregatedJavadocExtension.applyDefaults(project: Project) {
    name.set(project.name)
    version.set(project.version.toString())
    includes.set(emptyList())
    modules.set(emptyList())
    maxWarns.set(Int.MAX_VALUE)

    outputDir.set(project.layout.buildDirectory.dir("docs"))
}
