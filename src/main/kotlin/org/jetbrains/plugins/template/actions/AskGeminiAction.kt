package org.jetbrains.plugins.template.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.wm.ToolWindowManager
import org.jetbrains.plugins.template.settings.GeminiSettings

class AskGeminiAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        
        // Get selected text or current line
        val selectedText = editor.selectionModel.selectedText
        
        // Set to Ask mode and File scope
        val settings = service<GeminiSettings>()
        settings.chatMode = GeminiSettings.ChatMode.ASK
        settings.analysisScope = GeminiSettings.AnalysisScope.FILE
        
        // Open Gemini tool window
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Gemini AI")
        toolWindow?.show()
        
        // Pre-fill with selected text if any
        if (selectedText != null) {
            // The chat panel will pick up the context automatically
        }
    }

    override fun update(e: AnActionEvent) {
        // Enable action only when editor is available
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }
}
