package ui;

import logic.QuizManager;
import logic.Timer;
import logic.TimerCallback;
import model.Question;

import javax.swing.*;
import java.awt.*;

public class QuizFrame extends BaseFrame implements TimerCallback {

    private static final int SECONDS_PER_QUESTION = 15;

    private QuizManager quizManager;
    private Timer timer;
    private String studentName;

    private JPanel bgPanel;
    private JLabel topicLabel;
    private JLabel progressLabel;
    private JLabel timerLabel;
    private JProgressBar timerBar;
    private JLabel questionLabel;
    private JButton[] optionButtons;
    private JButton prevButton;
    private JButton nextButton;
    private JButton exitButton;

    public QuizFrame(String topic, String studentName) {
        super("Quiz — " + topic, 660, 580);
        this.studentName = studentName;
        quizManager = new QuizManager();
        quizManager.loadQuestions(topic);
        optionButtons = new JButton[4];
        initializeUI();
        setupListeners();
        showQuestion(0);
        setVisible(true);
    }

    @Override
    protected void initializeUI() {

        bgPanel = new JPanel();
        bgPanel.setLayout(null);
        bgPanel.setBackground(BG_PRIMARY);
        bgPanel.setBounds(0, 0, 660, 580);
        setContentPane(bgPanel);

        topicLabel = createLabel("", FONT_SUBTITLE, ACCENT_PRIMARY);
        topicLabel.setBounds(25, 12, 300, 24);
        bgPanel.add(topicLabel);

        progressLabel = createLabel("", FONT_SMALL, TEXT_SECONDARY);
        progressLabel.setBounds(25, 38, 200, 16);
        bgPanel.add(progressLabel);

        // Timer
        timerLabel = createLabel("15s", new Font("SansSerif", Font.BOLD, 20), ACCENT_WARNING);
        timerLabel.setBounds(560, 12, 70, 26);
        timerLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bgPanel.add(timerLabel);

        timerBar = new JProgressBar(0, SECONDS_PER_QUESTION);
        timerBar.setValue(SECONDS_PER_QUESTION);
        timerBar.setBounds(25, 60, 610, 8);
        timerBar.setBackground(BG_CARD);
        timerBar.setForeground(ACCENT_PRIMARY);
        timerBar.setBorderPainted(false);
        bgPanel.add(timerBar);

        JPanel questionCard = createCardPanel();
        questionCard.setBounds(25, 80, 610, 90);
        bgPanel.add(questionCard);

        questionLabel = new JLabel();
        questionLabel.setFont(FONT_BODY_BOLD);
        questionLabel.setForeground(TEXT_PRIMARY);
        questionLabel.setBounds(20, 10, 570, 70);
        questionLabel.setVerticalAlignment(SwingConstants.CENTER);
        questionCard.add(questionLabel);

        String[] labels = { "A", "B", "C", "D" };
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            optionButtons[i] = new JButton();
            optionButtons[i].setFont(FONT_BODY);
            optionButtons[i].setForeground(TEXT_PRIMARY);
            optionButtons[i].setBackground(Color.WHITE);
            optionButtons[i].setFocusPainted(false);
            optionButtons[i].setBorderPainted(true);
            optionButtons[i].setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            optionButtons[i].setOpaque(true);
            optionButtons[i].setHorizontalAlignment(SwingConstants.LEFT);
            optionButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));

            int yPos = 185 + (i * 65);
            optionButtons[i].setBounds(25, yPos, 610, 52);
            bgPanel.add(optionButtons[i]);

            // Hover
            optionButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (optionButtons[idx].getBackground().equals(Color.WHITE)) {
                        optionButtons[idx].setBackground(BG_CARD);
                    }
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    Question q = quizManager.getCurrentQuestion();
                    if (q != null && q.getSelectedOptionIndex() != idx) {
                        optionButtons[idx].setBackground(Color.WHITE);
                    }
                }
            });
        }

        prevButton = createStyledButton("Previous", BG_CARD, TEXT_PRIMARY);
        prevButton.setBounds(25, 460, 140, 40);
        bgPanel.add(prevButton);

        exitButton = createStyledButton("Exit Quiz", ACCENT_DANGER, Color.WHITE);
        exitButton.setBounds(260, 460, 140, 40);
        bgPanel.add(exitButton);

        nextButton = createStyledButton("Next", ACCENT_PRIMARY, Color.WHITE);
        nextButton.setBounds(495, 460, 140, 40);
        bgPanel.add(nextButton);
    }

    @Override
    protected void setupListeners() {

        for (int i = 0; i < 4; i++) {
            final int idx = i;
            optionButtons[i].addActionListener(e -> {
                quizManager.selectAnswer(idx);
                highlightSelectedOption(idx);
            });
        }

        prevButton.addActionListener(e -> {
            if (quizManager.hasPrevious()) {
                stopCurrentTimer();
                quizManager.previousQuestion();
                showQuestion(quizManager.getCurrentIndex());
            }
        });

        nextButton.addActionListener(e -> {
            stopCurrentTimer();
            if (quizManager.hasNext()) {
                quizManager.nextQuestion();
                showQuestion(quizManager.getCurrentIndex());
            } else {
                showResult();
            }
        });

        exitButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit the quiz?\nYour progress will be lost.",
                    "Exit Quiz", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                stopCurrentTimer();
                dispose();
                new LoginFrame();
            }
        });
    }

    public void showQuestion(int index) {
        Question q = quizManager.getCurrentQuestion();
        if (q == null)
            return;

        topicLabel.setText(quizManager.getCurrentTopic());
        progressLabel.setText("Question " + (index + 1) + " of " + quizManager.getTotalQuestions());

        questionLabel.setText("<html><body style='width:540px'>" + q.getQuestionText() + "</body></html>");

        String[] letters = { "A", "B", "C", "D" };
        String[] opts = q.getOptions();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText("   " + letters[i] + ".  " + opts[i]);
            optionButtons[i].setBackground(Color.WHITE);
            optionButtons[i].setForeground(TEXT_PRIMARY);
            optionButtons[i].setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        }

        if (q.getSelectedOptionIndex() != -1) {
            highlightSelectedOption(q.getSelectedOptionIndex());
        }

        if (!quizManager.hasNext()) {
            nextButton.setText("Finish");
            nextButton.setBackground(ACCENT_SUCCESS);
        } else {
            nextButton.setText("Next");
            nextButton.setBackground(ACCENT_PRIMARY);
        }

        prevButton.setEnabled(quizManager.hasPrevious());
        prevButton.setBackground(quizManager.hasPrevious() ? BG_CARD : new Color(230, 230, 230));

        startTimer();
    }

    private void highlightSelectedOption(int idx) {
        for (int i = 0; i < 4; i++) {
            if (i == idx) {
                optionButtons[i].setBackground(ACCENT_PRIMARY);
                optionButtons[i].setForeground(Color.WHITE);
                optionButtons[i].setBorder(BorderFactory.createLineBorder(ACCENT_PRIMARY, 2));
            } else {
                optionButtons[i].setBackground(Color.WHITE);
                optionButtons[i].setForeground(TEXT_PRIMARY);
                optionButtons[i].setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
            }
        }
    }

    public void startTimer() {
        stopCurrentTimer();
        timer = new Timer(SECONDS_PER_QUESTION, this);
        timerLabel.setText(SECONDS_PER_QUESTION + "s");
        timerLabel.setForeground(ACCENT_WARNING);
        timerBar.setValue(SECONDS_PER_QUESTION);
        timerBar.setForeground(ACCENT_PRIMARY);
        timer.startTimer();
    }

    public void stopTimer() {
        stopCurrentTimer();
    }

    private void stopCurrentTimer() {
        if (timer != null) {
            timer.stopTimer();
        }
    }

    @Override
    public void onTick(int secondsLeft) {
        timerLabel.setText(secondsLeft + "s");
        timerBar.setValue(secondsLeft);
        if (secondsLeft <= 5) {
            timerLabel.setForeground(ACCENT_DANGER);
            timerBar.setForeground(ACCENT_DANGER);
        } else {
            timerLabel.setForeground(ACCENT_WARNING);
            timerBar.setForeground(ACCENT_PRIMARY);
        }
    }

    @Override
    public void onTimeout() {
        if (quizManager.hasNext()) {
            quizManager.nextQuestion();
            showQuestion(quizManager.getCurrentIndex());
        } else {
            showResult();
        }
    }

    public void showResult() {
        showResult(false);
    }

    public void showResult(boolean showReview) {
        stopCurrentTimer();
        dispose();
        new ResultFrame(quizManager, showReview, studentName);
    }
}

