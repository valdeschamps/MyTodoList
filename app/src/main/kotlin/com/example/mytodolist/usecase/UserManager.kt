package com.example.mytodolist.usecase

import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class UserManager : KoinComponent {
    private val userGateway: UserGateway by inject()

    fun userAlreadyLogged(): Boolean {
        return userGateway.userAlreadyLogged()
    }

    fun createUser(email: String, password: String) {
        if (email == "") {
            throw FieldMissingException("email")
        } else if (password == "") {
            throw FieldMissingException("password")
        }
        userGateway.createUser(email, password)
    }

    fun loginUser(email: String, password: String) {
        if (email == "") {
            throw FieldMissingException("email")
        } else if (password == "") {
            throw FieldMissingException("password")
        }
        userGateway.loginUser(email, password)
    }

    class FieldMissingException(message: String) : IllegalArgumentException(message)
}

interface UserGateway {
    fun userAlreadyLogged(): Boolean
    fun createUser(email: String, password: String)
    fun loginUser(email: String, password: String)
}