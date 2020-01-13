package com.example.mytodolist.adapter

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.R
import com.example.mytodolist.fragments.main.TaskListFragment
import com.example.mytodolist.model.TodoTask
import kotlinx.android.synthetic.main.taskcard.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TaskAdapter(taskListFragment: TaskListFragment) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private var todoTaskList = ArrayList<TodoTask>()
    private val taskListFragment: TaskListFragmentInterface = taskListFragment

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var taskDone = false
        private var todoTask = TodoTask()
        var isExpanded = false

        init {
            val checkSpinnerRate = 2
            var progressStatus = 0
            itemView.progressBarCheck.progress = 0
            //itemView.textViewDetails.visibility = View.GONE

            itemView.constraintLayoutCheck.setOnTouchListener { _, event ->
                if (!taskDone) {
                    val action: Int = event.actionMasked
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

            /*itemView.linearLayoutTask.setOnClickListener {
                setExpanded(!isExpanded)
            }*/
        }

        fun displayTask(newTodoTask: TodoTask) {
            this.todoTask = newTodoTask
            itemView.apply {
                textViewTitle.text = todoTask.title
                textViewDetails.text = todoTask.details
                var dateString = String()

                if (todoTask.dateTimestamp != -1L) {
                    dateString = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(
                        Date(todoTask.dateTimestamp)
                    )
                    if (todoTask.timeTimestamp != -1L) {
                        dateString += " - ${SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
                            Date(todoTask.timeTimestamp)
                        )}"
                    }
                } else if (todoTask.timeTimestamp != -1L) {
                    dateString = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
                        Date(todoTask.timeTimestamp)
                    )
                }

                if (dateString.isNotBlank()) {
                    textViewDateTime.text = dateString
                    textViewDateTime.visibility = View.VISIBLE
                } else {
                    textViewDateTime.visibility = View.GONE
                }
                progressBarCheck.progressDrawable =
                    resources.getDrawable(R.drawable.custom_progressbar, null)
            }
        }

        fun setDoneState(done: Boolean) {
            taskDone = done
            if (done) {
                ObjectAnimator.ofObject(
                    itemView.linearLayoutTaskMain,
                    "backgroundColor",
                    ArgbEvaluator(),
                    ContextCompat.getColor(itemView.context, R.color.colorBackground),
                    ContextCompat.getColor(itemView.context, R.color.colorTaskBackgroundDone)
                ).setDuration(1000).start()
            }

            itemView.apply {
                checkBoxTodoTask.isChecked = done
                progressBarCheck.progress = if (done) 100 else 0
            }
        }

        /*private fun setExpanded(expand: Boolean) {
            if (todoTask.details.isNotEmpty()) {
                val animator = ValueAnimator.ofFloat(0f, 90f).setDuration(500L)
                animator.addUpdateListener {
                    itemView.imageViewTriangle.rotation = it.animatedValue as Float
                }

                if(expand) {
                    animator.start()
                    itemView.textViewDetails.visibility = View.VISIBLE
                }else{
                    animator.reverse()
                    itemView.textViewDetails.visibility = View.GONE
                }

                isExpanded = expand
            }
        }*/

        fun setExpandedDetails() {
            if (isExpanded) {
                itemView.textViewDetails.visibility = View.VISIBLE
            } else {
                itemView.textViewDetails.visibility = View.GONE
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
        val todoTask = todoTaskList[position]
        holder.displayTask(todoTask)
        holder.setDoneState(todoTask.done)
        holder.setExpandedDetails()
        if (todoTask.details.isNotEmpty()) {
            holder.itemView.linearLayoutTask.setOnClickListener {
                holder.isExpanded = !holder.isExpanded
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return todoTaskList.size
    }

    interface TaskListFragmentInterface {
        fun onTodoTaskChecked(todoTask: TodoTask, currentPos: Int)
    }
}