package com.example.mytodolist.presenter

import com.example.mytodolist.firebase.FirestoreRepository
import com.example.mytodolist.model.TodoTask
import com.example.mytodolist.repo.FirestoreRepo
import com.example.mytodolist.usecase.TodoTaskManager
import com.example.mytodolist.usecase.UserManager
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MainPresenter() : KoinComponent {
    private val firestoreRepository: FirestoreRepository by inject()
    private var taskListView: TaskListPresenterListener? = null
    private var newTaskView: NewTaskPresenterListener? = null

    private var userTodoTaskList = ArrayList<TodoTask>()

    private val job = SupervisorJob()
    private val scopeMain = CoroutineScope(Dispatchers.Main + job)

    private val todoTaskManager: TodoTaskManager by inject()

    fun setTaskListView(taskListPresenterListener: TaskListPresenterListener?) {
        taskListView = taskListPresenterListener
    }

    fun setNewTaskView(newTaskPresenterListener: NewTaskPresenterListener?) {
        newTaskView = newTaskPresenterListener
    }

    fun getUserTasks() {
        scopeMain.launch {
            try {
                val tasksList = withContext(Dispatchers.Default) {
                    todoTaskManager.getAllTasks()
                }
                taskListView?.displayTasks(tasksList)
            } catch (e: FirestoreRepo.EmptyTaskResultException) {
                taskListView?.displayHint()
            } catch (e: FirebaseFirestoreException) {
                taskListView?.displayError("error") //todo use res
            }
        }
    }

    fun addNewTask(newTodoTask: TodoTask) {
        scopeMain.launch {
            try {
                withContext(Dispatchers.Default) {
                    todoTaskManager.addNewTask(newTodoTask)
                }
                newTaskView?.closeNewTaskFragment()
            } catch (e: UserManager.FieldMissingException) {
                newTaskView?.displayMissingField(e.message?: "")
            } catch (e: FirebaseFirestoreException){
                newTaskView?.displayError("error")
            }
        }
    }

    /*
    private suspend fun getUserTasks(): Pair<Boolean, String> {
        val result = withContext(Dispatchers.Default) {
            firestoreRepository.getUserTodoTasksList()
        }
        return if (result.first && result.second.isNotEmpty()) {
            userTodoTaskList = result.second
            Pair(true, "")
        } else {
            Pair(false, result.third)
        }
    }
    */

    /*
    fun getUserTasksForDisplay() {
        scopeMain.launch {
            val result = withContext(Dispatchers.Default) { getUserTasks() }
            if (result.first) {
                if (userTodoTaskList.isNotEmpty()) {
                    taskListView?.displayTasks(userTodoTaskList)
                } else {
                    taskListView?.displayHint()
                }
            } else {
                Log.d("test", "aaaaaaa")
                taskListView?.displayError(result.second)
            }
        }
    }
    */

    /*
    fun getUserTasksForDisplay(){
        scopeMain.launch {
            val result = withContext(Dispatchers.Default) {
                firestoreRepository.getUserTodoTasksList()
            }
            if(result.first){
                if(!result.second.isEmpty()) {
                    userTodoTaskList = result.second
                    taskListView?.displayTasks(userTodoTaskList)
                }else{
                    taskListView?.displayHint()
                }
            }else{
                taskListView?.displayError(result.third)
            }
        }
    }
    */

    /*
    fun addNewTask(newTodoTask: TodoTask) {
        //todo add loading spinner
        scopeMain.launch {

            newTodoTask.nextID =
                if (!userTodoTaskList.isEmpty()) userTodoTaskList[0].id else firestoreRepository.TODOTASK_NEXTID_LAST

            val result = withContext(Dispatchers.Default) {
                firestoreRepository.addNewTodoTask(newTodoTask)
            }
            if (result.first) {
                newTaskView?.closeNewTaskFragment()
                //todo animation on adapter
            } else {
                //todo display error
            }
        }
    }
    */

    fun updateTaskDone(currentTodoTask: TodoTask) {
        scopeMain.launch {


        }
    }

    interface TaskListPresenterListener {
        fun displayTasks(newTodoTaskList: ArrayList<TodoTask>)
        fun displayHint()
        fun displayError(error: String)
        fun updateTodoTaskView(id: String)
    }

    interface NewTaskPresenterListener {
        fun closeNewTaskFragment()
        fun displayError(message: String)
        fun displayMissingField(field: String)
    }

}