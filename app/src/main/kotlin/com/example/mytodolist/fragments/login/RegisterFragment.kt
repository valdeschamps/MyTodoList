package com.example.mytodolist.fragments.login

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
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
import kotlinx.android.synthetic.main.fragment_register.*
import org.koin.android.ext.android.inject

class RegisterFragment : Fragment(), LoginPresenter.RegisterInterfaceListener, TextView.OnEditorActionListener {
    private var loginActivity: RegisterFragmentInterface? = null
    private val loginPresenter: LoginPresenter by inject()
    //todo display error for email/pwd coming from firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginPresenter.setRegisterView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textInputRegisterEmail.setOnEditorActionListener(this)
        textInputRegisterPwd.setOnEditorActionListener(this)
        textInputRegisterPwdCheck.setOnEditorActionListener(this)

        textInputRegisterEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textLayoutRegisterEmail.error = null
            }
        })

        textInputRegisterPwd.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textLayoutRegisterPwd.error = null
            }
        })

        textInputRegisterPwdCheck.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textLayoutRegisterPwdCheck.error = null
            }
        })

        buttonConfirmRegister.setOnClickListener {
            val email = textInputRegisterEmail.text.toString()
            val password = textInputRegisterPwd.text.toString()
            val passwordConfirm = textInputRegisterPwdCheck.text.toString()

            if (isFormValid(email, password, passwordConfirm)) {
                loginPresenter.createUser(email, password)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RegisterFragmentInterface) {
            loginActivity = context
        } else {
            throw RuntimeException("$context must implement SignInFragmentInterface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        loginActivity = null
        loginPresenter.setRegisterView(null)
    }

    override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        when (view) {
            textInputRegisterEmail -> {
                textInputRegisterPwd.requestFocus()
            }
            textInputRegisterPwd -> {
                textInputRegisterPwdCheck.requestFocus()
            }
            textInputRegisterPwdCheck -> {
                closeKeyboard()
                buttonConfirmRegister.requestFocus()
            }
        }
        return true
    }

    private fun isFormValid(email: String, password: String, passwordConfirm: String): Boolean {
        //todo optimize
        var valid = true
        if (TextUtils.isEmpty(email)) {
            valid = false
            textLayoutRegisterEmail.error = getString(R.string.empty_field)
        }

        if (TextUtils.isEmpty(password)) {
            valid = false
            textLayoutRegisterPwd.error = getString(R.string.empty_field)
        }

        if (TextUtils.isEmpty(passwordConfirm)) {
            valid = false
            textLayoutRegisterPwdCheck.error = getString(R.string.empty_field)
        }

        if (password != passwordConfirm) {
            valid = false
            displayErrorMessage(getString(R.string.errorRegisterPwdConfirm))
        }
        return valid
    }

    private fun displayErrorMessage(message: String) {
        textViewError.text = message
    }

    private fun closeKeyboard() {
        val view = activity?.currentFocus
        if (view != null) {
            val inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                activity!!.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    override fun displayRegisterError(message: String) {
        displayErrorMessage(message)
    }

    override fun confirmRegister() {
        loginActivity?.goToSignInFragment()
    }

    interface RegisterFragmentInterface {
        fun goToSignInFragment()
    }
}
