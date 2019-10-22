package com.example.mytodolist.usercase

import com.example.mytodolist.di.appModule
import com.example.mytodolist.usecase.UserGateway
import com.example.mytodolist.usecase.UserManager
import com.example.mytodolist.utils.FieldMissingException
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.*
import kotlin.test.assertFailsWith

class UserManagerTest : KoinTest {
    private val userManager: UserManager by inject()
    private val userGateway: UserGateway by inject()

    private val emailValid = "test@test.com"
    private val passwordValid = "passwordValid"

    private val emailEmpty = ""
    private val passwordEmpty = ""

    @Before
    fun before() {
        startKoin {
            modules(appModule)
        }
        declareMock<UserGateway>()
    }

    @After
    fun after() {
        stopKoin()
    }

    //create new user
    @Test
    fun createUser_success() {
        doNothing().`when`(userGateway).createUser(emailValid, passwordValid)
        userManager.createUser(emailValid, passwordValid)
        verify(userGateway, times(1)).createUser(emailValid, passwordValid)
    }

    @Test
    fun createUser_fail_email() {
        assertFailsWith(FieldMissingException::class) {
            userManager.createUser(emailEmpty, passwordValid)
        }
    }

    @Test
    fun createUser_fail_password() {
        assertFailsWith(FieldMissingException::class) {
            userManager.createUser(emailValid, passwordEmpty)
        }
    }

    //login user
    @Test
    fun loginUser_success() {
        doNothing().`when`(userGateway).loginUser(emailValid, passwordValid)
        userManager.loginUser(emailValid, passwordValid)
        verify(userGateway, times(1)).loginUser(emailValid, passwordValid)
    }

    @Test
    fun loginUser_fail_email() {
        assertFailsWith(FieldMissingException::class) {
            userManager.loginUser(emailEmpty, passwordValid)
        }
    }

    @Test
    fun loginUser_fail_password() {
        assertFailsWith(FieldMissingException::class) {
            userManager.loginUser(emailValid, passwordEmpty)
        }
    }
}