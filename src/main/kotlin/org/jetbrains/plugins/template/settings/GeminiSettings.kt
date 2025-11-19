package org.jetbrains.plugins.template.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

/**
 * Persistent settings for the Gemini AI Agent plugin.
 * 
 * Stores:
 * - API key for Google Gemini
 * - Current chat mode (Ask or Edit)
 * - Current analysis scope (File or Codebase)
 */
@State(
    name = "GeminiSettings",
    storages = [Storage("geminiSettings.xml")]
)
@Service(Service.Level.APP)
class GeminiSettings : PersistentStateComponent<GeminiSettings> {
    
    var apiKey: String = ""
    var chatMode: ChatMode = ChatMode.ASK
    var analysisScope: AnalysisScope = AnalysisScope.FILE

    override fun getState(): GeminiSettings = this

    override fun loadState(state: GeminiSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    /**
     * Chat mode determines how Gemini responds.
     */
    enum class ChatMode {
        /** Mode for editing/modifying code with suggestions */
        EDIT,
        /** Mode for asking questions without code modifications */
        ASK
    }

    /**
     * Analysis scope determines what context is provided to Gemini.
     */
    enum class AnalysisScope {
        /** Analyze only the current file */
        FILE,
        /** Analyze the entire codebase/project */
        CODEBASE
    }
}
