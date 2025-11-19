# Gemini AI Agent for IntelliJ

An IntelliJ IDEA plugin that integrates Google Gemini AI to provide intelligent code assistance with two distinct modes of operation.

## Features

### Two Interaction Modes

1. **Ask Mode** - Question and Answer
   - Ask questions about your code
   - Get explanations and insights
   - Non-intrusive - no code modifications

2. **Edit Mode** - Code Modification
   - Request code changes and improvements
   - Get code suggestions with explanations
   - Review and apply changes as needed

### Two Analysis Scopes

1. **File Level** - Focused Analysis
   - Analyzes the currently open file
   - Considers selected code if any
   - Faster and more targeted responses

2. **Codebase Level** - Broad Analysis
   - Considers the entire project context
   - Better for architectural questions
   - More comprehensive understanding

## Installation

1. Build the plugin:
   ```bash
   ./gradlew buildPlugin
   ```

2. Install from disk in IntelliJ IDEA:
   - Go to `Settings > Plugins > ⚙️ > Install Plugin from Disk...`
   - Select the built plugin ZIP file from `build/distributions/`

## Configuration

1. Obtain a Gemini API key from [Google AI Studio](https://makersuite.google.com/app/apikey)

2. Configure the plugin:
   - Go to `Settings > Tools > Gemini AI Agent`
   - Enter your API key
   - Click OK to save

## Usage

### Opening the Tool Window

- Click on "Gemini AI" in the right sidebar, or
- Go to `View > Tool Windows > Gemini AI`

### Interacting with Gemini

1. **Select Mode**:
   - Choose "Ask Mode" for questions
   - Choose "Edit Mode" for code modifications

2. **Select Scope**:
   - Choose "File Level" for current file analysis
   - Choose "Codebase Level" for project-wide context

3. **Enter your prompt** in the input area at the bottom

4. **Send** your message:
   - Click the "Send" button, or
   - Press `Ctrl+Enter`

### Example Prompts

**Ask Mode (File Level)**:
- "What does this function do?"
- "Explain this code block"
- "What are the potential issues with this implementation?"

**Ask Mode (Codebase Level)**:
- "What is the overall architecture of this project?"
- "How are services organized in this application?"
- "What design patterns are used here?"

**Edit Mode (File Level)**:
- "Add error handling to this function"
- "Refactor this code to be more efficient"
- "Add documentation to this class"

**Edit Mode (Codebase Level)**:
- "Suggest improvements to the project structure"
- "How can I make this codebase more maintainable?"
- "Add unit tests for the main components"

## Technical Details

### Dependencies

- Google Generative AI SDK for Kotlin
- Kotlin Coroutines for async operations
- IntelliJ Platform SDK

### Architecture

- `GeminiService`: Handles API communication with Google Gemini
- `GeminiSettings`: Persists user settings (API key, mode, scope)
- `GeminiChatPanel`: Main UI for chat interaction
- `GeminiToolWindowFactory`: Creates the tool window

## Privacy & Security

- API key is stored securely in IntelliJ's credential store
- Code context is sent to Google Gemini API for processing
- No data is stored or logged beyond the chat session
- Review the code before applying any Edit mode suggestions

## License

See LICENSE file for details.

## Support

For issues and feature requests, please visit the [GitHub repository](https://github.com/ckalan/intellij-gemini-agent).
