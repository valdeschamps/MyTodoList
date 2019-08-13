package com.example.mytodolist.presenter

import com.example.mytodolist.usecase.UserManager
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class LoginPresenter : KoinComponent {
    private val userManager: UserManager by inject()
    private var loadingView: LoadingView? = null
    private var loginView: SignInView? = null
    private var registerView: RegisterView? = null
    private val job = SupervisorJob()
    private val scopeMain = CoroutineScope(Dispatchers.Main + job)

    companion object {
        const val UNKNOWN = "unknown"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val PASSWORDCONFIRMATION = "confirmPassword"
        const val ERROR_INVALIDCRED = "ERROR_INVALIDCRED"
        const val ERROR_NETWORK = "ERROR_NETWORK"
        const val ERROR_INVALIDUSER = "ERROR_INVALIDUSER"
        const val ERROR_FIELDMISSING = "ERROR_FIELDMISSING"
    }

    fun checkLoggedUser() {
        if (userManager.userAlreadyLogged()) {
            loadingView?.connectUser()
        } else {
            loadingView?.displaySignFragment()
        }
    }

    private fun checkRegisterForm(email: String, password: String, passwordConfirmation: String): Boolean {
        var valid = true
        if (email == "") {
            valid = false
            registerView?.displayMissingField(EMAIL)
        }
        if (password == "") {
            valid = false
            registerView?.displayMissingField(PASSWORD)
        }
        if (passwordConfirmation == "") {
            valid = false
            registerView?.displayMissingField(PASSWORDCONFIRMATION)
        }
        if (password != passwordConfirmation) {
            valid = false
            registerView?.displayPasswordConfirmationError()
        }
        return valid
    }

    fun createUser(email: String, password: String, passwordConfirmation: String) {
        if (checkRegisterForm(email, password, passwordConfirmation)) {
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
                    loginView?.displayError(ERROR_FIELDMISSING)
                } catch (e: FirebaseAuthInvalidCredentialsException) {
                    loginView?.displayError(ERROR_INVALIDCRED)
                } catch (e: FirebaseNetworkException){
                    loginView?.displayError(ERROR_NETWORK)
                } catch (e: FirebaseAuthInvalidUserException){
                    loginView?.displayError(ERROR_INVALIDUSER)
                }
            }
        }
    }

    fun setLoadingView(loadingView: LoadingView?) {
        this.loadingView = loadingView
    }

    fun setLoginView(loginView: SignInView?) {
        this.loginView = loginView
    }

    fun setRegisterView(registerView: RegisterView?) {
        this.registerView = registerView
    }

    interface LoadingView {
        fun connectUser()
        fun displaySignFragment()
    }

    interface SignInView {
        fun displayError(code: String)
        fun displayMissingField(field: String)
        fun connectUser()
    }

    interface RegisterView {
        fun displayRegisterError(message: String)
        fun displayPasswordConfirmationError()
        fun displayMissingField(field: String)
        fun confirmRegister()
    }
}