package org.jetbrains.plugins.template.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

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

    enum class ChatMode {
        EDIT,  // Mode for editing/modifying code
        ASK    // Mode for asking questions without modifications
    }

    enum class AnalysisScope {
        FILE,      // File-level analysis
        CODEBASE   // Codebase-level analysis
    }
}
