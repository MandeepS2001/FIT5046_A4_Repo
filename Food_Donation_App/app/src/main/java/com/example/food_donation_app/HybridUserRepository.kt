package com.example.food_donation_app


import com.example.food_donation_app.User
import com.example.food_donation_app.UserDao
import com.example.food_donation_app.FirebaseUser
import com.example.food_donation_app.toFirebaseUser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HybridUserRepository @Inject constructor(
    private val userDao: UserDao,
    private val firebaseRepository: FirebaseRepository
) {

    // Registration with Firebase + Room sync
    suspend fun registerUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        dateOfBirth: String,
        gender: String,
        street: String,
        suburb: String,
        state: String,
        postalCode: String
    ): Result<Unit> {
        return try {
            // 1. Create user in Firebase Auth
            val firebaseResult = firebaseRepository.signUpWithEmail(email, password)
            if (firebaseResult.isFailure) {
                return Result.failure(firebaseResult.exceptionOrNull() ?: Exception("Firebase registration failed"))
            }

            val uid = firebaseResult.getOrThrow()

            // 2. Create user objects
            val roomUser = User(
                email = email,
                password = password, // Store encrypted/hashed in production
                firstName = firstName,
                lastName = lastName,
                phoneNumber = phoneNumber,
                dateOfBirth = dateOfBirth,
                gender = gender,
                street = street,
                suburb = suburb,
                state = state,
                postalCode = postalCode
            )

            val firebaseUser = roomUser.toFirebaseUser(uid)

            // 3. Save to Room (local storage)
            userDao.registerUser(roomUser)

            // 4. Save to Firestore (cloud storage)
            val firestoreResult = firebaseRepository.saveUserToFirestore(firebaseUser)
            if (firestoreResult.isFailure) {
                // Optionally rollback Room insertion if Firestore fails
                // userDao.deleteUser(email)
                return Result.failure(firestoreResult.exceptionOrNull() ?: Exception("Firestore save failed"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login with Firebase Auth
    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            // 1. Authenticate with Firebase
            val firebaseResult = firebaseRepository.signInWithEmail(email, password)
            if (firebaseResult.isFailure) {
                return Result.failure(firebaseResult.exceptionOrNull() ?: Exception("Firebase login failed"))
            }

            val uid = firebaseResult.getOrThrow()

            // 2. Try to get user from Room first (offline-first approach)
            var roomUser = userDao.getUserByEmail(email)

            // 3. If not in Room, get from Firestore and sync to Room
            if (roomUser == null) {
                val firestoreResult = firebaseRepository.getUserFromFirestore(uid)
                if (firestoreResult.isSuccess) {
                    val firebaseUser = firestoreResult.getOrNull()
                    if (firebaseUser != null) {
                        roomUser = firebaseUser.toRoomUser()
                        // Sync to Room for offline access
                        userDao.registerUser(roomUser)
                    }
                }
            }

            if (roomUser != null) {
                Result.success(roomUser)
            } else {
                Result.failure(Exception("User data not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Google Sign-In
    suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            // 1. Authenticate with Google
            val firebaseResult = firebaseRepository.signInWithGoogle(idToken)
            if (firebaseResult.isFailure) {
                return Result.failure(firebaseResult.exceptionOrNull() ?: Exception("Google sign in failed"))
            }

            val uid = firebaseResult.getOrThrow()
            val email = firebaseRepository.getCurrentUserEmail() ?: return Result.failure(Exception("Email not found"))

            // 2. Check if user exists in Firestore
            val firestoreResult = firebaseRepository.getUserFromFirestore(uid)
            if (firestoreResult.isSuccess) {
                val firebaseUser = firestoreResult.getOrNull()
                if (firebaseUser != null) {
                    // User exists, sync to Room
                    val roomUser = firebaseUser.toRoomUser()
                    val existingUser = userDao.getUserByEmail(email)
                    if (existingUser == null) {
                        userDao.registerUser(roomUser)
                    }
                    return Result.success(roomUser)
                }
            }

            // 3. New Google user - create minimal profile
            val newUser = User(
                email = email,
                password = "", // No password for Google users
                firstName = "",
                lastName = "",
                phoneNumber = "",
                dateOfBirth = "",
                gender = "",
                street = "",
                suburb = "",
                state = "",
                postalCode = ""
            )

            // Save to both Room and Firestore
            userDao.registerUser(newUser)
            firebaseRepository.saveUserToFirestore(newUser.toFirebaseUser(uid))

            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update user in both Room and Firebase
    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            val uid = firebaseRepository.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            // 1. Update in Room
            userDao.updateUser(user)

            // 2. Update in Firestore
            val firebaseUser = user.toFirebaseUser(uid)
            val firestoreResult = firebaseRepository.updateUserInFirestore(firebaseUser)

            if (firestoreResult.isFailure) {
                return Result.failure(firestoreResult.exceptionOrNull() ?: Exception("Firestore update failed"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Sync data from Firestore to Room
    suspend fun syncUserData(): Result<Unit> {
        return try {
            val uid = firebaseRepository.getCurrentUserId()
                ?: return Result.failure(Exception("User not authenticated"))

            val firestoreResult = firebaseRepository.getUserFromFirestore(uid)
            if (firestoreResult.isSuccess) {
                val firebaseUser = firestoreResult.getOrNull()
                if (firebaseUser != null) {
                    val roomUser = firebaseUser.toRoomUser()
                    userDao.updateUser(roomUser)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Check authentication status
    fun isUserAuthenticated(): Boolean = firebaseRepository.isUserSignedIn()

    // Sign out
    fun signOut() {
        firebaseRepository.signOut()
    }

    // Get current user from Room (offline-first)
    suspend fun getCurrentUser(): User? {
        val email = firebaseRepository.getCurrentUserEmail() ?: return null
        return userDao.getUserByEmail(email)
    }
}