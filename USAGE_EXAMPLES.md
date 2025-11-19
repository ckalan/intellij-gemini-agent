# Usage Examples

## Quick Start

### 1. Configuration

First, obtain a Gemini API key from [Google AI Studio](https://makersuite.google.com/app/apikey).

Then configure it in IntelliJ:
1. Go to `Settings` (or `Preferences` on macOS)
2. Navigate to `Tools > Gemini AI Agent`
3. Enter your API key
4. Click `OK`

### 2. Open the Tool Window

- Click on "Gemini AI" in the right sidebar, OR
- Go to `View > Tool Windows > Gemini AI`

## Example Use Cases

### Example 1: Explaining Code (Ask Mode, File Level)

**Scenario**: You're reviewing a complex function and want to understand it better.

**Steps**:
1. Open the file with the function
2. Select the function code (optional, but helps focus)
3. Open Gemini AI tool window
4. Select **"Ask Mode"** from the mode dropdown
5. Select **"File Level"** from the scope dropdown
6. Type your question: "What does this function do?"
7. Click Send or press Ctrl+Enter

**Example Prompt**:
```
What does this function do?
```

**Expected Response**:
```
Gemini: This function calculates the factorial of a number using recursion. 
It takes an integer n as input and returns n! (n factorial). The base case 
is when n equals 0 or 1, returning 1. Otherwise, it recursively calls itself 
with n-1 and multiplies the result by n.
```

### Example 2: Code Improvement Suggestions (Edit Mode, File Level)

**Scenario**: You want to improve error handling in your code.

**Steps**:
1. Open the file you want to improve
2. Optionally select the relevant code section
3. Open Gemini AI tool window
4. Select **"Edit Mode"** from the mode dropdown
5. Select **"File Level"** from the scope dropdown
6. Type your request: "Add comprehensive error handling to this function"
7. Click Send

**Example Prompt**:
```
Add comprehensive error handling to this function
```

**Expected Response**:
```
Gemini: Here are the suggested improvements with error handling:

\`\`\`kotlin
fun calculateFactorial(n: Int): Result<Long> {
    return try {
        when {
            n < 0 -> Result.failure(IllegalArgumentException("Negative numbers not supported"))
            n > 20 -> Result.failure(ArithmeticException("Result would overflow Long"))
            n == 0 || n == 1 -> Result.success(1L)
            else -> {
                val result = calculateFactorial(n - 1).getOrThrow() * n
                Result.success(result)
            }
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
\`\`\`

Changes made:
1. Added validation for negative numbers
2. Added overflow protection for numbers > 20
3. Wrapped result in Result type for better error handling
4. Added try-catch block for unexpected errors
```

### Example 3: Architecture Questions (Ask Mode, Codebase Level)

**Scenario**: You want to understand the overall project structure.

**Steps**:
1. Open Gemini AI tool window
2. Select **"Ask Mode"** from the mode dropdown
3. Select **"Codebase Level"** from the scope dropdown
4. Type your question: "What is the overall architecture of this project?"
5. Click Send

**Example Prompt**:
```
What is the overall architecture of this project?
```

**Expected Response**:
```
Gemini: Based on the project structure, this appears to be an IntelliJ Platform 
plugin project following a service-oriented architecture:

1. **UI Layer**: Tool windows and actions for user interaction
2. **Service Layer**: Business logic in project and application services
3. **Settings Layer**: Persistent configuration management

The plugin uses:
- Kotlin as the primary language
- Gradle for build management
- IntelliJ Platform SDK for IDE integration
- Google Generative AI SDK for AI capabilities

The architecture follows IntelliJ's recommended patterns with proper separation 
of concerns between UI, business logic, and persistence.
```

### Example 4: Refactoring Suggestions (Edit Mode, File Level)

**Scenario**: You want to refactor code to follow better practices.

**Steps**:
1. Open the file to refactor
2. Select the code section
3. Open Gemini AI tool window (or right-click and select "Edit with Gemini")
4. It will automatically switch to **"Edit Mode"** and **"File Level"**
5. Type: "Refactor this to follow SOLID principles"
6. Click Send

**Example Prompt**:
```
Refactor this to follow SOLID principles
```

**Expected Response**:
```
Gemini: Here's a refactored version following SOLID principles:

\`\`\`kotlin
// Single Responsibility: Separate validation
interface Validator {
    fun validate(value: Int): ValidationResult
}

class FactorialValidator : Validator {
    override fun validate(value: Int): ValidationResult {
        return when {
            value < 0 -> ValidationResult.Error("Negative not allowed")
            value > 20 -> ValidationResult.Error("Too large")
            else -> ValidationResult.Success
        }
    }
}

// Single Responsibility: Calculation logic
interface Calculator {
    fun calculate(n: Int): Long
}

class FactorialCalculator : Calculator {
    override fun calculate(n: Int): Long {
        return if (n <= 1) 1L else n * calculate(n - 1)
    }
}

// Open/Closed: Easy to extend with new validators or calculators
// Liskov Substitution: Implementations are substitutable
// Interface Segregation: Small, focused interfaces
// Dependency Inversion: Depends on abstractions, not concretions
\`\`\`

This refactoring improves:
- Testability (can mock validators/calculators)
- Maintainability (clear separation of concerns)
- Extensibility (easy to add new validation rules or calculation methods)
```

### Example 5: Quick Question via Context Menu (Ask Mode)

**Scenario**: Quick question about selected code without switching to tool window.

**Steps**:
1. Select code in the editor
2. Right-click to open context menu
3. Click **"Ask Gemini"**
4. Tool window opens automatically in Ask Mode with File Level scope
5. Type your question
6. Get instant answer

### Example 6: Documentation Generation (Edit Mode, File Level)

**Example Prompt**:
```
Add comprehensive KDoc documentation to this class
```

**Expected Response**:
```
Gemini: Here's your class with KDoc documentation:

\`\`\`kotlin
/**
 * Service for managing user authentication and authorization.
 * 
 * This service handles:
 * - User login/logout operations
 * - Session management
 * - Permission checking
 * 
 * @property userRepository Repository for user data access
 * @property sessionManager Manager for user sessions
 */
class AuthService(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) {
    /**
     * Authenticates a user with provided credentials.
     * 
     * @param username The username to authenticate
     * @param password The user's password
     * @return Result containing authenticated User or error
     * @throws IllegalArgumentException if username or password is blank
     */
    fun login(username: String, password: String): Result<User> {
        // Implementation...
    }
}
\`\`\`
```

### Example 7: Bug Detection (Ask Mode, File Level)

**Example Prompt**:
```
Are there any potential bugs or issues in this code?
```

**Expected Response**:
```
Gemini: I've identified several potential issues:

1. **Null Pointer Risk**: Line 15 - `user.name` could throw NPE if user is null
2. **Resource Leak**: Line 23 - FileInputStream not closed in finally block
3. **Race Condition**: Line 45 - Shared mutable state without synchronization
4. **Performance**: Line 67 - Using string concatenation in a loop

Recommendations:
1. Add null checks or use safe call operator (?.)
2. Use try-with-resources or .use {} for automatic resource management
3. Add @Synchronized annotation or use locks for shared state
4. Use StringBuilder for string concatenation in loops
```

## Tips for Better Results

### 1. Be Specific
❌ "Fix this"
✅ "Add null safety checks and improve error handling"

### 2. Provide Context
❌ "What does this do?"
✅ "What does this authentication function do and how does it handle errors?"

### 3. Use File Level for Focused Questions
- When asking about a specific function or class
- When requesting changes to current file
- For code reviews of current file

### 4. Use Codebase Level for Architecture
- When asking about overall structure
- When discussing design patterns
- For cross-file dependencies understanding

### 5. Iterate on Responses
- If response isn't clear, ask follow-up questions
- Request specific aspects: "Can you explain the error handling part?"
- Ask for alternatives: "Are there other approaches to this?"

## Keyboard Shortcuts

- **Send Message**: `Ctrl+Enter` (while in input area)
- **Open Tool Window**: Configure in `Settings > Keymap > Tool Windows > Gemini AI`

## Common Workflows

### Code Review Workflow
1. Open file to review
2. Ask Mode + File Level
3. Ask: "Review this code for potential issues"
4. Read response
5. Switch to Edit Mode
6. Ask: "Fix the issues you identified"

### Learning Workflow
1. Open unfamiliar code
2. Ask Mode + File Level
3. Ask: "Explain this code step by step"
4. Ask follow-ups for clarification
5. Ask: "What are the best practices for this pattern?"

### Refactoring Workflow
1. Select code to refactor
2. Edit Mode + File Level
3. Ask: "Refactor this following [specific pattern]"
4. Review suggestions
5. Ask for alternatives if needed
6. Manually apply changes

## Troubleshooting

### "Error: API key not configured"
- Go to Settings > Tools > Gemini AI Agent
- Enter your API key
- Click OK

### Empty or unhelpful responses
- Be more specific in your question
- Provide more context
- Try rephrasing the question

### Slow responses
- Check your internet connection
- Consider using File Level instead of Codebase Level
- Gemini API might be experiencing high load

### Context not including selected code
- Make sure code is actually selected in editor
- Try clicking in the editor after selection
- Reopen the tool window
