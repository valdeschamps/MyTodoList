package com.example.mytodolist.di

import com.example.mytodolist.firebase.FirebaseInfos
import com.example.mytodolist.firebase.FirestoreRepository
import com.example.mytodolist.presenter.TaskListPresenter
import org.koin.dsl.module.module

val appModule = module {
    single {FirebaseInfos()}
    single {FirestoreRepository()}
    single {TaskListPresenter()}
}