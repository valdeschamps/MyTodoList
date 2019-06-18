package com.example.mytodolist.fragments.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.mytodolist.R
import com.example.mytodolist.presenter.LoginPresenter
import com.example.mytodolist.presenter.LoginPresenter.Companion.EMAIL
import com.example.mytodolist.presenter.LoginPresenter.Companion.PASSWORD
import kotlinx.android.synthetic.main.fragment_sign_in.*
import org.koin.android.ext.android.inject

class SignInFragment : Fragment(), LoginPresenter.SignInView, TextView.OnEditorActionListener {
    private var loginActivity: LoginActivityInterface? = null
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
        textInputEmail.setOnEditorActionListener(this)
        textInputPassword.setOnEditorActionListener(this)

        textInputEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textLayoutEmail.error = null
            }
        })

        textInputPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textLayoutPassword.error = null
            }
        })

        buttonSignIn.setOnClickListener {
            val email = textInputEmail.text.toString()
            val password = textInputPassword.text.toString()
            loginPresenter.loginUser(email, password)
        }

        buttonRegister.setOnClickListener {
            loginActivity?.goToRegisterFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginActivityInterface) {
            loginActivity = context
        } else {
            throw RuntimeException("$context must implement LoginActivityInterface")
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

    private fun closeKeyboard() {
        if (activity?.currentFocus != null) {
            val inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                activity!!.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    //from LoginPresenter
    override fun displayConnectionError(message: String) {
        textViewSignInError.text = message
    }

    override fun connectUser() {
        loginActivity?.connectUser()
    }

    override fun displayMissingField(field: String) {
        when (field) {
            EMAIL -> textLayoutEmail.error = getString(R.string.empty_field)
            PASSWORD -> textLayoutPassword.error = getString(R.string.empty_field)
        }
    }

    interface LoginActivityInterface {
        fun goToRegisterFragment()
        fun connectUser()
    }
}
