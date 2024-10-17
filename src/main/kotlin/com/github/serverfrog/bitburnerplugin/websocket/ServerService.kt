package com.github.serverfrog.bitburnerplugin.websocket

import com.github.serverfrog.bitburnerplugin.MyBundle
import com.github.serverfrog.bitburnerplugin.config.BitburnerSettings
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch

@Service(Service.Level.PROJECT)
class ServerService(
    private val project: Project,
    private val cs: CoroutineScope
) : Disposable {

    private var isRunning = false

    private var job: Job? = null

    private val messageHandler = MessageHandler(project)

    private var server: Server? = null

    fun startServer() {
        if (isRunning) return
        // Start your WebSocket server
        println("Starting WebSocket server...")
        job = cs.launch {
            server = Server(BitburnerSettings(), messageHandler)
            val notificationGroup: NotificationGroup = NotificationGroupManager.getInstance()
                .getNotificationGroup(MyBundle.message("groupId"))
            val notification =
                notificationGroup.createNotification("Server Started", NotificationType.INFORMATION)
            notification.notify(project)
            server!!.start()

        }
        isRunning = true
    }

    fun stopServer() {
        if (isRunning) {
            // Stop your WebSocket server
            println("Stopping WebSocket server...")
            isRunning = false
            server?.stop()

            suspend {
                job!!.cancelAndJoin()
            }
        }
    }

    fun isServerRunning(): Boolean = isRunning


    override fun dispose() {
        // Cleanup when the project is closed
        stopServer()
    }

    fun getMessageHandler(): MessageHandler = messageHandler
}
