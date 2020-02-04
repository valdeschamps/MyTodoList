package com.example.mytodolist.presenter

import com.example.mytodolist.di.appModule
import com.example.mytodolist.usecase.UserManager
import com.example.mytodolist.utils.FieldMissingException
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class LoginPresenterTest : KoinTest {
    private val userManager: UserManager by inject()
    private val loginPresenter: LoginPresenter by inject()
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private val email = "email"
    private val emailEmpty = ""
    private val password = "password"
    private val passwordConfirmationFail = "passwordFail"
    private val passwordConfirmationSuccess = "password"
    private val delay = 3000L

    @Before
    fun before() {
        startKoin {
            modules(appModule)
        }
        declareMock<UserManager>()

        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun after() {
        stopKoin()

        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }

    @Test
    fun createUser_success() {
        val registerView = mock(LoginPresenter.RegisterView::class.java)
        loginPresenter.setRegisterView(registerView)

        runBlocking {
            loginPresenter.createUser(email, password, passwordConfirmationSuccess)
            delay(delay)
            verify(registerView)?.confirmRegister()
        }
    }

    @Test
    fun createUser_fail_password() {
        val registerView = mock(LoginPresenter.RegisterView::class.java)
        loginPresenter.setRegisterView(registerView)

        runBlocking {
            loginPresenter.createUser(email, password, passwordConfirmationFail)
            delay(delay)
            verify(registerView)?.displayPasswordConfirmationError()
        }
    }

    @Test
    fun loginUser_success() {
        val loginView = mock(LoginPresenter.SignInView::class.java)
        loginPresenter.setLoginView(loginView)

        runBlocking {
            loginPresenter.loginUser(email, password)
            delay(delay)
            verify(loginView)?.connectUser()
        }
    }

    @Test
    fun loginUser_fail_email_empty() {
        val loginView = mock(LoginPresenter.SignInView::class.java)
        loginPresenter.setLoginView(loginView)

        runBlocking {
            loginPresenter.loginUser(emailEmpty, password)
            delay(delay)
            verify(loginView)?.displayMissingField(FieldMissingException.FIELD_EMAIL)
        }
    }

    @Test
    fun loginUser_fail_exception() {
        val loginView = mock(LoginPresenter.SignInView::class.java)
        loginPresenter.setLoginView(loginView)

        given(userManager.loginUser(email, password)).willAnswer {
            throw FirebaseNetworkException("test")
        }

        runBlocking {
            loginPresenter.loginUser(email, password)
            delay(delay)
            verify(loginView)?.displayError(LoginPresenter.ERROR_NETWORK)
        }
    }
}