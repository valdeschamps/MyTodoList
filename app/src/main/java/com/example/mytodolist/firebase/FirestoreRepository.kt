package com.example.mytodolist.firebase

import com.example.mytodolist.model.TodoTask
import com.example.mytodolist.model.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class FirestoreRepository(): KoinComponent {
    private val firebaseInfos: FirebaseInfos by inject()
    private val firestoreDB = firebaseInfos.firestoreDB
    private val firebaseAuth = firebaseInfos.firebaseAuth

    private fun user(): FirebaseUser? {
        return firebaseInfos.currentUSer()
    }

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

    private fun createUSerDoc(): Boolean{
        val newUser = User(user()?.email ?: "", user()?.uid ?: "")
        val createUserDocTask = firestoreDB.collection(firebaseInfos.collectionUsersName)
            .document(newUser.uid)
            .set(newUser)

        return try {
            Tasks.await(createUserDocTask)
            createUserDocTask.isSuccessful
        }catch (e: Exception) {
            false
        }
    }

    private fun checkUserDocExist(): Boolean{
        val getUserDocTask = firestoreDB.collection(firebaseInfos.collectionUsersName)
            .document(user()?.uid.toString())
            .get()

        return try{
            Tasks.await(getUserDocTask)
            val user = getUserDocTask.result
            if(user?.exists() == true){
                true
            }else{
                createUSerDoc()
            }
        }catch (e: Exception) {
            false
        }
    }

    private fun saveTodoTask(newTodoTask: TodoTask): Boolean{
        val docRef = firestoreDB.collection(firebaseInfos.collectionUsersName)
            .document(user()?.uid ?: "")
            .collection(firebaseInfos.collectionTasksName)
            .document()

        newTodoTask.id = docRef.id
        val addTodoTask = docRef.set(newTodoTask)

        return try {
            Tasks.await(addTodoTask)
            addTodoTask.isSuccessful
        }catch (e: Exception) {
            false
        }
    }

    fun addNewTodoTask(newTodoTask: TodoTask): Pair<Boolean, String> {
        if (checkUserDocExist()) {
            return if(saveTodoTask(newTodoTask)){
                Pair(true, "")
            }else{
                Pair(false, "")
            }
        } else {
            return Pair(false, "")
        }
        //todo use pair to get error and display
    }

    fun editTask(){

    }

    fun deleteTask(){

    }

    fun getUserTodoTasksList(): Triple<Boolean, ArrayList<TodoTask>, String>{
        val taskList = ArrayList<TodoTask>()

        val getTodoTaskList = firestoreDB.collection(firebaseInfos.collectionUsersName)
            .document(user()?.uid ?: "")//todo can crash after login
            .collection(firebaseInfos.collectionTasksName)
            .get()

        try{
            Tasks.await(getTodoTaskList)
            if(getTodoTaskList.isSuccessful){
                val taskResult = getTodoTaskList.result
                if(taskResult != null) {
                    for (document in taskResult) {
                        taskList.add(document.toObject(TodoTask::class.java))
                    }

                    //todo sort
                    return Triple(true, taskList, "")
                }
                return Triple(true, taskList, "empty")
            }else{
                return Triple(false, taskList, "error")
            }
        }catch (e: Exception) {
            return Triple(false, taskList, getTodoTaskList.exception?.message ?: "error")
        }
    }

    fun getTask(){

    }

}