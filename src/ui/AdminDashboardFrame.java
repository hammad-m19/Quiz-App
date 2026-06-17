package ui;

import logic.Validator;
import model.QuestionBank;
import model.ResultStore;
import model.StudentResult;
import model.User;
import model.UserStore;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * Admin Dashboard — manage all users and access quiz administration tools.
 */
public class AdminDashboardFrame extends BaseFrame {

    private final String loggedInUsername;
    private JPanel bgPanel;
    private JTable usersTable;
    private DefaultTableModel tableModel;
    private JLabel statsLabel;
    private JButton addUserBtn;
    private JButton editUserBtn;
    private JButton deleteUserBtn;
    private JButton manageQuizzesBtn;
    private JButton viewResultsBtn;
    private JButton logoutBtn;

    public AdminDashboardFrame(String loggedInUsername) {
        super("Admin Dashboard", 880, 650);
        this.loggedInUsername = loggedInUsername.trim().toLowerCase();
        initializeUI();
        setupListeners();
        loadUsers();
        setVisible(true);
    }

    @Override
    protected void initializeUI() {
        bgPanel = new JPanel();
        bgPanel.setLayout(null);
        bgPanel.setBackground(BG_PRIMARY);
        bgPanel.setBounds(0, 0, 880, 650);
        setContentPane(bgPanel);

        JLabel title = createLabel("Admin Dashboard", FONT_TITLE, TEXT_PRIMARY);
        title.setBounds(25, 12, 350, 32);
        bgPanel.add(title);

        statsLabel = createLabel("", FONT_SMALL, TEXT_SECONDARY);
        statsLabel.setBounds(25, 46, 600, 16);
        bgPanel.add(statsLabel);

        logoutBtn = createStyledButton("Logout", ACCENT_DANGER, Color.WHITE);
        logoutBtn.setBounds(740, 15, 110, 36);
        bgPanel.add(logoutBtn);

        JPanel userCard = createCardPanel();
        userCard.setBounds(25, 72, 830, 55);
        bgPanel.add(userCard);

        JLabel userSectionLabel = createLabel("User Accounts", FONT_BODY_BOLD, TEXT_PRIMARY);
        userSectionLabel.setBounds(15, 15, 200, 22);
        userCard.add(userSectionLabel);

        String[] columns = {"#", "Username", "Role"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usersTable = new JTable(tableModel);
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

        usersTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        usersTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        usersTable.getColumnModel().getColumn(2).setPreferredWidth(120);

        usersTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected && value != null) {
                    String role = value.toString().toLowerCase();
                    if ("admin".equals(role)) {
                        c.setForeground(ACCENT_WARNING);
                    } else if ("teacher".equals(role)) {
                        c.setForeground(ACCENT_PRIMARY);
                    } else {
                        c.setForeground(ACCENT_SUCCESS);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBounds(25, 140, 830, 390);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        bgPanel.add(scrollPane);

        addUserBtn = createStyledButton("+ Add User", ACCENT_SUCCESS, Color.WHITE);
        addUserBtn.setBounds(25, 548, 150, 40);
        bgPanel.add(addUserBtn);

        editUserBtn = createStyledButton("Edit Selected", ACCENT_PRIMARY, Color.WHITE);
        editUserBtn.setBounds(190, 548, 150, 40);
        bgPanel.add(editUserBtn);

        deleteUserBtn = createStyledButton("Delete Selected", ACCENT_DANGER, Color.WHITE);
        deleteUserBtn.setBounds(355, 548, 160, 40);
        bgPanel.add(deleteUserBtn);

        manageQuizzesBtn = createStyledButton("Manage Quizzes", ACCENT_PRIMARY, Color.WHITE);
        manageQuizzesBtn.setBounds(530, 548, 155, 40);
        bgPanel.add(manageQuizzesBtn);

        viewResultsBtn = createStyledButton("View Results", ACCENT_WARNING, Color.WHITE);
        viewResultsBtn.setBounds(700, 548, 155, 40);
        bgPanel.add(viewResultsBtn);
    }

    @Override
    protected void setupListeners() {
        addUserBtn.addActionListener(e -> openAddUserDialog());
        editUserBtn.addActionListener(e -> openEditUserDialog());
        deleteUserBtn.addActionListener(e -> deleteSelectedUser());
        manageQuizzesBtn.addActionListener(e -> new TeacherDashboardFrame());
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

    private void loadUsers() {
        tableModel.setRowCount(0);
        List<User> users = UserStore.getAllUsers();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            tableModel.addRow(new Object[]{
                (i + 1),
                user.getUsername(),
                user.getRoleDisplayName()
            });
        }
        updateStats();
    }

    private void updateStats() {
        int students = UserStore.countByRole(Validator.ROLE_STUDENT);
        int teachers = UserStore.countByRole(Validator.ROLE_TEACHER);
        int admins = UserStore.countByRole(Validator.ROLE_ADMIN);
        statsLabel.setText("Students: " + students + "   |   Teachers: " + teachers
            + "   |   Admins: " + admins + "   |   Total Users: " + UserStore.getAllUsers().size());
    }

    private User getSelectedUser() {
        int row = usersTable.getSelectedRow();
        if (row < 0) return null;
        String username = (String) tableModel.getValueAt(row, 1);
        return UserStore.findByUsername(username);
    }

    private void openAddUserDialog() {
        UserDialog dialog = new UserDialog(this, "Add New User", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            User newUser = dialog.getUser();
            if (UserStore.addUser(newUser)) {
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Could not create user. Username may already exist.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openEditUserDialog() {
        User existing = getSelectedUser();
        if (existing == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserDialog dialog = new UserDialog(this, "Edit User", existing);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            User updated = dialog.getUser();
            if (UserStore.countAdmins() <= 1
                && Validator.ROLE_ADMIN.equals(existing.getRole())
                && !Validator.ROLE_ADMIN.equals(updated.getRole())) {
                JOptionPane.showMessageDialog(this,
                    "Cannot remove the last admin account.",
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (UserStore.updateUser(existing.getUsername(), updated)) {
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Could not update user. Username may already be taken.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedUser() {
        User user = getSelectedUser();
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (user.getUsername().equals(loggedInUsername)) {
            JOptionPane.showMessageDialog(this,
                "You cannot delete your own account while logged in.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (Validator.ROLE_ADMIN.equals(user.getRole()) && UserStore.countAdmins() <= 1) {
            JOptionPane.showMessageDialog(this,
                "Cannot delete the last admin account.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
            "Delete user '" + user.getUsername() + "' (" + user.getRoleDisplayName() + ")?",
            "Delete User", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            if (UserStore.removeUser(user.getUsername())) {
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Could not delete user.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
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

        String[] columns = {"#", "Student", "Topic", "Score", "Percentage", "Status", "Date/Time"};
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

        JLabel summaryLabel = createLabel("", FONT_SMALL, TEXT_SECONDARY);
        summaryLabel.setBounds(20, 410, 500, 18);
        panel.add(summaryLabel);

        JButton closeBtn = createStyledButton("Close", BG_CARD, TEXT_PRIMARY);
        closeBtn.setBounds(610, 410, 110, 36);
        panel.add(closeBtn);
        closeBtn.addActionListener(e -> dialog.dispose());

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
                resultsTableModel.addRow(new Object[]{
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

        loadResults.run();
        filterCombo.addActionListener(e -> loadResults.run());
        dialog.setVisible(true);
    }

    private static class UserDialog extends JDialog {

        private JTextField usernameField;
        private JPasswordField passwordField;
        private JComboBox<String> roleCombo;
        private boolean confirmed = false;
        private final User existing;

        public UserDialog(JFrame parent, String title, User existing) {
            super(parent, title, true);
            this.existing = existing;
            setSize(420, 300);
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

            JLabel userLabel = new JLabel("Username:");
            userLabel.setFont(labelFont);
            userLabel.setForeground(textCol);
            userLabel.setBounds(20, 20, 100, 18);
            panel.add(userLabel);

            usernameField = new JTextField();
            usernameField.setFont(bodyFont);
            usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderCol, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            usernameField.setBounds(20, 42, 370, 36);
            panel.add(usernameField);

            JLabel passLabel = new JLabel("Password:");
            passLabel.setFont(labelFont);
            passLabel.setForeground(textCol);
            passLabel.setBounds(20, 90, 100, 18);
            panel.add(passLabel);

            passwordField = new JPasswordField();
            passwordField.setFont(bodyFont);
            passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderCol, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            passwordField.setBounds(20, 112, 370, 36);
            panel.add(passwordField);

            JLabel roleLabel = new JLabel("Role:");
            roleLabel.setFont(labelFont);
            roleLabel.setForeground(textCol);
            roleLabel.setBounds(20, 160, 100, 18);
            panel.add(roleLabel);

            roleCombo = new JComboBox<>(new String[]{"Student", "Teacher", "Admin"});
            roleCombo.setFont(bodyFont);
            roleCombo.setBounds(20, 182, 180, 32);
            panel.add(roleCombo);

            JButton saveBtn = new JButton("Save");
            saveBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            saveBtn.setForeground(Color.WHITE);
            saveBtn.setBackground(new Color(30, 142, 62));
            saveBtn.setFocusPainted(false);
            saveBtn.setBorderPainted(false);
            saveBtn.setOpaque(true);
            saveBtn.setBounds(190, 230, 95, 36);
            panel.add(saveBtn);

            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
            cancelBtn.setForeground(textCol);
            cancelBtn.setBackground(new Color(237, 241, 247));
            cancelBtn.setFocusPainted(false);
            cancelBtn.setBorderPainted(false);
            cancelBtn.setOpaque(true);
            cancelBtn.setBounds(295, 230, 95, 36);
            panel.add(cancelBtn);

            if (existing != null) {
                usernameField.setText(existing.getUsername());
                passwordField.setText(existing.getPassword());
                roleCombo.setSelectedItem(existing.getRoleDisplayName());
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
            if (usernameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username cannot be empty.",
                    "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (passwordField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Password cannot be empty.",
                    "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            if (passwordField.getPassword().length < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.",
                    "Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        }

        public boolean isConfirmed() { return confirmed; }

        public User getUser() {
            String roleKey = roleCombo.getSelectedItem().toString().toLowerCase();
            return new User(
                usernameField.getText().trim(),
                new String(passwordField.getPassword()),
                roleKey
            );
        }
    }
}
