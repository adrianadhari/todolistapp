package com.hadyaddien.todolistapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hadyaddien.todolistapp.TaskAdapter.OnItemClickListener;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // Tag untuk keperluan debugging log
    private static final String TAG = "MainActivity";

    private TaskAdapter adapter;
    private TaskRepo taskRepository; // Menggunakan TaskRepository
    private EditText editTextTitle, editTextDescription, editTextDate;
    private Button buttonAddTask;
    private int selectedYear, selectedMonth, selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate called"); // Log awal lifecycle onCreate

        setContentView(R.layout.activity_main);
        Log.d(TAG, "Layout set with activity_main");

        // Inisialisasi RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inisialisasi adapter
        adapter = new TaskAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // button
        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        editTextDate = findViewById(R.id.edit_text_date);
        buttonAddTask = findViewById(R.id.button_add_task);

        if (editTextTitle == null || editTextDescription == null || editTextDate == null || buttonAddTask == null) {
            Log.e(TAG, "EditText atau EditDescription atau EditDate atau ButtonAdd tidak ditemukan di layout");
            return; // Hentikan eksekusi jika elemen null
        } else {
            Log.d(TAG, "EditText  dan EditDescription dan EditDate dan ButtonAdd berhasil diinisialisasi");
        }

        // Inisialisasi TaskRepository untuk mengelola data
        taskRepository = new TaskRepo(getApplication());
        if (taskRepository == null) {
            Log.e(TAG, "TaskRepository tidak berhasil diinisialisasi");
            return;
        }

        // Mengatur tombol untuk menambahkan task
        buttonAddTask.setOnClickListener(v -> addNewTask());

        // Mengatur Listener untuk EditText tanggal
        editTextDate.setOnClickListener(v -> showDatePickerDialog(editTextDate));

        // Mengatur Listener untuk TaskAdapter
        adapter.setOnItemClickListener(new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                showTaskDetail(task); // Menampilkan detail task
            }

            @Override
            public void onItemEdit(Task task) {
                editTask(task); // Mengedit task
            }

            @Override
            public void onItemDelete(Task task) {
                deleteTask(task); // Menghapus task
            }

            @Override
            public void onTaskChecked(Task task, boolean isChecked) {
                task.setCompleted(isChecked);
                taskRepository.update(task); // Update task ke database
                refreshTaskList();;
            }
        });
        // Menampilkan semua task yang ada
        refreshTaskList();
    }

    // Method untuk menampilkan DatePickerDialog
    private void showDatePickerDialog(EditText editTextDate) {
        // Mengambil tanggal saat ini
        try {
            Calendar calendar = Calendar.getInstance();
            selectedYear = calendar.get(Calendar.YEAR);
            selectedMonth = calendar.get(Calendar.MONTH);
            selectedDay = calendar.get(Calendar.DAY_OF_MONTH);

            // Menampilkan DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    MainActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        try {
                            selectedYear = year;
                            selectedMonth = month;
                            selectedDay = dayOfMonth;
                            editTextDate.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
                        } catch (Exception e) {
                            Log.e(TAG, "Error saat memilih tanggal: " + e.getMessage());
                        }
                    },
                    selectedYear, selectedMonth, selectedDay
            );
            datePickerDialog.show();
        } catch (Exception e) {
            Toast.makeText(this, "Error showing date picker: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Method untuk menambahkan task baru
    private void addNewTask() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please enter title, description, and date!", Toast.LENGTH_SHORT).show();
            return;
        }

        Task newTask = new Task(title, description, false, date);
        taskRepository.insert(newTask); // Simpan task ke database
        Toast.makeText(this, "Task added!", Toast.LENGTH_SHORT).show();
        refreshTaskList(); // Refresh daftar task

        // Kosongkan input field setelah task ditambahkan
        editTextTitle.setText("");
        editTextDescription.setText("");
        editTextDate.setText("");
    }

    // Method untuk mengambil data task dan menampilkannya di RecyclerView
    private void refreshTaskList() {
        taskRepository.getAllTasksOrderedByDate().observe(this, tasks -> {
            if (tasks == null || tasks.isEmpty()) {
                Log.d(TAG, "Tidak ada task yang ditemukan");
            } else {
                Log.d(TAG, tasks.size() + " task berhasil dimuat");
            }
            adapter.setTasks(tasks);
        });
    }

    // Fungsi untuk menampilkan detail task
    private void showTaskDetail(Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(task.getTitle());
        builder.setMessage("Description: " + task.getDescription() + "\nDate: " + task.getDate());
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Fungsi untuk mengedit task
    private void editTask(Task task) {
        // Membuat dialog untuk mengedit task
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");

        // Menambahkan form input untuk judul, deskripsi, dan tanggal
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_task, null);
        EditText editTextTitle = dialogView.findViewById(R.id.edit_text_edit_title);
        EditText editTextDescription = dialogView.findViewById(R.id.edit_text_edit_description);
        EditText editTextDate = dialogView.findViewById(R.id.edit_text_edit_date);

        // Mengisi EditText dengan data task saat ini
        editTextTitle.setText(task.getTitle());
        editTextDescription.setText(task.getDescription());
        editTextDate.setText(task.getDate());

        builder.setView(dialogView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = editTextTitle.getText().toString().trim();
            String newDescription = editTextDescription.getText().toString().trim();
            String newDate = editTextDate.getText().toString().trim();

            if (newTitle.isEmpty() || newDescription.isEmpty() || newDate.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Update task dengan data baru
            task.setTitle(newTitle);
            task.setDescription(newDescription);
            task.setDate(newDate);

            taskRepository.update(task); // Update task di database
            refreshTaskList(); // Refresh daftar task

            Toast.makeText(MainActivity.this, "Task updated!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // Fungsi untuk menghapus task
    private void deleteTask(Task task) {
        // Menampilkan dialog konfirmasi sebelum menghapus task
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Task");
        builder.setMessage("Are you sure you want to delete this task?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Menghapus task dari database
            taskRepository.delete(task);
            refreshTaskList(); // Refresh daftar task setelah penghapusan

            Toast.makeText(MainActivity.this, "Task deleted!", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss()); // Menutup dialog tanpa menghapus
        builder.show();
        refreshTaskList(); // Menampilkan task terbaru
    }
}