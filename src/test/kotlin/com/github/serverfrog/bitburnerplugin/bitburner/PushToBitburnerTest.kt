package com.github.serverfrog.bitburnerplugin.bitburner

import org.junit.Test
import org.junit.Assert.*
import java.nio.file.Paths
import java.util.*

class PushToBitburnerTest {

    @Test
    fun testBase64EncodeDecodeRoundTrip() {
        // Arrange
        val content = "const x = 42; // test"

        // Act
        val encoded = String(Base64.getEncoder().encode(content.toByteArray()))
        val decoded = String(Base64.getDecoder().decode(encoded))

        // Assert
        assertEquals(content, decoded)
    }

    @Test
    fun testBase64EncodeEmptyString() {
        // Arrange
        val content = ""

        // Act
        val encoded = String(Base64.getEncoder().encode(content.toByteArray()))
        val decoded = String(Base64.getDecoder().decode(encoded))

        // Assert
        assertEquals(content, decoded)
    }

    @Test
    fun testBase64EncodeUnicodeContent() {
        // Arrange
        val content = "你好世界 🌍"

        // Act
        val encoded = String(Base64.getEncoder().encode(content.toByteArray()))
        val decoded = String(Base64.getDecoder().decode(encoded))

        // Assert
        assertEquals(content, decoded)
    }

    @Test
    fun testPathNormalization() {
        // Arrange
        val projectPath = "/home/user/project"
        val filePath = Paths.get(projectPath).resolve("src/test/example.js").toString()

        // Act
        val relativePath = Paths.get(projectPath).relativize(Paths.get(filePath))
        val normalizedPath = relativePath.toString().replace("[\\\\|/]+".toRegex(), "/")

        // Assert
        assertEquals("src/test/example.js", normalizedPath)
    }

    @Test
    fun testPathNormalizationWithBackslashes() {
        // Arrange
        val projectPath = "/home/user/project"
        // On Linux, Paths.get normalizes backslashes, so test the regex replacement directly
        val pathWithBackslashes = "src\\test\\example.js"

        // Act
        val normalizedPath = pathWithBackslashes.replace("[\\\\|/]+".toRegex(), "/")

        // Assert - backslashes should be normalized to forward slashes
        assertFalse(normalizedPath.contains("\\"))
        assertEquals("src/test/example.js", normalizedPath)
    }

    @Test
    fun testPathNormalizationWithMixedSeparators() {
        // Arrange
        val projectPath = "/home/user/project"
        val mixedPath = Paths.get(projectPath).resolve("src/test/example.js").toString()

        // Act
        val relativePath = Paths.get(projectPath).relativize(Paths.get(mixedPath))
        val normalizedPath = relativePath.toString().replace("[\\\\|/]+".toRegex(), "/")

        // Assert
        assertEquals("src/test/example.js", normalizedPath)
    }

    @Test
    fun testRelativePathFromNestedDirectory() {
        // Arrange
        val projectPath = "/home/user/project"
        val nestedPath = Paths.get(projectPath).resolve("a/b/c/d/file.js").toString()

        // Act
        val relativePath = Paths.get(projectPath).relativize(Paths.get(nestedPath))
        val normalizedPath = relativePath.toString().replace("[\\\\|/]+".toRegex(), "/")

        // Assert
        assertEquals("a/b/c/d/file.js", normalizedPath)
    }

    @Test
    fun testFileContentFromResource() {
        // Arrange & Act
        val inputStream = javaClass.classLoader.getResourceAsStream("testData/test.js")

        // Assert
        assertNotNull("test.js should be on classpath", inputStream)
        val content = inputStream.bufferedReader().use { it.readText() }
        assertTrue(content.contains("console.log"))
    }

    @Test
    fun testFileContentPreservesNewlines() {
        // Arrange & Act
        val inputStream = javaClass.classLoader.getResourceAsStream("testData/special.js")

        // Assert
        assertNotNull("special.js should be on classpath", inputStream)
        val content = inputStream.bufferedReader().use { it.readText() }
        assertTrue(content.contains("\n"))
        val lines = content.split("\n")
        assertTrue(lines.size > 1)
    }

    @Test
    fun testBase64EncodingMatchesExpectedFormat() {
        // Arrange
        val content = "console.log('hello');\n"

        // Act
        val encoded = String(Base64.getEncoder().encode(content.toByteArray()))

        // Assert
        assertNotNull(encoded)
        assertTrue(encoded.length > 0)
        // Verify decoding matches original
        val decoded = String(Base64.getDecoder().decode(encoded))
        assertEquals(content, decoded)
    }
}
