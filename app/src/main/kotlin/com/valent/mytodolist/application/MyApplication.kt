package com.valent.mytodolist.application

import android.app.Application
import com.google.firebase.FirebaseApp
import com.valent.mytodolist.di.appModule
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