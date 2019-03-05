package com.example.mytodolist.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FirebaseInfos{

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun userDisconnect(){
        firebaseAuth.signOut()
    }

    fun currentUSer(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

}