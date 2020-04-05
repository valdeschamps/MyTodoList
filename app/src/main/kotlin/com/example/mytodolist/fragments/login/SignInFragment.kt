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
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mytodolist.R
import com.example.mytodolist.presenter.LoginPresenter
import com.example.mytodolist.presenter.LoginPresenter.Companion.ERROR_FIELDMISSING
import com.example.mytodolist.presenter.LoginPresenter.Companion.ERROR_INVALIDCRED
import com.example.mytodolist.presenter.LoginPresenter.Companion.ERROR_INVALIDUSER
import com.example.mytodolist.presenter.LoginPresenter.Companion.ERROR_NETWORK
import com.example.mytodolist.utils.FieldMissingException.Companion.FIELD_EMAIL
import com.example.mytodolist.utils.FieldMissingException.Companion.FIELD_PASSWORD
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_sign_in.*
import org.koin.android.ext.android.inject

class SignInFragment : Fragment(), LoginPresenter.SignInView, TextView.OnEditorActionListener {
    private val loginPresenter: LoginPresenter by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginPresenter.setLoginView(this)
        loginPresenter.checkLoggedUser()
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = ""
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            closeKeyboard()
            loginPresenter.loginUser(email, password)
        }

        buttonRegister.setOnClickListener {
            closeKeyboard()
            val action = SignInFragmentDirections.actionSignInFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        val toggle = ActionBarDrawerToggle(
            activity,
            drawerLayoutMain,
            toolbarmain,
            R.string.yes,
            R.string.no
        )
        toggle.isDrawerIndicatorEnabled = false

        requireActivity().drawerLayoutMain?.apply {
            closeDrawers()
            setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
        if (requireActivity().currentFocus != null) {
            val inputManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    //from LoginPresenter
    override fun displayError(code: String) {
        when (code) {
            ERROR_FIELDMISSING -> textViewSignInError.text = getString(R.string.ERROR_FIELDMISSING)
            ERROR_INVALIDCRED -> textViewSignInError.text = getString(R.string.ERROR_INVALIDCRED)
            ERROR_NETWORK -> textViewSignInError.text = getString(R.string.ERROR_NETWORK)
            ERROR_INVALIDUSER -> textViewSignInError.text = getString(R.string.ERROR_INVALIDUSER)
        }
    }

    override fun connectUser() {
        findNavController().navigate(R.id.action_signInFragment_to_taskListFragment)
    }

    override fun displayMissingField(field: String) {
        when (field) {
            FIELD_EMAIL -> textLayoutEmail.error = getString(R.string.empty_field)
            FIELD_PASSWORD -> textLayoutPassword.error = getString(R.string.empty_field)
        }
    }
}
