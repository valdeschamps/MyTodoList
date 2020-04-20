package com.valent.mytodolist.di

import com.valent.mytodolist.firebase.FirebaseInfos
import com.valent.mytodolist.presenter.LoginPresenter
import com.valent.mytodolist.presenter.MainPresenter
import com.valent.mytodolist.repo.FirebaseUserGateway
import com.valent.mytodolist.repo.FirestoreRepo
import com.valent.mytodolist.usecase.Repository
import com.valent.mytodolist.usecase.TodoTaskManager
import com.valent.mytodolist.usecase.UserGateway
import com.valent.mytodolist.usecase.UserManager
import org.koin.dsl.module

val appModule = module {
    single { FirebaseInfos() }
    single { LoginPresenter() }
    single { MainPresenter() }
    single<UserGateway> { FirebaseUserGateway() }
    single { UserManager() }
    single<Repository> { FirestoreRepo() }
    single { TodoTaskManager() }
}