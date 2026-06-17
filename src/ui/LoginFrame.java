package ui;

import logic.Validator;
import model.QuestionBank;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Clean, simple login screen — username, password, topic selection.
 * Routes students to QuizFrame, teachers to TeacherDashboardFrame,
 * and admins to AdminDashboardFrame.
 */
public class LoginFrame extends BaseFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> topicCombo;
    private JButton loginButton;
    private JLabel errorLabel;

    public LoginFrame() {
        super("Student Quiz Application", 460, 600);
        initializeUI();
        setupListeners();
        setVisible(true);
    }

    @Override
    protected void initializeUI() {

        JPanel bg = new JPanel();
        bg.setLayout(null);
        bg.setBackground(BG_PRIMARY);
        bg.setBounds(0, 0, 460, 600);
        setContentPane(bg);

        // ── Title ──────────────────────────────────────────────────
        JLabel title = createLabel("Student Quiz App", FONT_TITLE, TEXT_PRIMARY);
        title.setBounds(0, 35, 460, 32);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        bg.add(title);

        JLabel subtitle = createLabel("Sign in to get started", FONT_SMALL, TEXT_SECONDARY);
        subtitle.setBounds(0, 70, 460, 18);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        bg.add(subtitle);

        // ── Login Card ─────────────────────────────────────────────
        JPanel card = createCardPanel();
        card.setBounds(50, 110, 360, 380);
        bg.add(card);

        // Username
        JLabel userLabel = createLabel("Username", FONT_BODY_BOLD, TEXT_PRIMARY);
        userLabel.setBounds(25, 25, 310, 20);
        card.add(userLabel);

        usernameField = createStyledTextField();
        usernameField.setBounds(25, 48, 310, 40);
        card.add(usernameField);

        // Password
        JLabel passLabel = createLabel("Password", FONT_BODY_BOLD, TEXT_PRIMARY);
        passLabel.setBounds(25, 100, 310, 20);
        card.add(passLabel);

        passwordField = createStyledPasswordField();
        passwordField.setBounds(25, 123, 310, 40);
        card.add(passwordField);

        // Topic
        JLabel topicLabel = createLabel("Quiz Topic (for students)", FONT_BODY_BOLD, TEXT_PRIMARY);
        topicLabel.setBounds(25, 178, 310, 20);
        card.add(topicLabel);

        List<String> topics = QuestionBank.getAvailableTopics();
        topicCombo = new JComboBox<>(topics.toArray(new String[0]));
        topicCombo.setFont(FONT_BODY);
        topicCombo.setForeground(TEXT_PRIMARY);
        topicCombo.setBackground(Color.WHITE);
        topicCombo.setBounds(25, 201, 310, 40);
        topicCombo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        card.add(topicCombo);

        // Error label
        errorLabel = createLabel("", FONT_SMALL, ACCENT_DANGER);
        errorLabel.setBounds(25, 252, 310, 18);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(errorLabel);

        // Login button
        loginButton = createStyledButton("Sign In", ACCENT_PRIMARY, Color.WHITE);
        loginButton.setBounds(25, 278, 310, 42);
        card.add(loginButton);

        // Credentials hints
        JLabel hint1 = createLabel("Student: student / password123", FONT_SMALL, TEXT_MUTED);
        hint1.setBounds(25, 330, 310, 16);
        hint1.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(hint1);

        JLabel hint2 = createLabel("Teacher: teacher / admin123", FONT_SMALL, TEXT_MUTED);
        hint2.setBounds(25, 348, 310, 16);
        hint2.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(hint2);

        JLabel hint3 = createLabel("Admin: admin / admin123", FONT_SMALL, TEXT_MUTED);
        hint3.setBounds(25, 366, 310, 16);
        hint3.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(hint3);
    }

    @Override
    protected void setupListeners() {
        loginButton.addActionListener(e -> attemptLogin());
        passwordField.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        char[] password = passwordField.getPassword();

        if (!Validator.validateInput(username)) {
            showError("Please enter your username.");
            return;
        }
        if (password.length == 0) {
            showError("Please enter your password.");
            return;
        }

        String role = Validator.getRole(username, password);

        switch (role) {
            case Validator.ROLE_STUDENT:
                String selectedTopic = (String) topicCombo.getSelectedItem();
                dispose();
                new QuizFrame(selectedTopic, username);
                break;
            case Validator.ROLE_TEACHER:
                dispose();
                new TeacherDashboardFrame();
                break;
            case Validator.ROLE_ADMIN:
                dispose();
                new AdminDashboardFrame(username.trim());
                break;
            default:
                showError("Invalid username or password.");
                passwordField.setText("");
                break;
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        javax.swing.Timer flashTimer = new javax.swing.Timer(3000, ev -> errorLabel.setText(""));
        flashTimer.setRepeats(false);
        flashTimer.start();
    }
}
