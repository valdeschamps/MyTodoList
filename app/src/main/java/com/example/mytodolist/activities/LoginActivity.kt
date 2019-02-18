package com.example.mytodolist.activities

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mytodolist.R
import com.example.mytodolist.fragments.login.RegisterFragment
import com.example.mytodolist.fragments.login.SignInFragment

class LoginActivity : AppCompatActivity(), SignInFragment.OnSignInFragmentInteractionListener, RegisterFragment.OnRegisterFragmentInteractionListener {
    private val fragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (isUserAlreadyLogged()){
            //todo go to main activity
        }else{
            displaySignInFragment()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if(fragmentManager.findFragmentById(R.id.frameLayoutLogin) is RegisterFragment) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
        //todo press 2 times to leave app + display toast
        super.onBackPressed()
    }

    private fun isUserAlreadyLogged():Boolean{
        return false
    }

    private fun displayRegisterFragment(){
        fragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutLogin, RegisterFragment())
            addToBackStack(null)
            commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.register)
    }

    private fun displaySignInFragment(){
        fragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutLogin, SignInFragment())
            commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setTitle(R.string.signIn)
    }

    override fun signInButtonTrigger(email:String, password:String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerButtonTrigger() {
        displayRegisterFragment()
    }

    override fun onRegisterFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
