package com.hadyaddien.todolistapp;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepo {
    private final TasksManager tasksManager;
    private final LiveData<List<Task>> allTasks;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public TaskRepo(Application application) {
        TaskDB db = TaskDB.getInstance(application);
        tasksManager = db.tasksManager();
        allTasks = tasksManager.getAllTasks();
    }

    public void insert(Task task) {
        executor.execute(() -> {
            try {
                tasksManager.insertTask(task);
            } catch (Exception e) {
                Log.e("TaskRepo", "Error inserting task: " + e.getMessage());
            }
        });
    }
    public void update(Task task) {
        executor.execute(() -> {
            try {
                tasksManager.updateTask(task);
            } catch (Exception e) {
                Log.e("TaskRepo", "Error updating task: " + e.getMessage());
            }
        });
    }
    public void delete(Task task) {
        executor.execute(() -> {
            try {
                tasksManager.deleteTask(task);
            } catch (Exception e) {
                Log.e("TaskRepo", "Error deleting task: " + e.getMessage());
            }
        });
    }
    public LiveData<List<Task>> getAllTasks() { return allTasks; }
}