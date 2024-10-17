package com.github.serverfrog.bitburnerplugin.config

import com.github.serverfrog.bitburnerplugin.MyBundle
import com.intellij.openapi.options.ConfigurableUi
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class BitBurnerProjectSettingsUi : ConfigurableUi<BitburnerProjectSettings> {

    private val myPanel: JPanel = JPanel()
    private val pathField = JTextField()

    init {
        myPanel.layout = GridLayout(1, 2)
        myPanel.add(JLabel(MyBundle.message("folder")))
        myPanel.add(pathField)
    }

    override fun isModified(settings: BitburnerProjectSettings): Boolean {
        val systemPort = settings.getProjectFolder()

        return systemPort != pathField.text
    }

    override fun apply(settings: BitburnerProjectSettings) {
        val path = pathField.text

        if (path != null) {
            settings.setProjectFolder(path)
        }
    }

    override fun reset(settings: BitburnerProjectSettings) {
        pathField.text = settings.getProjectFolder() ?: "./"
    }

    override fun getComponent(): JComponent {
        return myPanel
    }
}
