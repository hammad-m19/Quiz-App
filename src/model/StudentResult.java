package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StudentResult {

    private String studentName;
    private String topic;
    private int score;
    private int totalQuestions;
    private double percentage;
    private boolean passed;
    private String timestamp;

    public StudentResult(String studentName, String topic, int score,
                         int totalQuestions, double percentage, boolean passed) {
        this.studentName = studentName;
        this.topic = topic;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.percentage = percentage;
        this.passed = passed;
        this.timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getStudentName() { return studentName; }
    public String getTopic() { return topic; }
    public int getScore() { return score; }
    public int getTotalQuestions() { return totalQuestions; }
    public double getPercentage() { return percentage; }
    public boolean isPassed() { return passed; }
    public String getTimestamp() { return timestamp; }
}
