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
    val UNKNOWN = "unknown"

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
                registerView?.displayRegisterError(e.message ?: UNKNOWN)
            }
        }
    }

    fun loginUser(email: String, password: String) {
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

    fun setLoginView(loginView: SignInInterfaceListener?) {
        this.loginView = loginView
    }

    fun setRegisterView(registerView: RegisterInterfaceListener?) {
        this.registerView = registerView
    }

    interface SignInInterfaceListener {
        fun displayConnectionError(message: String)
        fun connectUser()
    }

    interface RegisterInterfaceListener {
        fun displayRegisterError(message: String)
        fun confirmRegister()
    }
}