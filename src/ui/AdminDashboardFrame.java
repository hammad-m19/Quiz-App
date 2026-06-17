package ui;

import model.User;
import model.UserStore;
import model.Question;
import model.QuestionBank;
import model.StudentResult;
import model.ResultStore;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends BaseFrame {

    private JPanel bgPanel;
    private JTabbedPane tabbedPane;
    private JButton logoutBtn;
    private JLabel welcomeLabel;

    // User management components
    private JTable usersTable;
    private DefaultTableModel usersTableModel;
    private JButton addUserBtn;
    private JButton editUserBtn;
    private JButton deleteUserBtn;
    private JLabel userStatsLabel;

    // Quiz management components
    private JComboBox<String> topicCombo;
    private JTable questionsTable;
    private DefaultTableModel questionsTableModel;
    private JButton addQuestionBtn;
    private JButton editQuestionBtn;
    private JButton deleteQuestionBtn;
    private JButton addTopicBtn;
    private JButton deleteTopicBtn;
    private JLabel quizStatsLabel;

    // Results components
    private JComboBox<String> filterCombo;
    private JTable resultsTable;
    private DefaultTableModel resultsTableModel;
    private JLabel resultsStatsLabel;

    public AdminDashboardFrame(String adminUsername) {
        super("Admin Control Portal", 900, 720);
        initializeUI();
        setupListeners();
        
        // Initial data loading
        loadUsersData();
        loadQuestionsForSelectedTopic();
        loadResultsData();
        
        setVisible(true);
    }

    @Override
    protected void initializeUI() {
        bgPanel = new JPanel();
        bgPanel.setLayout(null);
        bgPanel.setBackground(BG_PRIMARY);
        bgPanel.setBounds(0, 0, 900, 720);
        setContentPane(bgPanel);

        JLabel titleLabel = createLabel("Admin Control Portal", FONT_TITLE, TEXT_PRIMARY);
        titleLabel.setBounds(25, 15, 350, 32);
        bgPanel.add(titleLabel);

        welcomeLabel = createLabel("Welcome, Administrator", FONT_SMALL, TEXT_SECONDARY);
        welcomeLabel.setBounds(25, 48, 400, 16);
        bgPanel.add(welcomeLabel);

        logoutBtn = createStyledButton("Logout", ACCENT_DANGER, Color.WHITE);
        logoutBtn.setBounds(760, 20, 110, 36);
        bgPanel.add(logoutBtn);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_BODY_BOLD);
        tabbedPane.setBounds(25, 80, 845, 580);
        
        // Add Tabs
        tabbedPane.addTab("User Accounts", createUserManagementPanel());
        tabbedPane.addTab("Quiz & Questions", createQuizManagementPanel());
        tabbedPane.addTab("Student Results", createResultsPanel());

        bgPanel.add(tabbedPane);
    }

    @Override
    protected void setupListeners() {
        // Logout listener
        logoutBtn.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame();
            }
        });

        // Tab selection change listener to refresh data
        tabbedPane.addChangeListener(e -> {
            int index = tabbedPane.getSelectedIndex();
            if (index == 0) {
                loadUsersData();
            } else if (index == 1) {
                loadQuestionsForSelectedTopic();
            } else if (index == 2) {
                loadResultsData();
            }
        });
    }

    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        // Stats label
        userStatsLabel = createLabel("", FONT_SMALL, TEXT_SECONDARY);
        userStatsLabel.setBounds(20, 15, 500, 20);
        panel.add(userStatsLabel);

        // Table
        String[] columns = {"#", "Username", "Password", "Role"};
        usersTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = new JTable(usersTableModel);
        usersTable.setFont(FONT_SMALL);
        usersTable.setForeground(TEXT_PRIMARY);
        usersTable.setBackground(Color.WHITE);
        usersTable.setGridColor(BORDER_COLOR);
        usersTable.setSelectionBackground(ACCENT_PRIMARY);
        usersTable.setSelectionForeground(Color.WHITE);
        usersTable.setRowHeight(34);
        usersTable.setShowGrid(true);
        usersTable.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = usersTable.getTableHeader();
        header.setFont(FONT_BODY_BOLD);
        header.setBackground(BG_CARD);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        usersTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        usersTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        usersTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        usersTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        usersTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        usersTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBounds(20, 45, 800, 390);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane);

        // Action buttons
        addUserBtn = createStyledButton("+ Add Account", ACCENT_SUCCESS, Color.WHITE);
        addUserBtn.setBounds(20, 450, 180, 40);
        panel.add(addUserBtn);

        editUserBtn = createStyledButton("Edit Selected", ACCENT_PRIMARY, Color.WHITE);
        editUserBtn.setBounds(215, 450, 180, 40);
        panel.add(editUserBtn);

        deleteUserBtn = createStyledButton("Delete Selected", ACCENT_DANGER, Color.WHITE);
        deleteUserBtn.setBounds(410, 450, 180, 40);
        panel.add(deleteUserBtn);

        // User management listeners
        addUserBtn.addActionListener(e -> openAddUserDialog());
        editUserBtn.addActionListener(e -> openEditUserDialog());
        deleteUserBtn.addActionListener(e -> deleteSelectedUser());

        return panel;
    }

    private void loadUsersData() {
        usersTableModel.setRowCount(0);
        List<User> userList = UserStore.getAllUsers();
        for (int i = 0; i < userList.size(); i++) {
            User u = userList.get(i);
            usersTableModel.addRow(new Object[]{
                (i + 1),
                u.getUsername(),
                u.getPassword(),
                u.getRoleDisplayName()
            });
        }
        int total = userList.size();
        int students = UserStore.countByRole("student");
        int teachers = UserStore.countByRole("teacher");
        int admins = UserStore.countByRole("admin");
        userStatsLabel.setText("Total Accounts: " + total + "   |   Students: " + students 
            + "   |   Teachers: " + teachers + "   |   Admins: " + admins);
    }

    private void openAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add New User Account", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        dialog.setContentPane(panel);

        Font labelFont = new Font("SansSerif", Font.BOLD, 13);
        Color textCol = new Color(30, 30, 30);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        userLabel.setForeground(textCol);
        userLabel.setBounds(30, 25, 100, 18);
        panel.add(userLabel);

        JTextField userField = new JTextField();
        userField.setFont(FONT_BODY);
        userField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        userField.setBounds(30, 48, 340, 36);
        panel.add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(textCol);
        passLabel.setBounds(30, 100, 100, 18);
        panel.add(passLabel);

        JTextField passField = new JTextField();
        passField.setFont(FONT_BODY);
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        passField.setBounds(30, 123, 340, 36);
        panel.add(passField);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(labelFont);
        roleLabel.setForeground(textCol);
        roleLabel.setBounds(30, 175, 100, 18);
        panel.add(roleLabel);

        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Student", "Teacher"});
        roleCombo.setFont(FONT_BODY);
        roleCombo.setBackground(Color.WHITE);
        roleCombo.setBounds(110, 172, 160, 32);
        panel.add(roleCombo);

        JButton saveBtn = createStyledButton("Save", ACCENT_SUCCESS, Color.WHITE);
        saveBtn.setBounds(160, 230, 100, 36);
        panel.add(saveBtn);

        JButton cancelBtn = createStyledButton("Cancel", BG_CARD, TEXT_PRIMARY);
        cancelBtn.setBounds(270, 230, 100, 36);
        panel.add(cancelBtn);

        cancelBtn.addActionListener(ev -> dialog.dispose());
        saveBtn.addActionListener(ev -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();
            String role = ((String) roleCombo.getSelectedItem()).toLowerCase();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (UserStore.usernameExists(username)) {
                JOptionPane.showMessageDialog(dialog, "Username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            UserStore.addUser(new User(username, password, role));
            loadUsersData();
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private void openEditUserDialog() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an account to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) usersTable.getValueAt(selectedRow, 1);
        User user = UserStore.findByUsername(username);
        if (user == null) return;

        JDialog dialog = new JDialog(this, "Edit User Account", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);
        dialog.setContentPane(panel);

        Font labelFont = new Font("SansSerif", Font.BOLD, 13);
        Color textCol = new Color(30, 30, 30);

        JLabel userLabel = new JLabel("Username (Read-only):");
        userLabel.setFont(labelFont);
        userLabel.setForeground(textCol);
        userLabel.setBounds(30, 25, 200, 18);
        panel.add(userLabel);

        JTextField userField = new JTextField(user.getUsername());
        userField.setFont(FONT_BODY);
        userField.setEditable(false);
        userField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        userField.setBounds(30, 48, 340, 36);
        panel.add(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(textCol);
        passLabel.setBounds(30, 100, 100, 18);
        panel.add(passLabel);

        JTextField passField = new JTextField(user.getPassword());
        passField.setFont(FONT_BODY);
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        passField.setBounds(30, 123, 340, 36);
        panel.add(passField);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(labelFont);
        roleLabel.setForeground(textCol);
        roleLabel.setBounds(30, 175, 100, 18);
        panel.add(roleLabel);

        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Student", "Teacher", "Admin"});
        roleCombo.setFont(FONT_BODY);
        roleCombo.setBackground(Color.WHITE);
        roleCombo.setSelectedItem(user.getRoleDisplayName());
        roleCombo.setBounds(110, 172, 160, 32);
        
        // Prevent changing own role if logged in as admin to avoid lock-outs
        if ("admin".equals(user.getUsername())) {
            roleCombo.setEnabled(false);
        }
        panel.add(roleCombo);

        JButton saveBtn = createStyledButton("Save", ACCENT_SUCCESS, Color.WHITE);
        saveBtn.setBounds(160, 230, 100, 36);
        panel.add(saveBtn);

        JButton cancelBtn = createStyledButton("Cancel", BG_CARD, TEXT_PRIMARY);
        cancelBtn.setBounds(270, 230, 100, 36);
        panel.add(cancelBtn);

        cancelBtn.addActionListener(ev -> dialog.dispose());
        saveBtn.addActionListener(ev -> {
            String password = passField.getText().trim();
            String role = ((String) roleCombo.getSelectedItem()).toLowerCase();

            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            user.setPassword(password);
            user.setRole(role);
            UserStore.updateUser(user.getUsername(), user);
            loadUsersData();
            dialog.dispose();
        });

        dialog.setVisible(true);
    }

    private void deleteSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an account to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) usersTable.getValueAt(selectedRow, 1);
        
        // Safety guard checks
        if ("admin".equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "The primary admin account cannot be deleted.", "Security Guard", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete user '" + username + "'?",
            "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            UserStore.removeUser(username);
            loadUsersData();
        }
    }

    private JPanel createQuizManagementPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        // Topic select bar
        JLabel topicLabel = createLabel("Select Topic:", FONT_BODY_BOLD, TEXT_PRIMARY);
        topicLabel.setBounds(20, 15, 110, 22);
        panel.add(topicLabel);

        List<String> topics = QuestionBank.getAvailableTopics();
        topicCombo = new JComboBox<>(topics.toArray(new String[0]));
        topicCombo.setFont(FONT_BODY);
        topicCombo.setBackground(Color.WHITE);
        topicCombo.setBounds(130, 11, 220, 32);
        panel.add(topicCombo);

        addTopicBtn = createStyledButton("+ New Topic", ACCENT_SUCCESS, Color.WHITE);
        addTopicBtn.setBounds(500, 9, 150, 36);
        panel.add(addTopicBtn);

        deleteTopicBtn = createStyledButton("Delete Topic", ACCENT_DANGER, Color.WHITE);
        deleteTopicBtn.setBounds(660, 9, 150, 36);
        panel.add(deleteTopicBtn);

        // Table
        String[] columns = {"#", "QuestionText", "Option A", "Option B", "Option C", "Option D", "Ans"};
        questionsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        questionsTable = new JTable(questionsTableModel);
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
        scrollPane.setBounds(20, 55, 800, 380);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane);

        // Summary Stats
        quizStatsLabel = createLabel("", FONT_SMALL, TEXT_SECONDARY);
        quizStatsLabel.setBounds(20, 442, 600, 20);
        panel.add(quizStatsLabel);

        // Buttons
        addQuestionBtn = createStyledButton("+ Add Question", ACCENT_SUCCESS, Color.WHITE);
        addQuestionBtn.setBounds(20, 475, 180, 40);
        panel.add(addQuestionBtn);

        editQuestionBtn = createStyledButton("Edit Selected", ACCENT_PRIMARY, Color.WHITE);
        editQuestionBtn.setBounds(215, 475, 180, 40);
        panel.add(editQuestionBtn);

        deleteQuestionBtn = createStyledButton("Delete Selected", ACCENT_DANGER, Color.WHITE);
        deleteQuestionBtn.setBounds(410, 475, 180, 40);
        panel.add(deleteQuestionBtn);

        // Setup Quiz management listeners
        topicCombo.addActionListener(e -> loadQuestionsForSelectedTopic());
        addQuestionBtn.addActionListener(e -> openAddQuestionDialog());
        editQuestionBtn.addActionListener(e -> openEditQuestionDialog());
        deleteQuestionBtn.addActionListener(e -> deleteSelectedQuestion());
        addTopicBtn.addActionListener(e -> openAddTopicDialog());
        deleteTopicBtn.addActionListener(e -> deleteSelectedTopic());

        return panel;
    }

    private void loadQuestionsForSelectedTopic() {
        String topic = (String) topicCombo.getSelectedItem();
        if (topic == null) return;

        questionsTableModel.setRowCount(0);
        List<Question> questions = QuestionBank.getQuestionsByTopicOrdered(topic);
        String[] letters = {"A", "B", "C", "D"};

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            String[] opts = q.getOptions();
            questionsTableModel.addRow(new Object[]{
                (i + 1),
                q.getQuestionText(),
                opts.length > 0 ? opts[0] : "",
                opts.length > 1 ? opts[1] : "",
                opts.length > 2 ? opts[2] : "",
                opts.length > 3 ? opts[3] : "",
                letters[q.getCorrectOptionIndex()]
            });
        }
        updateQuizStats();
    }

    private void updateQuizStats() {
        int topicCount = QuestionBank.getAvailableTopics().size();
        int totalQ = QuestionBank.getTotalQuestionCount();
        String topic = (String) topicCombo.getSelectedItem();
        int topicQ = topic != null ? QuestionBank.getQuestionsByTopicOrdered(topic).size() : 0;
        quizStatsLabel.setText("Total Topics: " + topicCount + "   |   Total Questions: " + totalQ
            + "   |   Current Topic: " + topicQ + " questions");
    }

    private void openAddQuestionDialog() {
        String topic = (String) topicCombo.getSelectedItem();
        if (topic == null) return;

        TeacherDashboardFrame.QuestionDialog dialog = new TeacherDashboardFrame.QuestionDialog(this, "Add New Question", null);
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
            JOptionPane.showMessageDialog(this, "Please select a question to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Question> questions = QuestionBank.getQuestionsByTopicOrdered(topic);
        TeacherDashboardFrame.QuestionDialog dialog = new TeacherDashboardFrame.QuestionDialog(this, "Edit Question", questions.get(row));
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
            JOptionPane.showMessageDialog(this, "Please select a question to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
            "Delete Question #" + (row + 1) + "?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
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
                JOptionPane.showMessageDialog(this, "Topic already exists.", "Duplicate Topic", JOptionPane.WARNING_MESSAGE);
                return;
            }
            QuestionBank.addTopic(trimmed);
            refreshTopicCombo(trimmed);
        }
    }

    private void deleteSelectedTopic() {
        String topic = (String) topicCombo.getSelectedItem();
        if (topic == null) return;

        int choice = JOptionPane.showConfirmDialog(this,
            "Delete topic '" + topic + "' and ALL its questions?",
            "Confirm Delete Topic", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            QuestionBank.removeTopic(topic);
            refreshTopicCombo(null);
        }
    }

    private void refreshTopicCombo(String selectTopic) {
        topicCombo.removeAllItems();
        List<String> topics = QuestionBank.getAvailableTopics();
        for (String t : topics) topicCombo.addItem(t);
        if (selectTopic != null && topics.contains(selectTopic)) {
            topicCombo.setSelectedItem(selectTopic);
        }
        loadQuestionsForSelectedTopic();
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        // Filter label
        JLabel filterLabel = createLabel("Filter by Topic:", FONT_BODY_BOLD, TEXT_PRIMARY);
        filterLabel.setBounds(20, 15, 120, 22);
        panel.add(filterLabel);

        List<String> topics = QuestionBank.getAvailableTopics();
        String[] filterOptions = new String[topics.size() + 1];
        filterOptions[0] = "All Topics";
        for (int i = 0; i < topics.size(); i++) {
            filterOptions[i + 1] = topics.get(i);
        }

        filterCombo = new JComboBox<>(filterOptions);
        filterCombo.setFont(FONT_BODY);
        filterCombo.setBackground(Color.WHITE);
        filterCombo.setBounds(140, 11, 220, 32);
        panel.add(filterCombo);

        // Results table
        String[] columns = {"#", "Student", "Topic", "Score", "Percentage", "Status", "Date/Time"};
        resultsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultsTable = new JTable(resultsTableModel);
        resultsTable.setFont(FONT_SMALL);
        resultsTable.setForeground(TEXT_PRIMARY);
        resultsTable.setBackground(Color.WHITE);
        resultsTable.setGridColor(BORDER_COLOR);
        resultsTable.setSelectionBackground(ACCENT_PRIMARY);
        resultsTable.setSelectionForeground(Color.WHITE);
        resultsTable.setRowHeight(32);
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

        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        resultsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        resultsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        resultsTable.getColumnModel().getColumn(6).setPreferredWidth(180);

        // Status column renderer
        resultsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
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
        scrollPane.setBounds(20, 55, 800, 380);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane);

        // Stats summary label
        resultsStatsLabel = createLabel("", FONT_SMALL, TEXT_SECONDARY);
        resultsStatsLabel.setBounds(20, 450, 700, 20);
        panel.add(resultsStatsLabel);

        // Filter listener
        filterCombo.addActionListener(e -> loadResultsData());

        return panel;
    }

    private void loadResultsData() {
        resultsTableModel.setRowCount(0);
        String selectedFilter = (String) filterCombo.getSelectedItem();
        List<StudentResult> resultsList;
        
        if (selectedFilter == null || "All Topics".equals(selectedFilter)) {
            resultsList = ResultStore.getAllResults();
        } else {
            resultsList = ResultStore.getResultsByTopic(selectedFilter);
        }

        for (int i = 0; i < resultsList.size(); i++) {
            StudentResult r = resultsList.get(i);
            resultsTableModel.addRow(new Object[]{
                (i + 1),
                r.getStudentName(),
                r.getTopic(),
                r.getScore() + " / " + r.getTotalQuestions(),
                String.format("%.0f%%", r.getPercentage()),
                r.isPassed() ? "PASSED" : "FAILED",
                r.getTimestamp()
            });
        }

        int total = resultsList.size();
        long passed = resultsList.stream().filter(StudentResult::isPassed).count();
        long failed = total - passed;
        resultsStatsLabel.setText("Total Quiz Attempts: " + total + "   |   Passed: " + passed + "   |   Failed: " + failed);
    }
}
