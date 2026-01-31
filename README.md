# Spell Checker

A desktop spell checker application built with C++ and Qt, featuring intelligent word suggestions and auto-completion using a Trie data structure.

![C++](https://img.shields.io/badge/C%2B%2B-00599C?style=flat&logo=c%2B%2B&logoColor=white)
![Qt](https://img.shields.io/badge/Qt-41CD52?style=flat&logo=qt&logoColor=white)

## Features

✨ **Spell Checking** - Verify if words are correctly spelled against a dictionary
- Real-time validation with visual feedback (green for correct, red for incorrect)
- Support for alphabetic characters only
- Input validation to ensure only letters are processed

🔍 **Smart Suggestions** - Get intelligent word recommendations using Levenshtein distance algorithm
- Provides up to 10 suggestions for misspelled words
- Suggestions sorted by edit distance and alphabetically
- Maximum edit distance of 2 for optimal relevance
- Click any suggestion to instantly check it

⚡ **Auto-Complete** - Predictive text completion as you type
- Displays up to 10 matching words based on your prefix
- Alphabetically sorted results
- Efficient prefix matching using Trie structure
- Interactive list - click any word to use it as the new prefix

## How It Works

### Trie Data Structure (Prefix Tree)

The application uses a **Trie** as its core data structure for efficient word storage and retrieval:

- **Structure**: Each node represents a single character, with 26 possible children (one for each letter a-z)
- **Word Storage**: Complete words are formed by following paths from the root to nodes marked as "end of word"
- **Efficiency**: Searching for a word takes O(m) time where m is the word length, regardless of dictionary size
- **Memory Efficient**: Common prefixes are shared, reducing redundant storage

**Why Trie?** Unlike a simple list or hash table, a Trie excels at prefix-based operations, making it perfect for auto-complete functionality.

### Levenshtein Distance Algorithm

For spelling suggestions, the application uses the **Levenshtein distance** (edit distance) algorithm:

- **What it measures**: The minimum number of single-character edits needed to transform one word into another
- **Edit operations**: Insertions, deletions, and substitutions
- **Implementation**: Recursive approach that explores all possible edit paths
- **Filtering**: Only words within 2 edits are considered for suggestions
- **Sorting**: Results are ordered by similarity (lower distance first) and then alphabetically

**Example**: "cat" → "bat" requires 1 substitution (distance = 1), while "cat" → "cats" requires 1 insertion (distance = 1)

### Core Operations Explained

#### Dictionary Loading
When the application starts, it reads `dictionary.txt` and inserts each word into the Trie. The process:
1. Converts each word to lowercase for case-insensitive matching
2. Validates that words contain only letters
3. Creates Trie nodes for each character in the word
4. Marks the final node as "end of word"

#### Word Checking
To verify a word's spelling:
1. Convert input to lowercase
2. Traverse the Trie character by character
3. If any character path doesn't exist, the word is not in the dictionary
4. If we reach the end and the node is marked as "end of word", it's correctly spelled

#### Generating Suggestions
When a word is misspelled:
1. Collect all words from the dictionary (by traversing the entire Trie)
2. Calculate Levenshtein distance between the input and each dictionary word
3. Keep only words within the maximum allowed distance (2 edits)
4. Sort by distance first, then alphabetically
5. Return the top 10 matches

#### Auto-Complete
For predictive text:
1. Navigate through the Trie following the input prefix
2. If the prefix doesn't exist, return empty results
3. From the prefix node, collect all complete words underneath it
4. Sort results alphabetically
5. Return up to 10 matches

### Custom Dynamic Array

The application implements a custom `StringArray` class that automatically grows as needed:
- **Initial capacity**: 10 strings
- **Growth strategy**: Doubles in size when full
- **Why custom?**: Avoids standard library dependencies and demonstrates manual memory management
- **Methods**: `pushBack()` for adding elements with automatic resizing

## Technologies Used

- **C++** - Core application logic, memory management, and algorithms
- **Qt Framework** - Cross-platform GUI development
  - `QMainWindow` - Main application window
  - `QLineEdit` - Text input fields
  - `QListWidget` - Displaying suggestions and auto-complete results
  - `QPushButton` - Trigger actions
  - `QLabel` - Display status messages
  - `QGroupBox` - Organize related UI elements
  - `QStatusBar` - Show dictionary load status
- **Custom Data Structures** - Trie and dynamic array implementations

## Project Structure

```
Spell-Checker/
├��─ main.cpp              # Application entry point - creates Qt app and main window
├── MainWindow.h          # Main window class declaration
├── MainWindow.cpp        # GUI implementation, event handlers, user interaction logic
├── SpellChecker.h        # Spell checker class, Trie node, and StringArray declarations
├── SpellChecker.cpp      # Core algorithms: Trie operations, Levenshtein distance, suggestions
├── MainWindow.ui         # Qt UI design file (designer layout)
└── dictionary.txt        # Word dictionary - one word per line
```

### File Descriptions

**SpellChecker.cpp/h** - The brain of the application
- Trie node implementation and memory management
- Word insertion and lookup algorithms
- Recursive Levenshtein distance calculation
- Trie traversal for collecting words
- Dictionary loading from text file

**MainWindow.cpp/h** - The user interface
- Qt widget setup and layout management
- Signal-slot connections for button clicks
- Input validation (letters only)
- Display formatting (colors, styling)
- Interactive list item selection

**main.cpp** - Simple entry point
- Creates Qt application instance
- Instantiates and displays main window
- Starts the event loop

## Prerequisites

Before building this project, ensure you have:

- **C++ Compiler** - Supporting C++11 or later (GCC, Clang, MSVC)
- **Qt Framework** - Version 5.x or 6.x
  - Qt Core
  - Qt Widgets
  - Qt GUI
- **CMake** or **qmake** - For building the project
- **Dictionary file** - `dictionary.txt` in the same directory as the executable

## Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/Mahmoud7111/Spell-Checker.git
cd Spell-Checker
```

### 2. Install Qt
**Ubuntu/Debian:**
```bash
sudo apt-get install qt5-default qtbase5-dev
```

**macOS (using Homebrew):**
```bash
brew install qt
```

**Windows:**
Download and install from [Qt Official Website](https://www.qt.io/download)

### 3. Build the Project

**Using qmake:**
```bash
qmake
make
```

**Using Qt Creator:**
1. Open Qt Creator
2. File → Open File or Project
3. Select the project files
4. Configure build settings
5. Build → Build Project
6. Run

### 4. Prepare Dictionary
Ensure `dictionary.txt` is in the same directory as your executable. The file should contain one word per line:
```
apple
banana
cherry
dictionary
...
```

## Usage

### Launching the Application
Run the compiled executable:
```bash
./Spell-Checker
```

On launch, the status bar displays the number of words loaded from the dictionary.

### Spell Check Workflow
1. **Enter a word** in the "Spell Check" input field
2. **Click "Check Word"** or press Enter
3. **View the result:**
   - ✅ Green text: "The word is spelled correctly."
   - ❌ Red text: "The word is NOT in the dictionary."
4. **Review suggestions** in the list below (if word is incorrect)
5. **Click any suggestion** to auto-fill it and check again

### Auto-Complete Workflow
1. **Type a prefix** in the "Auto-complete" input field (e.g., "app")
2. **Click "Auto-complete"** button
3. **Browse results** - all words starting with your prefix appear
4. **Click any word** to use it as the new prefix and see more completions

### Input Validation
- Only alphabetic characters (A-Z, a-z) are accepted
- Numbers, symbols, and special characters trigger a warning dialog
- Empty input is rejected

## Performance Analysis

### Time Complexity
- **Dictionary Loading**: O(n × m) where n = number of words, m = average word length
- **Word Check**: O(m) - constant time relative to dictionary size
- **Auto-Complete**: O(k) where k = number of matching words
- **Suggestions**: O(n × m²) - needs optimization for very large dictionaries

### Space Complexity
- **Trie Storage**: O(ALPHABET_SIZE × n × m) = O(26 × n × m)
- Shared prefixes reduce actual memory usage significantly

### Optimization Opportunities
The Levenshtein distance implementation is currently recursive without memoization. For better performance with larger dictionaries, dynamic programming with a 2D table would reduce time complexity from exponential to O(m₁ × m₂).

## Future Enhancements

- [ ] **Dynamic Programming** - Optimize Levenshtein distance with memoization or iterative DP
- [ ] **Word Frequency** - Prioritize common words in suggestions using frequency data
- [ ] **Multi-language Support** - Load different dictionaries for various languages
- [ ] **Custom Dictionary Management** - GUI to add/remove words from the dictionary
- [ ] **File Spell Check** - Open and check entire text documents
- [ ] **Persistent Custom Words** - Save user-added words between sessions
- [ ] **Keyboard Shortcuts** - Enter key for checking, Ctrl+K for quick check
- [ ] **Dark Mode / Themes** - User-selectable color schemes
- [ ] **Phonetic Matching** - Suggest words that sound similar (Soundex algorithm)
- [ ] **Contextual Suggestions** - Use previous words to improve suggestions

## Technical Challenges Solved

### Memory Management
- Manual Trie node allocation and deallocation without memory leaks
- Recursive destructor properly frees all child nodes
- Dynamic array that grows without leaving orphaned memory

### Algorithm Implementation
- Recursive Levenshtein distance without standard library helpers
- Efficient Trie traversal for word collection
- Sorting without STL algorithms (bubble sort for small datasets)

### Qt Integration
- Signal-slot connections for responsive UI
- Lambda functions for inline event handling
- Proper widget lifecycle management within Qt's parent-child system

## Contributing

Contributions are welcome! If you'd like to improve this project:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Areas for Contribution
- Implementing dynamic programming for Levenshtein distance
- Adding unit tests
- Creating a CLI version alongside the GUI
- Improving UI/UX design
- Adding internationalization support

## License

This project is open source and available 

## Author

**Mahmoud7111**
- GitHub: [@Mahmoud7111](https://github.com/Mahmoud7111)

## Acknowledgments

- Qt Framework for providing excellent cross-platform GUI tools
- Trie data structure for efficient prefix-based operations
- Levenshtein distance algorithm for intelligent similarity matching
- Open-source community for inspiration and best practices

---

⭐ If you find this project helpful, please consider giving it a star!
