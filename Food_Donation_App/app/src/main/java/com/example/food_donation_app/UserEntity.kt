package com.example.food_donation_app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val dateOfBirth: String,
    val gender: String,
    val street: String,
    val suburb: String,
    val state: String,
    val postalCode: String
)