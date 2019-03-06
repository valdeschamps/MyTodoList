package com.example.mytodolist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.R
import com.example.mytodolist.fragments.main.TodoListFragment
import com.example.mytodolist.model.Task
import kotlinx.android.synthetic.main.taskcard.view.*

class TaskAdapter(todoListFragment: TodoListFragment) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private var taskList = ArrayList<Task>()
    private val listener: OnTaskCliCkListener = todoListFragment

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentTaskPosition = 0

        init {
            itemView.setOnClickListener {
                listener.onTaskClick(taskList[currentTaskPosition].id)
            }
        }

        fun displayTask(task: Task, position: Int) {
            currentTaskPosition = position
            itemView.apply{
                textViewOrder.text = task.order.toString()
                textViewTitle.text = task.title
                textViewDeadline.text = task.deadLine.toString()
            }
        }
    }

    fun updateData(newTaskList: ArrayList<Task>){
        taskList = newTaskList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.taskcard, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.displayTask(taskList[position], position)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    interface OnTaskCliCkListener{
        fun onTaskClick(id: String)
    }
}