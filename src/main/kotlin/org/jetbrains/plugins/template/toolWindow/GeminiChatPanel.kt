package org.jetbrains.plugins.template.toolWindow

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.ui.components.*
import com.intellij.util.ui.JBUI
import org.jetbrains.plugins.template.services.GeminiService
import org.jetbrains.plugins.template.settings.GeminiSettings
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.*

class GeminiChatPanel(private val project: Project) : JPanel(BorderLayout()) {
    
    private val geminiService = project.service<GeminiService>()
    private val settings = service<GeminiSettings>()
    
    private val chatArea = JTextArea()
    private val inputArea = JTextArea(3, 40)
    private val sendButton = JButton("Send")
    private val clearButton = JButton("Clear")
    
    // Mode selection
    private val modeComboBox = JComboBox(arrayOf("Ask Mode", "Edit Mode"))
    private val scopeComboBox = JComboBox(arrayOf("File Level", "Codebase Level"))
    
    init {
        setupUI()
        loadSettings()
        
        // Show welcome message
        appendToChat("System", "Welcome to Gemini AI Agent! Select a mode and scope, then start chatting.")
    }
    
    private fun setupUI() {
        // Top panel with mode selection
        val topPanel = JPanel(GridBagLayout()).apply {
            border = JBUI.Borders.empty(5)
            val gbc = GridBagConstraints()
            
            gbc.gridx = 0
            gbc.gridy = 0
            gbc.anchor = GridBagConstraints.WEST
            gbc.insets = JBUI.insets(0, 0, 0, 5)
            add(JBLabel("Mode:"), gbc)
            
            gbc.gridx = 1
            gbc.weightx = 1.0
            gbc.fill = GridBagConstraints.HORIZONTAL
            add(modeComboBox, gbc)
            
            gbc.gridx = 2
            gbc.weightx = 0.0
            gbc.fill = GridBagConstraints.NONE
            gbc.insets = JBUI.insets(0, 10, 0, 5)
            add(JBLabel("Scope:"), gbc)
            
            gbc.gridx = 3
            gbc.weightx = 1.0
            gbc.fill = GridBagConstraints.HORIZONTAL
            gbc.insets = JBUI.insets(0, 0, 0, 0)
            add(scopeComboBox, gbc)
        }
        
        // Chat display area
        chatArea.isEditable = false
        chatArea.lineWrap = true
        chatArea.wrapStyleWord = true
        val chatScrollPane = JBScrollPane(chatArea)
        
        // Input area
        inputArea.lineWrap = true
        inputArea.wrapStyleWord = true
        val inputScrollPane = JBScrollPane(inputArea)
        
        // Bottom panel with buttons
        val buttonPanel = JPanel().apply {
            add(sendButton)
            add(clearButton)
        }
        
        // Input panel (input + buttons)
        val inputPanel = JPanel(BorderLayout()).apply {
            add(inputScrollPane, BorderLayout.CENTER)
            add(buttonPanel, BorderLayout.SOUTH)
        }
        
        // Add components
        add(topPanel, BorderLayout.NORTH)
        add(chatScrollPane, BorderLayout.CENTER)
        add(inputPanel, BorderLayout.SOUTH)
        
        // Setup listeners
        setupListeners()
    }
    
    private fun setupListeners() {
        sendButton.addActionListener { sendMessage() }
        clearButton.addActionListener { clearChat() }
        
        modeComboBox.addActionListener {
            settings.chatMode = if (modeComboBox.selectedIndex == 0) {
                GeminiSettings.ChatMode.ASK
            } else {
                GeminiSettings.ChatMode.EDIT
            }
        }
        
        scopeComboBox.addActionListener {
            settings.analysisScope = if (scopeComboBox.selectedIndex == 0) {
                GeminiSettings.AnalysisScope.FILE
            } else {
                GeminiSettings.AnalysisScope.CODEBASE
            }
        }
        
        // Send on Ctrl+Enter
        inputArea.inputMap.put(
            KeyStroke.getKeyStroke("control ENTER"),
            "send"
        )
        inputArea.actionMap.put("send", object : AbstractAction() {
            override fun actionPerformed(e: java.awt.event.ActionEvent) {
                sendMessage()
            }
        })
    }
    
    private fun loadSettings() {
        modeComboBox.selectedIndex = if (settings.chatMode == GeminiSettings.ChatMode.ASK) 0 else 1
        scopeComboBox.selectedIndex = if (settings.analysisScope == GeminiSettings.AnalysisScope.FILE) 0 else 1
        
        // Initialize Gemini service with API key
        geminiService.setApiKey(settings.apiKey)
    }
    
    private fun sendMessage() {
        val userMessage = inputArea.text.trim()
        if (userMessage.isEmpty()) return
        
        // Check if API key is configured
        if (!geminiService.isConfigured()) {
            appendToChat("System", "Please configure your Gemini API key in Settings > Gemini AI Agent")
            return
        }
        
        appendToChat("You", userMessage)
        inputArea.text = ""
        
        // Disable send button while processing
        sendButton.isEnabled = false
        
        // Get context based on scope
        val context = getContext()
        
        // Build prompt based on mode
        val fullPrompt = buildPrompt(userMessage, context)
        
        // Send to Gemini in background thread
        ApplicationManager.getApplication().executeOnPooledThread {
            val response = geminiService.sendPromptSync(fullPrompt)
            
            SwingUtilities.invokeLater {
                appendToChat("Gemini", response)
                
                // If in Edit mode, offer to apply changes
                if (settings.chatMode == GeminiSettings.ChatMode.EDIT && response.contains("```")) {
                    appendToChat("System", "Code suggestions received. Review the changes above.")
                }
                
                sendButton.isEnabled = true
            }
        }
    }
    
    private fun getContext(): String {
        return when (settings.analysisScope) {
            GeminiSettings.AnalysisScope.FILE -> getFileContext()
            GeminiSettings.AnalysisScope.CODEBASE -> getCodebaseContext()
        }
    }
    
    private fun getFileContext(): String {
        val editor = FileEditorManager.getInstance(project).selectedTextEditor
        val virtualFile = editor?.let { FileEditorManager.getInstance(project).selectedFiles.firstOrNull() }
        
        return if (virtualFile != null && editor != null) {
            val document = editor.document
            val fileName = virtualFile.name
            val fileContent = document.text
            val selectedText = editor.selectionModel.selectedText
            
            buildString {
                appendLine("Current File: $fileName")
                if (selectedText != null) {
                    appendLine("Selected Code:")
                    appendLine(selectedText)
                } else {
                    appendLine("File Content:")
                    appendLine(fileContent.take(2000)) // Limit context size
                    if (fileContent.length > 2000) {
                        appendLine("... (truncated)")
                    }
                }
            }
        } else {
            "No file is currently open."
        }
    }
    
    private fun getCodebaseContext(): String {
        // Get basic project structure
        val projectName = project.name
        val projectPath = project.basePath ?: ""
        
        return buildString {
            appendLine("Project: $projectName")
            appendLine("Path: $projectPath")
            appendLine("(Codebase-level analysis - consider the entire project structure)")
        }
    }
    
    private fun buildPrompt(userMessage: String, context: String): String {
        return when (settings.chatMode) {
            GeminiSettings.ChatMode.ASK -> {
                """
                Context:
                $context
                
                User Question:
                $userMessage
                
                Please provide a helpful answer based on the context provided.
                """.trimIndent()
            }
            GeminiSettings.ChatMode.EDIT -> {
                """
                Context:
                $context
                
                User Request:
                $userMessage
                
                Please provide code modifications or suggestions. Format your code suggestions using markdown code blocks (```).
                Be specific about what changes should be made and why.
                """.trimIndent()
            }
        }
    }
    
    private fun appendToChat(sender: String, message: String) {
        chatArea.append("$sender: $message\n\n")
        chatArea.caretPosition = chatArea.document.length
    }
    
    private fun clearChat() {
        chatArea.text = ""
    }
}
