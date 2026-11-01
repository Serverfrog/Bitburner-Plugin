package com.github.serverfrog.bitburnerplugin.config

import org.junit.Test
import org.junit.Assert.*

class BitburnerSettingsTest {

    @Test
    fun testSettingsInstantiation() {
        // Act
        val settings = BitburnerSettings()

        // Assert
        assertNotNull(settings)
    }

    @Test
    fun testSettingsExtendsConfigurableBase() {
        // Arrange & Act
        val settings = BitburnerSettings()

        // Assert - verify it extends ConfigurableBase
        assertNotNull(settings)
        // ConfigurableBase implements Configurable interface
        val configurable: com.intellij.openapi.options.Configurable = settings
        assertNotNull(configurable)
    }

    @Test
    fun testSettingsUiInstantiation() {
        // Act
        val ui = BitBurnerSettingsUi()

        // Assert
        assertNotNull(ui)
    }

    @Test
    fun testSettingsUiGetComponentReturnsPanel() {
        // Arrange
        val ui = BitBurnerSettingsUi()

        // Act
        val component = ui.getComponent()

        // Assert
        assertNotNull(component)
        assertTrue(component is javax.swing.JPanel)
    }
}
