package logic;

import model.User;
import model.UserStore;

public class Validator {

    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_INVALID = "invalid";


    public static boolean validateLogin(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        return UserStore.authenticate(username, password) != null;
    }

    public static boolean validateLogin(String username, char[] password) {
        if (username == null || password == null) {
            return false;
        }
        return validateLogin(username, new String(password));
    }


    public static String getRole(String username, String password) {
        if (username == null || password == null) return ROLE_INVALID;
        User user = UserStore.authenticate(username, password);
        if (user != null) {
            return user.getRole();
        }
        return ROLE_INVALID;
    }

    public static String getRole(String username, char[] password) {
        if (username == null || password == null) return ROLE_INVALID;
        return getRole(username, new String(password));
    }


    public static boolean validateInput(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean validateInput(String text, int minLength) {
        return validateInput(text) && text.trim().length() >= minLength;
    }
}
