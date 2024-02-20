package com.example.cmu_g10.Data.User

import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.mutableStateOf

/**
 * State representing the user.
 */
data class UserState(
    val userId: Int = 0,
    val users: List<User> = emptyList(),
    var name: MutableState<String> = mutableStateOf(""),
    var email: MutableState<String> = mutableStateOf(""),
    var password: MutableState<String> = mutableStateOf(""),
    var confirmPassword: MutableState<String> = mutableStateOf(""),
    var phone: MutableState<String> = mutableStateOf(""),
    var photo: MutableState<String> = mutableStateOf(""),
    var balance: MutableState<Double> = mutableStateOf(0.0),
)
