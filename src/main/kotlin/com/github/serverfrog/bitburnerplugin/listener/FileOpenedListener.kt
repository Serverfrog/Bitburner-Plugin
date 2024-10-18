package com.github.serverfrog.bitburnerplugin.listener

import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project

class FileOpenedListener : FileEditorManagerListener {

    override fun selectionChanged(event: FileEditorManagerEvent) {
        super.selectionChanged(event)
        if (event.newFile == null) return
        val dataContext = DataManager.getInstance().dataContextFromFocusAsync.blockingGet(100)!!
        val project: Project = dataContext.getData(CommonDataKeys.PROJECT) ?: return

        val syncPublisher = project.messageBus.syncPublisher(FileChangedListener.FILE_CHANGE_ACTION_TOPIC)
        syncPublisher.onFileChanged(event.newFile!!)
    }
}
