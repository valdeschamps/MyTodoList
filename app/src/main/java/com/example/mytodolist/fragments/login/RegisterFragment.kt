package com.example.mytodolist.fragments.login

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.mytodolist.R

class RegisterFragment : Fragment() {
    private var listenerRegister: OnRegisterFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRegisterFragmentInteractionListener) {
            listenerRegister = context
        } else {
            throw RuntimeException("$context must implement OnSignInFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerRegister = null
    }

    interface OnRegisterFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onRegisterFragmentInteraction(uri: Uri)
    }
}
