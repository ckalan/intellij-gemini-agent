package org.jetbrains.plugins.template

import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.jetbrains.plugins.template.settings.GeminiSettings

class GeminiSettingsTest : BasePlatformTestCase() {

    fun testDefaultSettings() {
        val settings = service<GeminiSettings>()
        
        // Check default values
        assertEquals("", settings.apiKey)
        assertEquals(GeminiSettings.ChatMode.ASK, settings.chatMode)
        assertEquals(GeminiSettings.AnalysisScope.FILE, settings.analysisScope)
    }

    fun testSettingsPersistence() {
        val settings = service<GeminiSettings>()
        
        // Set values
        settings.apiKey = "test-key-123"
        settings.chatMode = GeminiSettings.ChatMode.EDIT
        settings.analysisScope = GeminiSettings.AnalysisScope.CODEBASE
        
        // Verify values are set
        assertEquals("test-key-123", settings.apiKey)
        assertEquals(GeminiSettings.ChatMode.EDIT, settings.chatMode)
        assertEquals(GeminiSettings.AnalysisScope.CODEBASE, settings.analysisScope)
    }

    fun testChatModeToggle() {
        val settings = service<GeminiSettings>()
        
        // Start with ASK mode
        settings.chatMode = GeminiSettings.ChatMode.ASK
        assertEquals(GeminiSettings.ChatMode.ASK, settings.chatMode)
        
        // Toggle to EDIT mode
        settings.chatMode = GeminiSettings.ChatMode.EDIT
        assertEquals(GeminiSettings.ChatMode.EDIT, settings.chatMode)
        
        // Toggle back to ASK mode
        settings.chatMode = GeminiSettings.ChatMode.ASK
        assertEquals(GeminiSettings.ChatMode.ASK, settings.chatMode)
    }

    fun testAnalysisScopeToggle() {
        val settings = service<GeminiSettings>()
        
        // Start with FILE scope
        settings.analysisScope = GeminiSettings.AnalysisScope.FILE
        assertEquals(GeminiSettings.AnalysisScope.FILE, settings.analysisScope)
        
        // Toggle to CODEBASE scope
        settings.analysisScope = GeminiSettings.AnalysisScope.CODEBASE
        assertEquals(GeminiSettings.AnalysisScope.CODEBASE, settings.analysisScope)
        
        // Toggle back to FILE scope
        settings.analysisScope = GeminiSettings.AnalysisScope.FILE
        assertEquals(GeminiSettings.AnalysisScope.FILE, settings.analysisScope)
    }
}
