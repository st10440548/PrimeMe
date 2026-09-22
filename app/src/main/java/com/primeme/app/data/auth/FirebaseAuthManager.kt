package com.primeme.app.data.auth

import android.content.Context
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthManager(
    private val context: Context
) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
