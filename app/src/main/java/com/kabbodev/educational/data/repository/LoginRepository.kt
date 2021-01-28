package com.kabbodev.educational.data.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kabbodev.educational.data.model.User
import com.kabbodev.educational.ui.`interface`.FirebaseCallback

class LoginRepository {

    private val firebaseAuth = Firebase.auth
    private val firebaseFirestore = Firebase.firestore
    private val userCollection: CollectionReference = firebaseFirestore.collection("USERS")

    fun login(email: String, password: String, listener: FirebaseCallback) {
        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                listener.onSuccessListener()
            }
            .addOnFailureListener { e ->
                listener.onFailureListener(e)
            }
    }

    fun register(user: User, listener: FirebaseCallback) {
        firebaseAuth
            .createUserWithEmailAndPassword(user.email!!, user.password!!)
            .addOnSuccessListener {
                saveData(user, listener)
            }
            .addOnFailureListener { e ->
                listener.onFailureListener(e)
            }
    }

    private fun saveData(user: User, listener: FirebaseCallback) {
        userCollection.document(firebaseAuth.uid!!).set(user)
            .addOnSuccessListener {
                listener.onSuccessListener()
            }
            .addOnFailureListener {
                listener.onFailureListener(it)
            }
    }

}