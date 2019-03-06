package com.example.mytodolist.fragments.main

import android.content.Context
import android.net.Uri
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
import com.example.mytodolist.model.Task
import kotlinx.android.synthetic.main.fragment_todo_list.*

class TodoListFragment : Fragment(), TaskAdapter.OnTaskCliCkListener {
    private var listenerTodoList: OnTodoListFragmentInteractionListener? = null
    private val recyclerAdapter: TaskAdapter by lazy { TaskAdapter(this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_todo_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewTask.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTodoListFragmentInteractionListener) {
            listenerTodoList = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnTodoListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerTodoList = null
    }

    fun updateTaskList(newTaskList: ArrayList<Task>){
        recyclerAdapter.apply {
            updateData(newTaskList)
            notifyDataSetChanged()
        }
    }

    interface OnTodoListFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onTaskClick(id: String) {
        Toast.makeText(context, "task number : ${id}", Toast.LENGTH_SHORT).show()
    }

}
