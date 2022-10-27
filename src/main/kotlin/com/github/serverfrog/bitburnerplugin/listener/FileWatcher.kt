package com.github.serverfrog.bitburnerplugin.listener

import com.github.serverfrog.bitburnerplugin.bitburner.Bitburner
import com.github.serverfrog.bitburnerplugin.bitburner.Bitburner.Companion.extensions
import com.github.serverfrog.bitburnerplugin.bitburner.Bitburner.Companion.getFileName
import com.github.serverfrog.bitburnerplugin.bitburner.Bitburner.Companion.getListOfProjectVirtualFilesByExt
import com.github.serverfrog.bitburnerplugin.bitburner.Bitburner.Companion.sendNotification
import com.github.serverfrog.bitburnerplugin.config.BitburnerSettings
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.vfs.newvfs.events.VFileMoveEvent
import com.intellij.openapi.vfs.newvfs.events.VFilePropertyChangeEvent

class FileWatcher : BulkFileListener {

    override fun before(events: List<VFileEvent>) {
        if (BitburnerSettings.getAuthToken() == null) {
            sendNotification("The token is null. Enable the API in Bitburner game, then copy the token and paste in IntelliJ settings under Tools section.")
            return
        }

        for (event in events) {
            if (event.javaClass.simpleName == "VFileDeleteEvent") {
                if (event.file?.isDirectory == true) {
                    for (extension in extensions) {
                        val allFiles = getListOfProjectVirtualFilesByExt(extension)
                        for (file in allFiles) {
                            if (file.path.startsWith(event.path)) {
                                val filePathToDelete = getFileName(file.path)
                                Bitburner.delete(filePathToDelete)
                            }
                        }
                    }
                }
            }

            if (event.javaClass.simpleName == "VFileMoveEvent") {
                val eventFileMove: VFileMoveEvent = event as VFileMoveEvent
                if (event.file.isDirectory) {
                    for (extension in extensions) {
                        val allFiles = getListOfProjectVirtualFilesByExt(extension)
                        for (file in allFiles) {
                            if (file.path.startsWith(event.path)) {
                                val filePathToDelete = getFileName(file.path)
                                Bitburner.delete(filePathToDelete)

                                val newFilePath = eventFileMove.newPath + "/" + file.name
                                val filePathToPush = getFileName(newFilePath)
                                Bitburner.push(filePathToPush, file.contentsToByteArray())
                            }
                        }
                    }
                }
            }

            if (event.javaClass.simpleName == "VFilePropertyChangeEvent") {
                val eventFileProperty: VFilePropertyChangeEvent = event as VFilePropertyChangeEvent

                if (event.file.isDirectory) {
                    if (eventFileProperty.propertyName == "name") {
                        for (extension in extensions) {
                            val allFiles = getListOfProjectVirtualFilesByExt(extension)
                            for (file in allFiles) {
                                if (file.path.startsWith(event.path)) {
                                    val filePathToDelete = getFileName(file.path)
                                    Bitburner.delete(filePathToDelete)

                                    val newFilePath =
                                        file.path.replace(eventFileProperty.oldPath, eventFileProperty.newPath)
                                    val filePathToPush = getFileName(newFilePath)
                                    Bitburner.push(filePathToPush, file.contentsToByteArray())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun after(events: List<VFileEvent>) {
        if (BitburnerSettings.getAuthToken() == null) {
            sendNotification("The token is null. Enable the API in Bitburner game, then copy the token and paste in IntelliJ settings under Tools section.")
            return
        }

        for (event in events) {
            val filePath = getFileName(event.path)

            if (event.javaClass.simpleName == "VFileCreateEvent" ||
                event.javaClass.simpleName == "VFileContentChangeEvent" ||
                event.javaClass.simpleName == "VFileCopyEvent"
            ) {
                if (event.file?.isDirectory == false) {
                    if (extensions.contains(event.file?.extension)) {
                        event.file?.let { Bitburner.push(filePath, it.contentsToByteArray()) }
                    }
                }
            }

            if (event.javaClass.simpleName == "VFileDeleteEvent") {
                if (event.file?.isDirectory == false) {
                    if (extensions.contains(event.file?.extension)) {
                        Bitburner.delete(filePath)
                    }
                }
            }

            if (event.javaClass.simpleName == "VFileMoveEvent") {
                val eventFileMove: VFileMoveEvent = event as VFileMoveEvent

                if (!event.file.isDirectory) {
                    if (extensions.contains(event.file.extension)) {
                        val fileOldParent = getFileName(eventFileMove.oldPath)
                        Bitburner.delete(fileOldParent)
                        event.file.let { Bitburner.push(filePath, it.contentsToByteArray()) }
                    }
                }
            }

            if (event.javaClass.simpleName == "VFilePropertyChangeEvent") {
                val eventFileProperty: VFilePropertyChangeEvent = event as VFilePropertyChangeEvent

                if (!event.file.isDirectory) {
                    if (extensions.contains(event.file.extension)) {
                        if (eventFileProperty.propertyName == "name") {
                            val fileOldPath = getFileName(eventFileProperty.oldPath)
                            Bitburner.delete(fileOldPath)
                            event.file.let { Bitburner.push(filePath, it.contentsToByteArray()) }
                        }
                    }
                }
            }
        }
    }
}
