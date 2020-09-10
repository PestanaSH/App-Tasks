package com.example.tasks.service.repository

import android.content.Context
import com.example.tasks.R
import com.example.tasks.service.Model.TaskModel
import com.example.tasks.service.constants.TaskConstants
import com.example.tasks.service.listener.APIListener
import com.example.tasks.service.repository.remote.RetrofitClient
import com.example.tasks.service.repository.remote.TaskService
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskRepository(val context: Context) {

    private val mRemote = RetrofitClient.createService(TaskService::class.java)

    fun all(listener: APIListener<List<TaskModel>>) {
        val call: Call<List<TaskModel>> = mRemote.all()
        list(call, listener)
    }

    fun nextWeek(listener: APIListener<List<TaskModel>>) {
        val call: Call<List<TaskModel>> = mRemote.newtWeek()
        list(call, listener)
    }

    fun overdue(listener: APIListener<List<TaskModel>>) {
        val call: Call<List<TaskModel>> = mRemote.newtWeek()
        list(call, listener)
    }

    private fun list(call: Call<List<TaskModel>>, listener: APIListener<List<TaskModel>>) {
        call.enqueue(object : Callback<List<TaskModel>>{
            override fun onFailure(call: Call<List<TaskModel>>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }

            override fun onResponse(call: Call<List<TaskModel>>, response: Response<List<TaskModel>>) {
                if (response.code() != TaskConstants.HTTP.SUCCESS) {
                    val validation =
                        Gson().fromJson(response.errorBody()!!.string(), String::class.java)
                    listener.onFailure(validation)
                } else {
                    response.body()?.let { listener.onSuccess(it) }
                }
            }
        })
    }

    fun create(task: TaskModel, listener: APIListener<Boolean>) {
        val call: Call<Boolean> =
            mRemote.create(task.priorityId, task.description, task.dueDate, task.complete)
        call.enqueue(object : Callback<Boolean> {

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                listener.onFailure(context.getString(R.string.ERROR_UNEXPECTED))
            }

            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if (response.code() != TaskConstants.HTTP.SUCCESS) {
                    val validation =
                        Gson().fromJson(response.errorBody()!!.string(), String::class.java)
                    listener.onFailure(validation)
                } else {
                    response.body()?.let { listener.onSuccess(it) }
                }
            }
        })

    }
}