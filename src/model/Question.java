package model;

/**
 * Represents a single multiple-choice quiz question.
 * Demonstrates OOP Concept: Encapsulation — private fields with getters/setters.
 */
public class Question {

    private String questionText;
    private String[] options;       // exactly 4 options
    private int correctOptionIndex; // 0-3
    private int selectedOptionIndex; // -1 means unanswered

    // ── Constructor ──────────────────────────────────────────────────
    public Question(String questionText, String[] options, int correctOptionIndex) {
        this.questionText = questionText;
        this.options = options;
        this.correctOptionIndex = correctOptionIndex;
        this.selectedOptionIndex = -1; // unanswered by default
    }

    // ── Getters ──────────────────────────────────────────────────────
    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    public int getSelectedOptionIndex() {
        return selectedOptionIndex;
    }

    // ── Setters ──────────────────────────────────────────────────────
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public void setCorrectOptionIndex(int correctOptionIndex) {
        this.correctOptionIndex = correctOptionIndex;
    }

    public void setSelectedOptionIndex(int selectedOptionIndex) {
        this.selectedOptionIndex = selectedOptionIndex;
    }

    // ── Utility ──────────────────────────────────────────────────────
    /**
     * Returns true if the user answered this question correctly.
     */
    public boolean isCorrect() {
        return selectedOptionIndex == correctOptionIndex;
    }

    /**
     * Returns true if the user has selected any answer.
     */
    public boolean isAnswered() {
        return selectedOptionIndex != -1;
    }
}
