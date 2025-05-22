package com.example.food_donation_app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.food_donation_app.User
import com.example.food_donation_app.HybridUserRepository
import com.example.food_donation_app.GoogleSignInHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Existing state classes
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

sealed class SignupState {
    object Idle : SignupState()
    object Loading : SignupState()
    object Success : SignupState()
    data class Error(val message: String) : SignupState()
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Loading : UpdateState()
    object Success : UpdateState()
    data class Error(val message: String) : UpdateState()
}

class AuthViewModel(
    private val repository: HybridUserRepository,
    private val googleSignInHelper: GoogleSignInHelper
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val _signupState = MutableStateFlow<SignupState>(SignupState.Idle)
    val signupState: StateFlow<SignupState> = _signupState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        // Check if user is already authenticated on app start
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            if (repository.isUserAuthenticated()) {
                val user = repository.getCurrentUser()
                _currentUser.value = user
                if (user != null) {
                    _loginState.value = LoginState.Success
                    // Sync user data from Firestore
                    repository.syncUserData()
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = repository.loginUser(email, password)

            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                _loginState.value = LoginState.Success
            } else {
                _loginState.value = LoginState.Error(
                    result.exceptionOrNull()?.message ?: "Login failed"
                )
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            val tokenResult = googleSignInHelper.signIn()
            if (tokenResult.isFailure) {
                _loginState.value = LoginState.Error(
                    tokenResult.exceptionOrNull()?.message ?: "Google Sign-In failed"
                )
                return@launch
            }

            val idToken = tokenResult.getOrThrow()
            val result = repository.signInWithGoogle(idToken)

            if (result.isSuccess) {
                _currentUser.value = result.getOrNull()
                _loginState.value = LoginState.Success
            } else {
                _loginState.value = LoginState.Error(
                    result.exceptionOrNull()?.message ?: "Google Sign-In failed"
                )
            }
        }
    }

    fun signup(
        email: String,
        password: String,
        confirmPassword: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        dateOfBirth: String,
        gender: String,
        street: String,
        suburb: String,
        state: String,
        postalCode: String
    ) {
        viewModelScope.launch {
            _signupState.value = SignupState.Loading

            // Validation
            if (password != confirmPassword) {
                _signupState.value = SignupState.Error("Passwords do not match")
                return@launch
            }

            if (password.length < 6) {
                _signupState.value = SignupState.Error("Password must be at least 6 characters")
                return@launch
            }

            val result = repository.registerUser(
                email, password, firstName, lastName, phoneNumber,
                dateOfBirth, gender, street, suburb, state, postalCode
            )

            if (result.isSuccess) {
                _signupState.value = SignupState.Success
                // Automatically log in the user after successful registration
                login(email, password)
            } else {
                _signupState.value = SignupState.Error(
                    result.exceptionOrNull()?.message ?: "Registration failed"
                )
            }
        }
    }

    fun updateUser(
        firstName: String,
        lastName: String,
        email: String,
        phoneNumber: String,
        dateOfBirth: String,
        gender: String,
        street: String,
        suburb: String,
        state: String,
        postalCode: String
    ) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading

            val currentUserValue = _currentUser.value
            if (currentUserValue == null) {
                _updateState.value = UpdateState.Error("No user logged in")
                return@launch
            }

            val updatedUser = currentUserValue.copy(
                firstName = firstName,
                lastName = lastName,
                email = email,
                phoneNumber = phoneNumber,
                dateOfBirth = dateOfBirth,
                gender = gender,
                street = street,
                suburb = suburb,
                state = state,
                postalCode = postalCode
            )

            val result = repository.updateUser(updatedUser)
            if (result.isSuccess) {
                _currentUser.value = updatedUser
                _updateState.value = UpdateState.Success
            } else {
                _updateState.value = UpdateState.Error(
                    result.exceptionOrNull()?.message ?: "Update failed"
                )
            }
        }
    }

    fun logout() {
        repository.signOut()
        _currentUser.value = null
        _loginState.value = LoginState.Idle
        _signupState.value = SignupState.Idle
        _updateState.value = UpdateState.Idle
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

    fun resetSignupState() {
        _signupState.value = SignupState.Idle
    }

    fun resetUpdateState() {
        _updateState.value = UpdateState.Idle
    }

    // Sync user data manually
    fun syncUserData() {
        viewModelScope.launch {
            repository.syncUserData()
            val user = repository.getCurrentUser()
            _currentUser.value = user
        }
    }
}