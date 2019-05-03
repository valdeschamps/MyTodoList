package com.example.mytodolist.presenter

import com.example.mytodolist.model.TodoTask
import com.example.mytodolist.repo.FirestoreRepo
import com.example.mytodolist.usecase.TodoTaskManager
import com.example.mytodolist.usecase.UserManager
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MainPresenter() : KoinComponent {
    private val todoTaskManager: TodoTaskManager by inject()
    private var taskListView: TaskListView? = null
    private var newTaskView: NewTaskView? = null
    private val job = SupervisorJob()
    private val scopeMain = CoroutineScope(Dispatchers.Main + job)

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
                //todo animation
            } catch (e: UserManager.FieldMissingException) {
                newTaskView?.displayMissingField(e.message ?: "")
            } catch (e: FirebaseFirestoreException) {
                newTaskView?.displayError("error")
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
                taskListView?.displayError("error")
            }
        }
    }

    interface TaskListView {
        fun displayTasks(newTodoTaskList: ArrayList<TodoTask>)
        fun displayHint()
        fun displayError(error: String)
        fun moveTask(newTodoTaskList: ArrayList<TodoTask>, oldPos: Int, newPos: Int)
    }

    interface NewTaskView {
        fun closeNewTaskFragment()
        fun displayError(message: String)
        fun displayMissingField(field: String)
    }
}