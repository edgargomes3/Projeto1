package com.example.projeto1.dataclass

data class UserProfileData(
    val id_userProfile: Int,
    val name: String,
    val birthDate: String,
    val gender: String,
    val typeDiabetes: String,
    val country: String,
    val diagnosis_year: Int,
    val isCaregiver: Boolean,
    val height: Int,
    val postalCode: String,
    val education: Int,
)
