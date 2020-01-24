package com.example.mytodolist.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mytodolist.R
import com.example.mytodolist.fragments.login.LoadingFragment
import com.example.mytodolist.fragments.login.RegisterFragment
import com.example.mytodolist.fragments.login.SignInFragment
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), SignInFragment.LoginActivityInterface,
    RegisterFragment.LoginActivityInterface, LoadingFragment.LoginActivityInterface {
    private val fragmentManager = supportFragmentManager
    private val registerFragment: RegisterFragment by lazy { RegisterFragment() }
    private val signInFragment: SignInFragment by lazy { SignInFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbarLogin)
        displayLoadingFragment()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun displayLoadingFragment() {
        fragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutLogin, LoadingFragment())
            commit()
        }
    }

    private fun displaySignInFragment() {
        fragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutLogin, signInFragment)
            commit()
        }
    }

    private fun displayRegisterFragment() {
        fragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutLogin, registerFragment)
            addToBackStack(null)
            commit()
        }
    }

    private fun gotoMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }

    //from SignInFragment
    override fun goToRegisterFragment() {
        displayRegisterFragment()
    }

    //from LoadingFragment
    override fun goToSignInFragment() {
        displaySignInFragment()
    }

    //from LoadingFragment, SignInFragment, RegisterFragment
    override fun connectUser() {
        gotoMainActivity()
    }
}
