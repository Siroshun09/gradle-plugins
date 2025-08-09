package dev.siroshun.gradle.plugins.aggregated.javadoc

import org.gradle.api.Action
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions

abstract class AggregateJavadocTask: Javadoc() {

    @get:Input
    abstract val docTitle: Property<String>

    @get:Input
    abstract val maxWarns: Property<Int>

    @get:Input
    @get:Optional
    abstract val javadocOptionsAction: Property<Action<StandardJavadocDocletOptions>>

    @get:Input
    val modules: MutableSet<String> = mutableSetOf()

    @get:Input
    val internalModules: MutableSet<String> = mutableSetOf()

    @get:Input
    val links: MutableSet<String> = mutableSetOf()

    @TaskAction
    override fun generate() {
        exclude("**/module-info.java")

        val opts = options as StandardJavadocDocletOptions
        opts.docTitle(docTitle.get())
            .windowTitle(docTitle.get())
            .links(*links.toTypedArray())

        opts.addStringOption("Xmaxwarns", maxWarns.get().toString())

        val externalModules = modules.filter { it !in internalModules }
        if (externalModules.isNotEmpty()) {
            opts.addStringOption("-add-modules", externalModules.joinToString(","))
        }

        if (javadocOptionsAction.isPresent) {
            javadocOptionsAction.get().execute(opts)
        }

        super.generate()
    }
}
