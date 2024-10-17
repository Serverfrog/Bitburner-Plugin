package com.github.serverfrog.bitburnerplugin.config

import com.github.serverfrog.bitburnerplugin.MyBundle
import com.intellij.openapi.options.ConfigurableUi
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class BitBurnerSettingsUi : ConfigurableUi<BitburnerSettings> {

    private val myPanel: JPanel = JPanel()
    private val portField = JTextField()

    init {
        myPanel.layout = GridLayout(1, 2)
        myPanel.add(JLabel(MyBundle.getMessage("systemWidePort")))
        myPanel.add(portField)
    }

    override fun isModified(settings: BitburnerSettings): Boolean {
        val systemPort = settings.getSystemPort()

        return systemPort?.toString() != portField.text
    }

    override fun apply(settings: BitburnerSettings) {
        val port = portField.text.toIntOrNull()

        if (port != null) {
            settings.setSystemPort(port)
        }
    }

    override fun reset(settings: BitburnerSettings) {
        portField.text = settings.getSystemPort()?.toString() ?: "12525"
    }

    override fun getComponent(): JComponent {
        return myPanel
    }
}
