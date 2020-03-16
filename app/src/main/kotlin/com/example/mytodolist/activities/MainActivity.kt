package com.example.mytodolist.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.mytodolist.R
import com.example.mytodolist.fragments.main.TaskListFragmentDirections
import com.example.mytodolist.presenter.MainPresenter
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val navController by lazy { findNavController(R.id.fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbarmain)

        navController.setGraph(R.navigation.nav_graph_all)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.taskListFragment, R.id.signInFragment), drawerLayoutMain)
        toolbarmain.setupWithNavController(navController, appBarConfiguration)

        navigationViewMain.setupWithNavController(navController)
        navigationViewMain.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.itemSignOut -> {
                signOut()
            }
            R.id.itemOptions -> {
                val action = TaskListFragmentDirections.actionTaskListFragmentToParametersFragment()
                findNavController(R.id.fragment).navigate(action)
            }
        }
        return true
    }

    private fun signOut() {
        val mainPresenter: MainPresenter by inject()
        mainPresenter.disconnectUser()
        findNavController(R.id.fragment).navigate(R.id.action_taskListFragment_to_signInFragment)
    }
}
