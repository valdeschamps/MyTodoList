package com.example.mytodolist.presenter

import com.example.mytodolist.firebase.FirestoreRepository
import com.example.mytodolist.model.TodoTask
import kotlinx.coroutines.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class MainPresenter(): KoinComponent {
    private val firestoreRepository: FirestoreRepository by inject()
    private var taskListView: TaskListPresenterListener? = null
    private var newTaskView: NewTaskPresenterListener? = null

    private val job = SupervisorJob()
    private val scopeMain = CoroutineScope(Dispatchers.Main + job)

    fun setTaskListView(taskListPresenterListener: TaskListPresenterListener?){
        taskListView = taskListPresenterListener
    }

    fun setNewTaskView(newTaskPresenterListener: NewTaskPresenterListener?){
        newTaskView = newTaskPresenterListener
    }

    fun getUserTasks(){
        scopeMain.launch {
            val result = withContext(Dispatchers.Default) {
                firestoreRepository.getUserTodoTasksList()
            }
            if(result.first){
                if(!result.second.isEmpty()) {
                    taskListView?.displayTasks(result.second)
                }else{
                    taskListView?.displayHint()
                }
            }else{
                taskListView?.displayError(result.third)
            }
        }
    }

    fun addNewTask(newTodoTask: TodoTask){
        scopeMain.launch {
            val result = withContext(Dispatchers.Default){
                firestoreRepository.addNewTodoTask(newTodoTask)
            }
            if(result.first){
                newTaskView?.closeNewTaskFragment()
            }else{
                //todo display error
            }
        }
    }

    interface TaskListPresenterListener{
        fun displayTasks(newTodoTaskList: ArrayList<TodoTask>)
        fun displayHint()
        fun displayError(error: String)
    }

    interface NewTaskPresenterListener{
        fun closeNewTaskFragment()
    }

}