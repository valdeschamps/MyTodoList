package com.example.mytodolist.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.mytodolist.R
import com.example.mytodolist.adapter.TaskAdapter
import com.example.mytodolist.model.TodoTask
import com.example.mytodolist.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_task_list.*
import org.koin.android.ext.android.inject

class TaskListFragment : Fragment(), TaskAdapter.TaskListFragmentInterface,
    MainPresenter.TaskListView {
    private val mainPresenter: MainPresenter by inject()
    private val recyclerAdapter: TaskAdapter by lazy { TaskAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainPresenter.setTaskListView(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todo pull to refresh

        (recyclerViewTodoTask.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerViewTodoTask.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerAdapter
            setHasFixedSize(true)
        }

        floatingActionButtonAdd.setOnClickListener {
            val action = TaskListFragmentDirections.actionTaskListFragmentToNewTaskFragment()
            findNavController().navigate(action)
        }

        //todo only get all tasks first time
        mainPresenter.displayUserTasks()
        activity?.drawerLayoutMain?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun onDetach() {
        super.onDetach()
        mainPresenter.setTaskListView(null)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_bar_task_list)
    }

    private fun displayTodoTaskList(newTodoTaskList: ArrayList<TodoTask>, insertNewTask: Boolean) {
        hideMessage()
        updateAdapterData(newTodoTaskList)
        if (insertNewTask) {
            recyclerViewTodoTask.scrollToPosition(0)
            recyclerAdapter.notifyItemInserted(0)
        } else {
            recyclerAdapter.notifyDataSetChanged()
        }
    }

    private fun displayMessage(message: String) {
        textViewTodoTaskList.apply {
            text = message
            visibility = View.VISIBLE
        }
    }

    private fun hideMessage() {
        textViewTodoTaskList.apply {
            text = ""
            visibility = View.INVISIBLE
        }
    }

    private fun updateAdapterData(newTodoTaskList: ArrayList<TodoTask>) {
        recyclerAdapter.setData(newTodoTaskList)
    }

    //from TaskAdapter
    override fun onTodoTaskChecked(todoTask: TodoTask, currentPos: Int) {
        mainPresenter.updateTaskDone(todoTask.id, currentPos)
    }

    //from MainPresenter
    override fun displayTasks(newTodoTaskList: ArrayList<TodoTask>, insertNewTask: Boolean) {
        displayTodoTaskList(newTodoTaskList, insertNewTask)
    }

    override fun displayHint() {
        displayMessage(resources.getString(R.string.todo_task_list_hint))
    }

    override fun displayError(error: String) {
        displayMessage(error)
    }

    override fun moveTask(newTodoTaskList: ArrayList<TodoTask>, oldPos: Int, newPos: Int) {
        updateAdapterData(newTodoTaskList)
        recyclerAdapter.notifyItemMoved(oldPos, newPos)
    }
}
