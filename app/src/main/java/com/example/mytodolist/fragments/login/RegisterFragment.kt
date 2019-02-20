package com.example.mytodolist.fragments.login

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mytodolist.R
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {
    private var listenerRegisterFragment: OnRegisterFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonConfirmRegister.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPwd.text.toString()
            val passwordConfirm = editTextPwdConfirm.text.toString()

            if(isFormValid(email, password, passwordConfirm)){
                listenerRegisterFragment?.registerFragmentCreateAccount(email, password)
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

    fun displayErrorMessage(message: String) {
        textViewError.text = message
    }

    interface OnRegisterFragmentInteractionListener {
        fun registerFragmentCreateAccount(email: String, password: String)
    }
}
