package com.example.mytodolist.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
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

        navController.setGraph(R.navigation.nav_graph_main)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayoutMain)
        toolbarmain.setupWithNavController(navController, appBarConfiguration)

        navigationViewMain.setupWithNavController(navController)
        navigationViewMain.setNavigationItemSelectedListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(drawerLayoutMain)
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
        val action = TaskListFragmentDirections.actionTaskListFragmentToLoginActivity()
        findNavController(R.id.fragment).navigate(action)
    }
}
