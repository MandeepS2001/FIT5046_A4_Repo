package com.example.food_donation_app



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Donation(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val location: String,
    val time: String,
    val weight: Float,
    val imageResId: Int
)