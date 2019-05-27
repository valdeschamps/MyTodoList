package com.example.mytodolist.fragments.login

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mytodolist.R
import com.example.mytodolist.presenter.LoginPresenter
import kotlinx.android.synthetic.main.fragment_sign_in.*
import org.koin.android.ext.android.inject

class SignInFragment : Fragment(), LoginPresenter.SignInInterfaceListener, TextView.OnEditorActionListener {
    private var loginActivity: SignInFragmentInterface? = null
    private val loginPresenter: LoginPresenter by inject()
    //todo display error message for email or password

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginPresenter.setLoginView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textInputEmail.setOnEditorActionListener(this)
        textInputPassword.setOnEditorActionListener(this)

        progressBarLoading.visibility = View.INVISIBLE
        buttonSignIn.setOnClickListener {
            val email = textInputEmail.text.toString()
            val password = textInputPassword.text.toString()
            if (isFormValid(email, password)) {
                loginPresenter.loginUser(email, password)
            }
        }

        buttonRegister.setOnClickListener {
            loginActivity?.goToRegisterFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SignInFragmentInterface) {
            loginActivity = context
        } else {
            throw RuntimeException("$context must implement SignInFragmentInterface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        loginActivity = null
        loginPresenter.setLoginView(null)
    }

    override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        when (view) {
            textInputEmail -> {
                textInputPassword.requestFocus()
            }
            textInputPassword -> {
                closeKeyboard()
                buttonSignIn.performClick()
            }
        }
        return true
    }

    private fun isFormValid(email: String, password: String): Boolean {
        var valid = true

        if(TextUtils.isEmpty(email)){
            valid = false
            textInputEmail.error = getString(R.string.empty_field)
        }
        if(TextUtils.isEmpty(password)){
            valid = false
            textInputPassword.error = getString(R.string.empty_field)
        }
        return valid
    }

    private fun displayErrorMessage(message: String) {
        textViewSignInError.text = message
    }

    private fun closeKeyboard() {
        if (activity?.currentFocus != null) {
            val inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                activity!!.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    override fun displayConnectionError(message: String) {
        displayErrorMessage(message)
    }

    override fun connectUser() {
        loginActivity?.connectUser()
    }

    interface SignInFragmentInterface {
        fun goToRegisterFragment()
        fun connectUser()
    }
}
