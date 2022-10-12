package com.github.serverfrog.bitburnerplugin.action

import com.github.serverfrog.bitburnerplugin.bitburner.Bitburner.Companion.extensions
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

class BitburnerActionGroup : ActionGroup() {
    private val projectViewPopup = "ProjectViewPopup"
    private val editorPopup = "EditorPopup"

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        var actions = emptyArray<AnAction>()

        if ((e?.place == projectViewPopup || e?.place == editorPopup) &&
            extensions.contains(e.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)?.canonicalFile?.extension)
        ) {
            actions = append(actions, BitburnerPushAction())
        }

        return actions
    }

    private fun append(arr: Array<AnAction>, element: AnAction): Array<AnAction> {
        val list: MutableList<AnAction> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }
}
