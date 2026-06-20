package ui;

import model.Question;
import model.QuestionBank;
import model.ResultStore;
import model.StudentResult;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class TeacherDashboardFrame extends BaseFrame {

    private JPanel bgPanel;
    private JComboBox<String> topicCombo;
    private JTable questionsTable;
    private DefaultTableModel tableModel;
    private JButton addQuestionBtn;
    private JButton editQuestionBtn;
    private JButton deleteQuestionBtn;
    private JButton addTopicBtn;
    private JButton deleteTopicBtn;
    private JButton viewResultsBtn;
    private JButton logoutBtn;
    private JLabel statsLabel;

    public TeacherDashboardFrame() {
        super("Teacher Dashboard", 880, 650);
        initializeUI();
        setupListeners();
        loadQuestionsForSelectedTopic();
        setVisible(true);
    }

    @Override
    protected void initializeUI() {

        bgPanel = new JPanel();
        bgPanel.setLayout(null);
        bgPanel.setBackground(BG_PRIMARY);
        bgPanel.setBounds(0, 0, 880, 650);
        setContentPane(bgPanel);

        JLabel title = createLabel("Teacher Dashboard", FONT_TITLE, TEXT_PRIMARY);
        title.setBounds(25, 12, 350, 32);
        bgPanel.add(title);

        statsLabel = createLabel("", FONT_SMALL, TEXT_SECONDARY);
        statsLabel.setBounds(25, 46, 500, 16);
        bgPanel.add(statsLabel);

        logoutBtn = createStyledButton("Logout", ACCENT_DANGER, Color.WHITE);
        logoutBtn.setBounds(740, 15, 110, 36);
        bgPanel.add(logoutBtn);

        JPanel topicBar = createCardPanel();
        topicBar.setBounds(25, 72, 830, 55);
        bgPanel.add(topicBar);

        JLabel topicLabel = createLabel("Topic:", FONT_BODY_BOLD, TEXT_PRIMARY);
        topicLabel.setBounds(15, 15, 50, 22);
        topicBar.add(topicLabel);

        List<String> topics = QuestionBank.getAvailableTopics();
        topicCombo = new JComboBox<>(topics.toArray(new String[0]));
        topicCombo.setFont(FONT_BODY);
        topicCombo.setForeground(TEXT_PRIMARY);
        topicCombo.setBackground(Color.WHITE);
        topicCombo.setBounds(70, 12, 260, 32);
        topicCombo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        topicBar.add(topicCombo);

        addTopicBtn = createStyledButton("+ New Topic", ACCENT_SUCCESS, Color.WHITE);
        addTopicBtn.setBounds(530, 10, 140, 36);
        topicBar.add(addTopicBtn);

        deleteTopicBtn = createStyledButton("Delete Topic", ACCENT_DANGER, Color.WHITE);
        deleteTopicBtn.setBounds(680, 10, 135, 36);
        topicBar.add(deleteTopicBtn);

        String[] columns = { "#", "Question", "Option A", "Option B", "Option C", "Option D", "Ans" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        questionsTable = new JTable(tableModel);
        questionsTable.setFont(FONT_SMALL);
        questionsTable.setForeground(TEXT_PRIMARY);
        questionsTable.setBackground(Color.WHITE);
        questionsTable.setGridColor(BORDER_COLOR);
        questionsTable.setSelectionBackground(ACCENT_PRIMARY);
        questionsTable.setSelectionForeground(Color.WHITE);
        questionsTable.setRowHeight(34);
        questionsTable.setShowGrid(true);
        questionsTable.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = questionsTable.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(BG_CARD);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        questionsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        questionsTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        questionsTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        questionsTable.getColumnModel().getColumn(1).setPreferredWidth(230);
        questionsTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        questionsTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        questionsTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        questionsTable.getColumnModel().getColumn(5).setPreferredWidth(110);
        questionsTable.getColumnModel().getColumn(6).setPreferredWidth(40);

        JScrollPane scrollPane = new JScrollPane(questionsTable);
        scrollPane.setBounds(25, 140, 830, 390);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        bgPanel.add(scrollPane);

        addQuestionBtn = createStyledButton("+ Add Question", ACCENT_SUCCESS, Color.WHITE);
        addQuestionBtn.setBounds(25, 548, 170, 40);
        bgPanel.add(addQuestionBtn);

        editQuestionBtn = createStyledButton("Edit Selected", ACCENT_PRIMARY, Color.WHITE);
        editQuestionBtn.setBounds(210, 548, 170, 40);
        bgPanel.add(editQuestionBtn);

        deleteQuestionBtn = createStyledButton("Delete Selected", ACCENT_DANGER, Color.WHITE);
        deleteQuestionBtn.setBounds(395, 548, 170, 40);
        bgPanel.add(deleteQuestionBtn);

        viewResultsBtn = createStyledButton("View Results", ACCENT_WARNING, Color.WHITE);
        viewResultsBtn.setBounds(580, 548, 170, 40);
        bgPanel.add(viewResultsBtn);

        updateStats();
    }

    @Override
    protected void setupListeners() {
        topicCombo.addActionListener(e -> loadQuestionsForSelectedTopic());
        addQuestionBtn.addActionListener(e -> openAddQuestionDialog());
        editQuestionBtn.addActionListener(e -> openEditQuestionDialog());
        deleteQuestionBtn.addActionListener(e -> deleteSelectedQuestion());
        addTopicBtn.addActionListener(e -> openAddTopicDialog());
        deleteTopicBtn.addActionListener(e -> deleteSelectedTopic());
        viewResultsBtn.addActionListener(e -> openResultsDialog());

        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?", "Logout",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame();
            }
        });
    }

    private void loadQuestionsForSelectedTopic() {
        String topic = (String) topicCombo.getSelectedItem();
        if (topic == null)
            return;

        tableModel.setRowCount(0);
        List<Question> questions = QuestionBank.getQuestionsByTopicOrdered(topic);
        String[] letters = { "A", "B", "C", "D" };

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String[] opts = q.getOptions();
            tableModel.addRow(new Object[] {
                    (i + 1),
                    q.getQuestionText(),
                    opts.length > 0 ? opts[0] : "",
                    opts.length > 1 ? opts[1] : "",
                    opts.length > 2 ? opts[2] : "",
                    opts.length > 3 ? opts[3] : "",
                    letters[q.getCorrectOptionIndex()]
            });
        }
        updateStats();
    }

    private void openAddQuestionDialog() {
        String topic = (String) topicCombo.getSelectedItem();
        if (topic == null)
            return;

        QuestionDialog dialog = new QuestionDialog(this, "Add New Question", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            QuestionBank.addQuestion(topic, dialog.getQuestion());
            loadQuestionsForSelectedTopic();
        }
    }

    private void openEditQuestionDialog() {
        String topic = (String) topicCombo.getSelectedItem();
        int row = questionsTable.getSelectedRow();
        if (topic == null || row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a question to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Question> questions = QuestionBank.getQuestionsByTopicOrdered(topic);
        QuestionDialog dialog = new QuestionDialog(this, "Edit Question", questions.get(row));
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            QuestionBank.updateQuestion(topic, row, dialog.getQuestion());
            loadQuestionsForSelectedTopic();
        }
    }

    private void deleteSelectedQuestion() {
        String topic = (String) topicCombo.getSelectedItem();
        int row = questionsTable.getSelectedRow();
        if (topic == null || row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a question to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete Question #" + (row + 1) + "?",
                "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            QuestionBank.removeQuestion(topic, row);
            loadQuestionsForSelectedTopic();
        }
    }

    private void openAddTopicDialog() {
        String name = JOptionPane.showInputDialog(this,
                "Enter new topic name:", "Add Topic", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            String trimmed = name.trim();
            if (QuestionBank.getAvailableTopics().contains(trimmed)) {
                JOptionPane.showMessageDialog(this, "Topic already exists.",
                        "Duplicate", JOptionPane.WARNING_MESSAGE);
                return;
            }
            QuestionBank.addTopic(trimmed);
            refreshTopicCombo(trimmed);
        }
    }

    private void deleteSelectedTopic() {
        String topic = (String) topicCombo.getSelectedItem();
        if (topic == null)
            return;

        int choice = JOptionPane.showConfirmDialog(this,
                "Delete topic '" + topic + "' and ALL its questions?",
                "Delete Topic", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            QuestionBank.removeTopic(topic);
            refreshTopicCombo(null);
        }
    }

    private void refreshTopicCombo(String selectTopic) {
        topicCombo.removeAllItems();
        List<String> topics = QuestionBank.getAvailableTopics();
        for (String t : topics)
            topicCombo.addItem(t);
        if (selectTopic != null && topics.contains(selectTopic)) {
            topicCombo.setSelectedItem(selectTopic);
        }
        loadQuestionsForSelectedTopic();
    }

    private void updateStats() {
        int topicCount = QuestionBank.getAvailableTopics().size();
        int totalQ = QuestionBank.getTotalQuestionCount();
        String topic = (String) topicCombo.getSelectedItem();
        int topicQ = topic != null ? QuestionBank.getQuestionsByTopicOrdered(topic).size() : 0;
        statsLabel.setText("Topics: " + topicCount + "   |   Total Questions: " + totalQ
                + "   |   Current Topic: " + topicQ + " questions");
    }

    static class QuestionDialog extends JDialog {

        private JTextField questionField;
        private JTextField[] optionFields;
        private JComboBox<String> correctCombo;
        private boolean confirmed = false;

        public QuestionDialog(JFrame parent, String title, Question existing) {
            super(parent, title, true);
            setSize(500, 420);
            setLocationRelativeTo(parent);
            setResizable(false);

            JPanel panel = new JPanel();
            panel.setLayout(null);
            panel.setBackground(Color.WHITE);
            setContentPane(panel);

            Font bodyFont = new Font("SansSerif", Font.PLAIN, 14);
            Font labelFont = new Font("SansSerif", Font.BOLD, 13);
            Color textCol = new Color(30, 30, 30);
            Color borderCol = new Color(210, 215, 222);

            // Question
            JLabel qLabel = new JLabel("Question:");
            qLabel.setFont(labelFont);
            qLabel.setForeground(textCol);
            qLabel.setBounds(20, 18, 200, 18);
            panel.add(qLabel);

            questionField = new JTextField();
            questionField.setFont(bodyFont);
            questionField.setForeground(textCol);
            questionField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderCol, 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            questionField.setBounds(20, 38, 450, 36);
            panel.add(questionField);

            // Options
            String[] labels = { "Option A:", "Option B:", "Option C:", "Option D:" };
            optionFields = new JTextField[4];
            for (int i = 0; i < 4; i++) {
                JLabel oLabel = new JLabel(labels[i]);
                oLabel.setFont(labelFont);
                oLabel.setForeground(textCol);
                oLabel.setBounds(20, 88 + i * 52, 100, 18);
                panel.add(oLabel);

                optionFields[i] = new JTextField();
                optionFields[i].setFont(bodyFont);
                optionFields[i].setForeground(textCol);
                optionFields[i].setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderCol, 1),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)));
                optionFields[i].setBounds(115, 84 + i * 52, 355, 34);
                panel.add(optionFields[i]);
            }

            // Correct answer
            JLabel cLabel = new JLabel("Correct:");
            cLabel.setFont(labelFont);
            cLabel.setForeground(textCol);
            cLabel.setBounds(20, 304, 80, 18);
            panel.add(cLabel);

            correctCombo = new JComboBox<>(new String[] { "A", "B", "C", "D" });
            correctCombo.setFont(bodyFont);
            correctCombo.setBounds(115, 300, 80, 32);
            panel.add(correctCombo);

            // Buttons
            JButton saveBtn = new JButton("Save");
            saveBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            saveBtn.setForeground(Color.WHITE);
            saveBtn.setBackground(new Color(30, 142, 62));
            saveBtn.setFocusPainted(false);
            saveBtn.setBorderPainted(false);
            saveBtn.setOpaque(true);
            saveBtn.setBounds(270, 350, 95, 36);
            panel.add(saveBtn);

            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            cancelBtn.setForeground(textCol);
            cancelBtn.setBackground(new Color(237, 241, 247));
            cancelBtn.setFocusPainted(false);
            cancelBtn.setBorderPainted(false);
            cancelBtn.setOpaque(true);
            cancelBtn.setBounds(375, 350, 95, 36);
            panel.add(cancelBtn);

            // Pre-fill
            if (existing != null) {
                questionField.setText(existing.getQuestionText());
                String[] opts = existing.getOptions();
                for (int i = 0; i < 4 && i < opts.length; i++) {
                    optionFields[i].setText(opts[i]);
                }
                correctCombo.setSelectedIndex(existing.getCorrectOptionIndex());
            }

            saveBtn.addActionListener(e -> {
                if (validateFields()) {
                    confirmed = true;
                    dispose();
                }
            });
            cancelBtn.addActionListener(e -> dispose());
        }

        private boolean validateFields() {
            if (questionField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Question text cannot be empty.",
                        "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            for (int i = 0; i < 4; i++) {
                if (optionFields[i].getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Option " + (char) ('A' + i) + " cannot be empty.",
                            "Error", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
            return true;
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        public Question getQuestion() {
            String[] options = new String[4];
            for (int i = 0; i < 4; i++)
                options[i] = optionFields[i].getText().trim();
            return new Question(questionField.getText().trim(), options, correctCombo.getSelectedIndex());
        }
    }

    private void openResultsDialog() {
        JDialog dialog = new JDialog(this, "Student Results", true);
        dialog.setSize(750, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(BG_PRIMARY);
        dialog.setContentPane(panel);

        JLabel title = createLabel("Student Quiz Results", FONT_SUBTITLE, TEXT_PRIMARY);
        title.setBounds(20, 15, 300, 24);
        panel.add(title);

        // Filter combo
        JLabel filterLabel = createLabel("Filter by topic:", FONT_SMALL, TEXT_SECONDARY);
        filterLabel.setBounds(420, 18, 100, 16);
        panel.add(filterLabel);

        List<String> topics = QuestionBank.getAvailableTopics();
        String[] filterOptions = new String[topics.size() + 1];
        filterOptions[0] = "All Topics";
        for (int i = 0; i < topics.size(); i++) {
            filterOptions[i + 1] = topics.get(i);
        }
        JComboBox<String> filterCombo = new JComboBox<>(filterOptions);
        filterCombo.setFont(FONT_SMALL);
        filterCombo.setBackground(Color.WHITE);
        filterCombo.setBounds(520, 14, 200, 26);
        panel.add(filterCombo);

        // Results table
        String[] columns = { "#", "Student", "Topic", "Score", "Percentage", "Status", "Date/Time" };
        DefaultTableModel resultsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable resultsTable = new JTable(resultsTableModel);
        resultsTable.setFont(FONT_SMALL);
        resultsTable.setForeground(TEXT_PRIMARY);
        resultsTable.setBackground(Color.WHITE);
        resultsTable.setGridColor(BORDER_COLOR);
        resultsTable.setSelectionBackground(ACCENT_PRIMARY);
        resultsTable.setSelectionForeground(Color.WHITE);
        resultsTable.setRowHeight(30);
        resultsTable.setShowGrid(true);
        resultsTable.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = resultsTable.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(BG_CARD);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 34));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        resultsTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        resultsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        resultsTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        resultsTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        resultsTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        resultsTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        resultsTable.getColumnModel().getColumn(6).setPreferredWidth(140);

        // Custom cell renderer for pass/fail status coloring
        resultsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    if ("PASSED".equals(value)) {
                        c.setForeground(ACCENT_SUCCESS);
                    } else {
                        c.setForeground(ACCENT_DANGER);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBounds(20, 50, 700, 350);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane);

        // Summary label
        JLabel summaryLabel = createLabel("", FONT_SMALL, TEXT_SECONDARY);
        summaryLabel.setBounds(20, 410, 500, 18);
        panel.add(summaryLabel);

        // Close button
        JButton closeBtn = createStyledButton("Close", BG_CARD, TEXT_PRIMARY);
        closeBtn.setBounds(610, 410, 110, 36);
        panel.add(closeBtn);
        closeBtn.addActionListener(e -> dialog.dispose());

        // Load results helper
        Runnable loadResults = () -> {
            resultsTableModel.setRowCount(0);
            String selectedFilter = (String) filterCombo.getSelectedItem();
            List<StudentResult> resultsList;
            if ("All Topics".equals(selectedFilter)) {
                resultsList = ResultStore.getAllResults();
            } else {
                resultsList = ResultStore.getResultsByTopic(selectedFilter);
            }
            for (int i = 0; i < resultsList.size(); i++) {
                StudentResult r = resultsList.get(i);
                resultsTableModel.addRow(new Object[] {
                        (i + 1),
                        r.getStudentName(),
                        r.getTopic(),
                        r.getScore() + "/" + r.getTotalQuestions(),
                        String.format("%.0f%%", r.getPercentage()),
                        r.isPassed() ? "PASSED" : "FAILED",
                        r.getTimestamp()
                });
            }
            int total = resultsList.size();
            long passed = resultsList.stream().filter(StudentResult::isPassed).count();
            summaryLabel.setText("Total attempts: " + total + "   |   Passed: " + passed
                    + "   |   Failed: " + (total - passed));
        };

        // Initial load
        loadResults.run();

        // Filter listener
        filterCombo.addActionListener(e -> loadResults.run());

        dialog.setVisible(true);
    }
}

