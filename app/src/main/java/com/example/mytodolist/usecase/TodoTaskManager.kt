package com.example.mytodolist.usecase

import com.example.mytodolist.model.TodoTask
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class TodoTaskManager : KoinComponent {

    private val repository: Repository by inject()

    companion object {
        const val TODOTASK_NEXTID_LAST = "last"
        const val TODOTASK_DONE = "done"
        const val TODOTASK_NEXTID = "nextID"
    }

    fun addNewTask(newTodoTask: TodoTask) {
        if (newTodoTask.title.isEmpty()) {
            throw UserManager.FieldMissingException("title")
        }
        if (newTodoTask.description.isEmpty()) {
            throw UserManager.FieldMissingException("description")
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

    fun getAllTasks(): ArrayList<TodoTask> {
        return repository.getAllTasks()
    }
}

interface Repository {

    fun getAllTasks(): ArrayList<TodoTask>
    fun createNewTask(todoTask: TodoTask)
    fun checkTask(id: String)
    fun getLocalTasks(): ArrayList<TodoTask>
    fun updateTasks(tasksToUpdate: ArrayList<Pair<TodoTask, String>>)

}