package org.jetbrains.plugins.template.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.wm.ToolWindowManager
import org.jetbrains.plugins.template.settings.GeminiSettings

class EditWithGeminiAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        
        // Get selected text
        val selectedText = editor.selectionModel.selectedText
        
        // Set to Edit mode and File scope
        val settings = service<GeminiSettings>()
        settings.chatMode = GeminiSettings.ChatMode.EDIT
        settings.analysisScope = GeminiSettings.AnalysisScope.FILE
        
        // Open Gemini tool window
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Gemini AI")
        toolWindow?.show()
        
        // The chat panel will pick up the context automatically
    }

    override fun update(e: AnActionEvent) {
        // Enable action only when editor is available
        e.presentation.isEnabled = e.getData(CommonDataKeys.EDITOR) != null
    }
}
