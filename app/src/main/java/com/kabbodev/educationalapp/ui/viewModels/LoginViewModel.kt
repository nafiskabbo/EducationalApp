package com.kabbodev.educationalapp.ui.viewModels

import androidx.lifecycle.ViewModel
import com.kabbodev.educationalapp.data.model.User
import com.kabbodev.educationalapp.data.repository.LoginRepository
import com.kabbodev.educationalapp.ui.`interface`.FirebaseCallback

class LoginViewModel : ViewModel() {

    private val repository: LoginRepository = LoginRepository()

    fun login(email: String, password: String, listener: FirebaseCallback) {
        repository.login(email, password, listener)
    }

    fun registerUser(user: User, listener: FirebaseCallback) {
        repository.register(user, listener)
    }

}