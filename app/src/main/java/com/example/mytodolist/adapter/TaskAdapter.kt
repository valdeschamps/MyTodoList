package com.example.mytodolist.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MotionEventCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.R
import com.example.mytodolist.fragments.main.TaskListFragment
import com.example.mytodolist.model.TodoTask
import kotlinx.android.synthetic.main.taskcard.view.*

class TaskAdapter(taskListFragment: TaskListFragment) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private var todoTaskList = ArrayList<TodoTask>()
    private val taskListFragment: TaskListFragmentInterface = taskListFragment

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentTaskPosition = 0
        private var taskDone = false
        private var todoTask = TodoTask()

        init {
            val checkSpinnerRate = 2
            var progressStatus = 0
            itemView.progressBarCheck.progress = 0

            itemView.constraintLayoutCheck.setOnTouchListener { _, event ->
                if (!taskDone) {
                    val action: Int = MotionEventCompat.getActionMasked(event)//todo
                    if (action == MotionEvent.ACTION_UP) {
                        progressStatus = 0
                        itemView.progressBarCheck.progress = progressStatus
                    } else {
                        if (progressStatus == 100) {
                            taskDone = true
                            //todo lock task view
                            setDoneState(taskDone)
                            taskListFragment.onTodoTaskChecked(todoTask, adapterPosition)
                        } else {
                            progressStatus += checkSpinnerRate
                            itemView.progressBarCheck.incrementProgressBy(checkSpinnerRate)
                        }
                    }
                }
                //todo set to 0 if touch out of the view
                true
            }

            itemView.setOnClickListener {
                taskListFragment.onTodoTaskClick(todoTask.title)
            }
        }

        fun displayTask(newTodoTask: TodoTask, position: Int) {
            this.todoTask = newTodoTask
            currentTaskPosition = position
            itemView.apply {
                textViewTitle.text = todoTask.title
                //textViewDeadline.text = todoTask.deadLine.toString()
                progressBarCheck.progressDrawable = resources.getDrawable(R.drawable.custom_progressbar)//todo
            }
        }

        fun setDoneState(done: Boolean) {
            taskDone = done
            itemView.apply {
                checkBoxTodoTask.isChecked = done
                progressBarCheck.progress = if (done) 100 else 0
            }
        }
    }

    fun setData(newTodoTaskList: ArrayList<TodoTask>) {
        todoTaskList = newTodoTaskList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.taskcard, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.displayTask(todoTaskList[position], position)
        holder.setDoneState(todoTaskList[position].done)
    }

    override fun getItemCount(): Int {
        return todoTaskList.size
    }

    interface TaskListFragmentInterface {
        fun onTodoTaskClick(id: String)
        fun onTodoTaskChecked(todoTask: TodoTask, currentPos: Int)
    }
}