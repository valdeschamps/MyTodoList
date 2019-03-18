package com.example.mytodolist.presenter

import com.example.mytodolist.firebase.FirestoreRepository
import kotlinx.coroutines.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class LoginPresenter(): KoinComponent{
    private val firestoreRepository: FirestoreRepository by inject()
    private var loginView: SignInInterfaceListener? = null
    private var registerView: RegisterInterfaceListener? = null

    private val job = SupervisorJob()
    private val scopeMain = CoroutineScope(Dispatchers.Main + job)

    fun loginUser(email: String, password: String){
        scopeMain.launch {
            val result = withContext(Dispatchers.Default) {
                firestoreRepository.loginUser(email, password)
            }
            if(result.first){
                loginView?.connectUser()
            }else{
                loginView?.displayConnectionError(result.second)
            }
        }
    }

    fun createUser(email: String, password: String){
        scopeMain.launch {
            val result = withContext(Dispatchers.Default) {
                firestoreRepository.createUSer(email, password)
            }
            if (result.first) {
                registerView?.confirmRegister()
            } else {
                registerView?.displayRegisterError(result.second)
            }
        }
    }

    fun setLoginView(loginView: SignInInterfaceListener?){
        this.loginView = loginView
    }

    fun setRegisterView(registerView: RegisterInterfaceListener?){
        this.registerView = registerView
    }

    interface SignInInterfaceListener{
        fun displayConnectionError(message: String)
        fun connectUser()
    }

    interface RegisterInterfaceListener{
        fun displayRegisterError(message: String)
        fun confirmRegister()
    }

}