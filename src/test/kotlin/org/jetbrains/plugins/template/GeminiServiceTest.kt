package org.jetbrains.plugins.template

import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.plugins.template.services.GeminiService

class GeminiServiceTest : BasePlatformTestCase() {

    fun testServiceInitialization() {
        val geminiService = project.service<GeminiService>()
        assertNotNull(geminiService)
    }

    fun testApiKeyConfiguration() {
        val geminiService = project.service<GeminiService>()
        
        // Initially not configured
        assertFalse(geminiService.isConfigured())
        
        // Configure with API key
        geminiService.setApiKey("test-api-key")
        assertTrue(geminiService.isConfigured())
        
        // Clear API key
        geminiService.setApiKey("")
        assertFalse(geminiService.isConfigured())
    }

    fun testEmptyApiKey() {
        val geminiService = project.service<GeminiService>()
        
        geminiService.setApiKey("")
        assertFalse(geminiService.isConfigured())
        
        geminiService.setApiKey("   ")
        assertFalse(geminiService.isConfigured())
    }

    fun testApiKeyWithWhitespace() {
        val geminiService = project.service<GeminiService>()
        
        // Valid key with no whitespace
        geminiService.setApiKey("valid-key")
        assertTrue(geminiService.isConfigured())
    }
}
