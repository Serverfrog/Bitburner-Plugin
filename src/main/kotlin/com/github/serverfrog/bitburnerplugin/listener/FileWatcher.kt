package com.github.serverfrog.bitburnerplugin.listener

import com.github.serverfrog.bitburnerplugin.bitburner.Bitburner
import com.github.serverfrog.bitburnerplugin.bitburner.Bitburner.Companion.extensions
import com.github.serverfrog.bitburnerplugin.bitburner.Bitburner.Companion.getFileName
import com.github.serverfrog.bitburnerplugin.bitburner.Bitburner.Companion.sendNotification
import com.github.serverfrog.bitburnerplugin.config.BitburnerSettings
import com.github.serverfrog.bitburnerplugin.listener.FileWatcher.FileEvents.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope

class FileWatcher : BulkFileListener {
    enum class FileEvents(private val className: String) {
        DELETE("VFileDeleteEvent"),
        MOVE("VFileMoveEvent"),
        PROPERTY_CHANGED("VFilePropertyChangeEvent"),
        CREATE("VFileCreateEvent"),
        CONTENT_CHANGED("VFileContentChangeEvent"),
        COPY("VFileCopyEvent");

        fun isSameEvent(event: VFileEvent): Boolean {
            return event.javaClass.simpleName == this.className
        }
    }

    override fun before(events: List<VFileEvent>) {
        if (checkAuthToken()) return

        for (event in events) {
            if (event.file?.isDirectory == false) break

            if (DELETE.isSameEvent(event)) {
                deleteFolder(event)
            }
            if (MOVE.isSameEvent(event)) {
                moveFolder(event)
            }
            if (PROPERTY_CHANGED.isSameEvent(event)) {
                updateFolder(event)
            }
        }
    }

    private fun updateFolder(event: VFileEvent) {
        val eventFileProperty: VFilePropertyChangeEvent = event as VFilePropertyChangeEvent

        if (eventFileProperty.propertyName == "name") {
            val allFiles = getListOfProjectVirtualFilesByExtensions(extensions)
            for (file in filterByEventPath(event, allFiles)) {
                val filePathToDelete = getFileName(file.path)
                Bitburner.delete(filePathToDelete)
                val newFilePath = file.path.replace(eventFileProperty.oldPath, eventFileProperty.newPath)
                val filePathToPush = getFileName(newFilePath)
                Bitburner.push(filePathToPush, file.contentsToByteArray())
            }
        }
    }

    private fun moveFolder(event: VFileEvent) {
        val eventFileMove: VFileMoveEvent = event as VFileMoveEvent
        val allFiles = getListOfProjectVirtualFilesByExtensions(extensions)
        for (file in filterByEventPath(event, allFiles)) {
            val filePathToDelete = getFileName(file.path)
            Bitburner.delete(filePathToDelete)
            val newFilePath = eventFileMove.newPath + "/" + file.name
            val filePathToPush = getFileName(newFilePath)
            Bitburner.push(filePathToPush, file.contentsToByteArray())
        }
    }

    private fun deleteFolder(event: VFileEvent) {
        val allFiles = getListOfProjectVirtualFilesByExtensions(extensions)
        for (file in filterByEventPath(event, allFiles)) {
            val filePathToDelete = getFileName(file.path)
            Bitburner.delete(filePathToDelete)
        }
    }

    private fun filterByEventPath(event: VFileEvent, files: Collection<VirtualFile>): List<VirtualFile> {
        return files.filter { it.path.startsWith(event.path) }.toList()
    }

    override fun after(events: List<VFileEvent>) {
        if (checkAuthToken()) return

        for (event in events) {
            if (event.file?.isDirectory == true || !extensions.contains(event.file?.extension)) break

            val filePath = getFileName(event.path)
            if (CREATE.isSameEvent(event) || CONTENT_CHANGED.isSameEvent(event) || COPY.isSameEvent(event)) {
                event.file?.let { Bitburner.push(filePath, it.contentsToByteArray()) }
            }
            if (DELETE.isSameEvent(event)) {
                Bitburner.delete(filePath)
            }
            if (MOVE.isSameEvent(event)) {
                val eventFileMove: VFileMoveEvent = event as VFileMoveEvent
                val fileOldParent = getFileName(eventFileMove.oldPath)
                Bitburner.delete(fileOldParent)
                eventFileMove.file.let { Bitburner.push(filePath, it.contentsToByteArray()) }
            }
            if (PROPERTY_CHANGED.isSameEvent(event)) {
                val eventFileProperty: VFilePropertyChangeEvent = event as VFilePropertyChangeEvent

                if (eventFileProperty.propertyName == "name") {
                    val fileOldPath = getFileName(eventFileProperty.oldPath)
                    Bitburner.delete(fileOldPath)
                    eventFileProperty.file.let { Bitburner.push(filePath, it.contentsToByteArray()) }
                }
            }
        }
    }

    private fun getListOfProjectVirtualFilesByExtensions(extNames: List<String>): MutableCollection<VirtualFile> {
        val project: Project = Bitburner.getProject()
        val scope = GlobalSearchScope.projectScope(project)
        val files = mutableListOf<VirtualFile>()
        for (extension in extNames) {
            files.addAll(FilenameIndex.getAllFilesByExt(project, extension, scope))
        }
        return files
    }

    private fun checkAuthToken(): Boolean {
        if (BitburnerSettings.getAuthToken() == null) {
            sendNotification("The token is null. Enable the API in Bitburner game, then copy the token and paste in IntelliJ settings under Tools section.")
            return true
        }
        return false
    }
}
