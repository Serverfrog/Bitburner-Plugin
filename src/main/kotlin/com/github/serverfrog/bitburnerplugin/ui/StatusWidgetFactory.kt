package com.github.serverfrog.bitburnerplugin.ui

import com.intellij.openapi.wm.StatusBarWidgetFactory
import com.intellij.openapi.wm.WidgetPresentation
import com.intellij.openapi.wm.WidgetPresentationDataContext
import com.intellij.openapi.wm.WidgetPresentationFactory
import com.intellij.ui.UIBundle
import kotlinx.coroutines.CoroutineScope

class StatusWidgetFactory : StatusBarWidgetFactory, WidgetPresentationFactory {

    var presentation: StatusWidget? = null

    companion object {
        val ID = "BBStatusWidgetFactory"
    }

    override fun getId(): String = ID

    override fun getDisplayName(): String = UIBundle.message("status.bar.widget.name")

    override fun createPresentation(context: WidgetPresentationDataContext, scope: CoroutineScope): WidgetPresentation {
        presentation = StatusWidget(dataContext = context, scope = scope)
        return presentation!!
    }

}
