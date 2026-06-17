package logic;

import model.Question;
import model.QuestionBank;

import java.util.List;

public class QuizManager {

    private List<Question> questions;
    private int currentIndex;
    private String currentTopic;

    public QuizManager() {
        this.currentIndex = 0;
    }


    public void loadQuestions(String topic) {
        this.currentTopic = topic;
        this.questions = QuestionBank.getQuestionsByTopic(topic);
        this.currentIndex = 0;
    }


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


    public void selectAnswer(int optionIndex) {
        Question q = getCurrentQuestion();
        if (q != null) {
            q.setSelectedOptionIndex(optionIndex);
        }
    }

    public boolean checkAnswer(int selected) {
        Question q = getCurrentQuestion();
        return q != null && q.getCorrectOptionIndex() == selected;
    }


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

    public double getPercentage() {
        if (questions == null || questions.isEmpty()) return 0;
        return (calculateScore() * 100.0) / questions.size();
    }

    public boolean isPassed() {
        return getPercentage() >= 50.0;
    }


    public void resetQuiz() {
        if (questions != null) {
            for (Question q : questions) {
                q.setSelectedOptionIndex(-1);
            }
        }
        currentIndex = 0;
    }


    public List<Question> getQuestions() {
        return questions;
    }

    public String getCurrentTopic() {
        return currentTopic;
    }
}
