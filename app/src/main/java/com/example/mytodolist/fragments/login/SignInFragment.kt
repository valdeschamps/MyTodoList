package com.example.mytodolist.fragments.login

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mytodolist.R
import com.example.mytodolist.presenter.LoginPresenter
import kotlinx.android.synthetic.main.fragment_sign_in.*
import org.koin.android.ext.android.inject

class SignInFragment : Fragment(), LoginPresenter.SignInInterfaceListener, TextView.OnEditorActionListener {
    private var listenerSignInFragment: OnSignInFragmentInteractionListener? = null
    private val loginPresenter: LoginPresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginPresenter.setLoginView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBarLoading.visibility = View.INVISIBLE
        buttonSignIn.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            if(isFormValid(email, password)) {
                loginPresenter.loginUser(email, password)
            }
        }

        buttonRegister.setOnClickListener {
            listenerSignInFragment?.goToRegisterFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSignInFragmentInteractionListener) {
            listenerSignInFragment = context
        } else {
            throw RuntimeException("$context must implement OnSignInFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerSignInFragment = null
        loginPresenter.setLoginView(null)
    }

    override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        when(view){
            editTextEmail -> {
                editTextPassword.requestFocus()
            }
            editTextPassword -> {
                buttonSignIn.requestFocus()
            }
        }
        return true
    }

    private fun isFormValid(email: String, password: String): Boolean {
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            displayErrorMessage(getString(R.string.errorRegisterEmpty))
            return false
        }
        return true
    }

    private fun displayErrorMessage(message: String) {
        textViewSignInError.text = message
    }

    override fun displayConnectionError(message: String) {
        displayErrorMessage(message)
    }

    override fun connectUser() {
        listenerSignInFragment?.connectUser()
    }

    interface OnSignInFragmentInteractionListener {
        fun goToRegisterFragment()
        fun connectUser()
    }
}
