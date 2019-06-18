package com.example.mytodolist.fragments.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mytodolist.R
import com.example.mytodolist.presenter.LoginPresenter
import org.koin.android.ext.android.inject

class LoadingFragment : Fragment(), LoginPresenter.LoadingView {
    private var loginActivity: LoginActivityInterface? = null
    private val loginPresenter: LoginPresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginPresenter.setLoadingView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_loading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginPresenter.checkLoggedUser()
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
        loginPresenter.setLoadingView(null)
    }

    //from LoginPresenter
    override fun connectUser() {
        loginActivity?.connectUser()
    }

    override fun displaySignFragment() {
        loginActivity?.goToSignInFragment()
    }

    interface LoginActivityInterface {
        fun goToSignInFragment()
        fun connectUser()
    }
}
