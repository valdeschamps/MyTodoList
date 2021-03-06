package com.valent.mytodolist.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.valent.mytodolist.R
import com.valent.mytodolist.fragments.main.TaskListFragment
import com.valent.mytodolist.model.TodoTask
import kotlinx.android.synthetic.main.taskcard.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TaskAdapter(taskListFragment: TaskListFragment) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
    private var todoTaskList = ArrayList<TodoTask>()
    private val taskListFragment: TaskListFragmentInterface = taskListFragment
    var selectedTaskPos = -1
    var selectedTaskId = ""
    val checkTime = (taskListFragment.resources.getInteger(R.integer.task_check_time)).toLong()

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var todoTask = TodoTask()
        private var taskDone = false
        var isExpanded = false

        init {
            val progressBarAnimator = ValueAnimator.ofInt(0, 100).setDuration(checkTime)
            progressBarAnimator.addUpdateListener {
                itemView.progressBarCheck.progress = it.animatedValue as Int
            }

            progressBarAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    setTaskDone()
                    taskListFragment.onTodoTaskChecked(todoTask, adapterPosition)
                }
            })

            itemView.constraintLayoutCheck.setOnTouchListener { _, event ->
                if (!taskDone) {
                    val action: Int = event.actionMasked
                    if (action == MotionEvent.ACTION_DOWN) {
                        progressBarAnimator.start()
                    } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                        progressBarAnimator.pause()
                        progressBarAnimator.currentPlayTime = 0
                    }
                }
                true
            }

            itemView.linearLayoutTask.setOnClickListener {
                setExpandedDetails()
            }

            itemView.linearLayoutTask.setOnLongClickListener {
                taskListFragment.itemLongClicked(todoTask.id, adapterPosition)
                true
            }
        }

        fun displayTask(newTodoTask: TodoTask, position: Int) {
            if (todoTask.id != newTodoTask.id) {
                isExpanded = false //if holder is reused with another task
            }

            todoTask = newTodoTask
            taskDone = todoTask.done

            itemView.apply {
                textViewTitle.text = todoTask.title
                textViewDetails.text = todoTask.details
                itemView.textViewDetails.visibility = if (isExpanded) View.VISIBLE else View.GONE

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

                if (newTodoTask.done) {
                    checkBoxTodoTask.isChecked = true
                    progressBarCheck.progress = 100
                } else {
                    checkBoxTodoTask.isChecked = false
                    progressBarCheck.progress = 0
                }

                val color = ContextCompat.getColor(
                    itemView.context,
                    if (position == selectedTaskPos) R.color.accent
                    else R.color.colorBackground
                )
                CardViewTaskMain.setCardBackgroundColor(color)
            }
        }

        private fun setTaskDone() {
            taskDone = true
            itemView.apply {
                checkBoxTodoTask.isChecked = true
                progressBarCheck.progress = 100
            }
        }

        private fun setExpandedDetails() {
            if (todoTask.details.isNotEmpty()) {
                val animator = ValueAnimator.ofFloat(0f, 90f).setDuration(250L)
                animator.addUpdateListener {
                    itemView.imageViewTriangle.rotation = it.animatedValue as Float
                }

                //isn't expanded yet
                if (!isExpanded) {
                    animator.start()
                } else {
                    animator.reverse()
                }
                isExpanded = !isExpanded
                notifyItemChanged(adapterPosition)
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
        holder.displayTask(todoTask, position)
    }

    override fun getItemCount(): Int {
        return todoTaskList.size
    }

    interface TaskListFragmentInterface {
        fun onTodoTaskChecked(todoTask: TodoTask, currentPos: Int)
        fun itemLongClicked(id: String, position: Int)
    }
}