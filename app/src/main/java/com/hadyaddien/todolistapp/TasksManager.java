package com.hadyaddien.todolistapp;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface TasksManager {
    @Insert
    void insertTask(Task task);

    @Update
    void updateTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("SELECT * FROM task_table ORDER BY strftime('%s', date) ASC")
    LiveData<List<Task>> getAllTasksOrderedByDate();

}
