package com.example.mytodolist.presenter

import com.example.mytodolist.model.TodoTask
import com.example.mytodolist.presenter.LoginPresenter.Companion.ERROR_INVALIDCRED
import com.example.mytodolist.presenter.LoginPresenter.Companion.ERROR_INVALIDUSER
import com.example.mytodolist.presenter.LoginPresenter.Companion.ERROR_NETWORK
import com.example.mytodolist.repo.FirestoreRepo
import com.example.mytodolist.usecase.TodoTaskManager
import com.example.mytodolist.usecase.UserManager
import com.example.mytodolist.utils.FieldMissingException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class MainPresenter : KoinComponent {
    private val todoTaskManager: TodoTaskManager by inject()
    private val userManager: UserManager by inject()
    private var taskListView: TaskListView? = null
    private var newTaskView: NewTaskView? = null
    private var parametersView: ParametersView? = null
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

    fun setParametersView(parametersView: ParametersView?){
        this.parametersView = parametersView
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
                //todo getUserTasks() crash
                taskListView?.deleteTask(getUserTasks(), oldTaskPos)
            } catch (e: FirebaseFirestoreException) {
                taskListView?.displayError(ERROR)
            }
        }
    }

    fun deleteUser(email: String, password: String) {
        scopeMain.launch {
            try {
                withContext(Dispatchers.Default) {
                    userManager.reAuthenticate(email, password)
                    todoTaskManager.deleteAllTasks()
                    userManager.deleteUser()
                }
                parametersView?.disconnectUser()
            }catch (e: FirebaseFirestoreException) {
                parametersView?.displayError(ERROR)
            }catch (e: FieldMissingException) {
                parametersView?.displayMissingField(e.message.toString())
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                parametersView?.displayError(ERROR_INVALIDCRED)
            } catch (e: FirebaseNetworkException) {
                parametersView?.displayError(ERROR_NETWORK)
            } catch (e: FirebaseAuthInvalidUserException) {
                parametersView?.displayError(ERROR_INVALIDUSER)
            }
        }
    }

    fun disconnectUser() {
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

    interface ParametersView {
        fun disconnectUser()
        fun displayError(code: String)
        fun displayMissingField(field: String)
    }
}