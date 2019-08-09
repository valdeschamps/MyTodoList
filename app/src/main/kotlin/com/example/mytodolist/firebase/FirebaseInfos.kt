package com.example.mytodolist.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class FirebaseInfos {
    val firestoreDB = FirebaseFirestore.getInstance()
    val collectionUsersName: String = "users"
    val collectionTasksName: String = "tasks"

    init {
        firestoreDB.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    fun userDisconnect() {
        getFirebaseAuth().signOut()
    }

    fun currentUSer(): FirebaseUser? {
        return getFirebaseAuth().currentUser
    }

    fun getFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}