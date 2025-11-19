# Gemini AI Agent Architecture

## Overview

The Gemini AI Agent plugin is built using the IntelliJ Platform SDK and integrates with Google's Gemini AI API to provide intelligent code assistance.

## Component Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     IntelliJ IDEA IDE                            │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │            Tool Window (Right Sidebar)                   │   │
│  │  ┌───────────────────────────────────────────────────┐  │   │
│  │  │  GeminiChatPanel                                   │  │   │
│  │  │  ┌──────────────┬──────────────┐                  │  │   │
│  │  │  │ Mode: [Ask▼] │ Scope: [File▼]│                 │  │   │
│  │  │  └──────────────┴──────────────┘                  │  │   │
│  │  │  ┌───────────────────────────────────────────┐    │  │   │
│  │  │  │  Chat Display Area                        │    │  │   │
│  │  │  │  You: How does this work?                 │    │  │   │
│  │  │  │  Gemini: This function...                 │    │  │   │
│  │  │  └───────────────────────────────────────────┘    │  │   │
│  │  │  ┌───────────────────────────────────────────┐    │  │   │
│  │  │  │  Input Area                               │    │  │   │
│  │  │  └───────────────────────────────────────────┘    │  │   │
│  │  │  [Send]  [Clear]                                  │  │   │
│  │  └───────────────────────────────────────────────────┘  │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │            Editor Context Menu                           │   │
│  │  - Cut                                                   │   │
│  │  - Copy                                                  │   │
│  │  - Paste                                                 │   │
│  │  ...                                                     │   │
│  │  - Ask Gemini          ← New Action                     │   │
│  │  - Edit with Gemini    ← New Action                     │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │            Settings (Tools > Gemini AI Agent)            │   │
│  │  Gemini API Key: [****************]                      │   │
│  │  [OK]  [Cancel]  [Apply]                                │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                   │
└───────────────────────────────┬─────────────────────────────────┘
                                │
                                │ Uses
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Plugin Components                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌──────────────────┐      ┌──────────────────┐                │
│  │  GeminiService   │      │  GeminiSettings  │                │
│  │  (Project-level) │      │  (App-level)     │                │
│  │                  │      │                  │                │
│  │ - setApiKey()    │      │ - apiKey         │                │
│  │ - sendPrompt()   │◄─────┤ - chatMode       │                │
│  │ - isConfigured() │      │ - analysisScope  │                │
│  └────────┬─────────┘      └──────────────────┘                │
│           │                                                      │
│           │ Communicates                                         │
│           ▼                                                      │
│  ┌─────────────────────────────────┐                           │
│  │  Google Gemini API               │                           │
│  │  - GenerativeModel               │                           │
│  │  - GenerateContentResponse       │                           │
│  └─────────────────────────────────┘                           │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

## Component Details

### UI Layer

#### GeminiChatPanel
- Main chat interface component
- Handles user input and displays responses
- Manages mode and scope selection
- Extracts context from editor

#### GeminiToolWindowFactory
- Creates and registers the tool window
- Integrates the chat panel into IDE

#### Actions
- **AskGeminiAction**: Quick access to Ask mode from editor
- **EditWithGeminiAction**: Quick access to Edit mode from editor

### Service Layer

#### GeminiService
- **Scope**: Project-level service
- **Responsibilities**:
  - Manage Gemini API client
  - Send prompts and receive responses
  - Handle API errors
  - Async/sync communication

#### GeminiSettings
- **Scope**: Application-level service
- **Responsibilities**:
  - Store API key securely
  - Persist user preferences (mode, scope)
  - Provide settings UI

### Configuration Layer

#### GeminiSettingsConfigurable
- Settings UI panel
- API key input field
- Integration with IDE settings

## Data Flow

### Ask Mode Flow
```
User Input → Context Extraction → Prompt Building → Gemini API → Response Display
     ↓              ↓                    ↓               ↓              ↓
  "Explain      File/Codebase       "Context:...    API Call      Show answer
   this code"      Content          User: ..."     (async)      in chat area
```

### Edit Mode Flow
```
User Input → Context Extraction → Prompt Building → Gemini API → Response Display → Code Suggestions
     ↓              ↓                    ↓               ↓              ↓                  ↓
  "Add error   File/Codebase      "Context:...    API Call      Show changes    Format in code
   handling"      Content          User: ..."     (async)      in chat area    blocks (```)
```

## Context Extraction

### File-Level Context
1. Get current editor
2. Get current file name
3. Extract selected text OR full file content (max 2000 chars)
4. Build context string

### Codebase-Level Context
1. Get project name
2. Get project path
3. Build high-level context string
4. Note: Future enhancement could include deeper analysis

## Configuration

### Settings Storage
- Location: `~/.config/JetBrains/[IDE]/options/geminiSettings.xml`
- Format: XML
- Contents:
  - `apiKey`: String
  - `chatMode`: Enum (ASK/EDIT)
  - `analysisScope`: Enum (FILE/CODEBASE)

### Dependencies
- `com.google.ai.client.generativeai:generativeai:0.9.0`
- `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0`

## Extension Points

The plugin uses standard IntelliJ extension points:

1. **Tool Window**
   - Extension: `com.intellij.toolWindow`
   - ID: "Gemini AI"
   - Factory: GeminiToolWindowFactory

2. **Settings**
   - Extension: `com.intellij.applicationConfigurable`
   - Parent: "tools"
   - Configurable: GeminiSettingsConfigurable

3. **Actions**
   - Extension: `com.intellij.actions`
   - Group: EditorPopupMenu
   - Actions: AskGeminiAction, EditWithGeminiAction

4. **Services**
   - Application Service: GeminiSettings
   - Project Service: GeminiService

## Security Considerations

1. **API Key Storage**: Stored in IntelliJ's settings, not in source code
2. **Context Limits**: File content truncated to 2000 chars to avoid excessive API usage
3. **Error Handling**: All API calls wrapped in try-catch blocks
4. **Async Processing**: UI remains responsive during API calls

## Future Enhancements

Potential areas for improvement:

1. **Enhanced Codebase Analysis**
   - Parse project structure
   - Include dependency information
   - Analyze multiple related files

2. **Code Application**
   - Direct code modification from Edit mode
   - Diff view for suggested changes
   - Undo/redo support

3. **History**
   - Save chat history
   - Search previous conversations
   - Export conversations

4. **Advanced Features**
   - Multiple conversation threads
   - Custom prompts/templates
   - Team collaboration features

5. **Model Selection**
   - Support for different Gemini models
   - Model-specific configurations
   - Performance tuning options
