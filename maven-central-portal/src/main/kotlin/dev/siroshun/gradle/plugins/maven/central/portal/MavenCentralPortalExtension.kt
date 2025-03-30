package dev.siroshun.gradle.plugins.maven.central.portal

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

interface MavenCentralPortalExtension {
    val stagingDirectory: DirectoryProperty
    val bundledZipFile: RegularFileProperty
    val publishSnapshot: Property<Boolean>
}
