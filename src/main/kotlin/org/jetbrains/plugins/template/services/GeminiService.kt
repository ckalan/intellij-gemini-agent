package org.jetbrains.plugins.template.services

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.generationConfig
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import kotlinx.coroutines.runBlocking

@Service(Service.Level.PROJECT)
class GeminiService(private val project: Project) {

    companion object {
        private const val MODEL_NAME = "gemini-pro"
    }

    private var apiKey: String? = null
    private var model: GenerativeModel? = null

    init {
        thisLogger().info("GeminiService initialized for project: ${project.name}")
    }

    fun setApiKey(key: String) {
        apiKey = key
        model = if (key.isNotBlank()) {
            GenerativeModel(
                modelName = MODEL_NAME,
                apiKey = key,
                generationConfig = generationConfig {
                    temperature = 0.7f
                    topK = 40
                    topP = 0.95f
                    maxOutputTokens = 2048
                }
            )
        } else {
            null
        }
    }

    fun isConfigured(): Boolean = apiKey?.isNotBlank() == true

    suspend fun sendPrompt(prompt: String): String {
        val currentModel = model ?: return "Error: API key not configured"
        
        return try {
            val response = currentModel.generateContent(prompt)
            response.text ?: "No response from Gemini"
        } catch (e: Exception) {
            thisLogger().error("Error calling Gemini API", e)
            "Error: ${e.message}"
        }
    }

    fun sendPromptSync(prompt: String): String = runBlocking {
        sendPrompt(prompt)
    }
}
