package com.valent.mytodolist.fragments.main

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.valent.mytodolist.R
import com.valent.mytodolist.adapter.TaskAdapter
import com.valent.mytodolist.model.TodoTask
import com.valent.mytodolist.presenter.LoginPresenter
import com.valent.mytodolist.presenter.MainPresenter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_task_list.*
import org.koin.android.ext.android.inject

class TaskListFragment : Fragment(), TaskAdapter.TaskListFragmentInterface,
    MainPresenter.TaskListView {
    private val loginPresenter: LoginPresenter by inject()
    private val mainPresenter: MainPresenter by inject()
    private val recyclerAdapter: TaskAdapter by lazy { TaskAdapter(this) }
    var actionMode: ActionMode? = null

    private val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            closeAndLockDrawer()
            mode.menuInflater.inflate(R.menu.selected_task_menu, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            //todo update alarm icon
            selectTask(true)
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.selected_task_action_alarm -> {
                    //todo
                    mode.finish()
                    true
                }

                R.id.selected_task_action_edit -> {
                    //todo
                    mode.finish()
                    true
                }

                R.id.selected_task_action_delete -> {
                    val deleteDialogFragment = DeleteDialogFragment(actionMode, mainPresenter, recyclerAdapter.selectedTaskId, recyclerAdapter.selectedTaskPos)
                    deleteDialogFragment.show(requireActivity().supportFragmentManager, null)
                    true
                }
                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
            requireActivity().drawerLayoutMain?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            selectTask(false)
        }

        fun selectTask(selected: Boolean){
            val taskPos = recyclerAdapter.selectedTaskPos
            if(!selected) recyclerAdapter.selectedTaskPos = -1
            recyclerAdapter.notifyItemChanged(taskPos)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainPresenter.setTaskListView(this)
        setHasOptionsMenu(true)
        if(!loginPresenter.isUserLogged()){
            val action = TaskListFragmentDirections.actionTaskListFragmentToSignInFragment()
            findNavController().navigate(action)
        }
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

        if(loginPresenter.isUserLogged()) {
            mainPresenter.displayUserTasks()
        }
        requireActivity().drawerLayoutMain?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        closeAndLockDrawer()
        actionMode?.finish()
        mainPresenter.setTaskListView(null)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.title_bar_task_list)
    }

    private fun closeAndLockDrawer(){
        requireActivity().drawerLayoutMain?.apply {
            closeDrawers()
            setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
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

    override fun itemLongClicked(id: String, position: Int) {
        if (actionMode == null) {
            recyclerAdapter.selectedTaskPos = position
            recyclerAdapter.selectedTaskId = id
            actionMode = activity?.startActionMode(actionModeCallback)
        }
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

    override fun deleteTask(newTodoTaskList: ArrayList<TodoTask>, oldTaskPos: Int) {
        updateAdapterData(newTodoTaskList)
        recyclerAdapter.notifyItemRemoved(oldTaskPos)
    }

    class DeleteDialogFragment(
        private var actionMode: ActionMode?,
        private val mainPresenter: MainPresenter,
        private val selectedTaskId: String,
        private val selectedTaskPos: Int
    ) : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return requireActivity().let {
                val builder = AlertDialog.Builder(it)
                builder.setMessage(R.string.dialog_delete_message)
                    .setPositiveButton(R.string.yes
                    ) { _, _ ->
                        mainPresenter.deleteTask(selectedTaskId, selectedTaskPos )
                        actionMode?.finish()
                    }
                    .setNegativeButton(R.string.no
                    ) { _, _ ->
                    }
                builder.create()
            }
        }
    }
}
