#pragma once

#include "SpellChecker.h"
#include <QMainWindow>
#include <QLineEdit>
#include <QPushButton>
#include <QLabel>
#include <QListWidget>
#include <QVBoxLayout>
#include <QHBoxLayout>
#include <QGroupBox>
#include <QStatusBar>


class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow(QWidget* parent = nullptr);
    ~MainWindow();

private slots:
    void checkWord();
    void autoComplete();

private:
    SpellChecker checker;

    // Widgets
    QLineEdit* wordLineEdit;
    QPushButton* checkWordButton;
    QLabel* checkResultLabel;
    QListWidget* suggestionsListWidget;

    QLineEdit* prefixLineEdit;
    QPushButton* autoCompleteButton;
    QListWidget* autoCompleteListWidget;

    QStatusBar* statusbar;

    void loadDictionary();
    bool isValidInput(const QString& text);
};


