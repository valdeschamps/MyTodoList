package com.example.mytodolist.usecase

import com.example.mytodolist.utils.FieldMissingException
import com.example.mytodolist.utils.FieldMissingException.Companion.FIELD_EMAIL
import com.example.mytodolist.utils.FieldMissingException.Companion.FIELD_PASSWORD
import org.koin.core.KoinComponent
import org.koin.core.inject

class UserManager : KoinComponent {
    private val userGateway: UserGateway by inject()

    fun userAlreadyLogged(): Boolean {
        return userGateway.userAlreadyLogged()
    }

    fun createUser(email: String, password: String) {
        if (email == "") {
            throw FieldMissingException(FIELD_EMAIL)
        } else if (password == "") {
            throw FieldMissingException(FIELD_PASSWORD)
        }
        userGateway.createUser(email, password)
    }

    fun loginUser(email: String, password: String) {
        if (email == "") {
            throw FieldMissingException(FIELD_EMAIL)
        } else if (password == "") {
            throw FieldMissingException(FIELD_PASSWORD)
        }
        userGateway.loginUser(email, password)
    }

    fun disconnectUser(){
        userGateway.disconnectUser()
    }
}

interface UserGateway {
    fun userAlreadyLogged(): Boolean
    fun createUser(email: String, password: String)
    fun loginUser(email: String, password: String)
    fun disconnectUser()
}