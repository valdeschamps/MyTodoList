package com.example.mytodolist.fragments.login

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mytodolist.R
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : Fragment() {
    private var listenerSignInFragment: OnSignInFragmentInteractionListener? = null

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
                listenerSignInFragment?.signInFragmentSignIn(email, password)
            }
        }

        buttonRegister.setOnClickListener {
            listenerSignInFragment?.signInFragmentGoToRegister()
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
    }

    private fun isFormValid(email: String, password: String): Boolean {
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            displayErrorMessage(getString(R.string.errorRegisterEmpty))
            return false
        }
        return true
    }

    fun displayErrorMessage(message: String) {
        textViewSignInError.text = message
    }

    interface OnSignInFragmentInteractionListener {
        fun signInFragmentSignIn(email:String, password:String)
        fun signInFragmentGoToRegister()
    }
}
