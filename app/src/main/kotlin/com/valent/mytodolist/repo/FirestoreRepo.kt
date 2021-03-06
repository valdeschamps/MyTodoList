package com.valent.mytodolist.repo

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseUser
import com.valent.mytodolist.firebase.FirebaseInfos
import com.valent.mytodolist.model.TodoTask
import com.valent.mytodolist.model.User
import com.valent.mytodolist.usecase.Repository
import com.valent.mytodolist.usecase.TodoTaskManager.Companion.TODOTASK_DELETE
import com.valent.mytodolist.usecase.TodoTaskManager.Companion.TODOTASK_DONE
import com.valent.mytodolist.usecase.TodoTaskManager.Companion.TODOTASK_NEXTID
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.concurrent.ExecutionException

class FirestoreRepo : Repository, KoinComponent {
    private val firebaseInfos: FirebaseInfos by inject()
    private val firestoreDB = firebaseInfos.firestoreDB
    private var tasksList = ArrayList<TodoTask>()

    private fun user(): FirebaseUser? {
        return firebaseInfos.currentUSer()
    }

    private fun createUserDoc() {
        val newUser = User(user()?.email ?: "", user()?.uid ?: "")
        firestoreDB.collection(firebaseInfos.collectionUsersName)
            .document(newUser.uid)
            .set(newUser)
    }

    private fun checkUserDocExist(): Boolean {
        val getUserDocTask = firestoreDB.collection(firebaseInfos.collectionUsersName)
            .document(user()?.uid.toString())
            .get()
        Tasks.await(getUserDocTask)
        return getUserDocTask.result?.exists() == true
    }

    private fun orderTasks(tasks: ArrayList<TodoTask>): ArrayList<TodoTask> {
        val orderedList = ArrayList<TodoTask>()
        val last = tasks.single { it.nextID == "last" }
        orderedList.add(0, last)
        var found = false
        var current = last

        while (!found) {
            val previous = tasks.singleOrNull { it.nextID == current.id }
            if (previous != null) {
                orderedList.add(0, previous)
                current = previous
            } else {
                found = true
            }
        }
        return orderedList
    }

    override fun createNewTask(todoTask: TodoTask) {
        try {
            if (!checkUserDocExist()) {
                createUserDoc()
            }
            val docRef = firestoreDB.collection(firebaseInfos.collectionUsersName)
                .document(user()?.uid ?: "")
                .collection(firebaseInfos.collectionTasksName)
                .document()

            todoTask.id = docRef.id
            docRef.set(todoTask)
        } catch (e: ExecutionException) {
            throw e.cause ?: e
        }
    }

    override fun getAllTasks(): ArrayList<TodoTask> {
        try {
            val getTodoTaskList = firestoreDB.collection(firebaseInfos.collectionUsersName)
                .document(user()?.uid ?: "")//todo can crash after login
                .collection(firebaseInfos.collectionTasksName)
                .get()

            Tasks.await(getTodoTaskList)
            val taskResult = getTodoTaskList.result
            if (taskResult != null && !taskResult.isEmpty) {
                var tasks = ArrayList<TodoTask>()
                for (document in taskResult) {
                    tasks.add(document.toObject(TodoTask::class.java))
                }
                tasks = orderTasks(tasks)
                tasksList = tasks
                return tasksList
            } else {
                tasksList.clear()
                return tasksList
            }
        } catch (e: ExecutionException) {
            throw e.cause ?: e
        }
    }

    override fun getLocalTasks(): ArrayList<TodoTask> {
        return tasksList
    }

    override fun updateTasks(tasksToUpdate: ArrayList<Pair<TodoTask, String>>) {
        try {
            val batch = firestoreDB.batch()
            tasksToUpdate.forEach {
                val docRef = firestoreDB.collection(firebaseInfos.collectionUsersName)
                    .document(user()?.uid ?: "")
                    .collection(firebaseInfos.collectionTasksName)
                    .document(it.first.id)

                when (it.second) {
                    TODOTASK_NEXTID -> {
                        batch.update(docRef, it.second, it.first.nextID)
                    }
                    TODOTASK_DONE -> {
                        batch.update(docRef, it.second, it.first.done)
                    }
                    TODOTASK_DELETE -> {
                        batch.delete(docRef.parent.document(it.first.id))
                    }
                }
            }
            batch.commit()
        } catch (e: ExecutionException) {
            throw e.cause ?: e
        }
    }

    override fun deleteUserDoc() {
        try {
            val batch = firestoreDB.batch()

            val userDoc = firestoreDB
                .collection(firebaseInfos.collectionUsersName)
                .document(user()?.uid ?: "")

            batch.delete(userDoc)
            batch.commit()
        } catch (e: ExecutionException) {
            throw e.cause ?: e
        }
    }
}