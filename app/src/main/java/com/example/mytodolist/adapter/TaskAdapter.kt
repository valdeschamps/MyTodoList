package com.example.mytodolist.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.R
import com.example.mytodolist.fragments.main.TaskListFragment
import com.example.mytodolist.model.TodoTask
import kotlinx.android.synthetic.main.taskcard.view.*

class TaskAdapter(taskListFragment: TaskListFragment) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private var taskList = ArrayList<TodoTask>()
    private val listener: OnTaskCliCkListener = taskListFragment

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentTaskPosition = 0

        init {
            itemView.setOnClickListener {
                listener.onTaskClick(taskList[currentTaskPosition].title)
            }
        }

        fun displayTask(todoTask: TodoTask, position: Int) {
            currentTaskPosition = position
            itemView.apply{
                textViewOrder.text = todoTask.order.toString()
                textViewTitle.text = todoTask.title
                textViewDeadline.text = todoTask.deadLine.toString()
            }
        }
    }

    fun updateData(newTodoTaskList: ArrayList<TodoTask>){
        taskList = newTodoTaskList
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