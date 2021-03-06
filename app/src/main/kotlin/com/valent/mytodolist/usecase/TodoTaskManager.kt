package com.valent.mytodolist.usecase

import com.valent.mytodolist.model.TodoTask
import com.valent.mytodolist.utils.FieldMissingException
import com.valent.mytodolist.utils.FieldMissingException.Companion.FIELD_TITLE
import org.koin.core.KoinComponent
import org.koin.core.inject

class TodoTaskManager : KoinComponent {
    private val repository: Repository by inject()

    companion object {
        const val TODOTASK_NEXTID_LAST = "last"
        const val TODOTASK_DONE = "done"
        const val TODOTASK_NEXTID = "nextID"
        const val TODOTASK_DELETE = "DELETE"
    }

    fun addNewTask(newTodoTask: TodoTask) {
        if (newTodoTask.title.isEmpty()) {
            throw FieldMissingException(FIELD_TITLE)
        }

        val userTasks = repository.getLocalTasks()
        if (userTasks.isEmpty()) {
            newTodoTask.nextID = TODOTASK_NEXTID_LAST
        } else {
            newTodoTask.nextID = userTasks.first().id
        }

        repository.createNewTask(newTodoTask)
    }

    fun changeTaskPosition(id: String, done: Boolean) {
        val changeList = ArrayList<Pair<TodoTask, String>>()
        val userTasks = repository.getLocalTasks()

        val currentTask = userTasks.single { it.id == id }
        val previousTask = userTasks.singleOrNull { it.nextID == id }
        if (previousTask != null) {
            previousTask.nextID = currentTask.nextID
            changeList.add(Pair(previousTask, TODOTASK_NEXTID))
        }
        userTasks.remove(currentTask)

        //if the current task wasn't the only one
        if (userTasks.isNotEmpty()) {
            val lastPendingTask = userTasks.lastOrNull { !it.done }
            //if the current task wasn't the last pending
            //change the order
            if (lastPendingTask != null) {
                currentTask.nextID = lastPendingTask.nextID
                changeList.add(Pair(currentTask, TODOTASK_NEXTID))

                lastPendingTask.nextID = currentTask.id
                changeList.add(Pair(lastPendingTask, TODOTASK_NEXTID))
            }
        }

        if (done) {
            currentTask.done = true
            changeList.add(Pair(currentTask, TODOTASK_DONE))
        }

        repository.updateTasks(changeList)
    }

    fun deleteTask(id: String) {
        val changeList = ArrayList<Pair<TodoTask, String>>()
        val userTasks = repository.getLocalTasks()

        val currentTask = userTasks.single { it.id == id }
        val previousTask = userTasks.singleOrNull { it.nextID == id }
        if (previousTask != null) {
            previousTask.nextID = currentTask.nextID
            changeList.add(Pair(previousTask, TODOTASK_NEXTID))
        }

        changeList.add(Pair(currentTask, TODOTASK_DELETE))
        repository.updateTasks(changeList)
    }

    fun deleteAllTasks(){
        val userTasks = repository.getLocalTasks()
        if(userTasks.isNotEmpty()) {
            val changeList = ArrayList<Pair<TodoTask, String>>()
            userTasks.forEach {
                changeList.add(Pair(it, TODOTASK_DELETE))
            }
            repository.updateTasks(changeList)
        }
        repository.deleteUserDoc()
    }

    fun getAllTasks(): ArrayList<TodoTask> {
        return repository.getAllTasks()
    }
}

interface Repository {
    fun getAllTasks(): ArrayList<TodoTask>
    fun createNewTask(todoTask: TodoTask)
    fun getLocalTasks(): ArrayList<TodoTask>
    fun updateTasks(tasksToUpdate: ArrayList<Pair<TodoTask, String>>)
    fun deleteUserDoc()
}