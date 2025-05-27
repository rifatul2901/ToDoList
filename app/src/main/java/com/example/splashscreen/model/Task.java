package com.example.splashscreen.model;

public class Task {
    public String title;
    public String description;
    public String deadline;
    private boolean completed;

    public Task(String title, String description, String deadline, boolean completed) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.completed = completed;
    }

    // Constructor tanpa completed (untuk backward compatibility)
    public Task(String title, String description, String deadline) {
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.completed = false; // Default false
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
