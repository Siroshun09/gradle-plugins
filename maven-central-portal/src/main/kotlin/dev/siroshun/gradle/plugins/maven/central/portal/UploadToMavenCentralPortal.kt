package dev.siroshun.gradle.plugins.maven.central.portal

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.util.*
import javax.inject.Inject

@DisableCachingByDefault
abstract class UploadToMavenCentralPortal @Inject constructor(
    objects: ObjectFactory
) : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.ABSOLUTE)
    abstract val bundledZipFile: RegularFileProperty

    /**
     * `AUTOMATIC` or `USER_MANAGED`. The Portal uses `USER_MANAGED` if not set.
     */
    @get:Input
    @get:Optional
    abstract val publishingType: Property<String>

    @get:Input
    val credentials: PasswordCredentials = objects.newInstance(PasswordCredentials::class.java)

    @TaskAction
    fun run() {
        val token = Base64.getEncoder().encodeToString("${credentials.username}:${credentials.password}".toByteArray())
        val authorizationHeader = "Bearer $token"

        val uploadUrl = "https://central.sonatype.com/api/v1/publisher/upload" +
            (publishingType.orNull?.let { "?publishingType=$it" } ?: "")
        val boundary = "----WebKitFormBoundary${UUID.randomUUID()}"
        val connection = URI(uploadUrl).toURL().openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Authorization", authorizationHeader)
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")

        DataOutputStream(connection.outputStream).use { output ->
            output.writeBytes("--$boundary\r\n")
            val filename = bundledZipFile.get().asFile.toPath().fileName
            output.writeBytes("Content-Disposition: form-data; name=\"bundle\"; filename=\"$filename\"\r\n")
            output.writeBytes("Content-Type: application/zip\r\n\r\n")
            output.flush()

            bundledZipFile.get().asFile.inputStream().use { it.copyTo(output) }
            output.writeBytes("\r\n")

            output.writeBytes("--$boundary--\r\n")
            output.flush()
        }

        if (connection.responseCode in 200..299) {
            val deploymentId = connection.inputStream.bufferedReader().readText()
            logger.lifecycle("Uploaded ${bundledZipFile.get().asFile.toPath().fileName} to Maven Central Portal (deployment: $deploymentId)")
        } else {
            throw GradleException(
                "Upload failed with response code: " +
                    "${connection.responseCode}, " +
                    "msg: ${connection.responseMessage}, " +
                    "body: ${connection.errorStream?.bufferedReader()?.readText()}"
            )
        }
    }
}
