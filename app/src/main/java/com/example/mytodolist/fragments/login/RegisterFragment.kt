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
import kotlinx.android.synthetic.main.fragment_register.*
import org.koin.android.ext.android.inject

class RegisterFragment : Fragment(), LoginPresenter.RegisterInterfaceListener, TextView.OnEditorActionListener {
    private var listenerRegisterFragment: OnRegisterFragmentInteractionListener? = null
    private val loginPresenter: LoginPresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginPresenter.setRegisterView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonConfirmRegister.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPwd.text.toString()
            val passwordConfirm = editTextPwdConfirm.text.toString()
            //todo use text layout

            if(isFormValid(email, password, passwordConfirm)){
                loginPresenter.createUser(email, password)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRegisterFragmentInteractionListener) {
            listenerRegisterFragment = context
        } else {
            throw RuntimeException("$context must implement OnSignInFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerRegisterFragment = null
        loginPresenter.setRegisterView(null)
    }

    override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        when (view){
            editTextEmail -> {
                editTextPwd.requestFocus()
            }
            editTextPwd -> {
                editTextPwdConfirm.requestFocus()
            }
            editTextPwdConfirm -> {
                buttonConfirmRegister.requestFocus()
            }
        }
        return true
    }

    private fun isFormValid(email: String, password: String, passwordConfirm: String):Boolean {
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordConfirm)){
            displayErrorMessage(getString(R.string.errorRegisterEmpty))
            return false
        }

        if(password != passwordConfirm){
            displayErrorMessage(getString(R.string.errorRegisterPwdConfirm))
            return false
        }
        return true
    }

    private fun displayErrorMessage(message: String) {
        textViewError.text = message
    }

    override fun displayRegisterError(message: String) {
        displayErrorMessage(message)
    }

    override fun confirmRegister() {
        listenerRegisterFragment?.goToSignInFragment()
    }

    interface OnRegisterFragmentInteractionListener {
        fun goToSignInFragment()
    }
}
