package com.example.mytodolist.presenter

import com.example.mytodolist.firebase.FirestoreRepository
import com.example.mytodolist.model.Task
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MainPresenter(): KoinComponent {
    private val firestoreRepository: FirestoreRepository by inject()
    private var currentView: MainActivityView? = null

    fun setView(view: MainActivityView?){
        currentView = view
    }


    fun getUserTasks(){
        //ask FirestoreRepository
        val userTasks = firestoreRepository.getUserTasksFromDB()
        userTasks.sortBy { it.order }
        currentView?.displayTasks(userTasks)
    }


    interface MainActivityView{
        fun displayTasks(newTaskList: ArrayList<Task>)
    }

}