package com.kabbodev.educational.ui.viewModels

import androidx.lifecycle.ViewModel
import com.kabbodev.educational.data.model.User
import com.kabbodev.educational.data.repository.LoginRepository
import com.kabbodev.educational.ui.interfaces.FirebaseCallback

class LoginViewModel : ViewModel() {

    private val repository: LoginRepository = LoginRepository()

    fun login(email: String, password: String, listener: FirebaseCallback) {
        repository.login(email, password, listener)
    }

    fun registerUser(user: User, listener: FirebaseCallback) {
        repository.register(user, listener)
    }

}