package org.jetbrains.plugins.template.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPasswordField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class GeminiSettingsConfigurable : Configurable {
    
    private var settingsPanel: JPanel? = null
    private val apiKeyField = JBPasswordField()

    override fun createComponent(): JComponent {
        settingsPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Gemini API Key:"), apiKeyField, 1, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
        
        return settingsPanel!!
    }

    override fun isModified(): Boolean {
        val settings = service<GeminiSettings>()
        return String(apiKeyField.password) != settings.apiKey
    }

    override fun apply() {
        val settings = service<GeminiSettings>()
        settings.apiKey = String(apiKeyField.password)
    }

    override fun reset() {
        val settings = service<GeminiSettings>()
        apiKeyField.text = settings.apiKey
    }

    override fun getDisplayName(): String = "Gemini AI Agent"

    override fun disposeUIResources() {
        settingsPanel = null
    }
}
