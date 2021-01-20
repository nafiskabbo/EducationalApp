package com.kabbodev.educationalapp.data.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kabbodev.educationalapp.data.model.User
import com.kabbodev.educationalapp.ui.`interface`.FirebaseCallback
import java.util.*

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
        val userdata: MutableMap<String, Any> = HashMap()
        userdata["fullName"] = user.fullName!!
        userdata["email"] = user.email!!
        userdata["board"] = user.board!!
        userdata["class_"] = user.class_!!
        userdata["password"] = user.password!!

        userCollection.document(firebaseAuth.uid!!).set(userdata)
            .addOnSuccessListener {
                listener.onSuccessListener()
            }
            .addOnFailureListener {
                listener.onFailureListener(it)
            }
    }

}