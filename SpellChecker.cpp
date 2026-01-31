#include "SpellChecker.h"
#include <fstream>
using namespace std;


// ================= TRIE NODE ===============

SpellChecker::TrieNode::TrieNode() : isEndOfWord(false) {
    for (int i = 0; i < 26; i++)
        children[i] = nullptr;
}

SpellChecker::TrieNode::~TrieNode() {
    for (int i = 0; i < 26; i++)
        delete children[i];
}


// ================= STRING ARRAY =============

SpellChecker::StringArray::StringArray() : size(0), capacity(10) {
    data = new string[capacity];
}

SpellChecker::StringArray::~StringArray() {
    delete[] data;
}

void SpellChecker::StringArray::pushBack(const string& value) {
    if (size == capacity) {
        capacity *= 2;
        string* newData = new string[capacity];  //A new larger array is allocated on the heap

        for (int i = 0; i < size; i++) // All existing elements are copied into the new array
            newData[i] = data[i];

        delete[] data;
        data = newData;  //data now points to the resized array
    }
    data[size++] = value; // insert element - increase element count 
}


// ======================= CONSTRUCTOR/DESTRUCTOR =================

SpellChecker::SpellChecker() : wordCount(0) {
    root = new TrieNode();
}

SpellChecker::~SpellChecker() {
    delete root;
}


// ================ STATIC HELPERS ==================

int SpellChecker::charToIndex(char c) {  // 'a'->0, 'b'->1, ..., 'z'->25
    if (c >= 'A' && c <= 'Z')
        return c - 'A';
    if (c >= 'a' && c <= 'z')
        return c - 'a';

    return -1; //if invalid
}

string SpellChecker::toLower(const string& s) {
    string result;
    for (char c : s) {
        if (c >= 'A' && c <= 'Z') {
            result += c + ('a' - 'A');
        }
        else {
            result += c;
        }
    }
    return result;
}

bool SpellChecker::isLetter(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
}


// ===================== DICTIONARY OPERATIONS ====================

void SpellChecker::addWord(const string& word) {
    string lowerWord = toLower(word);

    // Validate: word must contain only letters
    if (lowerWord.empty())
        return;

    for (char c : lowerWord) {
        if (!isLetter(c))
            return;
    }


    // Iterative insert
    TrieNode* node = root;
    for (char c : lowerWord) {
        int index = charToIndex(c);
        if (index < 0 || index >= 26)
            continue;

        if (node->children[index] == nullptr) {
            node->children[index] = new TrieNode();
        }
        node = node->children[index];
    }

    // Mark as end of word 
    if (!node->isEndOfWord) {
        node->isEndOfWord = true;
        wordCount++;
    }
}

bool SpellChecker::checkWord(const string& word) {
    string lowerWord = toLower(word);

    // Validate
    for (char c : lowerWord) {
        if (!isLetter(c)) 
            return false;
    }

    // Iterative search    
    TrieNode* node = root;
    for (char c : lowerWord) {
        int index = charToIndex(c);
        if (index < 0 || index >= 26 || node->children[index] == nullptr) {
            return false;
        }
        node = node->children[index];
    }

    return node->isEndOfWord;
}

// =========================== EDIT DISTANCE ==============================

int SpellChecker::levenshteinDistance(const string& s1, const string& s2,  
    int i, int j) {                     //i = length of prefix considered in s1
                                       //j = length of prefix considered in s2
    // Base cases
    if (i == 0) 
        return j; // s1 is empty, insert j characters
    if (j == 0)
        return i; // s2 is empty, delete i characters

    // If characters match
    if (s1[i - 1] == s2[j - 1]) {    //Compare the last characters of the current prefixes.
                                    //If they match -  No edit is needed

        return levenshteinDistance(s1, s2, i - 1, j - 1);   //Move one step back in both strings
    }

    // Consider insert, delete, replace
    int del = levenshteinDistance(s1, s2, i - 1, j);        //Delete last character of s1
    int ins = levenshteinDistance(s1, s2, i, j - 1);       //Insert a character into s1
    int rep = levenshteinDistance(s1, s2, i - 1, j - 1);  //Replace last character of s1 with last of s2

    int min = del;  //Select the cheapest operation among them
    if (ins < min)
        min = ins;

    if (rep < min)
        min = rep;

    return 1 + min;   // +1  Because we performed one edit operation at this step.
}

//Wrapper function
int SpellChecker::levenshtein(const string& s1, const string& s2) {
    return levenshteinDistance(s1, s2, s1.length(), s2.length());
}

// ====================== RECURSIVE WORD COLLECTION ====================

void SpellChecker::collectWords(TrieNode* node,
    const string& prefix,
    StringArray& results) {

    //Stop if node is null
    if (!node)
        return;

    //If this node marks a complete word: Add that word to results
    if (node->isEndOfWord)
        results.pushBack(prefix);   //prefix contains the letters accumulated from the root to this node.

    for (int i = 0; i < 26; i++) {
        if (node->children[i]) {           //Check if there is a child node for that letter
            collectWords(node->children[i],
                prefix + char('a' + i),
                results);
        }
    }
}

// ====================== SUGGESTIONS ====================
SpellChecker::StringArray
SpellChecker::getSuggestions(const string& word,
    int maxDistance,
    int maxResults) {

    StringArray allWords;      //all dictionary words (from Trie)
    StringArray suggestions;  // final output list

    string lower = toLower(word);

    // If word is correct, return it
    if (checkWord(lower)) {
        suggestions.pushBack(lower);

        return suggestions;
    }
    
    // Use collectWords to create a StringArray of all dictionary words.
    collectWords(root, "", allWords);

    //Dynamic array for distances
    int* distances = new int[allWords.size];   //Store Levenshtein distance for each candidate
    int count = 0;                          // tracks valid suggestions

    // Filter words by maxDistance
    for (int i = 0; i < allWords.size; i++) {

        int d = levenshtein(lower, allWords.data[i]);   //Compute edit distance between input and dictionary word

        if (d <= maxDistance) {                     //Only keep “close enough” words
            allWords.data[count] = allWords.data[i];
            distances[count] = d;   //Store their distances in parallel array
            count++;
        }
    }

    // Bubble sort by distance, then alphabetically
    for (int i = 0; i < count - 1; i++) {
        for (int j = i + 1; j < count; j++) {
            if (distances[j] < distances[i] ||
                (distances[j] == distances[i] &&
                    allWords.data[j] < allWords.data[i])) {

                // swap distances      //Compare edit distances only
               //If word j is closer to the input than word i, it should come first
                int tempDist = distances[i];
                distances[i] = distances[j];
                distances[j] = tempDist;

                // swap words
                string tempWord = allWords.data[i];
                allWords.data[i] = allWords.data[j];
                allWords.data[j] = tempWord;
            }
        }
    }

    // Take top maxResults
    int limit = (count < maxResults) ? count : maxResults;
    for (int i = 0; i < limit; i++) {
        suggestions.pushBack(allWords.data[i]);
    }

    delete[] distances;
    return suggestions;
}

// ========================= AUTO-COMPLETE ====================

SpellChecker::StringArray
SpellChecker::autoComplete(const string& prefix,
    int maxResults) {
    StringArray results;
    string lower = toLower(prefix);

    // Navigate to prefix node
    TrieNode* node = root;
    for (char c : lower) {
        int index = charToIndex(c);
        if (index < 0 || index >= 26 || node->children[index] == nullptr) {
            return results;  // No words with this prefix
        }
        node = node->children[index];
    }

    // Collect all words from this node
    collectWords(node, lower, results);

    // SORT ALPHABETICALLY (BUBBLE SORT) 
    for (int i = 0; i < results.size - 1; i++) {
        for (int j = i + 1; j < results.size; j++) {
            if (results.data[j] < results.data[i]) {
                string temp = results.data[i];
                results.data[i] = results.data[j];
                results.data[j] = temp;
            }
        }
    }

    // LIMIT RESULTS 
    if (results.size > maxResults)
        results.size = maxResults;

    return results;
}


// ====================== DICTIONARY MANAGEMENT ====================

bool SpellChecker::loadDictionary(const std::string& filename) {
    ifstream file(filename);

    if (!file.is_open())
        return false;


    string word;
    while (getline(file, word)) {
        // Clean word: remove non-letters
        string cleanWord;
        for (char c : word) {
            if (isLetter(c)) {
                cleanWord += c;
            }
        }
        if (!cleanWord.empty()) {
            addWord(cleanWord);
        }
    }

    file.close();
    return true;
}

int SpellChecker::getWordCount() const {
    return wordCount;
}