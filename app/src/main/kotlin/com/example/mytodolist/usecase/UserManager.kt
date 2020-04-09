package com.example.mytodolist.usecase

import com.example.mytodolist.utils.FieldMissingException
import com.example.mytodolist.utils.FieldMissingException.Companion.FIELD_EMAIL
import com.example.mytodolist.utils.FieldMissingException.Companion.FIELD_PASSWORD
import org.koin.core.KoinComponent
import org.koin.core.inject

class UserManager : KoinComponent {
    private val userGateway: UserGateway by inject()

    private fun checkEmailPwd(email: String, password: String) {
        if (email == "" || email == "null") {
            throw FieldMissingException(FIELD_EMAIL)
        } else if (password == "" || password == "null") {
            throw FieldMissingException(FIELD_PASSWORD)
        }
    }

    fun userAlreadyLogged(): Boolean {
        return userGateway.userAlreadyLogged()
    }

    fun createUser(email: String, password: String) {
        checkEmailPwd(email, password)
        userGateway.createUser(email, password)
    }

    fun loginUser(email: String, password: String) {
        checkEmailPwd(email, password)
        userGateway.loginUser(email, password)
    }

    fun disconnectUser() {
        userGateway.disconnectUser()
    }

    fun deleteUser() {
        userGateway.deleteUser()
    }

    fun reAuthenticate(email: String, password: String){
        checkEmailPwd(email, password)
        userGateway.reAuthenticate(email, password)
    }
}

interface UserGateway {
    fun userAlreadyLogged(): Boolean
    fun createUser(email: String, password: String)
    fun loginUser(email: String, password: String)
    fun disconnectUser()
    fun deleteUser()
    fun reAuthenticate(email: String, password: String)
}