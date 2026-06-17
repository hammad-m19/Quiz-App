package logic;

/**
 * Validates login credentials and user inputs.
 * Demonstrates OOP Concept: Polymorphism (Method Overloading).
 *
 * Hardcoded credentials for demo purposes:
 *   Student  → username: student   | password: password123
 *   Teacher  → username: teacher   | password: admin123
 */
public class Validator {

    private static final String STUDENT_USERNAME = "student";
    private static final String STUDENT_PASSWORD = "password123";

    private static final String TEACHER_USERNAME = "teacher";
    private static final String TEACHER_PASSWORD = "admin123";

    /** User role returned after successful login. */
    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_INVALID = "invalid";

    // ── Method Overloading: validateLogin ────────────────────────────

    /**
     * Validates login with String password. Returns true for any valid role.
     */
    public static boolean validateLogin(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        String u = username.trim();
        return (u.equals(STUDENT_USERNAME) && password.equals(STUDENT_PASSWORD))
            || (u.equals(TEACHER_USERNAME) && password.equals(TEACHER_PASSWORD));
    }

    /**
     * Validates login with char[] password (as returned by JPasswordField).
     * Overloaded version for Swing compatibility.
     */
    public static boolean validateLogin(String username, char[] password) {
        if (username == null || password == null) {
            return false;
        }
        return validateLogin(username, new String(password));
    }

    // ── Role Detection ──────────────────────────────────────────────

    /**
     * Returns the role of the user based on credentials.
     * Returns ROLE_STUDENT, ROLE_TEACHER, or ROLE_INVALID.
     */
    public static String getRole(String username, String password) {
        if (username == null || password == null) return ROLE_INVALID;
        String u = username.trim();
        if (u.equals(STUDENT_USERNAME) && password.equals(STUDENT_PASSWORD)) {
            return ROLE_STUDENT;
        }
        if (u.equals(TEACHER_USERNAME) && password.equals(TEACHER_PASSWORD)) {
            return ROLE_TEACHER;
        }
        return ROLE_INVALID;
    }

    /**
     * Overloaded getRole with char[] password.
     */
    public static String getRole(String username, char[] password) {
        if (username == null || password == null) return ROLE_INVALID;
        return getRole(username, new String(password));
    }

    // ── Method Overloading: validateInput ────────────────────────────

    /**
     * Validates that input is not null and not empty.
     */
    public static boolean validateInput(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Validates that input is not null, not empty, and meets minimum length.
     * Overloaded version with length constraint.
     */
    public static boolean validateInput(String text, int minLength) {
        return validateInput(text) && text.trim().length() >= minLength;
    }
}
