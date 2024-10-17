package com.github.serverfrog.bitburnerplugin.websocket

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.LinkedBlockingQueue

@Service(Service.Level.PROJECT)
class CommunicationQueue(
    private val project: Project,
    private val cs: CoroutineScope
) {


    // Queue for tasks that should be executed
    private val taskQueue: LinkedBlockingQueue<suspend () -> Unit> = LinkedBlockingQueue()

    // Flag to check if the server is started
    @Volatile
    private var isServerStarted = false

    // Flag to check if the clients are connected
    @Volatile
    private var isClientsConnected = false

    // Add a task to the queue (will execute when conditions are met)
    fun addTask(task: suspend () -> Unit) {
        taskQueue.add(task)
        executeTasksIfReady()
    }

    // Mark the server as started
    fun onServerStarted() {
        isServerStarted = true
        executeTasksIfReady()
    }

    // Mark clients as connected
    fun onClientsConnected() {
        isClientsConnected = true
        executeTasksIfReady()
    }

    fun noClients() {
        isClientsConnected = false
    }

    // Execute tasks only when the server and clients are ready
    private fun executeTasksIfReady() {
        if (isServerStarted && isClientsConnected) {
            while (taskQueue.isNotEmpty()) {
                val task = taskQueue.poll()
                if (task != null) {
                    cs.launch {
                        task() // Execute the task in a coroutine
                    }
                }
            }
        }
    }
}
