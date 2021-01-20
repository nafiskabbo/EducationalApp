package com.kabbodev.educationalapp.ui.viewModels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class DashboardViewModel: ViewModel() {

    private val mAuth = Firebase.auth

    fun getCurrentUser() : FirebaseUser? = mAuth.currentUser


}