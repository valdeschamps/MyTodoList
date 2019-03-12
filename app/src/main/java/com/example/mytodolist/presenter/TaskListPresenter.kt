package com.example.mytodolist.presenter

import com.example.mytodolist.firebase.FirestoreRepository
import com.example.mytodolist.model.Task
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class TaskListPresenter(): KoinComponent {
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

    interface TaskListPresenterListener{
        fun displayTasks(newTaskList: ArrayList<Task>)
    }

}