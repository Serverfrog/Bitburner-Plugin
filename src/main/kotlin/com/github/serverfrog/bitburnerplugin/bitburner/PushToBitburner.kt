package com.github.serverfrog.bitburnerplugin.bitburner

import com.github.serverfrog.bitburnerplugin.MyBundle
import com.github.serverfrog.bitburnerplugin.config.BitburnerSettings
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.vfs.VirtualFile
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*


val uri: URI = URI.create("http://localhost:9990/")

class PushToBitburner {

    companion object {
        private val notificationGroup: NotificationGroup = NotificationGroupManager.getInstance()
            .getNotificationGroup(MyBundle.message("groupId"))

        fun pushToBitburner(file: VirtualFile, e: AnActionEvent) {


            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + BitburnerSettings.getAuthToken())
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(createJson(file)))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            val status = response.statusCode()
            if (status == 200) {
                sendNotification(MyBundle.message("successMessage"), e)
            } else {
                sendNotification(MyBundle.message("failedMessage"), e)
            }


        }

        private fun createJson(file: VirtualFile): String {
            val fileName = file.name.replace("/[\\\\|/]+/g", "/")

            val fileContent = String(file.contentsToByteArray())
            val encode = String(Base64.getEncoder().encode(fileContent.toByteArray()))

            return "{\"filename\": \"$fileName\" ,\"code\": \"$encode\"}"
        }

        private fun sendNotification(message: String, e: AnActionEvent) {
            val notification =
                notificationGroup.createNotification(message, NotificationType.INFORMATION)
            notification.notify(e.project)

        }
    }

}
