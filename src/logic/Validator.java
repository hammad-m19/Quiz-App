package logic;

import model.User;
import model.UserStore;

/**
 * Validates login credentials and user inputs.
 * Demonstrates OOP Concept: Polymorphism (Method Overloading).
 */
public class Validator {

    /** User role returned after successful login. */
    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_INVALID = "invalid";

    // ── Method Overloading: validateLogin ────────────────────────────

    /**
     * Validates login with String password. Returns true for any valid role.
     */
    public static boolean validateLogin(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return UserStore.authenticate(username, password) != null;
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
     * Returns ROLE_STUDENT, ROLE_TEACHER, ROLE_ADMIN, or ROLE_INVALID.
     */
    public static String getRole(String username, String password) {
        if (username == null || password == null) return ROLE_INVALID;
        User user = UserStore.authenticate(username, password);
        if (user != null) {
            return user.getRole();
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
