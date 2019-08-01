package com.example.mytodolist.di

import com.example.mytodolist.firebase.FirebaseInfos
import com.example.mytodolist.presenter.LoginPresenter
import com.example.mytodolist.presenter.MainPresenter
import com.example.mytodolist.repo.FirebaseUserGateway
import com.example.mytodolist.repo.FirestoreRepo
import com.example.mytodolist.usecase.Repository
import com.example.mytodolist.usecase.TodoTaskManager
import com.example.mytodolist.usecase.UserGateway
import com.example.mytodolist.usecase.UserManager
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