package com.hadyaddien.todolistapp;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;
    private boolean isCompleted;
    private String date;

    // Constructor utama yang digunakan oleh Room
    public Task(String title, String description, boolean isCompleted, String date) {
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.date = date;
    }

    // Overloading constructor dengan tiga parameter
    @Ignore
    public Task(String title, String description, boolean isCompleted) {
        this.title = title;
        this.description = description;
        this.isCompleted = isCompleted;
        this.date = ""; // Default value untuk date
    }

    // Overloading constructor dengan dua parameter
    @Ignore
    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.isCompleted = false; // Default: tidak selesai
        this.date = ""; // Default value untuk date
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
