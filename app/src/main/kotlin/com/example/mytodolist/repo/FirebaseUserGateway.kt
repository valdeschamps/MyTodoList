package com.example.mytodolist.repo

import com.example.mytodolist.firebase.FirebaseInfos
import com.example.mytodolist.usecase.UserGateway
import com.google.android.gms.tasks.Tasks
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.util.concurrent.ExecutionException

class FirebaseUserGateway : UserGateway, KoinComponent {
    private val firebaseInfos: FirebaseInfos by inject()
    private val firebaseAuth = firebaseInfos.firebaseAuth

    override fun userAlreadyLogged(): Boolean {
        return firebaseInfos.currentUSer() != null
    }

    override fun createUser(email: String, password: String) {
        val task = firebaseAuth.createUserWithEmailAndPassword(email, password)
        try {
            Tasks.await(task)
        } catch (e: ExecutionException) {
            throw e.cause ?: e
        }
    }

    override fun loginUser(email: String, password: String) {
        val task = firebaseAuth.signInWithEmailAndPassword(email, password)
        try {
            Tasks.await(task)
        } catch (e: ExecutionException) {
            throw e.cause ?: e
        }
    }
}

