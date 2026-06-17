package ui;

import logic.QuizManager;
import model.Question;
import model.ResultStore;
import model.StudentResult;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Clean result screen — score, percentage, pass/fail, and answer review.
 */
public class ResultFrame extends BaseFrame {

    private QuizManager quizManager;
    private boolean showReviewOnStart;
    private String studentName;
    private JPanel bgPanel;
    private JPanel reviewContainer;
    private JScrollPane scrollPane;
    private JButton reviewButton;
    private JButton restartButton;
    private JButton exitButton;
    private boolean reviewVisible = false;

    public ResultFrame(QuizManager quizManager, boolean showReview, String studentName) {
        super("Quiz Results", 660, 640);
        this.quizManager = quizManager;
        this.showReviewOnStart = showReview;
        this.studentName = studentName;

        // Save the result to the store so the teacher can view it
        ResultStore.addResult(new StudentResult(
            studentName,
            quizManager.getCurrentTopic(),
            quizManager.calculateScore(),
            quizManager.getTotalQuestions(),
            quizManager.getPercentage(),
            quizManager.isPassed()
        ));

        initializeUI();
        setupListeners();
        if (showReviewOnStart) {
            toggleReview();
        }
        setVisible(true);
    }

    @Override
    protected void initializeUI() {

        bgPanel = new JPanel();
        bgPanel.setLayout(null);
        bgPanel.setBackground(BG_PRIMARY);
        bgPanel.setBounds(0, 0, 660, 640);
        setContentPane(bgPanel);

        int score = quizManager.calculateScore();
        int total = quizManager.getTotalQuestions();
        double pct = quizManager.getPercentage();
        boolean passed = quizManager.isPassed();

        // ── Title ──────────────────────────────────────────────────
        String titleText = passed ? "Congratulations!" : "Better Luck Next Time";
        JLabel title = createLabel(titleText, FONT_TITLE, passed ? ACCENT_SUCCESS : ACCENT_DANGER);
        title.setBounds(0, 25, 660, 32);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        bgPanel.add(title);

        JLabel topicInfo = createLabel("Topic: " + quizManager.getCurrentTopic(), FONT_SMALL, TEXT_SECONDARY);
        topicInfo.setBounds(0, 60, 660, 16);
        topicInfo.setHorizontalAlignment(SwingConstants.CENTER);
        bgPanel.add(topicInfo);

        // ── Score Cards ────────────────────────────────────────────
        JPanel scoreCard = createStatCard("Score", score + " / " + total, ACCENT_PRIMARY, 40, 95);
        bgPanel.add(scoreCard);

        JPanel pctCard = createStatCard("Percentage", String.format("%.0f%%", pct), ACCENT_WARNING, 240, 95);
        bgPanel.add(pctCard);

        JPanel statusCard = createStatCard("Status",
            passed ? "PASSED" : "FAILED",
            passed ? ACCENT_SUCCESS : ACCENT_DANGER, 440, 95);
        bgPanel.add(statusCard);

        // ── Breakdown ──────────────────────────────────────────────
        long unanswered = quizManager.getQuestions().stream().filter(q -> !q.isAnswered()).count();
        int wrong = total - score;

        JPanel breakdownCard = createCardPanel();
        breakdownCard.setBounds(40, 215, 580, 50);
        bgPanel.add(breakdownCard);

        JLabel correctLabel = createLabel("Correct: " + score, FONT_BODY_BOLD, ACCENT_SUCCESS);
        correctLabel.setBounds(25, 13, 150, 22);
        breakdownCard.add(correctLabel);

        JLabel wrongLabel = createLabel("Wrong: " + (wrong - unanswered), FONT_BODY_BOLD, ACCENT_DANGER);
        wrongLabel.setBounds(210, 13, 150, 22);
        breakdownCard.add(wrongLabel);

        JLabel skipLabel = createLabel("Skipped: " + unanswered, FONT_BODY_BOLD, TEXT_MUTED);
        skipLabel.setBounds(400, 13, 150, 22);
        breakdownCard.add(skipLabel);

        // ── Action Buttons ─────────────────────────────────────────
        reviewButton = createStyledButton("Review Answers", BG_CARD, TEXT_PRIMARY);
        reviewButton.setBounds(40, 285, 180, 40);
        bgPanel.add(reviewButton);

        restartButton = createStyledButton("Restart Quiz", ACCENT_PRIMARY, Color.WHITE);
        restartButton.setBounds(240, 285, 180, 40);
        bgPanel.add(restartButton);

        exitButton = createStyledButton("Exit", ACCENT_DANGER, Color.WHITE);
        exitButton.setBounds(440, 285, 180, 40);
        bgPanel.add(exitButton);

        // ── Review Panel (hidden by default) ───────────────────────
        reviewContainer = new JPanel();
        reviewContainer.setLayout(new BoxLayout(reviewContainer, BoxLayout.Y_AXIS));
        reviewContainer.setBackground(BG_PRIMARY);
        buildReviewContent();

        scrollPane = new JScrollPane(reviewContainer);
        scrollPane.setBounds(25, 345, 610, 250);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(BG_PRIMARY);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVisible(false);
        bgPanel.add(scrollPane);
    }

    private void buildReviewContent() {
        List<Question> questions = quizManager.getQuestions();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);

            JPanel qPanel = new JPanel();
            qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));
            qPanel.setBackground(Color.WHITE);
            qPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
            ));
            qPanel.setMaximumSize(new Dimension(590, 200));

            JLabel qLabel = new JLabel("<html><b>Q" + (i + 1) + ".</b> " + q.getQuestionText() + "</html>");
            qLabel.setFont(FONT_BODY_BOLD);
            qLabel.setForeground(TEXT_PRIMARY);
            qLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            qPanel.add(qLabel);
            qPanel.add(Box.createVerticalStrut(5));

            String[] options = q.getOptions();
            String[] letters = {"A", "B", "C", "D"};
            for (int j = 0; j < options.length; j++) {
                String prefix = letters[j] + ". " + options[j];
                JLabel optLabel = new JLabel();
                optLabel.setFont(FONT_BODY);
                optLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                if (j == q.getCorrectOptionIndex()) {
                    optLabel.setText("  ✓ " + prefix);
                    optLabel.setForeground(ACCENT_SUCCESS);
                } else if (j == q.getSelectedOptionIndex()) {
                    optLabel.setText("  ✗ " + prefix);
                    optLabel.setForeground(ACCENT_DANGER);
                } else {
                    optLabel.setText("     " + prefix);
                    optLabel.setForeground(TEXT_SECONDARY);
                }
                qPanel.add(optLabel);
            }

            // Status
            JLabel statusTag = new JLabel();
            statusTag.setFont(FONT_SMALL);
            statusTag.setAlignmentX(Component.LEFT_ALIGNMENT);
            if (!q.isAnswered()) {
                statusTag.setText("  Not answered");
                statusTag.setForeground(TEXT_MUTED);
            } else if (q.isCorrect()) {
                statusTag.setText("  Correct");
                statusTag.setForeground(ACCENT_SUCCESS);
            } else {
                statusTag.setText("  Incorrect");
                statusTag.setForeground(ACCENT_DANGER);
            }
            qPanel.add(Box.createVerticalStrut(3));
            qPanel.add(statusTag);

            reviewContainer.add(qPanel);
            reviewContainer.add(Box.createVerticalStrut(6));
        }
    }

    @Override
    protected void setupListeners() {
        reviewButton.addActionListener(e -> toggleReview());

        restartButton.addActionListener(e -> {
            quizManager.resetQuiz();
            dispose();
            new QuizFrame(quizManager.getCurrentTopic(), studentName);
        });

        exitButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?", "Exit",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame();
            }
        });
    }

    private void toggleReview() {
        reviewVisible = !reviewVisible;
        scrollPane.setVisible(reviewVisible);
        reviewButton.setText(reviewVisible ? "Hide Review" : "Review Answers");
        if (reviewVisible) {
            setSize(660, 640);
        } else {
            setSize(660, 380);
        }
        bgPanel.setBounds(0, 0, 660, getHeight());
        bgPanel.repaint();
    }

    private JPanel createStatCard(String label, String value, Color accent, int x, int y) {
        JPanel card = createCardPanel();
        card.setBounds(x, y, 180, 100);

        JLabel valLabel = createLabel(value, FONT_TITLE, accent);
        valLabel.setBounds(0, 20, 180, 32);
        valLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valLabel);

        JLabel nameLabel = createLabel(label, FONT_SMALL, TEXT_SECONDARY);
        nameLabel.setBounds(0, 60, 180, 16);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(nameLabel);

        return card;
    }
}
