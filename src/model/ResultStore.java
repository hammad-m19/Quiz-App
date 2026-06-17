package model;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory store for all student quiz results.
 * Allows the teacher to view past student attempts.
 */
public class ResultStore {

    private static final List<StudentResult> results = new ArrayList<>();

    /**
     * Adds a new result to the store.
     */
    public static void addResult(StudentResult result) {
        results.add(result);
    }

    /**
     * Returns all stored results (most recent first).
     */
    public static List<StudentResult> getAllResults() {
        List<StudentResult> reversed = new ArrayList<>(results);
        java.util.Collections.reverse(reversed);
        return reversed;
    }

    /**
     * Returns results filtered by topic.
     */
    public static List<StudentResult> getResultsByTopic(String topic) {
        List<StudentResult> filtered = new ArrayList<>();
        for (int i = results.size() - 1; i >= 0; i--) {
            if (results.get(i).getTopic().equals(topic)) {
                filtered.add(results.get(i));
            }
        }
        return filtered;
    }

    /**
     * Returns the total number of stored results.
     */
    public static int getResultCount() {
        return results.size();
    }

    /**
     * Clears all stored results.
     */
    public static void clearAll() {
        results.clear();
    }
}
