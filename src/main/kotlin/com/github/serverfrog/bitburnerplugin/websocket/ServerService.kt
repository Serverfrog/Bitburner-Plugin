package com.github.serverfrog.bitburnerplugin.websocket

import com.github.serverfrog.bitburnerplugin.MyBundle
import com.github.serverfrog.bitburnerplugin.config.BitburnerSettings
import com.github.serverfrog.bitburnerplugin.websocket.ServerService.ServerEvent
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
import org.java_websocket.WebSocket


typealias ServerEventHandler = (ServerEvent) -> Unit

@Service(Service.Level.PROJECT)
class ServerService(
    private val project: Project,
    private val cs: CoroutineScope
) : Disposable {

    data class ServerEvent(val serverUp: Boolean, val clients: List<WebSocket>)

    private var isRunning = false

    private var job: Job? = null

    private val messageHandler = MessageHandler(project)

    private var server: Server? = null

    private val listener = mutableListOf<ServerEventHandler>()

    fun startServer() {
        if (isRunning) return
        // Start your WebSocket server
        println("Starting WebSocket server...")
        job = cs.launch {
            server = Server(BitburnerSettings(), messageHandler, ::emitUpdate)
            val notificationGroup: NotificationGroup = NotificationGroupManager.getInstance()
                .getNotificationGroup(MyBundle.message("groupId"))
            val notification =
                notificationGroup.createNotification("Server Started", NotificationType.INFORMATION)
            notification.notify(project)
            server!!.start()

        }
        isRunning = true
    }

    private fun stopServer() {
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


    fun emitUpdate() {
        val event = ServerEvent(isServerRunning(), server?.clients ?: arrayListOf())
        for (listener in listener) {
            listener(event)
        }
    }

    fun addListener(listener: ServerEventHandler) {
        this.listener.add(listener)
    }

    fun removeListener(listener: ServerEventHandler) {
        this.listener.remove(listener)
    }
}
