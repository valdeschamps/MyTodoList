package com.example.mytodolist.adapter

import android.util.Log
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
    private val listener: OnTaskCliCkListener = taskListFragment

    //todo if user check task -->  ask presenter to update task (loading + lock the task) --> when result update the view of the task

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var currentTaskPosition = 0
        var taskDone = false
        var todoTask = TodoTask()

        init {
            val checkSpinnerRate = 1
            var progressStatus = 0
            itemView.progressBarCheck.progress = 0

            itemView.constraintLayoutCheck.setOnTouchListener { view, event ->
                if(!taskDone) {
                    val action: Int = MotionEventCompat.getActionMasked(event)
                    if (action == MotionEvent.ACTION_UP) {
                        progressStatus = 0
                        itemView.progressBarCheck.progress = progressStatus
                    } else {
                        Log.d("test", "$progressStatus     bar : ${itemView.progressBarCheck.progress}")
                        if (progressStatus == 100) {
                            taskDone = true
                            //todo lock task view
                            setDoneState(taskDone)
                            Log.d("test", "DONE")
                            listener.onTodoTaskChecked(todoTask, taskDone)
                            //itemView.checkBoxTodoTask.isChecked = true
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
                listener.onTodoTaskClick(todoTask.title)
            }
        }

        fun displayTask(newTodoTask: TodoTask, position: Int) {
            this.todoTask = newTodoTask
            currentTaskPosition = position
            itemView.apply{
                textViewOrder.text = todoTask.order.toString()
                textViewTitle.text = todoTask.title
                textViewDeadline.text = todoTask.deadLine.toString()
                progressBarCheck.progressDrawable = resources.getDrawable(R.drawable.custom_progressbar)
            }
        }

        fun setDoneState(done: Boolean){
            taskDone = done
            itemView.apply {
                checkBoxTodoTask.isChecked = done
                progressBarCheck.progress = if (done) 100 else 0
            }
        }
    }

    fun updateData(newTodoTaskList: ArrayList<TodoTask>){
        todoTaskList = newTodoTaskList
    }

    fun setData(newTodoTaskList: ArrayList<TodoTask>){
        todoTaskList = newTodoTaskList
        notifyDataSetChanged()
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

    fun updateTodoTaskView(id: String){
        //val pos = todoTaskList.find {it.id == id}
        val pos = todoTaskList.indexOf(todoTaskList.find {it.id == id})
        //val a =

    }

    interface OnTaskCliCkListener{
        fun onTodoTaskClick(id: String)
        fun onTodoTaskChecked(todoTask: TodoTask, done: Boolean)
    }
}