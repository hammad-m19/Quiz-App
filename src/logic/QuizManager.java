package logic;

import model.Question;
import model.QuestionBank;

import java.util.List;

/**
 * Manages the active quiz session — loaded questions, navigation,
 * answer tracking, and score calculation.
 */
public class QuizManager {

    private List<Question> questions;
    private int currentIndex;
    private String currentTopic;

    // ── Constructor ──────────────────────────────────────────────────
    public QuizManager() {
        this.currentIndex = 0;
    }

    // ── Load Questions ───────────────────────────────────────────────

    /**
     * Loads a shuffled set of questions for the given topic.
     */
    public void loadQuestions(String topic) {
        this.currentTopic = topic;
        this.questions = QuestionBank.getQuestionsByTopic(topic);
        this.currentIndex = 0;
    }

    // ── Navigation ───────────────────────────────────────────────────

    public Question getCurrentQuestion() {
        if (questions == null || questions.isEmpty()) return null;
        return questions.get(currentIndex);
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTotalQuestions() {
        return (questions == null) ? 0 : questions.size();
    }

    public boolean hasNext() {
        return questions != null && currentIndex < questions.size() - 1;
    }

    public boolean hasPrevious() {
        return currentIndex > 0;
    }

    public void nextQuestion() {
        if (hasNext()) {
            currentIndex++;
        }
    }

    public void previousQuestion() {
        if (hasPrevious()) {
            currentIndex--;
        }
    }

    // ── Answer Handling ──────────────────────────────────────────────

    /**
     * Records the user's selected option for the current question.
     */
    public void selectAnswer(int optionIndex) {
        Question q = getCurrentQuestion();
        if (q != null) {
            q.setSelectedOptionIndex(optionIndex);
        }
    }

    /**
     * Checks if the given option index is correct for the current question.
     */
    public boolean checkAnswer(int selected) {
        Question q = getCurrentQuestion();
        return q != null && q.getCorrectOptionIndex() == selected;
    }

    // ── Score Calculation ────────────────────────────────────────────

    /**
     * Computes total correct answers across all questions.
     */
    public int calculateScore() {
        if (questions == null) return 0;
        int score = 0;
        for (Question q : questions) {
            if (q.isCorrect()) {
                score++;
            }
        }
        return score;
    }

    /**
     * Returns the score as a percentage.
     */
    public double getPercentage() {
        if (questions == null || questions.isEmpty()) return 0;
        return (calculateScore() * 100.0) / questions.size();
    }

    /**
     * Returns true if the user scored 50% or above.
     */
    public boolean isPassed() {
        return getPercentage() >= 50.0;
    }

    // ── Reset ────────────────────────────────────────────────────────

    /**
     * Resets all state to allow retaking the quiz.
     */
    public void resetQuiz() {
        if (questions != null) {
            for (Question q : questions) {
                q.setSelectedOptionIndex(-1);
            }
        }
        currentIndex = 0;
    }

    // ── Getters ──────────────────────────────────────────────────────

    public List<Question> getQuestions() {
        return questions;
    }

    public String getCurrentTopic() {
        return currentTopic;
    }
}
