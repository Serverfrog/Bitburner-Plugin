package com.github.serverfrog.bitburnerplugin.listener

import com.github.serverfrog.bitburnerplugin.action.extensions
import com.github.serverfrog.bitburnerplugin.bitburner.PushToBitburner
import com.github.serverfrog.bitburnerplugin.config.BitburnerSettings
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent


class FileWatcher : BulkFileListener {

    override fun after(events: MutableList<out VFileEvent>) {
        if (BitburnerSettings.getAuthToken() == null) {
            return
        }
        val dataContext = DataManager.getInstance().dataContextFromFocusAsync.blockingGet(100)!!
        val project: Project = dataContext.getData(CommonDataKeys.PROJECT)!!

        for (event in events) {
            val file = event.file
            if (extensions.contains(file?.canonicalFile?.extension) && file != null) {
                PushToBitburner.pushToBitburner(file, project)
            }
        }

    }
}
