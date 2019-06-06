package com.example.mytodolist.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.mytodolist.R
import com.example.mytodolist.firebase.FirebaseInfos
import com.example.mytodolist.fragments.main.NewTaskFragment
import com.example.mytodolist.fragments.main.TaskListFragment
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    TaskListFragment.TaskListFragmentInterface, NewTaskFragment.NewTaskFragmentInterface {
    private val firebaseInfos: FirebaseInfos by inject()
    private val fragmentManager = supportFragmentManager
    private val taskListFragment: TaskListFragment by lazy { TaskListFragment() }
    private val newTaskFragment: NewTaskFragment by lazy { NewTaskFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbarmain)
        navigationViewMain.setNavigationItemSelectedListener(this)
        displayTaskListFragment()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (fragmentManager.findFragmentById(R.id.frameLayoutMain) == newTaskFragment) {
            setTopBarTaskList()
        }

        //todo press 2 times to leave
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (fragmentManager.findFragmentById(R.id.frameLayoutMain) == newTaskFragment) {
                    onBackPressed()
                } else {
                    drawerLayoutMain.openDrawer(GravityCompat.START)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.itemSignOut -> {
                userDisconnect()
            }
            R.id.itemOptions -> {
                //todo
            }
        }
        return true
    }

    private fun userDisconnect() {
        firebaseInfos.userDisconnect()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun displayTaskListFragment() {
        fragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutMain, taskListFragment)
            commit()
        }
        setTopBarTaskList()
    }

    private fun displayNewTaskFragment() {
        fragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutMain, newTaskFragment)
            addToBackStack(null)
            commit()
        }
        setTopBarNewTask()
    }

    private fun setTopBarTaskList() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
            title = getString(R.string.title_bar_task_list)
        }
    }

    private fun setTopBarNewTask() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back)
            title = getString(R.string.title_bar_new_task)
        }
    }

    //from TaskListFragment
    override fun displayAddTaskFragment() {
        displayNewTaskFragment()
    }

    //from NewTaskFragment
    override fun newTaskFragmentDismiss() {
        onBackPressed()
    }
}
