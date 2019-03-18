package com.example.mytodolist.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.mytodolist.R
import com.example.mytodolist.firebase.FirebaseInfos
import com.example.mytodolist.fragments.login.LoadingFragment
import com.example.mytodolist.fragments.login.RegisterFragment
import com.example.mytodolist.fragments.login.SignInFragment
import com.example.mytodolist.model.User
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject

class LoginActivity : AppCompatActivity(), SignInFragment.OnSignInFragmentInteractionListener, RegisterFragment.OnRegisterFragmentInteractionListener {
    private val firebaseInfos: FirebaseInfos by inject()
    private val firebaseAuth = firebaseInfos.firebaseAuth
    private val fragmentManager = supportFragmentManager
    private val registerFragment: RegisterFragment by lazy {RegisterFragment()}
    private val signInFragment:SignInFragment by lazy {SignInFragment()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbarLogin)

        displayLoadingFragment()
    }

    override fun onStart() {
        super.onStart()

        if (firebaseInfos.currentUSer() != null){
            gotoMainActivity()
        }else{
            displaySignInFragment()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if(fragmentManager.findFragmentById(R.id.frameLayoutLogin) == registerFragment) {
            setTopBarForSignIn()
        }
        //todo press 2 times to leave app + display toast
        super.onBackPressed()
    }

    private fun displayLoadingFragment() {
        fragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutLogin, LoadingFragment())
            commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun displaySignInFragment() {
        fragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutLogin, signInFragment)
            commit()
        }
        setTopBarForSignIn()
    }

    private fun displayRegisterFragment() {
        fragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutLogin, registerFragment)
            addToBackStack(null)
            commit()
        }
        setTopBarForRegister()
    }

    private fun setTopBarForSignIn(){
        supportActionBar?.apply{
            setDisplayHomeAsUpEnabled(false)
            setTitle(R.string.signIn)
        }
    }

    private fun setTopBarForRegister(){
        supportActionBar?.apply{
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.register)
        }
    }

    private fun gotoMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun connectUser() {
        gotoMainActivity()
    }

    override fun goToRegisterFragment() {
        displayRegisterFragment()
    }

    override fun goToSignInFragment() {
        onBackPressed()
    }

    private fun createUserDoc(){
        val newUser= User(firebaseAuth.currentUser?.email ?: "", firebaseAuth.currentUser?.uid ?: "")

        if (TextUtils.isEmpty(newUser.email) && TextUtils.isEmpty(newUser.uid)){
            firebaseInfos
        }
    }
}
