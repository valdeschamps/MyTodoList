package com.example.mytodolist.presenter

import android.util.Log
import com.example.mytodolist.firebase.FirestoreRepository
import com.example.mytodolist.model.Task
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MainPresenter(): KoinComponent {
    private val firestoreRepository: FirestoreRepository by inject()
    private var currentPresenterListener: TaskListPresenterListener? = null

    fun setView(presenterListener: TaskListPresenterListener?){
        currentPresenterListener = presenterListener
    }

    fun getUserTasks(){
        //ask FirestoreRepository
        val userTasks = firestoreRepository.getUserTasksFromDB()
        userTasks.sortBy { it.order }
        currentPresenterListener?.displayTasks(userTasks)
    }

    fun addNewTask(newTask: Task){
        Log.d("test", newTask.toString())
    }

    interface TaskListPresenterListener{
        fun displayTasks(newTaskList: ArrayList<Task>)
    }

}