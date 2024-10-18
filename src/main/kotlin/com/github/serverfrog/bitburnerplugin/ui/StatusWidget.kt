package com.github.serverfrog.bitburnerplugin.ui

import com.github.serverfrog.bitburnerplugin.MyBundle
import com.github.serverfrog.bitburnerplugin.listener.FileChangedListener
import com.github.serverfrog.bitburnerplugin.websocket.ServerService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.TextWidgetPresentation
import com.intellij.openapi.wm.WidgetPresentationDataContext
import com.intellij.openapi.wm.impl.status.EditorBasedWidgetHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import java.awt.Component
import java.awt.event.MouseEvent

open class StatusWidget(
    private val dataContext: WidgetPresentationDataContext,
    scope: CoroutineScope,
    protected val helper: EditorBasedWidgetHelper = EditorBasedWidgetHelper(dataContext.project)
) : TextWidgetPresentation {

    private val updateServerStatusRequests =
        MutableSharedFlow<String>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
            .also { it.tryEmit(MyBundle.message("serverStatus", MyBundle.message("serverStopped"), 0)) }

    private val updateRamRequest = MutableSharedFlow<String>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
        .also { it.tryEmit("") }

    val BB_TEMP_DIRECTORY = ".idea-temp"

    init {
        getServerServices().addListener { serverEvent -> updateTextFromServerEvent(serverEvent) }

        val fileChangeListener = FileChangedListener { file -> fileChanged(file) }


        dataContext.project.messageBus.connect()
            .subscribe(FileChangedListener.FILE_CHANGE_ACTION_TOPIC, fileChangeListener)
    }

    override val alignment: Float
        get() = Component.CENTER_ALIGNMENT

    override fun text(): Flow<String> {

        return combine(updateServerStatusRequests, updateRamRequest) { status, ram ->
            if (ram.isNotBlank())
                "$status | $ram"
            else status
        }
    }

    override fun getClickConsumer(): (MouseEvent) -> Unit {
        return h@{
            if (!getServerServices().isServerRunning()) {
                getServerServices().startServer()
            }
            getServerServices().emitUpdate()
        }
    }

    private fun updateTextFromServerEvent(event: ServerService.ServerEvent) {
        val serverStatus = if (event.serverUp) MyBundle.message("serverStarted") else MyBundle.message("serverStopped")

        updateServerStatusRequests.tryEmit(MyBundle.message("serverStatus", serverStatus, event.clients.size))
    }

    private fun getServerServices(): ServerService {

        val project: Project = dataContext.project
        return project.getService(ServerService::class.java)
    }

    fun fileChanged(virtualFile: VirtualFile?) {
        if (!getServerServices().isServerRunning()) return
        if (virtualFile == null) return

        val messageHandler = getServerServices().getMessageHandler()
        val fileContent = String(virtualFile.contentsToByteArray())
        messageHandler.pushFile("$BB_TEMP_DIRECTORY/${virtualFile.name}", fileContent, "home")
        { response ->
            if (response.error == null) {
                messageHandler.calculateRam("$BB_TEMP_DIRECTORY/${virtualFile.name}", "home")
                { ramResponse ->
                    val ram = ramResponse.result as Double
                    updateRamRequest.tryEmit("$ram GB")
                }
            }
        }

    }
}
