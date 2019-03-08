package com.example.mytodolist.firebase

import com.example.mytodolist.model.Task
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class FirestoreRepository(): KoinComponent {
    private val firebaseInfos: FirebaseInfos by inject()

    fun saveTask(){

    }

    fun editTask(){

    }

    fun deleteTask(){

    }

    fun getUserTasksFromDB(): ArrayList<Task>{
        //get from DB
        //quick test

        return arrayListOf(
            Task("1", 1, "title 1", "desc 1", 1.00, 1.00),
            Task("0", 0, "title 0", "desc 0", 0.00, 0.00),
            Task("3", 3, "title 3", "desc 3", 3.00, 3.00),
            Task("4", 4, "title 4", "desc 4", 4.00, 4.00),
            Task("2", 2, "title 2", "desc 2", 2.00, 2.00)
        )
    }

    fun getTask(){

    }

}