package com.example.mytodolist.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseInfos {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val firestoreDB = FirebaseFirestore.getInstance()
    val collectionUsersName: String = "users"
    val collectionTasksName: String = "tasks"

    fun userDisconnect() {
        firebaseAuth.signOut()
    }

    fun currentUSer(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}