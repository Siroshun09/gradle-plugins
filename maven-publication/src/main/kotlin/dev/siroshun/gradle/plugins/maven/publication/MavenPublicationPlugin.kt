package dev.siroshun.gradle.plugins.maven.publication

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin

const val MAVEN_PUBLICATION_EXTENSION_NAME = "mavenPublication"
const val MAVEN_PUBLICATION_NAME = "maven"

abstract class MavenPublicationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val isRootProject = target.rootProject == target
        if (isRootProject) {
            target.extensions.create(MAVEN_PUBLICATION_EXTENSION_NAME, MavenPublicationExtension::class.java)
        }

        if (isRootProject && !target.plugins.hasPlugin(JavaPlugin::class.java)) {
            return
        }

        target.plugins.apply(MavenPublishPlugin::class.java)
        target.plugins.apply(SigningPlugin::class.java)

        target.afterEvaluate {
            runAfterEvaluate(target)
        }
    }

    private fun runAfterEvaluate(target: Project) {
        val extension = target.rootProject.extensions.getByType(MavenPublicationExtension::class.java)

        val publishingExtension = target.extensions.getByType(PublishingExtension::class.java)
        publishingExtension.apply {
            publications {
                create(MAVEN_PUBLICATION_NAME, MavenPublication::class.java) {
                    target.extensions.getByType(JavaPluginExtension::class.java).apply {
                        withJavadocJar()
                        withSourcesJar()
                    }

                    groupId = target.group.toString()
                    artifactId = target.name
                    version = target.version.toString()

                    from(target.components.getByName("java"))

                    pom {
                        name.set(target.name)
                    }

                    extension.pomAction.get().execute(pom)
                }

                extension.repositoriesAction.get().execute(repositories)
            }
        }

        val signingExtension = target.extensions.getByType(SigningExtension::class.java)
        signingExtension.apply {
            useGpgCmd()
            sign(publishingExtension.publications.getByName(MAVEN_PUBLICATION_NAME))
        }
    }
}
