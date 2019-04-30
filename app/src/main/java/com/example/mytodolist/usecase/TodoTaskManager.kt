package com.example.mytodolist.usecase

import com.example.mytodolist.model.TodoTask
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


class TodoTaskManager: KoinComponent {

    private val repository: Repository by inject()

    fun addNewTask(newTodoTask: TodoTask) {
        if (newTodoTask.title.isEmpty()){
            throw UserManager.FieldMissingException("title")
        }
        if (newTodoTask.description.isEmpty()){
            throw UserManager.FieldMissingException("description")
        }

        repository.createNewTask(newTodoTask)
    }

    fun checkTask(){

    }

    fun getAllTasks(): ArrayList<TodoTask> {
        return repository.getAllTasks()
    }



}

interface Repository{

    fun getAllTasks(): ArrayList<TodoTask>
    fun createNewTask(todoTask: TodoTask)
    fun checkTask(id: String)

}