package com.github.serverfrog.bitburnerplugin.action

import org.junit.Test
import org.junit.Assert.*

class BitburnerPushActionTest {

    @Test
    fun testPushActionInstantiation() {
        // Act
        val action = BitburnerPushAction()

        // Assert
        assertNotNull(action)
    }

    @Test
    fun testPushActionHasTemplatePresentation() {
        // Act
        val action = BitburnerPushAction()

        // Assert
        assertNotNull(action.templatePresentation)
    }

    @Test
    fun testPushActionTemplateName() {
        // Act
        val action = BitburnerPushAction()

        // Assert - the action should have a non-null template presentation text
        assertNotNull(action.templatePresentation.text)
    }
}
