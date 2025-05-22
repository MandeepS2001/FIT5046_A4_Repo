package com.example.food_donation_app

class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(user: User): Result<Unit> {
        return try {
            // Check if user already exists
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                Result.failure(Exception("User with this email already exists"))
            } else {
                userDao.registerUser(user)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val user = userDao.loginUser(email, password)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkUserExists(email: String): Boolean {
        return userDao.getUserByEmail(email) != null
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            // If email is being changed, check if new email already exists
            if (user.email != userDao.getUserByEmail(user.email)?.email) {
                val existingUser = userDao.getUserByEmail(user.email)
                if (existingUser != null) {
                    return Result.failure(Exception("Email already in use"))
                }
            }
            userDao.updateUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}