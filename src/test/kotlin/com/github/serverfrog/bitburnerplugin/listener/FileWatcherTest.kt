package com.github.serverfrog.bitburnerplugin.listener

import com.github.serverfrog.bitburnerplugin.action.extensions
import org.junit.Test
import org.junit.Assert.*

class FileWatcherTest {

    @Test
    fun testFileWatcherInstantiation() {
        // Act
        val watcher = FileWatcher()

        // Assert
        assertNotNull(watcher)
    }

    @Test
    fun testFileWatcherImplementsBulkFileListener() {
        // Act
        val watcher = FileWatcher()

        // Assert - assign to interface type to verify it implements BulkFileListener
        val listener: com.intellij.openapi.vfs.newvfs.BulkFileListener = watcher
        assertNotNull(listener)
    }

    @Test
    fun testExtensionsListUsedByWatcher() {
        // The FileWatcher uses the `extensions` list from the action package
        assertTrue(extensions.contains("js"))
        assertTrue(extensions.contains("script"))
        assertTrue(extensions.contains("ns"))
        assertTrue(extensions.contains("txt"))
    }

    @Test
    fun testExtensionsListDoesNotContainJava() {
        assertFalse(extensions.contains("java"))
    }

    @Test
    fun testExtensionsListDoesNotContainKotlin() {
        assertFalse(extensions.contains("kt"))
    }

    @Test
    fun testExtensionsListDoesNotContainPython() {
        assertFalse(extensions.contains("py"))
    }
}
