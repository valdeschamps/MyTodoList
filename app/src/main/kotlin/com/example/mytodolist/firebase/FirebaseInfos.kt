package com.example.mytodolist.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseInfos {
    val firestoreDB = FirebaseFirestore.getInstance()
    val collectionUsersName: String = "users"
    val collectionTasksName: String = "tasks"

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