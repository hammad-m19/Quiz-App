package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores all quiz questions in-memory using ArrayLists, grouped by topic.
 * Now supports mutable operations (add, edit, delete) for the Teacher Portal.
 * Acts as an in-memory data repository (no database needed).
 */
public class QuestionBank {

    // ── Mutable storage — initialized once with defaults ────────────
    private static final Map<String, List<Question>> topicQuestions = new HashMap<>();

    static {
        // Load default questions on first access
        loadDefaults();
    }

    private static void loadDefaults() {

        // ── Core Java ──────────────────────────────────────────────
        List<Question> java = new ArrayList<>();
        java.add(new Question(
            "Which keyword is used to create a new instance of a class in Java?",
            new String[]{"class", "new", "create", "instance"}, 1));
        java.add(new Question(
            "What is the default value of a boolean variable in Java?",
            new String[]{"true", "false", "null", "0"}, 1));
        java.add(new Question(
            "Which of the following is NOT a primitive data type in Java?",
            new String[]{"int", "float", "String", "char"}, 2));
        java.add(new Question(
            "What does JVM stand for?",
            new String[]{"Java Very Machine", "Java Virtual Machine", "Java Variable Method", "Java Verified Module"}, 1));
        java.add(new Question(
            "Which access modifier makes a member accessible only within its own class?",
            new String[]{"public", "protected", "private", "default"}, 2));
        java.add(new Question(
            "Which method is the entry point of a Java application?",
            new String[]{"start()", "run()", "main()", "init()"}, 2));
        java.add(new Question(
            "What is the size of an int variable in Java?",
            new String[]{"8 bits", "16 bits", "32 bits", "64 bits"}, 2));
        java.add(new Question(
            "Which collection class allows duplicate elements?",
            new String[]{"HashSet", "TreeSet", "ArrayList", "HashMap"}, 2));
        java.add(new Question(
            "Which keyword is used to inherit a class in Java?",
            new String[]{"implements", "extends", "inherits", "super"}, 1));
        java.add(new Question(
            "What does the 'final' keyword prevent when applied to a class?",
            new String[]{"Instantiation", "Inheritance", "Compilation", "Serialization"}, 1));
        topicQuestions.put("Core Java", java);

        // ── Software Engineering ───────────────────────────────────
        List<Question> se = new ArrayList<>();
        se.add(new Question(
            "Which software development model follows a linear sequential approach?",
            new String[]{"Agile", "Spiral", "Waterfall", "RAD"}, 2));
        se.add(new Question(
            "What does SDLC stand for?",
            new String[]{"Software Design Life Cycle", "Software Development Life Cycle",
                         "System Development Logic Cycle", "Software Deployment Launch Cycle"}, 1));
        se.add(new Question(
            "Which diagram in UML shows the interaction between objects over time?",
            new String[]{"Class Diagram", "Use Case Diagram", "Sequence Diagram", "Activity Diagram"}, 2));
        se.add(new Question(
            "What is the primary goal of requirement engineering?",
            new String[]{"Writing code", "Testing software", "Gathering and documenting user needs", "Deploying the system"}, 2));
        se.add(new Question(
            "Which testing level checks individual components or functions?",
            new String[]{"Integration Testing", "System Testing", "Unit Testing", "Acceptance Testing"}, 2));
        se.add(new Question(
            "In Agile methodology, what is a 'Sprint'?",
            new String[]{"A bug report", "A short development iteration", "A deployment phase", "A testing framework"}, 1));
        se.add(new Question(
            "Which design pattern ensures only one instance of a class exists?",
            new String[]{"Observer", "Factory", "Singleton", "Adapter"}, 2));
        se.add(new Question(
            "What does the term 'Refactoring' mean?",
            new String[]{"Adding new features", "Restructuring code without changing behavior",
                         "Removing all comments", "Deploying to production"}, 1));
        se.add(new Question(
            "Which principle states that a class should have only one reason to change?",
            new String[]{"Open/Closed Principle", "Single Responsibility Principle",
                         "Liskov Substitution", "Dependency Inversion"}, 1));
        se.add(new Question(
            "What type of maintenance involves fixing bugs after delivery?",
            new String[]{"Adaptive", "Perfective", "Corrective", "Preventive"}, 2));
        topicQuestions.put("Software Engineering", se);

        // ── Database Systems ───────────────────────────────────────
        List<Question> db = new ArrayList<>();
        db.add(new Question(
            "Which SQL command is used to retrieve data from a database?",
            new String[]{"INSERT", "UPDATE", "SELECT", "DELETE"}, 2));
        db.add(new Question(
            "What does DBMS stand for?",
            new String[]{"Data Base Management System", "Digital Binary Management System",
                         "Data Backup Main System", "Database Manipulation Software"}, 0));
        db.add(new Question(
            "Which key uniquely identifies each record in a table?",
            new String[]{"Foreign Key", "Primary Key", "Candidate Key", "Super Key"}, 1));
        db.add(new Question(
            "What is normalization in databases?",
            new String[]{"Adding redundancy", "Organizing data to reduce redundancy",
                         "Deleting duplicate tables", "Encrypting data"}, 1));
        db.add(new Question(
            "Which SQL clause is used to filter records?",
            new String[]{"ORDER BY", "GROUP BY", "WHERE", "HAVING"}, 2));
        db.add(new Question(
            "What does the JOIN operation do in SQL?",
            new String[]{"Deletes tables", "Combines rows from two or more tables",
                         "Creates a new database", "Backs up data"}, 1));
        db.add(new Question(
            "Which normal form eliminates transitive dependencies?",
            new String[]{"1NF", "2NF", "3NF", "BCNF"}, 2));
        db.add(new Question(
            "What is a Foreign Key?",
            new String[]{"A key from another country", "A field that links to a primary key in another table",
                         "An encrypted key", "A unique identifier"}, 1));
        db.add(new Question(
            "Which command is used to remove a table from a database?",
            new String[]{"DELETE TABLE", "REMOVE TABLE", "DROP TABLE", "ERASE TABLE"}, 2));
        db.add(new Question(
            "What type of relationship allows many records in one table to relate to many in another?",
            new String[]{"One-to-One", "One-to-Many", "Many-to-Many", "Self-referencing"}, 2));
        topicQuestions.put("Database Systems", db);
    }

    // ── Read Operations ─────────────────────────────────────────────

    /**
     * Returns the list of available quiz topics.
     */
    public static List<String> getAvailableTopics() {
        return new ArrayList<>(topicQuestions.keySet());
    }

    /**
     * Returns a shuffled copy of questions for the given topic (used by students).
     */
    public static List<Question> getQuestionsByTopic(String topic) {
        List<Question> original = topicQuestions.get(topic);
        if (original == null) return new ArrayList<>();
        // Return a shuffled copy so the original order is preserved
        List<Question> copy = new ArrayList<>(original);
        Collections.shuffle(copy);
        return copy;
    }

    /**
     * Returns the original (unshuffled) list of questions for a topic (used by teachers).
     */
    public static List<Question> getQuestionsByTopicOrdered(String topic) {
        List<Question> original = topicQuestions.get(topic);
        if (original == null) return new ArrayList<>();
        return original;
    }

    // ── Write Operations (Teacher Portal) ───────────────────────────

    /**
     * Adds a new question to the specified topic.
     */
    public static void addQuestion(String topic, Question question) {
        topicQuestions.computeIfAbsent(topic, k -> new ArrayList<>()).add(question);
    }

    /**
     * Removes a question at the given index from the specified topic.
     */
    public static boolean removeQuestion(String topic, int index) {
        List<Question> list = topicQuestions.get(topic);
        if (list != null && index >= 0 && index < list.size()) {
            list.remove(index);
            return true;
        }
        return false;
    }

    /**
     * Replaces a question at the given index in the specified topic.
     */
    public static boolean updateQuestion(String topic, int index, Question updated) {
        List<Question> list = topicQuestions.get(topic);
        if (list != null && index >= 0 && index < list.size()) {
            list.set(index, updated);
            return true;
        }
        return false;
    }

    /**
     * Adds a new topic with an empty question list.
     */
    public static void addTopic(String topic) {
        topicQuestions.putIfAbsent(topic, new ArrayList<>());
    }

    /**
     * Removes a topic and all its questions.
     */
    public static boolean removeTopic(String topic) {
        return topicQuestions.remove(topic) != null;
    }

    /**
     * Returns the total number of questions across all topics.
     */
    public static int getTotalQuestionCount() {
        int count = 0;
        for (List<Question> list : topicQuestions.values()) {
            count += list.size();
        }
        return count;
    }
}
