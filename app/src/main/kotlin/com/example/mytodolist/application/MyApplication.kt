package com.example.mytodolist.application

import android.app.Application
import com.example.mytodolist.di.appModule
import com.google.firebase.FirebaseApp
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        startKoin {
            modules(appModule)
        }
    }
}