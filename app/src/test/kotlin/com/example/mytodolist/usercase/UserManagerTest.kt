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
    val userManager: UserManager by inject()
    val userGateway: UserGateway by inject()

    @Before
    fun before() {
        startKoin {
            modules(appModule)
        }
    }

    @After
    fun after() {
        stopKoin()
    }

    @Test
    fun createUser_success() {
        val email = "test@test.com"
        val password = "password"

        declareMock<UserGateway> {
            given(this.createUser(email, password)).willAnswer {}
        }

        userManager.createUser(email, password)
        verify(userGateway, times(1)).createUser(email, password)
    }

    @Test
    fun createUser_fail() {
        assertFailsWith(FieldMissingException::class) {
            userManager.createUser("", "")
        }
    }
}