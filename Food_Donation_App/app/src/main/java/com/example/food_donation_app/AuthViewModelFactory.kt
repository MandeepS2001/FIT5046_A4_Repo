package com.example.food_donation_app



import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.food_donation_app.HybridUserRepository
import com.example.food_donation_app.GoogleSignInHelper

class AuthViewModelFactory(
    private val repository: HybridUserRepository,
    private val googleSignInHelper: GoogleSignInHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository, googleSignInHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}