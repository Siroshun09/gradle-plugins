package dev.siroshun.gradle.plugins.maven.central.portal

import org.gradle.api.DefaultTask
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.util.*
import javax.inject.Inject

abstract class UploadToMavenCentralPortal @Inject constructor(
    objects: ObjectFactory
) : DefaultTask() {

    @get:InputFile
    abstract val bundledZipFile: RegularFileProperty
    @get:Input
    val credentials: PasswordCredentials = objects.newInstance(PasswordCredentials::class.java)

    @TaskAction
    fun run() {
        val token = Base64.getEncoder().encodeToString("${credentials.username}:${credentials.password}".toByteArray())
        val authorizationHeader = "Bearer $token"

        val uploadUrl = "https://central.sonatype.com/api/v1/publisher/upload"
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
            logger.info("Successfully uploaded ${bundledZipFile.get().asFile.toPath().fileName} to Maven Central Portal!")
        } else {
            logger.error("Upload failed with response code: ${connection.responseCode}, msg: ${connection.responseMessage}, body: ${connection.inputStream.bufferedReader().readText()}")
        }
    }
}
