package com.example.food_donation_app



data class FirebaseUser(
    val uid: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val street: String = "",
    val suburb: String = "",
    val state: String = "",
    val postalCode: String = "",
    val profileImageUrl: String = ""
) {
    // No-argument constructor for Firebase
    constructor() : this(
        uid = "",
        email = "",
        firstName = "",
        lastName = "",
        phoneNumber = "",
        dateOfBirth = "",
        gender = "",
        street = "",
        suburb = "",
        state = "",
        postalCode = "",
        profileImageUrl = ""
    )

    // Convert to Room User
    fun toRoomUser(): com.example.food_donation_app.User {
        return com.example.food_donation_app.User(
            email = email,
            password = "", // Firebase handles authentication, no password needed
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
    }
}

// Extension function to convert Room User to Firebase User
fun com.example.food_donation_app.User.toFirebaseUser(uid: String): FirebaseUser {
    return FirebaseUser(
        uid = uid,
        email = email,
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
}