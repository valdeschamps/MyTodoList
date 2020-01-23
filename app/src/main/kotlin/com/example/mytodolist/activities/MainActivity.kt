package com.example.mytodolist.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.mytodolist.R
import com.example.mytodolist.fragments.main.NewTaskFragment
import com.example.mytodolist.fragments.main.TaskListFragment
import com.example.mytodolist.presenter.MainPresenter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    TaskListFragment.TaskListFragmentInterface, NewTaskFragment.NewTaskFragmentInterface {
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_task_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if ((fragmentManager.findFragmentById(R.id.frameLayoutMain) == taskListFragment) && (item.itemId == android.R.id.home)) {
            drawerLayoutMain.openDrawer(GravityCompat.START)
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.itemSignOut -> {
                signOut()
            }
            R.id.itemOptions -> {
                //todo
            }
        }
        return true
    }

    private fun signOut() {
        val mainPresenter: MainPresenter by inject()
        mainPresenter.disconnectUser()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    private fun displayTaskListFragment() {
        fragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutMain, taskListFragment)
            commit()
        }
    }

    private fun displayNewTaskFragment() {
        fragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutMain, newTaskFragment)
            addToBackStack(null)
            commit()
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
