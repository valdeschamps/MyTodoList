package com.example.mytodolist.firebase

import com.example.mytodolist.model.Task
import com.google.android.gms.tasks.Tasks
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class FirestoreRepository(): KoinComponent {
    private val firebaseInfos: FirebaseInfos by inject()
    private val user = firebaseInfos.currentUSer()
    private val firestoreDB = firebaseInfos.firestoreDB
    private val firebaseAuth = firebaseInfos.firebaseAuth

    fun createUSer(email: String, password: String): Pair<Boolean, String>{
        val task = firebaseAuth.createUserWithEmailAndPassword(email, password)
        return try {
            Tasks.await(task)
            if(task.isSuccessful){
                Pair(true, "")
            }else{
                Pair(false, task.exception.toString())
            }
        }catch (e: Exception){
            Pair(false, task.exception?.message?: "error account creation")//todo res string
        }
    }

    fun loginUser(email: String, password: String): Pair<Boolean, String>{
        val task = firebaseAuth.signInWithEmailAndPassword(email, password)

        return try {
            Tasks.await(task)
            if(task.isSuccessful){
                Pair(true, "")
            }else{
                Pair(false, task.exception.toString())
            }
        }catch (e: Exception){
            Pair(false, task.exception?.message?: "error auth")//todo res string
        }
    }

    fun saveTask(newTask: Task){
        firestoreDB.collection(firebaseInfos.collectionUsersName)
            .document(user?.uid ?: "")
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