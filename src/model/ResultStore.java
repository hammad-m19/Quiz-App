package model;

import java.util.ArrayList;
import java.util.List;

public class ResultStore {

    private static final List<StudentResult> results = new ArrayList<>();

    public static void addResult(StudentResult result) {
        results.add(result);
    }

    public static List<StudentResult> getAllResults() {
        List<StudentResult> reversed = new ArrayList<>(results);
        java.util.Collections.reverse(reversed);
        return reversed;
    }

    public static List<StudentResult> getResultsByTopic(String topic) {
        List<StudentResult> filtered = new ArrayList<>();
        for (int i = results.size() - 1; i >= 0; i--) {
            if (results.get(i).getTopic().equals(topic)) {
                filtered.add(results.get(i));
            }
        }
        return filtered;
    }

    public static int getResultCount() {
        return results.size();
    }

    public static void clearAll() {
        results.clear();
    }
}
