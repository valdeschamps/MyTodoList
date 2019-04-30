package com.example.mytodolist.firebase

import android.util.Log
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

    val TODOTASK_NEXTID_LAST = "last"
    val TODOTASK_DONE = "done"
    val TODOTASK_NEXTID = "nextID"

    private fun user(): FirebaseUser? {
        return firebaseInfos.currentUSer()
    }

    /*
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
    */

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
                    //return Triple(true, sortTodoTasks(taskList), "")
                    //return Triple(true, sortByOrder(taskList), "")
                    return Triple(true, sortTodoTaskList(taskList), "")
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

    private fun batchUpdateTaskOrder(newOrder: Int){
        val batch = firestoreDB.batch()
    }

    fun updateTodoTaskDone(id: String, done: Boolean, newOrder: Int, todoTaskUpdateList: ArrayList<TodoTask>, todoTaskListOrder: Int): Boolean {
        val batch = firestoreDB.batch()

        todoTaskUpdateList.forEach {
            val ref = firestoreDB.collection(firebaseInfos.collectionUsersName)
                .document(user()?.uid ?: "")
                .collection(firebaseInfos.collectionTasksName)
                .document(it.id)
            batch.update(ref, firebaseInfos.todoTaskPropertieOrder, it.order + todoTaskListOrder)
        }

        val mainTodoTaskRef = firestoreDB.collection(firebaseInfos.collectionUsersName)
            .document(user()?.uid ?: "")
            .collection(firebaseInfos.collectionTasksName)
            .document(id)

        batch
            .update(mainTodoTaskRef, firebaseInfos.todoTaskPropertieDone, done)
            .update(mainTodoTaskRef, firebaseInfos.todoTaskPropertieOrder, newOrder)

        return try{
            Tasks.await(batch.commit())
            true
        }catch (e: Exception) {
            false
        }
    }

    /*
    fun updateTaskDone(id: String, done: Boolean): Boolean {
        val updateTaskDone = firestoreDB.collection(firebaseInfos.collectionUsersName)
            .document(user()?.uid ?: "")
            .collection(firebaseInfos.collectionTasksName)
            .document(id)
            .update(firebaseInfos.todoTaskPropertieDone, done)

        return try{
            Tasks.await(updateTaskDone)
            true
        }catch (e: Exception) {
            false
        }
    }
    */

    private fun sortTodoTasks(todoTaskList: ArrayList<TodoTask>): ArrayList<TodoTask>{
        val sortedList = ArrayList<TodoTask>()
        val doneList = ArrayList<TodoTask>()
        todoTaskList.forEach {
            if(it.done){
                doneList.add(it)
            }else{
                sortedList.add(it)
            }
        }

        sortedList.addAll(doneList)
        return sortedList
    }

    private fun sortByOrder(todoTaskList: ArrayList<TodoTask>): ArrayList<TodoTask>{
        val sortedList = ArrayList(todoTaskList.sortedBy{ it.order })//.reversed())
        return sortedList
    }

    private fun sortTodoTaskList(todoTaskList: ArrayList<TodoTask>): ArrayList<TodoTask>{
        val result = ArrayList<TodoTask>()
        val last = todoTaskList.find { it.nextID == TODOTASK_NEXTID_LAST }

        if(last!=null) {
            var done = false
            var previous = last

            result.add(last)

            while (!done){
                val current = todoTaskList.find { it.nextID == previous?.id }
                if(current!=null){
                    result.add(current)
                    previous = current
                }else{
                    done= true
                }
            }
            result.reverse()
        }

        Log.d("test", result.toString())
        return result
    }

}