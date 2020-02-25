package com.example.mytodolist.presenter

import com.example.mytodolist.model.TodoTask
import com.example.mytodolist.repo.FirestoreRepo
import com.example.mytodolist.usecase.TodoTaskManager
import com.example.mytodolist.usecase.UserManager
import com.example.mytodolist.utils.FieldMissingException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainPresenter : KoinComponent {
    private val todoTaskManager: TodoTaskManager by inject()
    private var taskListView: TaskListView? = null
    private var newTaskView: NewTaskView? = null
    private val job = SupervisorJob()
    private val scopeMain = CoroutineScope(Dispatchers.Main + job)

    companion object {
        const val ERROR = "error"
    }

    var newTaskToDisplay = false

    fun setTaskListView(taskListView: TaskListView?) {
        this.taskListView = taskListView
    }

    fun setNewTaskView(newTaskView: NewTaskView?) {
        this.newTaskView = newTaskView
    }

    private suspend fun getUserTasks(): ArrayList<TodoTask> {
        return withContext(Dispatchers.Default) {
            todoTaskManager.getAllTasks()
        }
    }

    fun displayUserTasks() {
        scopeMain.launch {
            try {
                val tasksList = getUserTasks()
                taskListView?.displayTasks(tasksList, newTaskToDisplay)
                newTaskToDisplay = false
            } catch (e: FirestoreRepo.EmptyTaskResultException) {
                taskListView?.displayHint()
            } catch (e: FirebaseFirestoreException) {
                taskListView?.displayError(ERROR)
            }
        }
    }

    fun addNewTask(newTodoTask: TodoTask) {
        scopeMain.launch {
            try {
                withContext(Dispatchers.Default) {
                    todoTaskManager.addNewTask(newTodoTask)
                }
                newTaskToDisplay = true
                newTaskView?.closeNewTaskFragment()
            } catch (e: FieldMissingException) {
                newTaskView?.displayMissingField(e.message ?: "")
            } catch (e: FirebaseFirestoreException) {
                newTaskView?.displayError(ERROR)
            }
        }
    }

    fun updateTaskDone(taskId: String, oldPos: Int) {
        scopeMain.launch {
            try {
                withContext(Dispatchers.Default) {
                    todoTaskManager.changeTaskPosition(taskId, true)
                }
                val newTodoTaskList = getUserTasks()
                val newPos = newTodoTaskList.indexOfFirst { it.id == taskId }
                if (newPos != oldPos) {
                    taskListView?.moveTask(newTodoTaskList, oldPos, newPos)
                } else {
                    displayUserTasks()
                }
            } catch (e: FirebaseFirestoreException) {
                taskListView?.displayError(ERROR)
            }
        }
    }

    fun deleteTask(taskId: String, oldTaskPos: Int) {
        scopeMain.launch {
            try {
                withContext(Dispatchers.Default) {
                    todoTaskManager.deleteTask(taskId)
                }
                taskListView?.deleteTask(getUserTasks(), oldTaskPos)
            } catch (e: FirebaseFirestoreException) {
                taskListView?.displayError(ERROR)
            }
        }
    }

    fun disconnectUser() {
        val userManager: UserManager by inject()
        userManager.disconnectUser()
    }

    interface TaskListView {
        fun displayTasks(newTodoTaskList: ArrayList<TodoTask>, insertNewTask: Boolean)
        fun displayHint()
        fun displayError(error: String)
        fun moveTask(newTodoTaskList: ArrayList<TodoTask>, oldPos: Int, newPos: Int)
        fun deleteTask(newTodoTaskList: ArrayList<TodoTask>, oldTaskPos: Int)
    }

    interface NewTaskView {
        fun closeNewTaskFragment()
        fun displayError(message: String)
        fun displayMissingField(field: String)
    }
}