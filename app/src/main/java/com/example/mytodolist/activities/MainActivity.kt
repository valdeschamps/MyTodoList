package com.example.mytodolist.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.mytodolist.R
import com.example.mytodolist.firebase.FirebaseInfos
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val firebaseInfos: FirebaseInfos by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbarmain)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        navigationViewMain.setNavigationItemSelectedListener(this)

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
        }
        return true
    }

    private fun userDisconnect(){
        firebaseInfos.userDisconnect()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}
