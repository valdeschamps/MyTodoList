package com.example.mytodolist.fragments.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mytodolist.R
import com.example.mytodolist.adapter.TaskAdapter
import com.example.mytodolist.model.TodoTask
import com.example.mytodolist.presenter.MainPresenter
import kotlinx.android.synthetic.main.fragment_task_list.*
import org.koin.android.ext.android.inject

class TaskListFragment : Fragment(), TaskAdapter.OnTaskCliCkListener, MainPresenter.TaskListPresenterListener {
    private val mainPresenter: MainPresenter by inject()
    private var listenerTodoList: TodoListFragmentListener? = null
    private val recyclerAdapter: TaskAdapter by lazy { TaskAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainPresenter.setTaskListView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo pull to refresh
        recyclerViewTodoTask.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        floatingActionButtonAdd.setOnClickListener {
            listenerTodoList?.displayAddTaskFragment()
        }

        //todo only get all tasks first time
        mainPresenter.getUserTasks()
        //mainPresenter.getUserTasksForDisplay()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TodoListFragmentListener) {
            listenerTodoList = context
        } else {
            throw RuntimeException("$context must implement TodoListFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerTodoList = null
        mainPresenter.setTaskListView(null)
    }

    private fun updateTaskList(newTodoTaskList: ArrayList<TodoTask>){
        hideMessage()
        recyclerAdapter.apply {
            updateData(newTodoTaskList)
            notifyDataSetChanged()
        }
    }

    private fun displayTodoTaskList(newTodoTaskList: ArrayList<TodoTask>){
        hideMessage()
        recyclerAdapter.setData(newTodoTaskList)
    }

    private fun displayMessage(message: String){
        textViewTodoTaskList.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun hideMessage(){
        textViewTodoTaskList.apply {
            text = ""
            visibility = View.INVISIBLE
        }
    }

    interface TodoListFragmentListener {
        fun displayAddTaskFragment()
    }

    //from TaskAdapter
    override fun onTodoTaskClick(id: String) {
        Toast.makeText(context, "task number : ${id}", Toast.LENGTH_SHORT).show()
    }

    override fun onTodoTaskChecked(todoTask: TodoTask, done: Boolean) {
        //todo
        mainPresenter.updateTaskDone(todoTask)
    }


    //from MainPresenter
    override fun displayTasks(newTodoTaskList: ArrayList<TodoTask>) {
        //updateTaskList(newTodoTaskList)
        displayTodoTaskList(newTodoTaskList)
    }

    override fun displayHint() {
        //todo
        displayMessage(resources.getString(R.string.todo_task_list_hint))
    }

    override fun displayError(error: String) {
        //todo
        displayMessage(error)
    }

    override fun updateTodoTaskView(id: String) {
        //recyclerAdapter.updateTodoTaskView(id)
        //recyclerAdapter.notifyItemChanged()
        mainPresenter.getUserTasks()
        //mainPresenter.getUserTasksForDisplay()

        //todo save tasklist when getting all of them, update when check and update adapter
    }

}
