package com.example.mytodolist.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mytodolist.R
import com.example.mytodolist.fragments.login.LoadingFragment
import com.example.mytodolist.fragments.login.RegisterFragment
import com.example.mytodolist.fragments.login.SignInFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity(), SignInFragment.OnSignInFragmentInteractionListener, RegisterFragment.OnRegisterFragmentInteractionListener {
    private lateinit var firebaseAuth: FirebaseAuth
    private val fragmentManager = supportFragmentManager
    private val registerFragment: RegisterFragment by lazy {RegisterFragment()}
    private val signInFragment:SignInFragment by lazy {SignInFragment()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        displayLoadingFragment()
        FirebaseApp.initializeApp(this)
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        if (isUserAlreadyLogged(currentUser)){
            gotoMainActivity(currentUser?.email ?: "error email")
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
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
        //todo press 2 times to leave app + display toast
        super.onBackPressed()
    }

    private fun isUserAlreadyLogged(currentUser: FirebaseUser?):Boolean {
        return currentUser != null
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
        supportActionBar?.apply{
            setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setTitle(R.string.signIn)
        }
    }

    private fun displayRegisterFragment() {
        fragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutLogin, registerFragment)
            addToBackStack(null)
            commit()
        }
        supportActionBar?.apply{
            setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle(R.string.register)
        }
    }

    private fun gotoMainActivity(userEmail: String) {
        val intent = Intent(this, MainActivity::class.java).putExtra("email", userEmail)
        startActivity(intent)
    }

    override fun signInFragmentSignIn(email:String, password:String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    gotoMainActivity(user?.email ?: "error email")
                } else {
                    signInFragment.displayErrorMessage((task.exception?.message.toString()))
                }
            }
    }

    override fun signInFragmentGoToRegister() {
        displayRegisterFragment()
    }

    override fun registerFragmentCreateAccount(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                }else{
                    registerFragment.displayErrorMessage(task.exception?.message.toString())
                }
            }
    }
}
