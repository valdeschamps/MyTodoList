package com.example.mytodolist.fragments.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.mytodolist.R

class SignInFragment : Fragment() {
    private var listenerSignIn: OnSignInFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        val progressBarLoading = view.findViewById<ProgressBar>(R.id.progressBarLoading)
        progressBarLoading.visibility = View.INVISIBLE

        val buttonSignIn = view.findViewById<Button>(R.id.buttonSignIn)
        buttonSignIn.setOnClickListener {
            //todo check edit text and try to connect
        }

        val buttonRegister = view.findViewById<Button>(R.id.buttonRegister)
        buttonRegister.setOnClickListener {
            listenerSignIn?.registerButtonTrigger()
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSignInFragmentInteractionListener) {
            listenerSignIn = context
        } else {
            throw RuntimeException("$context must implement OnSignInFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerSignIn = null
    }

    interface OnSignInFragmentInteractionListener {
        fun signInButtonTrigger(email:String, password:String)
        fun registerButtonTrigger()
    }
}
