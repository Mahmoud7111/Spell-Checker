#include "MainWindow.h"
#include <QMessageBox>
#include <QBrush>
#include <QColor>

//constructor
MainWindow::MainWindow(QWidget* parent)
    : QMainWindow(parent)
{
    QWidget* central = new QWidget(this);  //central is the main container of the window //Everything goes inside it
    QVBoxLayout* mainLayout = new QVBoxLayout(central);
   
    //spacing and margins.
    mainLayout->setContentsMargins(10, 10, 10, 10);
    mainLayout->setSpacing(15);

    // -------- Spell Check Group --------
    QGroupBox* checkGroup = new QGroupBox("Spell Check");
    QVBoxLayout* checkLayout = new QVBoxLayout(checkGroup);

    wordLineEdit = new QLineEdit();   //Input field  //User types a word here
    wordLineEdit->setPlaceholderText("Enter a word to check");


    checkWordButton = new QPushButton("Check Word");  //Button -> triggers spell check
    checkResultLabel = new QLabel();                //Label -> shows “correct / incorrect”
    suggestionsListWidget = new QListWidget();     //List -> shows suggestions

    //Horizontal layout for input + button
    QHBoxLayout* checkInputLayout = new QHBoxLayout();
    checkInputLayout->addWidget(wordLineEdit);
    checkInputLayout->addWidget(checkWordButton);

    //Assemble spell-check section
    checkLayout->addLayout(checkInputLayout);
    checkLayout->addWidget(checkResultLabel);
    checkLayout->addWidget(new QLabel("Suggestions:"));
    checkLayout->addWidget(suggestionsListWidget);
    
    //Adds entire spell-check section to the window.
    mainLayout->addWidget(checkGroup);

    // -------- Auto-complete Group --------
    QGroupBox* autoGroup = new QGroupBox("Auto-complete");
    QVBoxLayout* autoLayout = new QVBoxLayout(autoGroup);

    prefixLineEdit = new QLineEdit();
    prefixLineEdit->setPlaceholderText("Enter a prefix for auto-complete");

    autoCompleteButton = new QPushButton("Auto-complete");
    autoCompleteListWidget = new QListWidget();

    QHBoxLayout* autoInputLayout = new QHBoxLayout();
    autoInputLayout->addWidget(prefixLineEdit);
    autoInputLayout->addWidget(autoCompleteButton);

    autoLayout->addLayout(autoInputLayout);
    autoLayout->addWidget(new QLabel("Auto-complete results:"));
    autoLayout->addWidget(autoCompleteListWidget);

    mainLayout->addWidget(autoGroup);


    //Attaches everything to the window.
    setCentralWidget(central);

    //Creates a status bar at the bottom.
    statusbar = new QStatusBar();
    setStatusBar(statusbar);

    //--------------------------------------------------------

    loadDictionary();

    // Signals
    connect(checkWordButton, &QPushButton::clicked, this, &MainWindow::checkWord);
    connect(autoCompleteButton, &QPushButton::clicked, this, &MainWindow::autoComplete);

    connect(suggestionsListWidget, &QListWidget::itemClicked, [=](QListWidgetItem* item) {
        wordLineEdit->setText(item->text());
        checkWord();
        });

    connect(autoCompleteListWidget, &QListWidget::itemClicked, [=](QListWidgetItem* item) {
        prefixLineEdit->setText(item->text());
        autoComplete();
        });

    setWindowTitle("Spell Checker");
    resize(500, 600);
}

MainWindow::~MainWindow() {}


// -------- Helper Functions --------

bool MainWindow::isValidInput(const QString& text) {
    if (text.isEmpty())
        return false;

    for (QChar c : text) {
        if (!c.isLetter())
            return false;
    }
    return true;
}

void MainWindow::loadDictionary() {
    if (!checker.loadDictionary("dictionary.txt")) {
        QMessageBox::critical(this, "Error", "Failed to load dictionary file.");
    }
    else {
        statusbar->showMessage(
            QString("Dictionary loaded: %1 words").arg(checker.getWordCount())
        );
    }
}


// -------- Slots --------

void MainWindow::checkWord() {
    QString word = wordLineEdit->text();

    if (!isValidInput(word)) {
        QMessageBox::warning(
            this,
            "Invalid Input",
            "Please enter letters only (A–Z).\nNumbers and symbols are not allowed."
        );
        return;
    }

    bool correct = checker.checkWord(word.toStdString());

    if (correct) {
        checkResultLabel->setText("The word is spelled correctly.");
        checkResultLabel->setStyleSheet("color: green; font-weight: bold;");
    }
    else {
        checkResultLabel->setText("The word is NOT in the dictionary.");
        checkResultLabel->setStyleSheet("color: red; font-weight: bold;");
    }

    suggestionsListWidget->clear();
    SpellChecker::StringArray suggestions =
        checker.getSuggestions(word.toStdString(), 2, 10);

    for (int i = 0; i < suggestions.size; i++) {
        QListWidgetItem* item =
            new QListWidgetItem(QString::fromStdString(suggestions.data[i]));
        suggestionsListWidget->addItem(item);
    }
}

void MainWindow::autoComplete() {
    QString prefix = prefixLineEdit->text();

    if (!isValidInput(prefix)) {
        QMessageBox::warning(
            this,
            "Invalid Input",
            "Auto-complete accepts letters only."
        );
        return;
    }

    autoCompleteListWidget->clear();
    SpellChecker::StringArray completions =
        checker.autoComplete(prefix.toStdString(), 10);

    for (int i = 0; i < completions.size; i++) {
        QListWidgetItem* item =
            new QListWidgetItem(QString::fromStdString(completions.data[i]));
        item->setForeground(QBrush(QColor("blue")));
        autoCompleteListWidget->addItem(item);
    }
}
