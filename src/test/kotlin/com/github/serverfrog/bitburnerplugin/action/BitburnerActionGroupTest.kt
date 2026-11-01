package com.github.serverfrog.bitburnerplugin.action

import com.intellij.openapi.actionSystem.AnAction
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class BitburnerActionGroupTest {

    private lateinit var actionGroup: BitburnerActionGroup

    @Before
    fun setUp() {
        actionGroup = BitburnerActionGroup()
    }

    @Test
    fun testExtensionsContainsSupportedFiles() {
        assertTrue("js should be supported", extensions.contains("js"))
        assertTrue("script should be supported", extensions.contains("script"))
        assertTrue("ns should be supported", extensions.contains("ns"))
        assertTrue("txt should be supported", extensions.contains("txt"))
    }

    @Test
    fun testExtensionsDoesNotContainUnsupportedFiles() {
        assertFalse("java should not be supported", extensions.contains("java"))
        assertFalse("py should not be supported", extensions.contains("py"))
        assertFalse("kt should not be supported", extensions.contains("kt"))
    }

    @Test
    fun testGetChildrenReturnsEmptyForNullEvent() {
        // Act
        val actions = actionGroup.getChildren(null)

        // Assert
        assertEquals(0, actions.size)
    }

    @Test
    fun testAppendAddsElementToArray() {
        // Arrange
        val emptyArray = emptyArray<AnAction>()
        val action = BitburnerPushAction()

        // Act
        val result = actionGroup.append(emptyArray, action)

        // Assert
        assertEquals(1, result.size)
        assertTrue(result[0] is BitburnerPushAction)
    }

    @Test
    fun testAppendToNonEmptyArray() {
        // Arrange
        val existing: Array<AnAction> = arrayOf(BitburnerPushAction())
        val action = BitburnerPushAction()

        // Act
        val result = actionGroup.append(existing, action)

        // Assert
        assertEquals(2, result.size)
    }

    @Test
    fun testActionGroupInstantiation() {
        // Act
        val group = BitburnerActionGroup()

        // Assert
        assertNotNull(group)
    }
}
