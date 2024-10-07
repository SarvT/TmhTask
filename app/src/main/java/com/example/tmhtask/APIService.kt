package com.example.tmhtask

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {
        @GET("task")
        fun getTasks(): Call<List<Task>>

        @POST("task")
        fun createTask(@Body task: Task): Call<Task>

        @PATCH("task/{id}")
        fun updateTask(@Path("id") id: Int, @Body task: Task): Call<Task>

        @DELETE("task/{id}")
        fun deleteTask(@Path("id") id: Int): Call<Void>
}