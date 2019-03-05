package com.example.mytodolist.di

import com.example.mytodolist.firebase.FirebaseInfos
import org.koin.dsl.module.module

val appModule = module {
    single {FirebaseInfos()}
}