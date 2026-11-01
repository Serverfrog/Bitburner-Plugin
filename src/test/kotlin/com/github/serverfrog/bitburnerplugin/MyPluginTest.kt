package com.github.serverfrog.bitburnerplugin

import org.junit.Test
import org.junit.Assert.*

class MyPluginTest {

    @Test
    fun testMyBundleMessageReturnsValue() {
        // Act
        val message = MyBundle.message("name")

        // Assert
        assertNotNull(message)
        assertFalse(message.isEmpty())
    }

    @Test
    fun testMyBundleMessageSuccessMessage() {
        // Act
        val message = MyBundle.message("successMessage")

        // Assert
        assertNotNull(message)
        assertFalse(message.isEmpty())
    }

    @Test
    fun testMyBundleMessageFailedMessage() {
        // Act
        val message = MyBundle.message("failedMessage")

        // Assert
        assertNotNull(message)
        assertFalse(message.isEmpty())
    }

    @Test
    fun testMyBundleMessagePushActionLabel() {
        // Act
        val message = MyBundle.message("pushActionLabel")

        // Assert
        assertNotNull(message)
        assertFalse(message.isEmpty())
    }
}
