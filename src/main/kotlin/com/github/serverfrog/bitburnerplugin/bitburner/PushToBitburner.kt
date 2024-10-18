package com.github.serverfrog.bitburnerplugin.bitburner

import com.github.serverfrog.bitburnerplugin.MyBundle
import com.github.serverfrog.bitburnerplugin.websocket.MessageHandler
import com.github.serverfrog.bitburnerplugin.websocket.ServerService
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Paths

class PushToBitburner {

    companion object {
        private val notificationGroup: NotificationGroup = NotificationGroupManager.getInstance()
            .getNotificationGroup(MyBundle.message("groupId"))

        private var messageHandler: MessageHandler? = null

        fun pushToBitburner(file: VirtualFile, project: Project) {
            websocketApi(file, project)
        }


        private fun websocketApi(file: VirtualFile, project: Project) {
            setUpServer(project)

            val relativePath = Paths.get(project.basePath!!).relativize(Paths.get(file.path))
            val fileName = relativePath.toString().replace("[\\\\|/]+".toRegex(), "/")
            val fileContent = String(file.contentsToByteArray())

            messageHandler!!.pushFile(fileName, fileContent, "home", { response ->
                if (response.error != null) {
                    sendNotification(MyBundle.message("failedMessage", response.error), project)
                }
                if (response.result != null) {
                    sendNotification(MyBundle.message("successMessage"), project)
                }
            })
        }

        private fun setUpServer(project: Project) {
            val serverService = project.getService(ServerService::class.java)
            messageHandler = serverService.getMessageHandler()
            if (serverService.isServerRunning()) return
            serverService.startServer()
        }

        private fun sendNotification(message: String, project: Project?) {
            val notification =
                notificationGroup.createNotification(message, NotificationType.INFORMATION)
            notification.notify(project)

        }
    }

}
