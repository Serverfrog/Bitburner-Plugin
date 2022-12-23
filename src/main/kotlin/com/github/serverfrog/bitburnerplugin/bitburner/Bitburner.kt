package com.github.serverfrog.bitburnerplugin.bitburner

import com.github.serverfrog.bitburnerplugin.MyBundle
import com.github.serverfrog.bitburnerplugin.config.BitburnerSettings
import com.intellij.ide.DataManager
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class Bitburner {
    companion object {

        // List of File Extensions BitBurner recognize
        val extensions = arrayListOf("js", "script")

        private val uri: URI = URI.create("http://localhost:9990/")

        private val notificationGroup: NotificationGroup = NotificationGroupManager.getInstance()
            .getNotificationGroup(MyBundle.message("groupId"))

        fun push(fileName: String, fileContent: ByteArray) {
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + BitburnerSettings.getAuthToken())
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(createJsonToPush(fileName, fileContent)))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            val ramUsagePattern: Pattern = Pattern.compile("\"ramUsage\":([0-9.]+)")
            val ramUsageMatcher: Matcher = ramUsagePattern.matcher(response.body())
            var ramUsage = ""
            if (ramUsageMatcher.find()) {
                ramUsage = " (${ramUsageMatcher.group(1)} GiB)"
            }
            val overwrittenPattern: Pattern = Pattern.compile("\"overwritten\":(true)")
            val overwrittenMatcher: Matcher = overwrittenPattern.matcher(response.body())
            var overwritten = "📘"
            if (overwrittenMatcher.find()) {
                overwritten = "📝"
            }
            var statusIcon = "✅"
            if (response.statusCode() != 200) {
                statusIcon = "❌"
            }
            val information = "$statusIcon 💻  ️🎮 | $overwritten $fileName$ramUsage"

            sendNotification(information)
        }

        fun delete(fileName: String) {
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + BitburnerSettings.getAuthToken())
                .uri(uri)
                .method("DELETE", HttpRequest.BodyPublishers.ofString(createJsonToDelete(fileName)))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            var statusIcon = "✅"
            if (response.statusCode() != 200) {
                statusIcon = "❌"
            }
            val information = "$statusIcon 💻  ️🎮 | 🗑 $fileName"

            sendNotification(information)
        }

        fun getFileName(filePath: String): String {
            val project: Project = getProject()

            return filePath
                .replace(project.basePath.toString(), "")
                .replace("\\\\", "/")
                .replace("\\", "/")
        }

        fun getProject(): Project {
            val dataContext = DataManager.getInstance().dataContextFromFocusAsync.blockingGet(100)!!
            return dataContext.getData(CommonDataKeys.PROJECT)!!
        }

        private fun createJsonToPush(fileName: String, fileContent: ByteArray): String {
            val encode = String(Base64.getEncoder().encode(fileContent))

            return "{\"filename\": \"$fileName\" ,\"code\": \"$encode\"}"
        }

        private fun createJsonToDelete(fileName: String): String {
            return "{\"filename\": \"$fileName\"}"
        }

        fun sendNotification(message: String) {
            val project: Project = getProject()

            val notification =
                notificationGroup.createNotification(message, NotificationType.INFORMATION)
            notification.notify(project)
        }
    }
}
