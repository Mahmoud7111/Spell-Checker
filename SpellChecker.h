#pragma once

#include <string>
using namespace std;

class SpellChecker {
public:
    struct StringArray {  //Temporarily hold a list of results that we can return or display (suggestions / auto-complete / ...)
        string* data;
        int size;
        int capacity;

        StringArray();
        ~StringArray();
        void pushBack(const string& value);
    };

private:

      struct TrieNode {
          TrieNode* children[26]; 
          bool isEndOfWord;

          TrieNode();
         ~TrieNode();
      };

      

      TrieNode* root;
      int wordCount;

      // Helpers
      static int charToIndex(char c);   //// 'a'->0, 'b'->1, ..., 'z'->25
      static string toLower(const string& s);
      static bool isLetter(char c);

      // Levenshtein  for word Suggestion  // = minimum number of edits (insert, delete, replace) to turn s1 into s2.
      static int levenshteinDistance(const string& s1,
          const string& s2,
          int i, int j);
      static int levenshtein(const string& s1,
          const string& s2);

      //Key idea: Traverse the Trie and  converts the Trie structure into a list of actual words.
      void collectWords(TrieNode* node,
          const string& prefix,
          StringArray& results);

public:
    SpellChecker();
    ~SpellChecker();

    void addWord(const string& word);
    bool checkWord(const string& word);

   
    StringArray getSuggestions(const string& word,
        int maxDistance = 2,
        int maxResults = 10);

    // completes a word
    StringArray autoComplete(const string& prefix,
        int maxResults = 10);



    bool loadDictionary(const string& filename);
    int getWordCount() const;
};


