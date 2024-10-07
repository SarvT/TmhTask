package com.example.tmhtask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.tmhtask.databinding.ActivityMainBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var taskAdapter: TaskAdapter
    val baseUri = "https://mp25c596583bdc671109.free.beeceptor.com/"
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUri) // Base URL
        .addConverterFactory(GsonConverterFactory.create()) // Converter for JSON
        .build()

    val apiService = retrofit.create(APIService::class.java)
    private lateinit var selectedAction:String
            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSpinner()

        binding.submitButton.setOnClickListener {
            when (selectedAction) {
                "Fetch" -> fetchTasks()
                "Add" -> addTask()
                "Update" -> updateTask(Task(1, "Updated Task", true)) // Example task
                "Delete" -> deleteTask(1) // Example task ID
            }
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(emptyList())
        binding.taskRV.layoutManager = LinearLayoutManager(this)
        binding.taskRV.adapter = taskAdapter
    }

    private fun setupSpinner() {
        val actions = resources.getStringArray(R.array.actions_array)
        val spinner: Spinner = findViewById(R.id.actionSpinner)
        if (spinner != null) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, actions)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    selectedAction = actions[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Optionally handle no selection
                }
            }
        }
    }

    private fun fetchTasks() {
        apiService.getTasks().enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                if (response.isSuccessful) {
                    val tasks: List<Task> = response.body() ?: emptyList()
                    taskAdapter.updateTasks(tasks)
                }
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun addTask() {
        val newTask = Task(id = 0, title = "New Task", completed = false)
        apiService.createTask(newTask).enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                if (response.isSuccessful) {
                    val tasks = response.body()
                    val taskList: List<Task> = listOf(tasks) as List<Task>
                    taskAdapter.updateTasks(taskList)
                }
            }

            override fun onFailure(call: Call<Task>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun updateTask(task: Task) {
        apiService.updateTask(task.id, task).enqueue(object : Callback<Task> {
            override fun onResponse(call: Call<Task>, response: Response<Task>) {
                if (response.isSuccessful) {

                    val tasks = response.body()
                    val taskList: List<Task> = listOf(tasks) as List<Task>
                    taskAdapter.updateTasks(taskList)
                }
            }

            override fun onFailure(call: Call<Task>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun deleteTask(id: Int) {
        apiService.deleteTask(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

                taskAdapter.updateTasks(emptyList())
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}