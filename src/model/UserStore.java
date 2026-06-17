package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory store for all application users.
 * Seeds default admin, teacher, and student accounts on startup.
 */
public class UserStore {

    private static final Map<String, User> users = new LinkedHashMap<>();

    static {
        users.put("admin", new User("admin", "123456", "admin"));
        users.put("teacher", new User("teacher", "123456", "teacher"));
        users.put("student", new User("student", "123456", "student"));
    }

    public static User authenticate(String username, String password) {
        if (username == null || password == null) return null;
        User user = users.get(username.trim().toLowerCase());
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public static User findByUsername(String username) {
        if (username == null) return null;
        return users.get(username.trim().toLowerCase());
    }

    public static List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public static List<User> getUsersByRole(String role) {
        List<User> filtered = new ArrayList<>();
        for (User user : users.values()) {
            if (user.getRole().equals(role)) {
                filtered.add(user);
            }
        }
        return filtered;
    }

    public static boolean usernameExists(String username) {
        if (username == null) return false;
        return users.containsKey(username.trim().toLowerCase());
    }

    public static boolean addUser(User user) {
        if (user == null || !isValidUser(user)) return false;
        String key = user.getUsername().trim().toLowerCase();
        if (users.containsKey(key)) return false;
        user.setUsername(key);
        users.put(key, user);
        return true;
    }

    public static boolean updateUser(String originalUsername, User updatedUser) {
        if (originalUsername == null || updatedUser == null || !isValidUser(updatedUser)) {
            return false;
        }
        String originalKey = originalUsername.trim().toLowerCase();
        String newKey = updatedUser.getUsername().trim().toLowerCase();
        if (!users.containsKey(originalKey)) return false;
        if (!originalKey.equals(newKey) && users.containsKey(newKey)) return false;

        users.remove(originalKey);
        updatedUser.setUsername(newKey);
        users.put(newKey, updatedUser);
        return true;
    }

    public static boolean removeUser(String username) {
        if (username == null) return false;
        String key = username.trim().toLowerCase();
        if (!users.containsKey(key)) return false;
        if ("admin".equals(users.get(key).getRole()) && countAdmins() <= 1) {
            return false;
        }
        users.remove(key);
        return true;
    }

    public static int countByRole(String role) {
        int count = 0;
        for (User user : users.values()) {
            if (user.getRole().equals(role)) count++;
        }
        return count;
    }

    public static int countAdmins() {
        return countByRole("admin");
    }

    private static boolean isValidUser(User user) {
        return user.getUsername() != null && !user.getUsername().trim().isEmpty()
            && user.getPassword() != null && !user.getPassword().isEmpty()
            && user.getRole() != null && !user.getRole().trim().isEmpty();
    }
}
