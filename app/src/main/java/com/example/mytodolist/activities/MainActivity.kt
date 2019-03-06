package com.example.mytodolist.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.mytodolist.R
import com.example.mytodolist.firebase.FirebaseInfos
import com.example.mytodolist.fragments.main.TodoListFragment
import com.example.mytodolist.model.Task
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, TodoListFragment.OnTodoListFragmentInteractionListener {
    private val firebaseInfos: FirebaseInfos by inject()
    private val fragmentManager = supportFragmentManager
    private val todoListFragment: TodoListFragment by lazy { TodoListFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbarmain)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        navigationViewMain.setNavigationItemSelectedListener(this)

        displayTodoListFragment()
        displayTodoListData()

        Log.d("test", "user email = ${firebaseInfos.currentUSer()?.email.toString()}")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
         return when (item.itemId) {
            android.R.id.home -> {
                drawerLayoutMain.openDrawer(GravityCompat.START)
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

    private fun userDisconnect(){
        firebaseInfos.userDisconnect()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun displayTodoListFragment(){
        fragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutMain, todoListFragment)
            commit()
        }
    }

    private fun displayTodoListData(){
        //quick test
        val testList = arrayListOf<Task>(
            Task("0", 0, "title 0", "desc 0", 0.00, 0.00),
            Task("1", 1, "title 1", "desc 1", 1.00, 1.00),
            Task("2", 2, "title 2", "desc 2", 2.00, 2.00),
            Task("3", 3, "title 3", "desc 3", 3.00, 3.00),
            Task("4", 4, "title 4", "desc 4", 4.00, 4.00)
        )
        todoListFragment.updateTaskList(testList)
    }

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
