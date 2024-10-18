package com.github.serverfrog.bitburnerplugin.listener

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.messages.Topic


fun interface FileChangedListener {
    fun onFileChanged(file: VirtualFile)

    companion object {
        @Topic.AppLevel
        val FILE_CHANGE_ACTION_TOPIC: Topic<FileChangedListener> = Topic.create(
            "custom name",
            FileChangedListener::class.java
        )
    }
}
