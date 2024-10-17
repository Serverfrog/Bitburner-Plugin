package com.github.serverfrog.bitburnerplugin.config

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import javax.swing.JComponent

class BitburnerProjectSettings(private val project: Project) : Configurable {

    private var settingsUi: BitBurnerProjectSettingsUi? = null

    override fun createComponent(): JComponent? {
        settingsUi = BitBurnerProjectSettingsUi()
        return settingsUi?.component
    }

    override fun isModified(): Boolean {
        return settingsUi?.isModified(this) ?: false
    }

    override fun apply() {
        settingsUi?.apply(this)
    }

    override fun reset() {
        settingsUi?.reset(this)
    }

    override fun getDisplayName(): String {
        return "Bitburner Project Settings"
    }

    override fun disposeUIResources() {
        settingsUi = null
    }

    fun getProjectFolder(): String? {
        return BitburnerProjectService(project).getProjectFolder()
    }

    fun setProjectFolder(folder: String) {
        BitburnerProjectService(project).setProjectFolder(folder)
    }
}
