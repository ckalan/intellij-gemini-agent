package org.jetbrains.plugins.template.services

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.generationConfig
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import kotlinx.coroutines.runBlocking

/**
 * Service for communicating with Google Gemini AI API.
 * 
 * This service handles:
 * - API key configuration
 * - Async communication with Gemini
 * - Generation configuration (temperature, tokens, etc.)
 * 
 * @property project The IntelliJ project instance
 */
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

    /**
     * Configure the Gemini API key and initialize the model.
     * 
     * @param key The API key for Google Gemini API
     */
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

    /**
     * Check if the service is properly configured with an API key.
     * 
     * @return true if API key is set and not blank
     */
    fun isConfigured(): Boolean = apiKey?.isNotBlank() == true

    /**
     * Send a prompt to Gemini AI and get a response asynchronously.
     * 
     * @param prompt The text prompt to send to Gemini
     * @return The response text from Gemini, or an error message
     */
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

    /**
     * Send a prompt to Gemini AI synchronously (blocking call).
     * 
     * @param prompt The text prompt to send to Gemini
     * @return The response text from Gemini, or an error message
     */
    fun sendPromptSync(prompt: String): String = runBlocking {
        sendPrompt(prompt)
    }
}
