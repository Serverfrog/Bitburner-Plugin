package com.github.serverfrog.bitburnerplugin.action

import com.github.serverfrog.bitburnerplugin.MyBundle
import com.github.serverfrog.bitburnerplugin.bitburner.PushToBitburner
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class BitburnerPushAction() : AnAction(MyBundle.message("pushActionLabel")) {

    override fun actionPerformed(e: AnActionEvent) {
        val file = e.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)?.canonicalFile!!
        PushToBitburner.pushToBitburner(file, e.project)
    }
}
