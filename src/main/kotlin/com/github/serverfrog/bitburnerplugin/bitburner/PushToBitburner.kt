package com.github.serverfrog.bitburnerplugin.bitburner

import com.github.serverfrog.bitburnerplugin.MyBundle
import com.github.serverfrog.bitburnerplugin.config.BitburnerSettings
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Paths
import java.util.*


val uri: URI = URI.create("http://localhost:9990/")

class PushToBitburner {

    companion object {
        private const val NOTIFICATION_GROUP_ID = "com.github.serverfrog.bitburnerplugin"

        fun pushToBitburner(file: VirtualFile, project: Project) {


            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + BitburnerSettings.getAuthToken())
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(createJson(file, project)))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            val status = response.statusCode()
            if (status == 200) {
                sendNotification(MyBundle.message("successMessage"), project)
            } else {
                sendNotification(MyBundle.message("failedMessage"), project)
            }


        }

        private fun createJson(file: VirtualFile, project: Project): String {
            val relativePath = Paths.get(project.basePath!!).relativize(Paths.get(file.path))
            val fileName = relativePath.toString().replace("[\\\\|/]+".toRegex(), "/")

            val fileContent = String(file.contentsToByteArray())
            val encode = String(Base64.getEncoder().encode(fileContent.toByteArray()))

            return "{\"filename\": \"$fileName\" ,\"code\": \"$encode\"}"
        }

        private fun sendNotification(message: String, project: Project?) {
            Notification(NOTIFICATION_GROUP_ID, message, NotificationType.INFORMATION).notify(project)
        }
    }

}
