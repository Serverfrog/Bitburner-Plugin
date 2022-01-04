package com.github.serverfrog.bitburnerplugin.config

import com.github.serverfrog.bitburnerplugin.MyBundle
import com.intellij.openapi.options.ConfigurableUi
import java.awt.GridLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JPasswordField

class BitBurnerSettingsUi : ConfigurableUi<BitburnerSettings> {

    private val myAuthTokenField: JPasswordField = JPasswordField()
    private val myAuthTokenLabel: JLabel = JLabel(MyBundle.message("authToken"))

    private val myPanel: JPanel = JPanel()

    init {
        val layout = GridLayout(1, 2, 5, 5)
        myPanel.layout = layout
        myPanel.add(myAuthTokenLabel)
        myPanel.add(myAuthTokenField)
    }

    override fun reset(settings: BitburnerSettings) {
        myAuthTokenField.text = BitburnerSettings.getAuthToken()
    }

    override fun isModified(settings: BitburnerSettings): Boolean {
        return !myAuthTokenField.password.contentEquals(BitburnerSettings.getAuthToken()?.toCharArray())
    }

    override fun apply(settings: BitburnerSettings) {
        BitburnerSettings.setAuthToken(String(myAuthTokenField.password))
    }

    override fun getComponent(): JComponent {
        return myPanel
    }
}
