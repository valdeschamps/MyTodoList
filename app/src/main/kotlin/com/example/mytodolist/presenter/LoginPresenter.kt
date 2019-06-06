package com.example.mytodolist.presenter

import com.example.mytodolist.usecase.UserManager
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class LoginPresenter : KoinComponent {
    private val userManager: UserManager by inject()
    private var loginView: SignInInterfaceListener? = null
    private var registerView: RegisterInterfaceListener? = null
    private val job = SupervisorJob()
    private val scopeMain = CoroutineScope(Dispatchers.Main + job)

    companion object {
        const val UNKNOWN = "unknown"
        const val EMAIL = "email"
        const val PASSWORD = "password"
    }

    fun createUser(email: String, password: String) {
        scopeMain.launch {
            try {
                withContext(Dispatchers.Default) {
                    userManager.createUser(email, password)
                }
                registerView?.confirmRegister()
            } catch (e: UserManager.FieldMissingException) {
                registerView?.displayRegisterError(e.message ?: UNKNOWN)
            } catch (e: FirebaseException) {
                //todo better error handling
                registerView?.displayRegisterError(e.message ?: UNKNOWN)
            }
        }
    }

    private fun checkLoginForm(email: String, password: String): Boolean {
        var valid = true
        if (email == "") {
            valid = false
            loginView?.displayMissingField(EMAIL)
        }
        if (password == "") {
            valid = false
            loginView?.displayMissingField(PASSWORD)
        }
        return valid
    }

    fun loginUser(email: String, password: String) {
        if (checkLoginForm(email, password)) {
            scopeMain.launch {
                try {
                    withContext(Dispatchers.Default) {
                        userManager.loginUser(email, password)
                    }
                    loginView?.connectUser()
                } catch (e: UserManager.FieldMissingException) {
                    loginView?.displayConnectionError(e.message ?: UNKNOWN)
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    loginView?.displayConnectionError(e.message ?: UNKNOWN)
                }
            }
        }
    }

    fun setLoginView(loginView: SignInInterfaceListener?) {
        this.loginView = loginView
    }

    fun setRegisterView(registerView: RegisterInterfaceListener?) {
        this.registerView = registerView
    }

    interface SignInInterfaceListener {
        fun displayConnectionError(message: String)
        fun displayMissingField(field: String)
        fun connectUser()
    }

    interface RegisterInterfaceListener {
        fun displayRegisterError(message: String)
        fun confirmRegister()
    }
}