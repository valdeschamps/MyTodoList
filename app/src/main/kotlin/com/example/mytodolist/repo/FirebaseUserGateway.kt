package com.example.mytodolist.repo

import com.example.mytodolist.firebase.FirebaseInfos
import com.example.mytodolist.usecase.UserGateway
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.concurrent.ExecutionException

class FirebaseUserGateway : UserGateway, KoinComponent {
    private val firebaseInfos: FirebaseInfos by inject()

    override fun disconnectUser() {
        firebaseInfos.userDisconnect()
    }

    override fun userAlreadyLogged(): Boolean {
        return firebaseInfos.currentUSer() != null
    }

    override fun createUser(email: String, password: String) {
        val task = firebaseInfos.getFirebaseAuth().createUserWithEmailAndPassword(email, password)
        try {
            Tasks.await(task)
        } catch (e: ExecutionException) {
            throw e.cause ?: e
        }
    }

    override fun loginUser(email: String, password: String) {
        val task = firebaseInfos.getFirebaseAuth().signInWithEmailAndPassword(email, password)
        try {
            Tasks.await(task)
        } catch (e: ExecutionException) {
            throw e.cause ?: e
        }
    }

    override fun deleteUser() {
        val task = firebaseInfos.getFirebaseAuth().currentUser?.delete()
        if (task != null) {
            try {
                Tasks.await(task)
                disconnectUser()
            } catch (e: ExecutionException) {
                throw e.cause ?: e
            }
        }
    }

    override fun reAuthenticate(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        val task = firebaseInfos.getFirebaseAuth().currentUser?.reauthenticate(credential)
        if (task != null) {
            try {
                Tasks.await(task)
            } catch (e: ExecutionException) {
                throw e.cause ?: e
            }
        }
    }
}

